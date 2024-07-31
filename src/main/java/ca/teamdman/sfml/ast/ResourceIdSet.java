package ca.teamdman.sfml.ast;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record ResourceIdSet(
        LinkedHashSet<ResourceIdentifier<?, ?, ?>> resourceIds
) implements ASTNode, Predicate<Object> {
    public ResourceIdSet(Collection<ResourceIdentifier<?,?,?>> contents) {
        this(new LinkedHashSet<>(contents));
    }
    public static final ResourceIdSet EMPTY = new ResourceIdSet(new ObjectArraySet<>());
    public static int tests = 0;
    public static int entriesTested = 0;

    @Override
    public boolean test(Object stack) {
        tests++;
        for (ResourceIdentifier<?, ?, ?> entry : resourceIds) {
            entriesTested++;
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
