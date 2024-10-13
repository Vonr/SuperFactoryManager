package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.resourcetype.ResourceType;

public record With(
        WithClause condition,
        WithMode mode,
        String sourceCode
) implements WithClause {
    public static final With ALWAYS_TRUE = new With(
            new WithClauseThatAlwaysReturnsTrue(),
            WithMode.WITHOUT,
            "(ANYTHING => TRUE)"
    );

    @Override
    public <STACK> boolean matchesStack(
            ResourceType<STACK, ?, ?> resourceType,
            STACK stack
    ) {
        return condition.matchesStack(resourceType, stack);
    }

    public enum WithMode {
        WITH,
        WITHOUT
    }
}
