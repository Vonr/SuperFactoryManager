package ca.teamdman.sfm.common.program;

import java.util.Arrays;
import java.util.List;

/**
 * A pool of {@link LimitedInputSlot} objects to avoid the garbage collector
 * <p>
 * This assumes that the pool will be used in a single thread.
 */
public class LimitedInputSlotObjectPool {
    private LimitedInputSlot[] pool = new LimitedInputSlot[1];
    private int index = -1;

    /**
     * Acquire a {@link LimitedInputSlot} from the pool, or creates a new one if none available
     */
    public <STACK, ITEM, CAP> LimitedInputSlot acquire(
            CAP handler,
            int slot,
            InputResourceTracker<STACK, ITEM, CAP> tracker
    ) {
        if (index == -1) {
            return new LimitedInputSlot<>(handler, slot, tracker);
        } else {
            LimitedInputSlot obj = pool[index];
            index--;
            obj.init(handler, slot, tracker);
            return obj;
        }
    }

    /**
     * Release a {@link LimitedInputSlot} back into the pool for it to be reused instead of garbage collected
     */
    public void release(LimitedInputSlot obj) {
        if (index == pool.length - 1) {
            // we need to grow the array
            pool = Arrays.copyOf(pool, pool.length * 2);
        }
        pool[++index] = obj;
    }

    /**
     * Release a {@link LimitedInputSlot} back into the pool for it to be reused instead of garbage collected
     */
    public void release(List<LimitedInputSlot> slots) {
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
    }
}
