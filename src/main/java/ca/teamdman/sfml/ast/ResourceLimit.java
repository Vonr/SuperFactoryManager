package ca.teamdman.sfml.ast;

import java.util.function.Predicate;

public record ResourceLimit<STACK, CAP>(
        Limit limit,
        ResourceIdentifier<STACK, CAP> resourceId
) implements ASTNode, Predicate<STACK> {
    public ResourceLimit(ResourceIdentifier<STACK, CAP> resourceId) {
        this(new Limit(), resourceId);
    }

    public ResourceLimit<STACK, CAP> withDefaults(long quantity, long retention) {
        return new ResourceLimit<>(limit.withDefaults(quantity, retention), resourceId);
    }

    public boolean test(STACK stack) {
        return resourceId.test(stack);
    }
}
