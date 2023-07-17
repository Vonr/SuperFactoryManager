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

    public Limit withDefaults(Limit limit) {
        if (quantity() == ResourceQuantity.UNSET && retention() == ResourceQuantity.UNSET) {
            return limit;
        } else if (quantity() == ResourceQuantity.UNSET) {
            return new Limit(
                    limit.quantity(),
                    retention()
            );
        } else if (retention() == ResourceQuantity.UNSET) {
            return new Limit(
                    quantity(),
                    limit.retention()
            );
        }
        return this;
    }

    @Override
    public String toString() {
        return quantity + " RETAIN " + retention;
    }

    public String toStringCondensed(Limit defaults) {
        if (!retention.number().equals(defaults.retention().number())) {
            return quantity + " RETAIN " + retention;
        } else {
            return quantity.toString();
        }
    }
}
