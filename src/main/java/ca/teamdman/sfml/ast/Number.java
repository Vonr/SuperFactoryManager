package ca.teamdman.sfml.ast;

public record Number(long value) implements ASTNode {
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
