package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.resourcetype.ResourceType;

public class LimitedInputSlot<STACK, ITEM, CAP> {

    public ResourceType<STACK, ITEM, CAP> type;
    public CAP handler;
    public int slot;
    public InputResourceTracker<STACK, ITEM, CAP> tracker;
    private boolean done = false;
    private STACK peekCache = null;

    public LimitedInputSlot(
            CAP handler, int slot, InputResourceTracker<STACK, ITEM, CAP> tracker
    ) {
        this.init(handler, slot, tracker);
    }

    public boolean isDone() {
        if (done) return true;
        if (tracker.isDone()) {
            setDone();
            return true;
        }
        STACK stack = peekExtractPotential();
        if (type.isEmpty(stack)) {
            setDone();
            return true;
        }
        if (!tracker.test(stack)) {
            setDone();
            return true;
        }
        return false;
    }

    public void setDone() {
        this.done = true;
    }

    public STACK getStackInSlot() {
        return type.getStackInSlot(handler, slot);
    }

    public STACK extract(long amount, boolean simulate) {
        peekCache = null;
        return type.extract(handler, slot, amount, simulate);
    }

    /**
     * Checks how much could possibly be extracted from this slot.
     * We need to simulate since there are some types of slots we can't undo an extract from.
     * You can't put something back in the output slot of a furnace.
     * This value is cached for performance.
     */
    public STACK peekExtractPotential() {
        if (peekCache == null) {
            peekCache = type.extract(handler, slot, Long.MAX_VALUE, true);
        }
        return peekCache;
    }

    public STACK insert(STACK stack, boolean simulate) {
        peekCache = null;
        return type.insert(handler, slot, stack, simulate);
    }

    public void init(CAP handler, int slot, InputResourceTracker<STACK, ITEM, CAP> tracker) {
        this.done = false;
        this.peekCache = null;
        this.handler = handler;
        this.tracker = tracker;
        this.slot = slot;
        this.type = tracker.LIMIT.resourceId().getResourceType();
    }
}
