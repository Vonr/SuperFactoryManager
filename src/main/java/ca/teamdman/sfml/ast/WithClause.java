package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.resourcetype.ResourceType;

public interface WithClause extends ASTNode {
    <STACK> boolean test(
            ResourceType<STACK, ?, ?> resourceType,
            STACK stack
    );

    final class WithClauseThatAlwaysReturnsTrue implements WithClause {
        @Override
        public <STACK> boolean test(
                ResourceType<STACK, ?, ?> resourceType,
                STACK stack
        ) {
            return true;
        }
    }
}
