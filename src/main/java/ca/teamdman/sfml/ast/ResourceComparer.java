package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.resourcetype.ResourceType;

import java.util.ArrayList;
import java.util.List;

public record ResourceComparer<STACK, ITEM, CAP>(
        ComparisonOperator op,
        Quantity num,
        ResourceIdentifier<STACK, ITEM, CAP> res
) implements ASTNode {
    public BoolExpr toBooleanExpression(SetOperator setOp, LabelAccess labelAccess) {
        return new BoolExpr(context -> {
            ResourceType<STACK, ITEM, CAP> type = res.getResourceType();
            if (type == null) return false;
            // get the inventories to check

            var handlers = type.getCapabilities(context, labelAccess);

            // track how many items seen
            long overallCount = 0;
            // track how many inventories satisfied the condition
            List<Boolean> satisfiedSet = new ArrayList<>();

            for (var cap : (Iterable<CAP>) handlers::iterator) {
                long invCount = 0;
                for (var stack : (Iterable<STACK>) type.collect(cap, labelAccess)::iterator) {
                    if (this.res.test(stack)) {
                        invCount += type.getCount(stack);
                        overallCount += type.getCount(stack);
                    }
                }
                satisfiedSet.add(this.op.test(invCount, this.num.value()));
            }

            var isOverallSatisfied = this.op.test(overallCount, this.num.value());
            return setOp.test(isOverallSatisfied, satisfiedSet);
        });
    }

}
