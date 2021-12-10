package ca.teamdman.sfml.ast;

public class Start implements ASTNode {
    private final World   WORLD;
    private final Program PROGRAM;

    public Start(World WORLD, Program PROGRAM) {
        this.WORLD   = WORLD;
        this.PROGRAM = PROGRAM;
    }
}
