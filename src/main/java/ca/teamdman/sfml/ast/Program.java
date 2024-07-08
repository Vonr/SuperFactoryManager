package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfm.common.util.SFMUtils;
import ca.teamdman.sfml.SFMLLexer;
import ca.teamdman.sfml.SFMLParser;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.antlr.v4.runtime.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public record Program(
        String name,
        List<Trigger> triggers,
        Set<String> referencedLabels,
        Set<ResourceIdentifier<?, ?, ?>> referencedResources
) implements Statement {
    public static final int MAX_PROGRAM_LENGTH = 80960;
    public static final int MAX_LABEL_LENGTH = 256;

    public static void compile(
            String programString,
            BiConsumer<Program, ASTBuilder> onSuccess,
            Consumer<List<TranslatableContents>> onFailure
    ) {
        SFMLLexer lexer = new SFMLLexer(CharStreams.fromString(programString));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SFMLParser parser = new SFMLParser(tokens);
        ASTBuilder builder = new ASTBuilder();

        // set up error capturing
        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        List<TranslatableContents> errors = new ArrayList<>();
        List<String> buildErrors = new ArrayList<>();
        ListErrorListener listener = new ListErrorListener(buildErrors);
        lexer.addErrorListener(listener);
        parser.addErrorListener(listener);

        // initial parse
        SFMLParser.ProgramContext context = parser.program();
        buildErrors.stream().map(Constants.LocalizationKeys.PROGRAM_ERROR_LITERAL::get).forEach(errors::add);


        // build AST
        Program program = null;
        if (errors.isEmpty()) {
            try {
                program = builder.visitProgram(context);
                // make sure all referenced resources exist now during compilation instead of waiting for the program to tick

                for (ResourceIdentifier<?, ?, ?> referencedResource : program.referencedResources) {
                    try {
                        ResourceType<?, ?, ?> resourceType = referencedResource.getResourceType();
                        if (resourceType == null) {
                            errors.add(Constants.LocalizationKeys.PROGRAM_ERROR_UNKNOWN_RESOURCE_TYPE.get(
                                    referencedResource));
                        }
                    } catch (ResourceLocationException e) {
                        errors.add(Constants.LocalizationKeys.PROGRAM_ERROR_MALFORMED_RESOURCE_TYPE.get(referencedResource));
                    }
                }
            } catch (ResourceLocationException | IllegalArgumentException | AssertionError e) {
                errors.add(Constants.LocalizationKeys.PROGRAM_ERROR_LITERAL.get(e.getMessage()));
            } catch (Throwable t) {
                errors.add(Constants.LocalizationKeys.PROGRAM_ERROR_COMPILE_FAILED.get());
                SFM.LOGGER.error("Encountered unhandled error \"{}\" while compiling program\n```\n{}\n```", t, programString);
                if (!FMLEnvironment.production) {
                    var message = t.getMessage();
                    if (message != null) {
                        errors.add(SFMUtils.getTranslatableContents(t.getClass().getSimpleName() + ": " + message));
                    } else {
                        errors.add(SFMUtils.getTranslatableContents(t.getClass().getSimpleName()));
                    }
                }
            }
        }

        if (program == null && errors.isEmpty()) {
            errors.add(Constants.LocalizationKeys.PROGRAM_ERROR_COMPILE_FAILED.get());
            SFM.LOGGER.error("Program was somehow null after a successful compile. I have no idea how this could happen, but it definitely shouldn't.\n```\n{}\n```", programString);
        }

        if (errors.isEmpty()) {
            onSuccess.accept(program, builder);
        } else {
            onFailure.accept(errors);
        }
    }

    public ArrayList<TranslatableContents> gatherWarnings(ItemStack disk, @Nullable ManagerBlockEntity manager) {
        var warnings = new ArrayList<TranslatableContents>();
        var labels = LabelPositionHolder.from(disk);
        // labels in code but not in world
        for (String label : referencedLabels) {
            var isUsed = !labels.getPositions(label).isEmpty();
            if (!isUsed) {
                warnings.add(Constants.LocalizationKeys.PROGRAM_WARNING_UNUSED_LABEL.get(label));
            }
        }

        // labels used in world but not defined in code
        labels.get().keySet()
                .stream()
                .filter(x -> !referencedLabels.contains(x))
                .forEach(label -> warnings.add(Constants.LocalizationKeys.PROGRAM_WARNING_UNDEFINED_LABEL.get(label)));

        var level = manager != null ? manager.getLevel() : null;
        if (level != null) {
            // labels in world but not connected via cables
            CableNetworkManager
                    .getOrRegisterNetworkFromManagerPosition(manager)
                    .ifPresent(network -> labels.forEach((label, pos) -> {
                        var adjacent = network.isAdjacentToCable(pos);
                        if (!adjacent) {
                            warnings.add(Constants.LocalizationKeys.PROGRAM_WARNING_DISCONNECTED_LABEL.get(
                                    label,
                                    String.format(
                                            "[%d,%d,%d]",
                                            pos.getX(),
                                            pos.getY(),
                                            pos.getZ()
                                    )
                            ));
                        }
                        var viable = SFMUtils.discoverCapabilityProvider(level, pos).isPresent();
                        if (!viable && adjacent) {
                            warnings.add(Constants.LocalizationKeys.PROGRAM_WARNING_CONNECTED_BUT_NOT_VIABLE_LABEL.get(
                                    label,
                                    String.format(
                                            "[%d,%d,%d]",
                                            pos.getX(),
                                            pos.getY(),
                                            pos.getZ()
                                    )
                            ));
                        }
                    }));
        }

        // try and validate that references resources exist
        for (var resource : referencedResources) {
            // skip regex resources
            Optional<ResourceLocation> loc = resource.getLocation();
            if (loc.isEmpty()) continue;

            // make sure resource type is registered
            var type = resource.getResourceType();
            if (type == null) {
                SFM.LOGGER.error("Resource type not found for resource: {}, should have been validated at program compile", resource);
                continue;
            }

            // make sure resource exists in the registry
            if (!type.registryKeyExists(loc.get())) {
                warnings.add(Constants.LocalizationKeys.PROGRAM_WARNING_UNKNOWN_RESOURCE_ID.get(resource));
            }
        }

        // check for poor round-robin usage
        getDescendantStatements()
                .filter(IOStatement.class::isInstance)
                .map(IOStatement.class::cast)
                .forEach(statement -> {
                    { // round robin smells
                        var smell = statement
                                .labelAccess()
                                .roundRobin()
                                .getSmell(statement.labelAccess(), statement.each());
                        if (smell != null) {
                            warnings.add(smell.get(statement.toStringPretty()));
                        }
                    }
                    { // resource each without pattern
                        boolean smells = statement
                                .resourceLimits()
                                .resourceLimits()
                                .stream()
                                .anyMatch(rl -> rl.limit().quantity().idExpansionBehaviour()
                                                == ResourceQuantity.IdExpansionBehaviour.EXPAND && !rl
                                        .resourceId()
                                        .usesRegex());
                        if (smells) {
                            warnings.add(Constants.LocalizationKeys.PROGRAM_WARNING_RESOURCE_EACH_WITHOUT_PATTERN.get(
                                    statement.toStringPretty()
                            ));
                        }
                    }
                });
        return warnings;
    }

    public void fixWarnings(ItemStack disk, ManagerBlockEntity manager) {
        var labels = LabelPositionHolder.from(disk);
        // remove labels not defined in code
        labels.removeIf(label -> !referencedLabels.contains(label));

        // remove labels not connected via cables
        CableNetworkManager
                .getOrRegisterNetworkFromManagerPosition(manager)
                .ifPresent(network -> labels.removeIf((label, pos) -> !network.isAdjacentToCable(pos)));
        labels.save(disk);

        // update warnings
        DiskItem.setWarnings(disk, gatherWarnings(disk, manager));
    }

    /**
     * Create a context and tick the program.
     *
     * @return {@code true} if a trigger entered its body
     */
    public boolean tick(ManagerBlockEntity manager) {
        var context = new ProgramContext(this, manager, ProgramContext.ExecutionPolicy.UNRESTRICTED);

        // log if there are unprocessed redstone pulses
        int unprocessedRedstonePulseCount = manager.getUnprocessedRedstonePulseCount();
        if (unprocessedRedstonePulseCount > 0) {
            manager.logger.debug(x -> x.accept(Constants.LocalizationKeys.LOG_PROGRAM_TICK_WITH_REDSTONE_COUNT.get(
                    unprocessedRedstonePulseCount)));
        }


        tick(context);

        manager.clearRedstonePulseQueue();

        return context.didSomething();
    }

    @Override
    public List<Statement> getStatements() {
        return triggers.stream().map(x -> (Statement) x).toList();
    }

    @Override
    public void tick(ProgramContext context) {
        for (Trigger t : triggers) {
            // Only process triggers that should tick
            if (!t.shouldTick(context)) {
                continue;
            }

            // Set flag and log on first trigger
            if (!context.didSomething()) {
                context.setDidSomething(true);
                context.getLogger().trace(getTraceLogWriter(context));
                context.getLogger().debug(debug -> debug.accept(Constants.LocalizationKeys.LOG_PROGRAM_TICK.get()));
            }

            // Log pretty triggers
            if (triggers instanceof ShortStatement ss) {
                context.getLogger().debug(x -> x.accept(Constants.LocalizationKeys.LOG_PROGRAM_TICK_TRIGGER_STATEMENT.get(
                        ss.toStringShort())));
            }

            // Perform and measure tick
            long start = System.nanoTime();
            ProgramContext forkedContext = context.copy();
            t.tick(forkedContext);
            forkedContext.free();
            long nanoTimePassed = System.nanoTime() - start;

            // Log trigger time
            context.getLogger().info(x -> x.accept(Constants.LocalizationKeys.PROGRAM_TICK_TRIGGER_TIME_MS.get(
                    nanoTimePassed / 1_000_000.0,
                    t.toString()
            )));

        }
    }

    private static @NotNull Consumer<Consumer<TranslatableContents>> getTraceLogWriter(ProgramContext context) {
        return trace -> {
            trace.accept(Constants.LocalizationKeys.LOG_CABLE_NETWORK_DETAILS_HEADER_1.get());
            trace.accept(Constants.LocalizationKeys.LOG_CABLE_NETWORK_DETAILS_HEADER_2.get());
            Level level = context
                    .getManager()
                    .getLevel();
            //noinspection DataFlowIssue
            context
                    .getNetwork()
                    .getCablePositions()
                    .map(pos -> "- "
                                + pos.toString()
                                + " "
                                + level
                                        .getBlockState(
                                                pos))
                    .forEach(body -> trace.accept(Constants.LocalizationKeys.LOG_CABLE_NETWORK_DETAILS_BODY.get(
                            body)));
            trace.accept(Constants.LocalizationKeys.LOG_CABLE_NETWORK_DETAILS_HEADER_3.get());
            //noinspection DataFlowIssue
            context
                    .getNetwork()
                    .getCapabilityProviderPositions()
                    .map(pos -> "- " + pos.toString() + " " + level
                            .getBlockState(pos))
                    .forEach(body -> trace.accept(Constants.LocalizationKeys.LOG_CABLE_NETWORK_DETAILS_BODY.get(
                            body)));
            trace.accept(Constants.LocalizationKeys.LOG_CABLE_NETWORK_DETAILS_FOOTER.get());

            trace.accept(Constants.LocalizationKeys.LOG_LABEL_POSITION_HOLDER_DETAILS_HEADER.get());
            //noinspection DataFlowIssue
            context
                    .getlabelPositions()
                    .get()
                    .forEach((label, positions) -> positions
                            .stream()
                            .map(
                                    pos -> "- "
                                           + label
                                           + ": "
                                           + pos.toString()
                                           + " "
                                           + level
                                                   .getBlockState(
                                                           pos)

                            )
                            .forEach(body -> trace.accept(Constants.LocalizationKeys.LOG_LABEL_POSITION_HOLDER_DETAILS_BODY.get(
                                    body))));
            trace.accept(Constants.LocalizationKeys.LOG_LABEL_POSITION_HOLDER_DETAILS_FOOTER.get());
            trace.accept(Constants.LocalizationKeys.LOG_PROGRAM_CONTEXT.get(context));
        };
    }

    @Override
    public String toString() {
        var rtn = new StringBuilder();
        rtn.append("NAME \"").append(name).append("\"\n");
        for (Trigger trigger : triggers) {
            rtn.append(trigger).append("\n");
        }
        return rtn.toString();
    }

    public void replaceOutputStatement(OutputStatement oldStatement, OutputStatement newStatement) {
        Deque<Statement> toPatch = new ArrayDeque<>();
        toPatch.add(this);
        while (!toPatch.isEmpty()) {
            Statement statement = toPatch.pollFirst();
            List<Statement> children = statement.getStatements();
            for (int i = 0; i < children.size(); i++) {
                Statement child = children.get(i);
                if (child == oldStatement) {
                    children.set(i, newStatement);
                } else {
                    toPatch.add(child);
                }
            }
        }
    }

    public int getConditionIndex(IfStatement statement) {
        Deque<Statement> toVisit = new ArrayDeque<>();
        toVisit.add(this);
        int seen = 0;
        while (!toVisit.isEmpty()) {
            Statement current = toVisit.pollFirst();
            if (current instanceof IfStatement ifStatement) {
                if (ifStatement == statement) {
                    return seen;
                }
                seen++;
            }
            toVisit.addAll(current.getStatements());
        }
        return -1;
    }

    public int getConditionCount() {
        Deque<Statement> toVisit = new ArrayDeque<>();
        toVisit.add(this);
        int seen = 0;
        while (!toVisit.isEmpty()) {
            Statement current = toVisit.pollFirst();
            if (current instanceof IfStatement) {
                seen++;
            }
            toVisit.addAll(current.getStatements());
        }
        return seen;
    }

    public static class ListErrorListener extends BaseErrorListener {
        private final List<String> errors;

        public ListErrorListener(List<String> errors) {
            this.errors = errors;
        }

        @Override
        public void syntaxError(
                Recognizer<?, ?> recognizer,
                Object offendingSymbol,
                int line,
                int charPositionInLine,
                String msg,
                RecognitionException e
        ) {
            errors.add("line " + line + ":" + charPositionInLine + " " + msg);
        }
    }
}
