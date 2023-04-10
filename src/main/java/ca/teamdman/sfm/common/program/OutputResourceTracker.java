package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.ResourceLimit;

public class OutputResourceTracker<STACK, ITEM, CAP> extends ResourceTracker<STACK, ITEM, CAP> {
    private long seen = 0;

    public OutputResourceTracker(ResourceLimit<STACK, ITEM, CAP> resourceLimit) {
        super(resourceLimit);
    }

    public void visit(LimitedOutputSlot<STACK, ITEM, CAP> slot) {
        var stack = slot.getStackInSlot();
        if (test(stack)) {
            seen += slot.TYPE.getCount(stack);
        }
    }

    @Override
    public boolean isDone() {
        return false;
    }

    private long getRemainingRoom() {
        return LIMIT.limit().retention() - seen;
    }

    @Override
    public void trackTransfer(long amount) {
        super.trackTransfer(amount);
        seen += amount;
    }

    @Override
    public long getMaxTransferable() {
        return Math.min(super.getMaxTransferable(), getRemainingRoom());
    }
}
