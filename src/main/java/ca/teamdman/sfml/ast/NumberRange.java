package ca.teamdman.sfml.ast;

public record NumberRange(
        int start,
        int end
) implements ASTNode {
    public boolean contains(int value) {
        return value >= start && value <= end;
    }
}
