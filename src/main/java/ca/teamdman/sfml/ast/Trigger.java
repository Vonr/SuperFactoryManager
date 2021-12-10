package ca.teamdman.sfml.ast;

public abstract class Trigger implements ASTNode {
    private final Block BLOCK;

    public Trigger(Block block) {
        this.BLOCK = block;
    }

    public abstract boolean shouldTick();
}
