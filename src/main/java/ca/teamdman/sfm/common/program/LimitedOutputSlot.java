package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.resourcetype.ResourceType;

public class LimitedOutputSlot<STACK, ITEM, CAP> {
    public ResourceType<STACK, ITEM, CAP> type;
    public CAP capability;
    public int slot;
    public OutputResourceTracker<STACK, ITEM, CAP> tracker;
    private STACK stackInSlotCache = null;
    private boolean done = false;

    public LimitedOutputSlot(
            CAP capability, int slot, OutputResourceTracker<STACK, ITEM, CAP> tracker
    ) {
        this.init(capability, slot, tracker);
    }

    public boolean isDone() {
        if (done) return true;
        if (tracker.isDone()) {
            return true;
        }
        // we don't bother setting done because if this returns true it should be the last time this is called
        STACK stack = getStackInSlot();
        long count = type.getCount(stack);
        if (count >= type.getMaxStackSize(capability, slot)) {
            // if the maxStackSize is different, that will be handled by moveTo
            // for the general case, it will be faster to just assume 64 is the max stack size
            return true;
        }
        return count != 0 && !tracker.test(stack);
    }

    public void setDone() {
        this.done = true;
    }

    public STACK getStackInSlot() {
        if (stackInSlotCache == null) {
            stackInSlotCache = type.getStackInSlot(capability, slot);
        }
        return stackInSlotCache;
    }

    public STACK insert(STACK stack, boolean simulate) {
        if (!simulate) stackInSlotCache = null;
        return type.insert(capability, slot, stack, simulate);
    }

    public void init(CAP handler, int slot, OutputResourceTracker<STACK, ITEM, CAP> tracker) {
        this.done = false;
        this.stackInSlotCache = null;
        this.capability = handler;
        this.tracker = tracker;
        this.slot = slot;
        this.type = tracker.LIMIT.resourceId().getResourceType();
        this.tracker.visit(this);
    }
}
