package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.LimitedInputSlot;
import ca.teamdman.sfm.common.program.LimitedOutputSlot;
import ca.teamdman.sfm.common.program.OutputItemMatcher;
import ca.teamdman.sfm.common.program.ProgramContext;
import net.minecraftforge.items.IItemHandler;

import java.util.List;
import java.util.stream.Stream;

public record OutputStatement(
        List<Label> labels,
        Matchers matchers,
        DirectionQualifier directions,
        boolean each,
        NumberRangeSet slots
) implements Statement {
    @Override
    public void tick(ProgramContext context) {
        var inputSlots  = context.getInputs().flatMap(x -> x.getSlots(context));
        var outputSlots = getSlots(context);
        grabbing:
        for (var in : (Iterable<LimitedInputSlot>) inputSlots::iterator) {
            for (var out : (Iterable<LimitedOutputSlot>) outputSlots::iterator) {
                in.moveTo(out);
                if (in.isDone()) continue grabbing;
            }
        }
    }

    public Stream<LimitedOutputSlot> getSlots(ProgramContext context) {
        var                     handlers     = context.getItemHandlersByLabels(labels, directions);
        var                     rtn          = Stream.<LimitedOutputSlot>builder();
        List<OutputItemMatcher> itemMatchers = null;
        for (var inv : (Iterable<IItemHandler>) handlers::iterator) {
            if (itemMatchers == null || each) itemMatchers = matchers.createOutputMatchers();
            for (int slot = 0; slot < inv.getSlots(); slot++) {
                if (slots.contains(slot)) {
                    for (var matcher : itemMatchers) {
                        rtn.add(new LimitedOutputSlot(this, inv, slot, matcher));
                    }
                }
            }
        }
        return rtn.build();
    }
}
