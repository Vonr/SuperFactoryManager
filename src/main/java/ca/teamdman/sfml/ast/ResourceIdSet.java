package ca.teamdman.sfml.ast;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record ResourceIdSet(
        LinkedHashSet<ResourceIdentifier<?, ?, ?>> resourceIds
) implements ASTNode, Predicate<Object> {
    public ResourceIdSet(Collection<ResourceIdentifier<?,?,?>> contents) {
        this(new LinkedHashSet<>(contents));
    }
    public static final ResourceIdSet EMPTY = new ResourceIdSet(new LinkedHashSet<>());

    @Override
    public boolean test(Object stack) {
        for (ResourceIdentifier<?, ?, ?> entry : resourceIds) {
            if (entry.test(stack)) {
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
