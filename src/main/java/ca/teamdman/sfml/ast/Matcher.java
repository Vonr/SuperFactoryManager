package ca.teamdman.sfml.ast;

public record Matcher(
        int quantity,
        int retention
) implements ASTNode {
}
