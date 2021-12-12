package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.program.QualifiedInventory;

import java.util.stream.Collectors;

public record InputStatement(
        Label label,
        DirectionQualifier directions
) implements Statement {

    @Override
    public void tick(ProgramContext context) {
        var inventories = context.getItemHandlersByLabel(label().name(), directions).collect(Collectors.toList());
        var input       = new QualifiedInventory(inventories, directions);
        context.addInput(input);
    }
}
