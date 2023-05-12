package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfml.ast.ResourceLimit;
import it.unimi.dsi.fastutil.ints.Int2LongArrayMap;

import java.util.function.Predicate;

public class InputResourceTracker<STACK, ITEM, CAP> implements Predicate<Object> {

    protected final ResourceLimit<STACK, ITEM, CAP> LIMIT;
    private final Int2LongArrayMap RETENTION_OBLIGATIONS = new Int2LongArrayMap();
    protected long transferred = 0;
    private int retentionObligationProgress = 0;

    public InputResourceTracker(ResourceLimit<STACK, ITEM, CAP> limit) {
        this.LIMIT = limit;
    }

    public boolean isDone() {
        return transferred >= LIMIT.limit().quantity();
    }

    public long getExistingRetentionObligation(int slot) {
        return RETENTION_OBLIGATIONS.getOrDefault(slot, 0);
    }

    public long getRemainingRetentionObligation() {
        return LIMIT.limit().retention() - retentionObligationProgress;
    }

    public void trackRetentionObligation(int slot, long promise) {
        this.retentionObligationProgress += promise;
        this.RETENTION_OBLIGATIONS.merge(slot, promise, Long::sum);
    }

    public ResourceLimit<STACK, ITEM, CAP> getLimit() {
        return LIMIT;
    }

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

    public boolean matchesCapabilityType(Object capability) {
        ResourceType<STACK, ITEM, CAP> resourceType = LIMIT.resourceId().getResourceType();
        return resourceType != null && resourceType.matchesCapabilityType(capability);
    }
}
