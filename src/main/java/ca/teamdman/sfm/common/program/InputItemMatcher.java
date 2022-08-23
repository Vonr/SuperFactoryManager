package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.ResourceLimit;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

public class InputItemMatcher extends ItemMatcher {

    private final Int2IntMap PROMISED      = new Int2IntOpenHashMap();
    private       int        promisedCount = 0;

    public InputItemMatcher(ResourceLimit limit) {
        super(limit);
    }

    public boolean isDone() {
        return transferred >= ITEM_LIMIT.limit().quantity() - ITEM_LIMIT.limit().retention();
    }

    public int getExistingPromise(int slot) {
        return PROMISED.getOrDefault(slot, 0);
    }

    public int getRemainingPromise() {
        int needed = ITEM_LIMIT.limit().retention() - promisedCount;
        return needed;
    }

    public void track(int slot, int transferred, int promise) {
        this.transferred += transferred;
        this.promisedCount += promise;
        this.PROMISED.merge(slot, promise, Integer::sum);
    }
}
