package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.ItemLimit;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public abstract class ItemMatcher implements Predicate<ItemStack> {
    protected final ItemLimit ITEM_LIMIT;
    protected       int       transferred = 0;

    public ItemMatcher(ItemLimit itemLimit) {
        this.ITEM_LIMIT = itemLimit;
    }

    public abstract boolean isDone();

    public int getMaxTransferable() {
        return ITEM_LIMIT.limit().quantity() - transferred;
    }

    public void trackTransfer(int amount) {
        transferred += amount;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return ITEM_LIMIT.test(itemStack);
    }
}
