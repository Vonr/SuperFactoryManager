package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.Matcher;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class ItemMatcher implements Predicate<ItemStack> {
    private final Matcher    MATCHER;
    private final Int2IntMap promised    = new Int2IntOpenHashMap();
    private       int        transferred = 0;
    private       int        found       = 0;

    public ItemMatcher(Matcher matcher) {
        this.MATCHER = matcher;
    }

    public boolean isDone() {
        return transferred >= MATCHER.quantity() - MATCHER.retention();
    }

    public int getPromised(int slot) {
        int needed = MATCHER.retention() - found;
        return needed + promised.getOrDefault(slot, 0);
    }

    public int clamp(int amount) {
        return Math.min(amount, MATCHER.quantity() - transferred);
    }

    public void track(int slot, int transferred, int found) {
        this.transferred += transferred;
        this.found += found;
        this.promised.merge(slot, found, Integer::sum);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return true;
    }
}
