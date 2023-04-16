package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.resourcetype.ResourceType;

public class LimitedOutputSlot<STACK, ITEM, CAP> {
    public ResourceType<STACK, ITEM, CAP> type;
    public CAP handler;
    public int slot;
    public OutputResourceTracker<STACK, ITEM, CAP> tracker;
    private STACK stackInSlotCache = null;
    private boolean done = false;

    public LimitedOutputSlot(
            CAP handler, int slot, OutputResourceTracker<STACK, ITEM, CAP> tracker
    ) {
        this.init(handler, slot, tracker);
    }

    public boolean isDone() {
        // this is done in an order that aims to be as efficient as possible
        if (done) return true;
        STACK stack = getStackInSlot();
        long count = type.getCount(stack);
        if (count >= 64) {
            // if the maxStackSize is different, that will be handled by moveTo
            // for the general case, it will be faster to just assume 64 is the max stack size
            setDone();
            return true;
        }
        if (tracker.isDone()) {
            setDone();
            return true;
        }
        if (count != 0 && !tracker.test(stack)) {
            setDone();
            return true;
        }
        return false;
    }

    public void setDone() {
        this.done = true;
    }

    public STACK getStackInSlot() {
        if (stackInSlotCache == null) {
            stackInSlotCache = type.getStackInSlot(handler, slot);
        }
        return stackInSlotCache;
    }

    public STACK extract(long amount, boolean simulate) {
        stackInSlotCache = null;
        return type.extract(handler, slot, amount, simulate);
    }

    public STACK insert(STACK stack, boolean simulate) {
        stackInSlotCache = null;
        return type.insert(handler, slot, stack, simulate);
    }

    public void init(CAP handler, int slot, OutputResourceTracker<STACK, ITEM, CAP> tracker) {
        this.done = false;
        this.stackInSlotCache = null;
        this.handler = handler;
        this.tracker = tracker;
        this.slot = slot;
        this.type = tracker.LIMIT.resourceId().getResourceType();
        this.tracker.visit(this);
    }
}
