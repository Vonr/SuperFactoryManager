package ca.teamdman.sfml.ast;

import net.minecraftforge.items.IItemHandler;

public record ItemComparer(
        ComparisonOperator op,
        Quantity num,
        ItemIdentifier item
) implements ASTNode {
    public static BoolExpr toBooleanExpression(LabelAccess labelAccess, ItemComparer itemComparer) {
        return new BoolExpr(context -> {
            var handlers = context.getItemHandlersByLabels(labelAccess);
            var count    = 0;
            for (var inv : (Iterable<IItemHandler>) handlers::iterator) {
                for (int slot = 0; slot < inv.getSlots(); slot++) {
                    if (labelAccess.slots().contains(slot)) {
                        var stack = inv.getStackInSlot(slot);
                        if (itemComparer.item.test(stack)) {
                            count += stack.getCount();
                        }
                    }
                }
            }
            return itemComparer.op.test(count, itemComparer.num().value());
        });
    }
}
