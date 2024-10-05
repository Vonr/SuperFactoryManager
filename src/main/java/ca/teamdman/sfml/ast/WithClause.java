package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.resourcetype.ResourceType;

public interface WithClause extends ASTNode {
    <STACK> boolean matchesStack(
            ResourceType<STACK, ?, ?> resourceType,
            STACK stack
    );

    final class WithClauseThatAlwaysReturnsTrue implements WithClause {
        @Override
        public <STACK> boolean matchesStack(
                ResourceType<STACK, ?, ?> resourceType,
                STACK stack
        ) {
            return true;
        }
    }
}
