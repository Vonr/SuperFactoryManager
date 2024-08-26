package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.compat.SFMCompat;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.util.SFMUtils;
import ca.teamdman.sfml.ast.*;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static ca.teamdman.sfm.common.localization.LocalizationKeys.*;
import static ca.teamdman.sfml.ast.RoundRobin.Behaviour.BY_BLOCK;
import static ca.teamdman.sfml.ast.RoundRobin.Behaviour.BY_LABEL;

public class ProgramLinter {
    public static ArrayList<TranslatableContents> gatherWarnings(
            Program program, LabelPositionHolder labelPositionHolder , @Nullable ManagerBlockEntity manager
    ) {
        var warnings = new ArrayList<TranslatableContents>();
        var level = manager != null ? manager.getLevel() : null;

        // label smells
        addWarningsForLabelsInProgramButNotInHolder(program, labelPositionHolder, warnings);
        addWarningsForLabelsInHolderButNotInProgram(program, labelPositionHolder, warnings);
        if (level != null) {
            addWarningsForLabelsUsedInWorldButNotConnectedByCables(manager, labelPositionHolder, warnings, level);
        }
        addWarningsForUsingIOWithoutCorrespondingOppositeIO(program, labelPositionHolder, warnings);

        // resource smells
        addWarningsForResourcesReferencedButNotFoundInRegistry(program, warnings);

        // simple io statement smells
        program
                .getDescendantStatements()
                .filter(IOStatement.class::isInstance)
                .map(IOStatement.class::cast)
                .forEach(statement -> {
                    addWarningsForSmellyRoundRobinUsage(warnings, statement);
                    addWarningsForUsingEachWithoutAPattern(warnings, statement);
                });

        return warnings;
    }

    public static void fixWarningsByRemovingBadLabelsFromDisk(
            ManagerBlockEntity manager,
            ItemStack disk,
            Program program
    ) {
        var labels = LabelPositionHolder.from(disk);
        // remove labels not defined in code
        labels.removeIf(label -> !program.referencedLabels().contains(label));

        // remove labels not connected via cables
        CableNetworkManager
                .getOrRegisterNetworkFromManagerPosition(manager)
                .ifPresent(network -> labels.removeIf((label, pos) -> !network.isAdjacentToCable(pos)));
        labels.save(disk);

        // update warnings
        DiskItem.setWarnings(disk, gatherWarnings(program, labels, manager));
    }

    private static void addWarningsForUsingIOWithoutCorrespondingOppositeIO(
            Program program,
            LabelPositionHolder labelPositionHolder,
            ArrayList<TranslatableContents> warnings
    ) {
        program.tick(ProgramContext.createSimulationContext(
                program,
                labelPositionHolder,
                0,
                new GatherWarningsProgramBehaviour(warnings::addAll)
        ));
    }


    private static void addWarningsForUsingEachWithoutAPattern(
            ArrayList<TranslatableContents> warnings,
            IOStatement statement
    ) {
        boolean smells = statement
                .resourceLimits()
                .resourceLimits()
                .stream()
                .anyMatch(rl -> rl.limit().quantity().idExpansionBehaviour()
                                == ResourceQuantity.IdExpansionBehaviour.EXPAND && !rl
                        .resourceId()
                        .usesRegex());
        if (smells) {
            warnings.add(PROGRAM_WARNING_RESOURCE_EACH_WITHOUT_PATTERN.get(statement.toStringPretty()));
        }
    }

    private static void addWarningsForSmellyRoundRobinUsage(
            ArrayList<TranslatableContents> warnings,
            IOStatement statement
    ) {
        RoundRobin roundRobin = statement.labelAccess().roundRobin();
        if (roundRobin.getBehaviour() == BY_BLOCK && statement.each()) {
            warnings.add(PROGRAM_WARNING_ROUND_ROBIN_SMELLY_EACH.get(statement.toStringPretty()));
        } else if (roundRobin.getBehaviour() == BY_LABEL
                   && statement.labelAccess().labels().size() == 1) {
            warnings.add(PROGRAM_WARNING_ROUND_ROBIN_SMELLY_COUNT.get(statement.toStringPretty()));
        }
    }

    private static void addWarningsForResourcesReferencedButNotFoundInRegistry(
            Program program, ArrayList<TranslatableContents> warnings
    ) {
        for (var resource : program.referencedResources()) {
            // skip regex resources
            Optional<ResourceLocation> loc = resource.getLocation();
            if (loc.isEmpty()) continue;

            // make sure resource type is registered
            var type = resource.getResourceType();
            if (type == null) {
                SFM.LOGGER.error(
                        "Resource type not found for resource: {}, should have been validated at program compile",
                        resource
                );
                continue;
            }

            // make sure resource exists in the registry
            if (!type.registryKeyExists(loc.get())) {
                warnings.add(PROGRAM_WARNING_UNKNOWN_RESOURCE_ID.get(resource));
            }
        }
    }

    private static void addWarningsForLabelsUsedInWorldButNotConnectedByCables(
            @NotNull ManagerBlockEntity manager,
            LabelPositionHolder labels,
            ArrayList<TranslatableContents> warnings,
            Level level
    ) {
        CableNetworkManager
                .getOrRegisterNetworkFromManagerPosition(manager)
                .ifPresent(network -> labels.forEach((label, pos) -> {
                    var adjacent = network.isAdjacentToCable(pos);
                    if (!adjacent) {
                        warnings.add(PROGRAM_WARNING_DISCONNECTED_LABEL.get(
                                label,
                                String.format(
                                        "[%d,%d,%d]",
                                        pos.getX(),
                                        pos.getY(),
                                        pos.getZ()
                                )
                        ));
                    }
                    var viable = SFMCompat.getCapabilities().stream()
                            .flatMap(capKind -> DirectionQualifier.EVERY_DIRECTION
                                    .stream()
                                    .map(dir -> level.getCapability(capKind, pos, dir)))
                            .anyMatch(Objects::nonNull);
                    if (!viable && adjacent) {
                        warnings.add(PROGRAM_WARNING_CONNECTED_BUT_NOT_VIABLE_LABEL.get(
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

    private static void addWarningsForLabelsInHolderButNotInProgram(
            Program program, LabelPositionHolder labels, ArrayList<TranslatableContents> warnings
    ) {
        labels
                .get()
                .keySet()
                .stream()
                .filter(x -> !program.referencedLabels().contains(x))
                .forEach(label -> warnings.add(PROGRAM_WARNING_UNDEFINED_LABEL.get(label)));
    }

    private static void addWarningsForLabelsInProgramButNotInHolder(
            Program program, LabelPositionHolder labels, ArrayList<TranslatableContents> warnings
    ) {
        for (String label : program.referencedLabels()) {
            var isUsed = !labels.getPositions(label).isEmpty();
            if (!isUsed) {
                warnings.add(PROGRAM_WARNING_UNUSED_LABEL.get(label));
            }
        }
    }
}
