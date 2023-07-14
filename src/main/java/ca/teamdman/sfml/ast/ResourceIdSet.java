package ca.teamdman.sfml.ast;

import java.util.Set;
import java.util.function.Predicate;

public record ResourceIdSet(
        Set<ResourceIdentifier<?, ?, ?>> resourceIds
) implements ASTNode, Predicate<Object> {
    public static final ResourceIdSet EMPTY = new ResourceIdSet(Set.of());

    public boolean test(Object stack) {
        return resourceIds.stream().anyMatch(exclude -> exclude.test(stack));
    }
}
