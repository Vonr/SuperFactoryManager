package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.registry.SFMResourceTypes;
import ca.teamdman.sfm.common.util.SFMLabelNBTHelper;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record Program(
        String name,
        List<Trigger> triggers,
        Set<String> referencedLabels,
        Set<ResourceIdentifier<?, ?>> referencedResources
) implements ASTNode {
    public static final int MAX_PROGRAM_LENGTH = 8096;

    public void addWarnings(ItemStack disk, ManagerBlockEntity manager) {
        var warnings = new ArrayList<TranslatableContents>();

        // labels in code but not in world
        for (String label : referencedLabels) {
            var isUsed = SFMLabelNBTHelper
                    .getLabelPositions(disk, label)
                    .findAny()
                    .isPresent();
            if (!isUsed) {
                warnings.add(new TranslatableContents("program.sfm.warnings.unused_label", label));
            }
        }

        // labels used in world but not defined in code
        SFMLabelNBTHelper.getPositionLabels(disk)
                .values().stream().distinct()
                .filter(x -> !referencedLabels.contains(x))
                .forEach(label -> warnings.add(new TranslatableContents(
                        "program.sfm.warnings.undefined_label",
                        label
                )));

        // labels in world but not connected via cables
        CableNetworkManager.getOrRegisterNetwork(manager).ifPresent(network -> {
            SFMLabelNBTHelper.getPositionLabels(disk)
                    .entries().stream()
                    .filter(e -> !network.containsInventoryLocation(e.getKey()))
                    .forEach(e -> warnings.add(new TranslatableContents(
                            "program.sfm.warnings.disconnected_label",
                            e.getValue(),
                            String.format("[%d,%d,%d]", e.getKey().getX(), e.getKey().getY(), e.getKey().getZ())
                    )));
        });

        for (var resource : referencedResources) {
            // skip wildcard since we can't validate
            if (resource.type().equals("*")) continue;
            if (resource.domain().equals("*")) continue;
            if (resource.value().equals("*")) continue;

            var type = SFMResourceTypes.DEFERRED_TYPES
                    .get()
                    .getValue(new ResourceLocation(SFM.MOD_ID, resource.type()));
            if (type == null) {
                warnings.add(new TranslatableContents(
                        "program.sfm.warnings.unknown_resource_type",
                        resource.type(),
                        resource.toString()
                ));
            } else {
                if (!type.containsKey(resource.getLocation())) {
                    warnings.add(new TranslatableContents("program.sfm.warnings.unknown_resource_id", resource));
                }
            }
        }
        DiskItem.setWarnings(disk, warnings);
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
                    .filter(e -> !network.containsInventoryLocation(e.getKey()))
                    .forEach(e -> SFMLabelNBTHelper.removeLabel(disk, e.getValue(), e.getKey()));
        });

        // update warnings
        addWarnings(disk, manager);
    }

    public void tick(ProgramContext context) {
        for (Trigger t : triggers) {
            if (t.shouldTick(context)) {
//                var start = System.nanoTime();
                t.tick(context.fork());
//                var end  = System.nanoTime();
//                var diff = end - start;
//                SFM.LOGGER.debug("Took {}ms ({}us)", diff / 1000000, diff);
            }
        }
    }

    public Set<String> getReferencedLabels() {
        return referencedLabels;
    }
}
