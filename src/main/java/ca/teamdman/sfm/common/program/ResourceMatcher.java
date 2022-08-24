package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.ResourceLimit;

import java.util.function.Predicate;

public abstract class ResourceMatcher<STACK> implements Predicate<STACK> {
    protected final ResourceLimit<STACK> LIMIT;
    protected       int                  transferred = 0;

    public ResourceMatcher(ResourceLimit<STACK> resourceLimit) {
        this.LIMIT = resourceLimit;
    }

    public ResourceLimit<STACK> getLimit() {
        return LIMIT;
    }

    public int getTransferred() {
        return transferred;
    }

    public abstract boolean isDone();

    public int getMaxTransferable() {
        return LIMIT.limit().quantity() - transferred;
    }

    public void trackTransfer(int amount) {
        transferred += amount;
    }

    @Override
    public boolean test(STACK stack) {
        return LIMIT.test(stack);
    }
}
