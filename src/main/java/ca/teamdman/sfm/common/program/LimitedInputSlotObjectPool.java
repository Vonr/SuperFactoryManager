package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.SFM;

import java.util.Arrays;
import java.util.List;

/**
 * A pool of {@link LimitedInputSlot} objects to avoid the garbage collector
 * <p>
 * This assumes that the pool will be used in a single thread.
 */
public class LimitedInputSlotObjectPool {
    public static final LimitedInputSlotObjectPool INSTANCE = new LimitedInputSlotObjectPool();
    @SuppressWarnings("rawtypes")
    private LimitedInputSlot[] pool = new LimitedInputSlot[1];
    private int index = -1;

    public int getIndex() {
        return index;
    }

    /**
     * Acquire a {@link LimitedInputSlot} from the pool, or creates a new one if none available
     */
    public <STACK, ITEM, CAP> LimitedInputSlot<STACK, ITEM, CAP> acquire(
            CAP handler,
            int slot,
            InputResourceTracker<STACK, ITEM, CAP> tracker
    ) {
        if (index == -1) {
            return new LimitedInputSlot<>(handler, slot, tracker);
        } else {
            @SuppressWarnings("unchecked") LimitedInputSlot<STACK, ITEM, CAP> obj = pool[index];
            index--;
            obj.init(handler, slot, tracker);
            return obj;
        }
    }

    /**
     * Release a {@link LimitedInputSlot} back into the pool for it to be reused instead of garbage collected
     */
    public void release(LimitedInputSlot<?, ?, ?> obj) {
        if (index == pool.length - 1) {
            // we need to grow the array
            pool = Arrays.copyOf(pool, pool.length * 2);
        }
        pool[++index] = obj;
    }

    /**
     * Release a {@link LimitedInputSlot} back into the pool for it to be reused instead of garbage collected
     * <p>
     * After acquiring slots, the end the index after release should be {@code check + slots.size()}
     */
    @SuppressWarnings("rawtypes")
    public void release(List<LimitedInputSlot> slots, int check) {
        // handle resizing
        if (index + slots.size() >= pool.length) {
            int slotsFree = pool.length - index - 1;
            int newLength = pool.length + slots.size() - slotsFree;
            pool = Arrays.copyOf(pool, newLength);
        }
        // add to pool
        for (LimitedInputSlot slot : slots) {
            index++;
            pool[index] = slot;
        }
        // assert
        if (index != check + slots.size()) {
            SFM.LOGGER.warn(
                    "Index mismatch after releasing input slots, got {} expected {}",
                    index,
                    check + slots.size()
            );

//            throw new IllegalStateException("Index mismatch after releasing slots, got " + index + " expected " + (check + slots.size()));
        }
    }
}
