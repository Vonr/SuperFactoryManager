package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.ResourceLimit;

import java.util.function.Predicate;

/**
 * Tracks how many things have been transferred.
 */
public abstract class ResourceTracker<STACK, ITEM, CAP> implements Predicate<Object> {
    protected final ResourceLimit<STACK, ITEM, CAP> LIMIT;
    protected       long                            transferred = 0;

    public ResourceTracker(ResourceLimit<STACK, ITEM, CAP> resourceLimit) {
        this.LIMIT = resourceLimit;
    }

    public ResourceLimit<STACK, ITEM, CAP> getLimit() {
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
    public boolean test(Object stack) {
        return LIMIT.test(stack);
    }
}
