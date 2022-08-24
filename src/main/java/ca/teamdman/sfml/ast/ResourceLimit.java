package ca.teamdman.sfml.ast;

import java.util.function.Predicate;

public record ResourceLimit<STACK>(
        Limit limit,
        ResourceIdentifier resourceId
) implements ASTNode, Predicate<STACK> {
    public ResourceLimit(Limit limit) {
        this(limit, ResourceIdentifier.MATCH_ALL);
    }

    public ResourceLimit(ResourceIdentifier resourceId) {
        this(new Limit(), resourceId);
    }

    public ResourceLimit<STACK> withDefaults(int quantity, int retention) {
        return new ResourceLimit<>(limit.withDefaults(quantity, retention), resourceId);
    }

    public boolean test(STACK stack) {
        return resourceId.test(stack);
    }
}
