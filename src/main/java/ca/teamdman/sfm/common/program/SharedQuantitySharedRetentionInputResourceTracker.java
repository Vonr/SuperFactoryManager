package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfml.ast.ResourceIdSet;
import ca.teamdman.sfml.ast.ResourceLimit;
import it.unimi.dsi.fastutil.ints.Int2LongArrayMap;

public class SharedQuantitySharedRetentionInputResourceTracker implements IInputResourceTracker {
    private final ResourceLimit resource_limit;
    private final ResourceIdSet exclusions;
    private final Int2LongArrayMap retention_obligations = new Int2LongArrayMap();
    private long transferred = 0;
    private long retention_obligation_progress = 0;

    public SharedQuantitySharedRetentionInputResourceTracker(
            ResourceLimit resourceLimit,
            ResourceIdSet exclusions
    ) {
        this.resource_limit = resourceLimit;
        this.exclusions = exclusions;
    }

    @Override
    public <STACK, CAP, ITEM> boolean isDone(
            ResourceType<STACK, ITEM, CAP> type,
            STACK stack
    ) {
        long can_transfer = resource_limit.limit().quantity().number().value();
        return transferred >= can_transfer;
    }

    @Override
    public <STACK, ITEM, CAP> long getRetentionObligationForSlot(
            ResourceType<STACK, ITEM, CAP> resourceType,
            STACK stack,
            int slot
    ) {
        return retention_obligations.getOrDefault(slot, 0);
    }

    @Override
    public <STACK, ITEM, CAP> long getRemainingRetentionObligation(
            ResourceType<STACK, ITEM, CAP> resourceType,
            STACK stack
    ) {
        return resource_limit.limit().retention().number().value() - retention_obligation_progress;
    }

    @Override
    public <STACK, ITEM, CAP> void trackRetentionObligation(
            ResourceType<STACK, ITEM, CAP> resourceType,
            STACK stack,
            int slot,
            long promise
    ) {
        this.retention_obligation_progress += promise;
        this.retention_obligations.merge(slot, promise, Long::sum);
    }

    @Override
    public ResourceLimit getResourceLimit() {
        return resource_limit;
    }

    @Override
    public ResourceIdSet getExclusions() {
        return exclusions;
    }


    @Override
    public <STACK, ITEM, CAP> long getMaxTransferable(
            ResourceType<STACK, ITEM, CAP> resourceType,
            STACK stack
    ) {
        return resource_limit.limit().quantity().number().value() - transferred;
    }

    @Override
    public <STACK, ITEM, CAP> void trackTransfer(
            ResourceType<STACK, ITEM, CAP> resourceType,
            STACK stack,
            long amount
    ) {
        transferred += amount;
    }

    @Override
    public String toString() {
        return "SharedQuantitySharedRetentionInputResourceTracker@"
               + Integer.toHexString(System.identityHashCode(this))
               + "{"
               +
               "TRANSFERRED="
               + transferred
               +
               ", RETENTION_OBLIGATION_PROGRESS="
               + retention_obligation_progress
               +
               ", RESOURCE_LIMIT="
               + resource_limit
               +
               ", EXCLUSIONS="
               + exclusions
               +
               "}";
    }
}
