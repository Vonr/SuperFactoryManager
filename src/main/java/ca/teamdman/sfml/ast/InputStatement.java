package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.InputItemMatcher;
import ca.teamdman.sfm.common.program.LimitedInputSlot;
import ca.teamdman.sfm.common.program.ProgramContext;
import net.minecraftforge.items.IItemHandler;

import java.util.List;
import java.util.stream.Stream;

public record InputStatement(
        LabelAccess labelAccess,
        Matchers matchers,
        boolean each
) implements Statement {

    @Override
    public void tick(ProgramContext context) {
        context.addInput(this);
    }

    public Stream<LimitedInputSlot> getSlots(ProgramContext context) {
        var                    handlers     = context.getItemHandlersByLabels(labelAccess);
        var                    rtn          = Stream.<LimitedInputSlot>builder();
        List<InputItemMatcher> itemMatchers = null;
        for (var inv : (Iterable<IItemHandler>) handlers::iterator) {
            if (itemMatchers == null || each) itemMatchers = matchers.createInputMatchers();
            for (int slot = 0; slot < inv.getSlots(); slot++) {
                if (labelAccess.slots().contains(slot)) {
                    for (var matcher : itemMatchers) {
                        rtn.add(new LimitedInputSlot(this, inv, slot, matcher));
                    }
                }
            }
        }
        return rtn.build();
    }
}
