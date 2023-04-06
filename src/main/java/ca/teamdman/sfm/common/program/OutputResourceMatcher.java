package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.ResourceLimit;

public class OutputResourceMatcher<STACK, CAP> extends ResourceMatcher<STACK, CAP> {
    private long seen = 0;

    public OutputResourceMatcher(ResourceLimit<STACK, CAP> resourceLimit) {
        super(resourceLimit);
    }

    public void visit(LimitedOutputSlot<STACK, ?> slot) {
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
