package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.resourcetype.ResourceType;

import javax.annotation.Nullable;

public class LimitedOutputSlot<STACK, ITEM, CAP> {
    @SuppressWarnings("NotNullFieldNotInitialized") // done in init method in constructor
    public ResourceType<STACK, ITEM, CAP> type;
    @SuppressWarnings("NotNullFieldNotInitialized") // done in init method in constructor
    public CAP handler;
    public int slot;
    @SuppressWarnings("NotNullFieldNotInitialized") // done in init method in constructor
    public OutputResourceTracker<STACK, ITEM, CAP> tracker;
    private @Nullable STACK stackInSlotCache = null;

    public LimitedOutputSlot(
            CAP handler, int slot, OutputResourceTracker<STACK, ITEM, CAP> tracker, STACK stack
    ) {
        this.init(handler, slot, tracker, stack);
    }

    public boolean isDone() {
        if (tracker.isDone()) {
            return true;
        }
        if (slot > type.getSlots(handler) - 1) {
            // composter block changes how many slots it has between insertions
            return true;
        }
        STACK stack = getStackInSlot();
        long count = type.getAmount(stack);
        if (count >= type.getMaxStackSize(handler, slot)) {
            // if the maxStackSize is different, that will be handled by moveTo
            // for the general case, it will be faster to just assume 64 is the max stack size
            return true;
        }
        return count != 0 && !tracker.test(stack);
    }

    public STACK getStackInSlot() {
        if (stackInSlotCache == null) {
            stackInSlotCache = type.getStackInSlot(handler, slot);
        }
        return stackInSlotCache;
    }

    public STACK insert(STACK stack, boolean simulate) {
        if (!simulate) stackInSlotCache = null;
        return type.insert(handler, slot, stack, simulate);
    }

    public void init(CAP handler, int slot, OutputResourceTracker<STACK, ITEM, CAP> tracker, STACK stack) {
        this.stackInSlotCache = stack;
        this.handler = handler;
        this.tracker = tracker;
        this.slot = slot;
        //noinspection DataFlowIssue
        this.type = tracker.getLimit().resourceId().getResourceType();
        assert type != null;
    }
}
