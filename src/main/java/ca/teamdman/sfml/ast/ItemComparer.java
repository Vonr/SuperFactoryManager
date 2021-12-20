package ca.teamdman.sfml.ast;

import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public record ItemComparer(
        ComparisonOperator op,
        Quantity num,
        ItemIdentifier item
) implements ASTNode {
    public static BoolExpr toBooleanExpression(
            SetOperator setOp,
            LabelAccess labelAccess,
            ItemComparer itemComparer
    ) {
        return new BoolExpr(context -> {
            var           handlers     = context.getItemHandlersByLabels(labelAccess);
            var           overallCount = 0;
            List<Boolean> satisfied    = new ArrayList<>();
            for (var inv : (Iterable<IItemHandler>) handlers::iterator) {
                var invCount = 0;
                for (int slot = 0; slot < inv.getSlots(); slot++) {
                    if (labelAccess.slots().contains(slot)) {
                        var stack = inv.getStackInSlot(slot);
                        if (itemComparer.item.test(stack)) {
                            invCount += stack.getCount();
                            overallCount += stack.getCount();
                        }
                    }
                }
                satisfied.add(itemComparer.op.test(invCount, itemComparer.num.value()));
            }
            var overall = itemComparer.op.test(overallCount, itemComparer.num.value());
            return setOp.test(overall, satisfied);
        });
    }
}
