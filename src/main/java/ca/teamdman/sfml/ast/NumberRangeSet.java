package ca.teamdman.sfml.ast;

public record NumberRangeSet(NumberRange[] ranges) implements ASTNode {
    public static final NumberRangeSet MAX_RANGE = new NumberRangeSet(new NumberRange[]{NumberRange.MAX_RANGE});
    public boolean contains(int value) {
        for (NumberRange range : ranges) {
            if (range.contains(value)) {
                return true;
            }
        }
        return false;
    }
}
