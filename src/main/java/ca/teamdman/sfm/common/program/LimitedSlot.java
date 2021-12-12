package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.SFM;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class LimitedSlot {
    protected final IItemHandler HANDLER;
    protected final int          SLOT;
    protected final int          MAX_TRANSFERABLE;
    protected       int          transferred = 0;

    public LimitedSlot(IItemHandler handler, int slot, int maxTransferable) {
        this.HANDLER          = handler;
        this.SLOT             = slot;
        this.MAX_TRANSFERABLE = maxTransferable;
    }

    public boolean isDone() {
        return transferred >= MAX_TRANSFERABLE;
    }

    public static class LimitedExtractionSlot extends LimitedSlot {

        public LimitedExtractionSlot(IItemHandler handler, int slot, int maxTransferable) {
            super(handler, slot, maxTransferable);
        }

        public ItemStack extract(int amount, boolean simulate) {
            int       toMove    = Math.min(MAX_TRANSFERABLE - transferred, amount);
            ItemStack extracted = HANDLER.extractItem(SLOT, toMove, simulate);
            if (!simulate) transferred += extracted.getCount();
            return extracted;
        }

        public void moveTo(LimitedInsertionSlot other) {
            var potential = extract(MAX_TRANSFERABLE, true);
            if (potential.isEmpty()) return;
            var remainder = other.insert(potential, false);
            if (remainder.getCount() == potential.getCount()) return;
            var toMove    = potential.getCount() - remainder.getCount();
            var extracted = extract(toMove, false);
            remainder = other.insert(extracted, false);
            if (remainder.isEmpty()) {
                SFM.LOGGER.error(
                        "Failed to move all promised items, took {} but had {} left over after insertion.",
                        extracted,
                        remainder
                );
            }
        }
    }

    public static class LimitedInsertionSlot extends LimitedSlot {
        public LimitedInsertionSlot(IItemHandler handler, int slot, int maxTransferable) {
            super(handler, slot, maxTransferable);
        }

        public ItemStack insert(ItemStack stack, boolean simulate) {
            int toMove = Math.min(MAX_TRANSFERABLE - transferred, stack.getCount());
            if (toMove != stack.getCount()) {
                stack = stack.copy();
                stack.setCount(toMove);
            }
            var result = HANDLER.insertItem(SLOT, stack, simulate);
            if (!simulate) transferred += stack.getCount() - result.getCount();
            return result;
        }
    }
}
