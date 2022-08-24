package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.ResourceLimit;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

public class InputResourceMatcher<STACK> extends ResourceMatcher<STACK> {

    private final Int2IntMap PROMISED      = new Int2IntOpenHashMap();
    private       int        promisedCount = 0;

    public InputResourceMatcher(ResourceLimit<STACK> limit) {
        super(limit);
    }

    public boolean isDone() {
        return transferred >= LIMIT.limit().quantity() - LIMIT.limit().retention();
    }

    public int getExistingPromise(int slot) {
        return PROMISED.getOrDefault(slot, 0);
    }

    public int getRemainingPromise() {
        int needed = LIMIT.limit().retention() - promisedCount;
        return needed;
    }

    public void track(int slot, int transferred, int promise) {
        this.transferred += transferred;
        this.promisedCount += promise;
        this.PROMISED.merge(slot, promise, Integer::sum);
    }
}
