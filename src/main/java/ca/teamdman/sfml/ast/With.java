package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.resourcetype.ResourceType;

public record With<ENTRY>(
        WithClause<ENTRY> condition,
        WithMode mode,
        String sourceCode
) implements WithClause<ENTRY> {
    public static final With<?> ALWAYS_TRUE = new With<>(
            (resourceType, entry) -> true,
            WithMode.WITHOUT,
            ""
    );

    @SuppressWarnings("unchecked")
    public static <A> With<A> alwaysTrue() {
        return (With<A>) ALWAYS_TRUE;
    }

    @Override
    public boolean test(
            ResourceType<ENTRY, ?, ?> entryResourceType,
            ENTRY entry
    ) {
        return condition.test(entryResourceType, entry);
    }

    public enum WithMode {
        WITH,
        WITHOUT
    }
}
