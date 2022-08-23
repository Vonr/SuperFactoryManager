package ca.teamdman.sfml.ast;

import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public record ResourceComparer(
        ComparisonOperator op,
        Quantity num,
        ResourceIdentifier res
) implements ASTNode {
    public BoolExpr toBooleanExpression(SetOperator setOp, LabelAccess labelAccess) {
        if (res.type().equals("item")) {
            return toItemBooleanExpression(setOp, labelAccess);
        } else {
            throw new IllegalArgumentException("boolean expression 'has' operator does not support type: "
                                               + res.type());
        }
    }

    private BoolExpr toItemBooleanExpression(SetOperator setOp, LabelAccess labelAccess) {
        return new BoolExpr(context -> {
            var           handlers     = context.getItemHandlersByLabels(labelAccess);
            var           overallCount = 0;
            List<Boolean> satisfied    = new ArrayList<>();
            for (var inv : (Iterable<IItemHandler>) handlers::iterator) {
                var invCount = 0;
                for (int slot = 0; slot < inv.getSlots(); slot++) {
                    if (labelAccess.slots().contains(slot)) {
                        var stack = inv.getStackInSlot(slot);
                        if (this.res.test(stack)) {
                            invCount += stack.getCount();
                            overallCount += stack.getCount();
                        }
                    }
                }
                satisfied.add(this.op.test(invCount, this.num.value()));
            }
            var overall = this.op.test(overallCount, this.num.value());
            return setOp.test(overall, satisfied);
        });
    }
}
