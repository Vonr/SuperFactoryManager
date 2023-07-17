package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;

import java.util.List;

public interface Trigger extends Statement {
    boolean shouldTick(ProgramContext context);

    Block getBlock();

    @Override
    default List<Statement> getStatements() {
        return List.of(getBlock());
    }
}
