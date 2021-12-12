package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.Matcher;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public abstract class ItemMatcher implements Predicate<ItemStack> {
    protected final Matcher MATCHER;
    protected       int     transferred = 0;

    public ItemMatcher(Matcher matcher) {
        this.MATCHER = matcher;
    }

    public abstract boolean isDone();

    public int getMaxTransferable() {
        return MATCHER.quantity() - transferred;
    }

    public void trackTransfer(int amount) {
        transferred += amount;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return true;
    }
}
