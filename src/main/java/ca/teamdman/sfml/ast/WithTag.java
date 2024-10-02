package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.resourcetype.ResourceType;

public record WithTag(TagMatcher tagMatcher) implements ASTNode, WithClause {
    @Override
    public <STACK> boolean test(
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
