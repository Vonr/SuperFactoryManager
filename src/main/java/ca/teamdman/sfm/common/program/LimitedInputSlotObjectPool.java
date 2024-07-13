package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.SFM;

import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;

/**
 * A pool of {@link LimitedInputSlot} objects to avoid the garbage collector
 */
public class LimitedInputSlotObjectPool {
    public static final IdentityHashMap<LimitedInputSlot<?, ?, ?>, Boolean> LEASED = new IdentityHashMap<>();
    @SuppressWarnings("rawtypes")
    private static LimitedInputSlot[] pool = new LimitedInputSlot[27];
    private static int index = -1;

    /**
     * Acquire a {@link LimitedInputSlot} from the pool, or creates a new one if none available
     */
    public static <STACK, ITEM, CAP> LimitedInputSlot<STACK, ITEM, CAP> acquire(
            CAP handler,
            int slot,
            InputResourceTracker<STACK, ITEM, CAP> tracker,
            STACK stack
    ) {
        if (index == -1) {
            var rtn = new LimitedInputSlot<>(
                    handler,
                    slot,
                    tracker,
                    stack
            );
            LEASED.put(rtn, true);
            return rtn;
        } else {
            @SuppressWarnings("unchecked") LimitedInputSlot<STACK, ITEM, CAP> obj = pool[index];
            index--;
            obj.init(handler, slot, tracker, stack);
            LEASED.put(obj, true);
            return obj;
        }
    }

    /**
     * Release a {@link LimitedInputSlot} back into the pool for it to be reused instead of garbage collected
     */
    public static void release(LimitedInputSlot<?, ?, ?> slot) {
        if (slot.freed) {
            SFM.LOGGER.warn("Release called on already freed input slot {}", slot);
            return;
        }
        slot.freed = true;
        if (LEASED.remove(slot) == null) {
            SFM.LOGGER.warn("Freed an input slot that wasn't tracked as leased: {}", slot);
        }
        if (index == pool.length - 1) {
            // we need to grow the array
            pool = Arrays.copyOf(pool, pool.length * 2);
        }
        pool[++index] = slot;
    }

    /**
     * Release a {@link LimitedInputSlot} back into the pool for it to be reused instead of garbage collected
     * <p>
     * After acquiring slots, the end the index after release should be {@code check + slots.size()}
     */
    @SuppressWarnings("rawtypes")
    public static void release(Collection<LimitedInputSlot<?, ?, ?>> slots) {
        // handle resizing
        if (index + slots.size() >= pool.length) {
            int slotsFree = pool.length - index - 1;
            int newLength = pool.length + slots.size() - slotsFree;
            pool = Arrays.copyOf(pool, newLength);
        }
        // add to pool
        for (LimitedInputSlot slot : slots) {
            if (slot.freed) {
                SFM.LOGGER.warn("Release batch called on already freed input slot {}", slot);
                continue;
            }
            slot.freed = true;
            index++;
            pool[index] = slot;
            if (LEASED.remove(slot) == null) {
                SFM.LOGGER.warn("Freed in batch an object that wasn't tracked as leased: {}", slot);
            }
        }
    }

    public static void checkInvariant() {
        if (!LEASED.isEmpty()) {
            SFM.LOGGER.warn("Leased objects not released: {}", LEASED);
        }
    }
}
