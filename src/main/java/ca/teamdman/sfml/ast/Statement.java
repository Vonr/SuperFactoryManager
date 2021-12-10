package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;

public abstract class Statement implements ASTNode {
    public abstract void tick(ProgramContext context);
}
