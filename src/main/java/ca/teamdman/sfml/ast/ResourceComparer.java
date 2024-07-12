package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.resourcetype.ResourceType;

import java.util.ArrayList;
import java.util.List;

public record ResourceComparer<STACK, ITEM, CAP>(
        ComparisonOperator op,
        ResourceQuantity quantity,
        ResourceIdentifier<STACK, ITEM, CAP> res
) implements ASTNode {
    public BoolExpr toBooleanExpression(SetOperator setOp, LabelAccess labelAccess, String sourceCode) {
        return new BoolExpr(
                context -> {
                    ResourceType<STACK, ITEM, CAP> type = res.getResourceType();
                    if (type == null) return false;
                    // get the inventories to check

                    var handlers = type.getAllLabelCapabilities(context, labelAccess);

                    // track how many items seen
                    long overallCount = 0;
                    // track how many inventories satisfied the condition
                    List<Boolean> satisfiedSet = new ArrayList<>();

                    for (var cap : (Iterable<CAP>) handlers::iterator) {
                        long invCount = 0;
                        for (var stack : (Iterable<STACK>) type.collect(cap, labelAccess)::iterator) {
                            if (this.res.test(stack)) {
                                invCount += type.getAmount(stack);
                                overallCount += type.getAmount(stack);
                            }
                        }
                        satisfiedSet.add(this.op.test(invCount, this.quantity.number().value()));
                    }
                    var isOverallSatisfied = this.op.test(overallCount, this.quantity.number().value());
                    return setOp.test(isOverallSatisfied, satisfiedSet);
                },
                sourceCode
        );
    }

    @Override
    public String toString() {
        return op().getSourceCode() + " " + quantity() + " " + res().toStringCondensed();
    }
}
