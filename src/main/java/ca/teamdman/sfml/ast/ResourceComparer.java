package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.resourcetype.ResourceType;

import java.util.ArrayList;
import java.util.List;

public record ResourceComparer<STACK, CAP>(
        ComparisonOperator op,
        Quantity num,
        ResourceIdentifier<STACK, CAP> res
) implements ASTNode {
    public BoolExpr toBooleanExpression(SetOperator setOp, LabelAccess labelAccess) {
        return new BoolExpr(context -> {
            ResourceType<STACK, CAP> type = res.getResourceType();
            // get the inventories to check

            var handlers = type.getCaps(context, labelAccess);

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
