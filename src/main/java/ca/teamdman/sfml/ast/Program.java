package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.program.ProgramContext;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Program implements ASTNode {
    private final String        NAME;
    private final List<Trigger> TRIGGERS;
    private final Set<String>   REFERENCED_LABELS;

    public Program(String name, List<Trigger> triggers, Set<String> referencedLabels) {
        this.NAME              = name;
        this.TRIGGERS          = triggers;
        this.REFERENCED_LABELS = referencedLabels;
    }

    public void addWarnings(ItemStack disk) {
        var warnings = new ArrayList<String>();
        for (String label : REFERENCED_LABELS) {
            var isUsed = DiskItem
                    .getPositions(disk, label)
                    .findAny()
                    .isPresent();
            if (!isUsed) {
                warnings.add("There are no blocks labelled with \"" + label + "\".");
            }
        }
        DiskItem.setWarnings(disk, warnings);
    }

    public String getName() {
        return NAME;
    }

    public void tick(ProgramContext context) {
        for (Trigger t : TRIGGERS) {
            if (t.shouldTick(context)) {
                var start = System.nanoTime();
                t.tick(context);
                var end  = System.nanoTime();
                var diff = end - start;
                SFM.LOGGER.info("Took {}ms ({}us)", diff / 1000000, diff);
            }
        }
    }
}
