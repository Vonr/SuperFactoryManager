package ca.teamdman.sfml.ast;

public record WithTag(TagMatcher tagMatcher) implements ASTNode, WithClause {
}
