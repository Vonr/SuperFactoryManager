package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import net.minecraft.tags.TagKey;

public record WithTag<STACK>(TagMatcher tagMatcher) implements ASTNode, WithClause<STACK> {
    @Override
    public boolean test(
            ResourceType<STACK, ?, ?> resourceType,
            STACK stack
    ) {
        return resourceType.getTagsForStack(stack).anyMatch(tagMatcher::testResourceLocation);
    }

    @Override
    public String toString() {
        return "TAG " + tagMatcher;
    }
}
