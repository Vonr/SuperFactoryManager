package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfml.ast.ResourceIdSet;
import ca.teamdman.sfml.ast.ResourceLimit;
import it.unimi.dsi.fastutil.ints.Int2LongArrayMap;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

public class InputResourceTracker<STACK, ITEM, CAP> implements Predicate<Object> {

    private final ResourceLimit<STACK, ITEM, CAP> RESOURCE_LIMIT;
    private final ResourceIdSet EXCLUSIONS;
    private final Int2LongArrayMap RETENTION_OBLIGATIONS = new Int2LongArrayMap();
    private final AtomicLong TRANSFERRED;
    private final AtomicLong RETENTION_OBLIGATION_PROGRESS;

    public InputResourceTracker(
            ResourceLimit<STACK, ITEM, CAP> limit,
            ResourceIdSet exclusions,
            AtomicLong transferred,
            AtomicLong retentionObligationProgress
    ) {
        this.RESOURCE_LIMIT = limit;
        this.EXCLUSIONS = exclusions;
        this.TRANSFERRED = transferred;
        this.RETENTION_OBLIGATION_PROGRESS = retentionObligationProgress;
    }

    public boolean isDone() {
        return TRANSFERRED.get() >= RESOURCE_LIMIT.limit().quantity().number().value();
    }

    public long getExistingRetentionObligation(int slot) {
        return RETENTION_OBLIGATIONS.getOrDefault(slot, 0);
    }

    public long getRemainingRetentionObligation() {
        return RESOURCE_LIMIT.limit().retention().number().value() - RETENTION_OBLIGATION_PROGRESS.get();
    }

    public void trackRetentionObligation(int slot, long promise) {
        this.RETENTION_OBLIGATION_PROGRESS.accumulateAndGet(promise, Long::sum);
        this.RETENTION_OBLIGATIONS.merge(slot, promise, Long::sum);
    }

    public ResourceLimit<STACK, ITEM, CAP> getResourceLimit() {
        return RESOURCE_LIMIT;
    }

    public long getMaxTransferable() {
        return RESOURCE_LIMIT.limit().quantity().number().value() - TRANSFERRED.get();
    }

    public void trackTransfer(long amount) {
        TRANSFERRED.accumulateAndGet(amount, Long::sum);
    }

    @Override
    public boolean test(Object stack) {
        return RESOURCE_LIMIT.test(stack) && !EXCLUSIONS.test(stack);
    }

    public boolean matchesCapabilityType(Object capability) {
        ResourceType<STACK, ITEM, CAP> resourceType = RESOURCE_LIMIT.resourceId().getResourceType();
        return resourceType != null && resourceType.matchesCapabilityType(capability);
    }

    @Override
    public String toString() {
        return "InputResourceTracker@" + Integer.toHexString(System.identityHashCode(this)) + "{" +
               "TRANSFERRED=" + TRANSFERRED +
               ", RETENTION_OBLIGATION_PROGRESS=" + RETENTION_OBLIGATION_PROGRESS +
               ", RESOURCE_LIMIT=" + RESOURCE_LIMIT +
               ", EXCLUSIONS=" + EXCLUSIONS +
               "}";
    }
}
