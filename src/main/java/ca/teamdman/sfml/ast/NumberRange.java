package ca.teamdman.sfml.ast;

public record NumberRange(
        long start,
        long end
) implements ASTNode {
    public boolean contains(int value) {
        return value >= start && value <= end;
    }
}
