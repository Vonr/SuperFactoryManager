package ca.teamdman.sfml.ast;

import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public record ResourceLimit(
        Limit limit,
        ResourceIdentifier item
) implements ASTNode, Predicate<ItemStack> {
    public ResourceLimit(Limit limit) {
        this(limit, ResourceIdentifier.MATCH_ALL);
    }

    public ResourceLimit(ResourceIdentifier item) {
        this(new Limit(), item);
    }

    public ResourceLimit withDefaults(int quantity, int retention) {
        return new ResourceLimit(limit.withDefaults(quantity, retention), item);
    }

    public boolean test(ItemStack stack) {
        return item.test(stack);
    }
}
