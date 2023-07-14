package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfml.ast.ResourceIdSet;
import ca.teamdman.sfml.ast.ResourceLimit;

import java.util.function.Predicate;

public class OutputResourceTracker<STACK, ITEM, CAP> implements Predicate<Object> {
    protected final ResourceLimit<STACK, ITEM, CAP> LIMIT;
    protected final ResourceIdSet EXCLUSIONS;
    protected long transferred = 0;
    private long retentionObligationProgress = 0;

    public OutputResourceTracker(ResourceLimit<STACK, ITEM, CAP> resourceLimit, ResourceIdSet exclusions) {
        this.LIMIT = resourceLimit;
        this.EXCLUSIONS = exclusions;
    }

    /**
     * Done when we have reached the transfer limit, or when the retention is satisfied
     */
    public boolean isDone() {
        return transferred >= LIMIT.limit().quantity() || retentionObligationProgress >= LIMIT.limit().retention();
    }

    /**
     * Update obligation progress as new limited slots are prepared
     */
    public void visit(LimitedOutputSlot<STACK, ITEM, CAP> slot) {
        var stack = slot.getStackInSlot();
        if (test(stack)) {
            retentionObligationProgress += slot.type.getCount(stack);
        }
    }

    public void trackTransfer(long amount) {
        transferred += amount;
        retentionObligationProgress += amount;
    }

    /**
     * How much more are we allowed to move
     */
    public long getMaxTransferable() {
        long remainingRetentionRoom = LIMIT.limit().retention() - retentionObligationProgress;
        long unusedQuantity = LIMIT.limit().quantity() - transferred;
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
}
