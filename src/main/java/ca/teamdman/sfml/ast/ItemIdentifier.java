package ca.teamdman.sfml.ast;

public record ItemIdentifier(
        String domain,
        String value
) implements ASTNode {
    public static final ItemIdentifier MATCH_ALL = new ItemIdentifier("*", "*");

    public ItemIdentifier(String value) {
        this("minecraft", value);
    }
}
