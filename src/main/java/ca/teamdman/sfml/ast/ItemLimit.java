package ca.teamdman.sfml.ast;

import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public record ItemLimit(
        Limit limit,
        ItemIdentifier item
) implements ASTNode, Predicate<ItemStack> {
    public ItemLimit(Limit limit) {
        this(limit, ItemIdentifier.MATCH_ALL);
    }

    public ItemLimit(ItemIdentifier item) {
        this(new Limit(), item);
    }

    public ItemLimit withDefaults(int quantity, int retention) {
        return new ItemLimit(limit.withDefaults(quantity, retention), item);
    }

    public boolean test(ItemStack stack) {
        return item.test(stack);
    }
}
