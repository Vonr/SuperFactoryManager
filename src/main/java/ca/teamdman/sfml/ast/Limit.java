package ca.teamdman.sfml.ast;

public record Limit(
        int quantity,
        int retention
) implements ASTNode {

    public Limit withDefaults(int quantity, int retention) {
        if (quantity() < 0 && retention() < 0)
            return new Limit(quantity, retention);
        else if (quantity() < 0)
            return new Limit(quantity, retention());
        else if (retention() < 0)
            return new Limit(quantity(), retention);
        return this;
    }
}
