package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.LimitedSlot.LimitedExtractionSlot;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.program.QualifiedInventory;

import java.util.stream.Collectors;

public record OutputStatement(
        Label label,
        DirectionQualifier directions
) implements Statement {
    @Override
    public void tick(ProgramContext context) {
        var inventories = context.getItemHandlersByLabel(label().name(), directions).collect(Collectors.toList());
        var outputs     = new QualifiedInventory(inventories, directions);
        var inputSlots  = context.getInputs().flatMap(QualifiedInventory::asInputSlots);
        var outputSlots = outputs.asOutputSlots().collect(Collectors.toList());

        grabbing:
        for (var in : (Iterable<LimitedExtractionSlot>) inputSlots::iterator) {
            for (var out : outputSlots) {
                in.moveTo(out);
                if (in.isDone()) continue grabbing;
            }
        }
    }
}
