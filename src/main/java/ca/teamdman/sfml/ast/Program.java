package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.util.SFMLabelNBTHelper;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record Program(
        String name,
        List<Trigger> triggers,
        Set<String> referencedLabels
) implements ASTNode {
    public static final int MAX_PROGRAM_LENGTH = 8096;

    public void addWarnings(ItemStack disk, ManagerBlockEntity manager) {
        var warnings = new ArrayList<TranslatableContents>();
        for (String label : referencedLabels) {
            var isUsed = SFMLabelNBTHelper
                    .getLabelPositions(disk, label)
                    .findAny()
                    .isPresent();
            if (!isUsed) {
                warnings.add(new TranslatableContents("program.sfm.warnings.unused_label", label));
            }
        }

        SFMLabelNBTHelper.getPositionLabels(disk)
                .values().stream().distinct()
                .filter(x -> !referencedLabels.contains(x))
                .forEach(label -> warnings.add(new TranslatableContents(
                        "program.sfm.warnings.undefined_label",
                        label
                )));

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

        DiskItem.setWarnings(disk, warnings);
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
