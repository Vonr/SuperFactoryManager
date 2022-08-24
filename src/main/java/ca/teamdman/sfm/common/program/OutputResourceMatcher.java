package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.ResourceLimit;

public class OutputResourceMatcher<STACK> extends ResourceMatcher<STACK> {
    private int seen = 0;

    public OutputResourceMatcher(ResourceLimit resourceLimit) {
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

    private int getRemainingRoom() {
        return LIMIT.limit().retention() - seen;
    }

    @Override
    public void trackTransfer(int amount) {
        super.trackTransfer(amount);
        seen += amount;
    }

    @Override
    public int getMaxTransferable() {
        return Math.min(super.getMaxTransferable(), getRemainingRoom());
    }
}
