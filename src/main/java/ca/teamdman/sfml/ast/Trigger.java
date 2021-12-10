package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;

public abstract class Trigger implements ASTNode {
    private final Block BLOCK;

    public Trigger(Block block) {
        this.BLOCK = block;
    }

    public abstract boolean shouldTick(ProgramContext manager);

    public void tick(ProgramContext context) {
        BLOCK.tick(context);
    }
}
