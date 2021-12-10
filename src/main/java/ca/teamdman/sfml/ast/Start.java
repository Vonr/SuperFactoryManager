package ca.teamdman.sfml.ast;

public class Start implements ASTNode {
    private final String  NAME;
    private final World   WORLD;
    private final Program PROGRAM;

    public Start(String name, World WORLD, Program PROGRAM) {
        this.NAME    = name;
        this.WORLD   = WORLD;
        this.PROGRAM = PROGRAM;
    }

    public String getName() {
        return NAME;
    }

    public World getWorld() {
        return WORLD;
    }

    public Program getProgram() {
        return PROGRAM;
    }
}
