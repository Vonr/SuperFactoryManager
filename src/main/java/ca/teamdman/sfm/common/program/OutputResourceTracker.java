package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfml.ast.ResourceIdSet;
import ca.teamdman.sfml.ast.ResourceLimit;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

public class OutputResourceTracker<STACK, ITEM, CAP> implements Predicate<Object> {
    private final ResourceLimit<STACK, ITEM, CAP> LIMIT;
    private final ResourceIdSet EXCLUSIONS;
    private final AtomicLong TRANSFERRED;
    private final AtomicLong RETENTION_OBLIGATION_PROGRESS;

    public OutputResourceTracker(
            ResourceLimit<STACK, ITEM, CAP> resourceLimit,
            ResourceIdSet exclusions,
            AtomicLong transferred,
            AtomicLong retentionObligationProgress
    ) {
        this.LIMIT = resourceLimit;
        this.EXCLUSIONS = exclusions;
        this.TRANSFERRED = transferred;
        this.RETENTION_OBLIGATION_PROGRESS = retentionObligationProgress;
    }

    /**
     * Done when we have reached the transfer limit, or when the retention is satisfied
     */
    public boolean isDone() {
        return TRANSFERRED.get() >= LIMIT.limit().quantity().number().value()
               || RETENTION_OBLIGATION_PROGRESS.get() >= LIMIT.limit().retention().number().value();
    }

    /**
     * Update obligation progress as new limited slots are prepared
     */
    public void visit(LimitedOutputSlot<STACK, ITEM, CAP> slot) {
        var stack = slot.getStackInSlot();
        if (test(stack)) {
            RETENTION_OBLIGATION_PROGRESS.accumulateAndGet(slot.type.getCount(stack), Long::sum);
        }
    }

    public void trackTransfer(long amount) {
        TRANSFERRED.accumulateAndGet(amount, Long::sum);
        RETENTION_OBLIGATION_PROGRESS.accumulateAndGet(amount, Long::sum);
    }

    /**
     * How much more are we allowed to move
     */
    public long getMaxTransferable() {
        long remainingRetentionRoom = LIMIT.limit().retention().number().value() - RETENTION_OBLIGATION_PROGRESS.get();
        long unusedQuantity = LIMIT.limit().quantity().number().value() - TRANSFERRED.get();
        return Math.min(unusedQuantity, remainingRetentionRoom);
    }

    public boolean matchesCapabilityType(Object capability) {
        ResourceType<STACK, ITEM, CAP> resourceType = LIMIT.resourceId().getResourceType();
        return resourceType != null && resourceType.matchesCapabilityType(capability);
    }

    @Override
    public boolean test(Object stack) {
        return LIMIT.test(stack) && !EXCLUSIONS.test(stack);
    }

    public ResourceLimit<STACK, ITEM, CAP> getLimit() {
        return LIMIT;
    }
}
