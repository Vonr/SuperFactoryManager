package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfml.ast.ResourceIdSet;
import ca.teamdman.sfml.ast.ResourceLimit;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;


public class OutputResourceTracker implements Predicate<Object> {
    private final ResourceLimit RESOURCE_LIMIT;
    private final ResourceIdSet EXCLUSIONS;
    private final AtomicLong TRANSFERRED;
    private final AtomicLong RETENTION_OBLIGATION_PROGRESS;

    public OutputResourceTracker(
            ResourceLimit resourceLimit,
            ResourceIdSet exclusions,
            AtomicLong transferred,
            AtomicLong retentionObligationProgress
    ) {
        this.RESOURCE_LIMIT = resourceLimit;
        this.EXCLUSIONS = exclusions;
        this.TRANSFERRED = transferred;
        this.RETENTION_OBLIGATION_PROGRESS = retentionObligationProgress;
    }

    /**
     * Done when we have reached the transfer limit, or when the retention is satisfied
     */
    public boolean isDone() {
        return TRANSFERRED.get() >= RESOURCE_LIMIT.limit().quantity().number().value()
               || RETENTION_OBLIGATION_PROGRESS.get() >= RESOURCE_LIMIT.limit().retention().number().value();
    }

    /**
     * Update obligation progress as new limited slots are prepared
     */
    public <STACK, ITEM, CAP>  void updateRetentionObservation(ResourceType<STACK, ITEM, CAP> type, STACK stack) {
        if (test(stack)) {
            RETENTION_OBLIGATION_PROGRESS.accumulateAndGet(type.getAmount(stack), Long::sum);
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
        long remainingRetentionRoom = RESOURCE_LIMIT.limit().retention().number().value() - RETENTION_OBLIGATION_PROGRESS.get();
        long unusedQuantity = RESOURCE_LIMIT.limit().quantity().number().value() - TRANSFERRED.get();
        return Math.min(unusedQuantity, remainingRetentionRoom);
    }

    public boolean matchesCapabilityType(Object capability) {
        return RESOURCE_LIMIT.resourceIds().getReferencedResourceTypes().anyMatch(rt -> rt.matchesCapabilityType(capability));
    }

    @Override
    public boolean test(Object stack) {
        return RESOURCE_LIMIT.test(stack) && EXCLUSIONS.noneMatchStack(stack);
    }

    public ResourceLimit getLimit() {
        return RESOURCE_LIMIT;
    }

    @Override
    public String toString() {
        return "OutputResourceTracker@" + Integer.toHexString(System.identityHashCode(this)) + "{" +
               "TRANSFERRED=" + TRANSFERRED +
               ", RETENTION_OBLIGATION_PROGRESS=" + RETENTION_OBLIGATION_PROGRESS +
               ", RESOURCE_LIMIT=" + RESOURCE_LIMIT +
               ", EXCLUSIONS=" + EXCLUSIONS +
               "}";
    }
}
