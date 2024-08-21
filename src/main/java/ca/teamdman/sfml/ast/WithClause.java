package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.resourcetype.ResourceType;

import java.util.function.BiPredicate;

public interface WithClause<ENTRY> extends ASTNode, BiPredicate<ResourceType<ENTRY, ?, ?>, ENTRY> {
}
