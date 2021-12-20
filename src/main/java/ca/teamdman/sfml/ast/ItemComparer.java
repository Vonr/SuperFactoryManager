package ca.teamdman.sfml.ast;

import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public record ItemComparer(
        ComparisonOperator op,
        Quantity num,
        ItemIdentifier item
) implements ASTNode, Predicate<ItemStack> {
    @Override
    public boolean test(ItemStack stack) {
        int count = stack.getCount();
        if (!item.test(stack)) return false;
        return op.test(count, num.value());
    }
}
