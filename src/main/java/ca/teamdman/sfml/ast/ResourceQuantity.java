package ca.teamdman.sfml.ast;

public record ResourceQuantity(
        Number number,
        IdExpansionBehaviour idExpansionBehaviour
) implements ASTNode {
    @SuppressWarnings("DataFlowIssue")
    public static ResourceQuantity UNSET = new ResourceQuantity(null, IdExpansionBehaviour.NO_EXPAND);
    public static ResourceQuantity MAX_QUANTITY = new ResourceQuantity(
            new Number(Long.MAX_VALUE),
            IdExpansionBehaviour.NO_EXPAND
    );

    public enum IdExpansionBehaviour {
        EXPAND,
        NO_EXPAND
    }
}
