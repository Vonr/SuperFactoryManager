package ca.teamdman.sfml.ast;

public record With<STACK, ITEM, CAP>(
        WithClause<?, ?, ?> condition,
        WithMode mode,
        String sourceCode
) implements WithClause<STACK, ITEM, CAP> {
    public static final With<?, ?, ?> ALWAYS_TRUE = new With<>(new WithClause<>() {
    }, WithMode.WITHOUT, "");

    @SuppressWarnings("unchecked")
    public static <A, B, C> With<A, B, C> alwaysTrue() {
        return (With<A, B, C>) ALWAYS_TRUE;
    }

    public enum WithMode {
        WITH,
        WITHOUT
    }
}
