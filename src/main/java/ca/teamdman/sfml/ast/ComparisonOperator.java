package ca.teamdman.sfml.ast;

import java.util.Locale;
import java.util.function.BiPredicate;

public enum ComparisonOperator implements ASTNode, BiPredicate<Integer, Integer> {
    GREATER((a, b) -> a > b),
    LESSER((a, b) -> a < b),
    EQUALS(Integer::equals),
    LESSER_OR_EQUAL((a, b) -> a <= b),
    GREATER_OR_EQUAL((a, b) -> a >= b);

    private final BiPredicate<Integer, Integer> PRED;

    ComparisonOperator(BiPredicate<Integer, Integer> pred) {
        this.PRED = pred;
    }

    public static ComparisonOperator from(String text) {
        return switch (text.toUpperCase(Locale.ROOT)) {
            case "GT" -> GREATER;
            case "LT" -> LESSER;
            case "EQ" -> EQUALS;
            case "LE" -> LESSER_OR_EQUAL;
            case "GE" -> GREATER_OR_EQUAL;
        };
    }

    @Override
    public boolean test(Integer a, Integer b) {
        return PRED.test(a, b);
    }
}
