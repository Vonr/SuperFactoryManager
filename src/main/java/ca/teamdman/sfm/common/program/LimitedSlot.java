package ca.teamdman.sfm.common.program;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public abstract class LimitedSlot<T extends ItemMatcher> {
    protected final IItemHandler HANDLER;
    protected final int          SLOT;
    protected final T            MATCHER;
    private         boolean      done = false;

    public LimitedSlot(IItemHandler handler, int slot, T matcher) {
        this.HANDLER = handler;
        this.SLOT    = slot;
        this.MATCHER = matcher;
    }

    public boolean isDone() {
        return done || MATCHER.isDone();
    }

    protected void setDone() {
        this.done = true;
    }

    public ItemStack getStackInSlot() {
        return HANDLER.getStackInSlot(SLOT);
    }

    public ItemStack extract(int amount, boolean simulate) {
        return HANDLER.extractItem(SLOT, amount, simulate);
    }

    public ItemStack insert(ItemStack stack, boolean simulate) {
        return HANDLER.insertItem(SLOT, stack, simulate);
    }

}
