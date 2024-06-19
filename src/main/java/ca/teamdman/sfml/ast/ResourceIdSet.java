package ca.teamdman.sfml.ast;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record ResourceIdSet(
        Set<ResourceIdentifier<?, ?, ?>> resourceIds
) implements ASTNode, Predicate<Object> {
    public static final ResourceIdSet EMPTY = new ResourceIdSet(Set.of());

    public boolean test(Object stack) {
        for (ResourceIdentifier<?, ?, ?> exclude : resourceIds) {
            if (exclude.test(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "ResourceIdSet{" +
               resourceIds.stream().map(ResourceIdentifier::toStringCondensed).collect(Collectors.joining(", ")) +
               '}';
    }
}
