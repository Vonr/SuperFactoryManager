package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.InventoryTracker;
import ca.teamdman.sfm.common.program.LimitedInputSlot;
import ca.teamdman.sfm.common.program.ProgramContext;

import java.util.stream.Collectors;

public record OutputStatement(
        Label label,
        Matchers matchers,
        DirectionQualifier directions
) implements Statement {
    @Override
    public void tick(ProgramContext context) {
        var inventories = context.getItemHandlersByLabel(label().name(), directions).collect(Collectors.toList());
        var outputs     = new InventoryTracker(inventories, matchers, directions);
        var inputSlots  = context.getInputs().flatMap(InventoryTracker::streamInputSlots);
        var outputSlots = outputs.streamOutputSlots().collect(Collectors.toList());

        grabbing:
        for (var in : (Iterable<LimitedInputSlot>) inputSlots::iterator) {
            for (var out : outputSlots) {
                in.moveTo(out);
                if (in.isDone()) continue grabbing;
            }
        }
    }
}
