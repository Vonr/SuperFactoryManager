package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.SFM;

import java.util.Arrays;
import java.util.List;

/**
 * A pool of {@link LimitedOutputSlot} objects to avoid the garbage collector
 * <p>
 * This assumes that the pool will be used in a single thread.
 */
public class LimitedOutputSlotObjectPool {
    public static final LimitedOutputSlotObjectPool INSTANCE = new LimitedOutputSlotObjectPool();
    @SuppressWarnings("rawtypes")
    private LimitedOutputSlot[] pool = new LimitedOutputSlot[1];
    private int index = -1;

    public int getIndex() {
        return index;
    }

    /**
     * Acquire a {@link LimitedOutputSlot} from the pool, or creates a new one if none available
     */
    public <STACK, ITEM, CAP> LimitedOutputSlot<STACK, ITEM, CAP> acquire(
            CAP handler,
            int slot,
            OutputResourceTracker<STACK, ITEM, CAP> tracker
    ) {
        if (index == -1) {
            return new LimitedOutputSlot<>(handler, slot, tracker);
        } else {
            @SuppressWarnings("unchecked") LimitedOutputSlot<STACK, ITEM, CAP> obj = pool[index];
            index--;
            obj.init(handler, slot, tracker);
            return obj;
        }
    }

    /**
     * Release a {@link LimitedOutputSlot} back into the pool for it to be reused instead of garbage collected
     */
    public void release(LimitedOutputSlot<?, ?, ?> obj) {
        if (index == pool.length - 1) {
            // we need to grow the array
            pool = Arrays.copyOf(pool, pool.length * 2);
        }
        pool[++index] = obj;
    }

    /**
     * Release a {@link LimitedOutputSlot} back into the pool for it to be reused instead of garbage collected
     *
     * After acquiring slots, the end the index after release should be {@code check + slots.size()}
     */
    @SuppressWarnings("rawtypes")
    public void release(List<LimitedOutputSlot> slots, int check) {
        // handle resizing
        if (index + slots.size() >= pool.length) {
            int slotsFree = pool.length - index - 1;
            int newLength = pool.length + slots.size() - slotsFree;
            pool = Arrays.copyOf(pool, newLength);
        }
        // add to pool
        for (LimitedOutputSlot<?, ?, ?> slot : slots) {
            index++;
            pool[index] = slot;
        }
        // assert
        if (index != check + slots.size()) {
            SFM.LOGGER.warn(
                    "Index mismatch after releasing output slots, got {} expected {}",
                    index,
                    check + slots.size()
            );
//            throw new IllegalStateException("Index mismatch after releasing slots, got " + index + " expected " + (check + slots.size()));
        }
    }
}
