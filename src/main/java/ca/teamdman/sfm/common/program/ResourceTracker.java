package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.ResourceLimit;

import java.util.function.Predicate;

/**
 * Tracks how many things have been transferred.
 */
public abstract class ResourceTracker<STACK, CAP> implements Predicate<STACK> {
    protected final ResourceLimit<STACK, CAP> LIMIT;
    protected       long                      transferred = 0;

    public ResourceTracker(ResourceLimit<STACK, CAP> resourceLimit) {
        this.LIMIT = resourceLimit;
    }

    public ResourceLimit<STACK, CAP> getLimit() {
        return LIMIT;
    }

    public long getTransferred() {
        return transferred;
    }

    public abstract boolean isDone();

    public long getMaxTransferable() {
        return LIMIT.limit().quantity() - transferred;
    }

    public void trackTransfer(long amount) {
        transferred += amount;
    }

    @Override
    public boolean test(STACK stack) {
        return LIMIT.test(stack);
    }
}
