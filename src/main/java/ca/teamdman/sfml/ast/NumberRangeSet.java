package ca.teamdman.sfml.ast;

public record NumberRangeSet(NumberRange[] ranges) implements ASTNode {
    public boolean contains(int value) {
        for (NumberRange range : ranges) {
            if (range.contains(value)) {
                return true;
            }
        }
        return false;
    }
}
