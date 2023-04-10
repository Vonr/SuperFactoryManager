package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfm.common.util.SFMLabelNBTHelper;
import ca.teamdman.sfml.SFMLLexer;
import ca.teamdman.sfml.SFMLParser;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public record Program(
        String name,
        List<Trigger> triggers,
        Set<String> referencedLabels,
        Set<ResourceIdentifier<?, ?, ?>> referencedResources
) implements ASTNode {
    public static final int MAX_PROGRAM_LENGTH = 8096;

    public static void compile(
            String programString,
            Consumer<Program> onSuccess,
            Consumer<List<TranslatableContents>> onFailure
    ) {
        var lexer = new SFMLLexer(CharStreams.fromString(programString));
        lexer.removeErrorListeners();
        var tokens = new CommonTokenStream(lexer);
        var parser = new SFMLParser(tokens);

        parser.removeErrorListeners();
        List<TranslatableContents> errors = new ArrayList<>();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(
                    Recognizer<?, ?> recognizer,
                    Object offendingSymbol,
                    int line,
                    int charPositionInLine,
                    String msg,
                    RecognitionException e
            ) {
                errors.add(Constants.LocalizationKeys.PROGRAM_ERROR_LITERAL.get("line "
                                                                                + line
                                                                                + ":"
                                                                                + charPositionInLine
                                                                                + " "
                                                                                + msg));
            }
        });

        var     context = parser.program();
        Program program = null;

        try {
            program = new ASTBuilder().visitProgram(context);
            // make sure all referenced resources exist now during compilation instead of waiting for the program to tick
        } catch (ResourceLocationException | IllegalArgumentException | AssertionError e) {
            errors.add(Constants.LocalizationKeys.PROGRAM_ERROR_LITERAL.get(e.getMessage()));
        } catch (Throwable t) {
            errors.add(Constants.LocalizationKeys.PROGRAM_ERROR_COMPILE_FAILED.get());
            t.printStackTrace();
            if (!FMLEnvironment.production) errors.add(new TranslatableContents(t.getMessage()));
        }

        for (ResourceIdentifier<?, ?, ?> referencedResource : program.referencedResources) {
            try {
                ResourceType<?, ?, ?> resourceType = referencedResource.getResourceType();
                if (resourceType == null) {
                    errors.add(Constants.LocalizationKeys.PROGRAM_WARNING_UNKNOWN_RESOURCE_TYPE.get(referencedResource));
                }
            } catch (ResourceLocationException e) {
                errors.add(Constants.LocalizationKeys.PROGRAM_ERROR_MALFORMED_RESOURCE_TYPE.get(referencedResource));
            }
        }

        if (errors.isEmpty()) {
            onSuccess.accept(program);
        } else {
            onFailure.accept(errors);
        }
    }

    public ArrayList<TranslatableContents> gatherWarnings(ItemStack disk, ManagerBlockEntity manager) {
        var warnings = new ArrayList<TranslatableContents>();

        // labels in code but not in world
        for (String label : referencedLabels) {
            var isUsed = SFMLabelNBTHelper
                    .getLabelPositions(disk, label)
                    .findAny()
                    .isPresent();
            if (!isUsed) {
                warnings.add(Constants.LocalizationKeys.PROGRAM_WARNING_UNUSED_LABEL.get(label));
            }
        }

        // labels used in world but not defined in code
        SFMLabelNBTHelper.getPositionLabels(disk)
                .values().stream().distinct()
                .filter(x -> !referencedLabels.contains(x))
                .forEach(label -> warnings.add(Constants.LocalizationKeys.PROGRAM_WARNING_UNDEFINED_LABEL.get(label)));

        // labels in world but not connected via cables
        CableNetworkManager.getOrRegisterNetwork(manager).ifPresent(network -> {
            for (var entry : SFMLabelNBTHelper.getPositionLabels(disk).entries()) {
                var label     = entry.getValue();
                var pos       = entry.getKey();
                var inNetwork = network.isInNetwork(pos);
                var adjacent  = network.hasCableNeighbour(pos);
                if (!inNetwork && !adjacent) {
                    warnings.add(Constants.LocalizationKeys.PROGRAM_WARNING_DISCONNECTED_LABEL.get(
                            label,
                            String.format(
                                    "[%d,%d,%d]",
                                    pos.getX(),
                                    pos.getY(),
                                    pos.getZ()
                            )
                    ));
                } else if (!inNetwork && adjacent) {
                    warnings.add(Constants.LocalizationKeys.PROGRAM_WARNING_ADJACENT_BUT_DISCONNECTED_LABEL.get(
                            label,
                            String.format(
                                    "[%d,%d,%d]",
                                    pos.getX(),
                                    pos.getY(),
                                    pos.getZ()
                            )
                    ));
                }
            }
        });

        // try and validate that references resources exist
        for (var resource : referencedResources) {
            // skip regex resources
            Optional<ResourceLocation> loc = resource.getLocation();
            if (loc.isEmpty()) continue;

            // make sure resource type is registered
            var type = resource.getResourceType();
            if (type == null) {
                warnings.add(Constants.LocalizationKeys.PROGRAM_WARNING_UNKNOWN_RESOURCE_TYPE.get(
                        resource.resourceTypeNamespace()
                        + ":"
                        + resource.resourceTypeName(),
                        resource
                ));
                continue;
            }

            // make sure resource exists in the registry
            if (!type.registryKeyExists(loc.get())) {
                warnings.add(Constants.LocalizationKeys.PROGRAM_WARNING_UNKNOWN_RESOURCE_ID.get(resource));
            }
        }
        return warnings;
    }

    public void fixWarnings(ItemStack disk, ManagerBlockEntity manager) {
        // remove labels not defined in code
        SFMLabelNBTHelper.getPositionLabels(disk)
                .values().stream().distinct()
                .filter(label -> !referencedLabels.contains(label))
                .forEach(label -> SFMLabelNBTHelper.removeLabel(disk, label));

        // remove labels not connected via cables
        CableNetworkManager.getOrRegisterNetwork(manager).ifPresent(network -> {
            SFMLabelNBTHelper.getPositionLabels(disk)
                    .entries().stream()
                    .filter(e -> !network.isInNetwork(e.getKey()))
                    .forEach(e -> SFMLabelNBTHelper.removeLabel(disk, e.getValue(), e.getKey()));
        });

        // update warnings
        gatherWarnings(disk, manager);
    }

    public Set<String> getReferencedLabels() {
        return referencedLabels;
    }

    public void tick(ManagerBlockEntity manager) {
        var context = new ProgramContext(manager);

        // update warnings on disk item every 20 seconds
        if (manager.getTick() % 20 == 0) {
            manager
                    .getDisk()
                    .ifPresent(disk -> gatherWarnings(disk, manager));
        }
        for (Trigger t : triggers) {
            if (t.shouldTick(context)) {
//                var start = System.nanoTime();
                t.tick(context.fork());
//                var end  = System.nanoTime();
//                var diff = end - start;
//                SFM.LOGGER.debug("Took {}ms ({}us)", diff / 1000000, diff);
            }
        }
        manager.clearRedstonePulseQueue();

    }
}
