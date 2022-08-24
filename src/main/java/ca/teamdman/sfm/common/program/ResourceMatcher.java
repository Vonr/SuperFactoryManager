package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.ResourceLimit;

import java.util.function.Predicate;

public abstract class ResourceMatcher<STACK, CAP> implements Predicate<STACK> {
    protected final ResourceLimit<STACK, CAP> LIMIT;
    protected       int                       transferred = 0;

    public ResourceMatcher(ResourceLimit<STACK, CAP> resourceLimit) {
        this.LIMIT = resourceLimit;
    }

    public ResourceLimit<STACK, CAP> getLimit() {
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
