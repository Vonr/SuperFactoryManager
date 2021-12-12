package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ItemInput;
import ca.teamdman.sfm.common.program.ProgramContext;

public record InputStatement(
        Label label,
        DirectionQualifier directions
) implements Statement {

    @Override
    public void tick(ProgramContext context) {
        var inventories = context.getItemHandlersByLabel(label().name());
        var input       = new ItemInput(inventories);
        context.addInput(input);
    }
}
