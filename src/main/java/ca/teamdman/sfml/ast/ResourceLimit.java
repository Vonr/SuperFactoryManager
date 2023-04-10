package ca.teamdman.sfml.ast;

import java.util.function.Predicate;

public record ResourceLimit<STACK, ITEM, CAP>(
        Limit limit,
        ResourceIdentifier<STACK, ITEM, CAP> resourceId
) implements ASTNode, Predicate<Object> {
    public ResourceLimit(ResourceIdentifier<STACK, ITEM, CAP> resourceId) {
        this(new Limit(), resourceId);
    }

    public ResourceLimit<STACK, ITEM, CAP> withDefaults(long quantity, long retention) {
        return new ResourceLimit<>(limit.withDefaults(quantity, retention), resourceId);
    }

    public boolean test(Object stack) {
        return resourceId.test(stack);
    }
}
