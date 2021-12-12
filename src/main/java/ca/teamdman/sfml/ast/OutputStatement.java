package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ItemOutput;
import ca.teamdman.sfm.common.program.ProgramContext;

public record OutputStatement(
        Label label,
        DirectionQualifier directions
) implements Statement {
    @Override
    public void tick(ProgramContext context) {
        var inventories = context.getItemHandlersByLabel(label().name());
        var output      = new ItemOutput(inventories);
        output.tick(context);
    }
}
