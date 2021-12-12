package ca.teamdman.sfml.ast;

public record ItemLimit(
        Limit limit,
        ItemIdentifier item
) implements ASTNode {
    public ItemLimit(Limit limit) {
        this(limit, ItemIdentifier.MATCH_ALL);
    }

    public ItemLimit withDefaults(int quantity, int retention) {
        return new ItemLimit(limit.withDefaults(quantity, retention), item);
    }
}
