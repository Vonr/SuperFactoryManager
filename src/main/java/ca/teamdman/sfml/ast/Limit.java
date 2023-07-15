package ca.teamdman.sfml.ast;

import static ca.teamdman.sfml.ast.ResourceQuantity.IdExpansionBehaviour.NO_EXPAND;

public record Limit(
        ResourceQuantity quantity,
        ResourceQuantity retention
) implements ASTNode {
    public static final Limit MAX_QUANTITY_NO_RETENTION = new Limit(
            new ResourceQuantity(new Number(Long.MAX_VALUE), NO_EXPAND),
            new ResourceQuantity(new Number(0), NO_EXPAND)
    );
    public static final Limit MAX_QUANTITY_MAX_RETENTION = new Limit(
            new ResourceQuantity(new Number(Long.MAX_VALUE), NO_EXPAND),
            new ResourceQuantity(new Number(Long.MAX_VALUE), NO_EXPAND)
    );

    public Limit() {
        this(ResourceQuantity.UNSET, ResourceQuantity.UNSET);
    }

    public Limit withDefaults(long quantity, long retention) {
        if (quantity() == ResourceQuantity.UNSET && retention() == ResourceQuantity.UNSET) {
            return new Limit(
                    new ResourceQuantity(new Number(quantity), NO_EXPAND),
                    new ResourceQuantity(new Number(retention), NO_EXPAND)
            );
        } else if (quantity() == ResourceQuantity.UNSET) {
            return new Limit(
                    new ResourceQuantity(new Number(quantity), NO_EXPAND),
                    retention()
            );
        } else if (retention() == ResourceQuantity.UNSET) {
            return new Limit(
                    quantity(),
                    new ResourceQuantity(new Number(retention), NO_EXPAND)
            );
        }
        return this;
    }

    @Override
    public String toString() {
        if (retention.number().value() != 0) {
            return quantity + " RETAIN " + retention;
        } else {
            return quantity.toString();
        }
    }
}
