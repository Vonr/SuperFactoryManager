package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.InventoryTracker;
import ca.teamdman.sfm.common.program.ProgramContext;

import java.util.stream.Collectors;

public record InputStatement(
        Label label,
        Matchers matchers,
        DirectionQualifier directions
) implements Statement {

    @Override
    public void tick(ProgramContext context) {
        var inventories = context.getItemHandlersByLabel(label().name(), directions).collect(Collectors.toList());
        var input       = new InventoryTracker(inventories, matchers.createMatchers(), directions);
        context.addInput(input);
    }
}
