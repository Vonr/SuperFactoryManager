package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.Matcher;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class ItemMatcher implements Predicate<ItemStack> {
    private final Matcher MATCHER;
    private       int     transferred = 0;
    private       int     found       = 0;

    public ItemMatcher(Matcher matcher) {
        this.MATCHER = matcher;
    }

    public boolean isDone() {
        return transferred >= MATCHER.quantity() - MATCHER.retention();
    }

    public int getStockRemaining() {
        return MATCHER.retention() - found;
    }

    public int clamp(int amount) {
        return Math.min(amount, MATCHER.quantity() - transferred);
    }

    public void track(int transferred, int found) {
        this.transferred += transferred;
        this.found += found;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return true;
    }
}
