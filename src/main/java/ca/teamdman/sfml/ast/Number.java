package ca.teamdman.sfml.ast;

public class Number implements ASTNode {
    private final int VALUE;

    public Number(int value) {
        this.VALUE = value;
    }

    public int getValue() {
        return VALUE;
    }
}
