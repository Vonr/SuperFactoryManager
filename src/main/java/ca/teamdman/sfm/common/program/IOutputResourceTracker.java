package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfml.ast.ResourceIdSet;
import ca.teamdman.sfml.ast.ResourceLimit;

public interface IOutputResourceTracker {
    ResourceLimit getResourceLimit();

    ResourceIdSet getExclusions();

    <STACK, CAP, ITEM> boolean isDone(
            ResourceType<STACK, ITEM, CAP> type,
            STACK stack
    );

    <STACK, ITEM, CAP> void updateRetentionObservation(
            ResourceType<STACK, ITEM, CAP> type,
            STACK stack
    );

    <STACK, ITEM, CAP> void trackTransfer(
            ResourceType<STACK, ITEM, CAP> resourceType,
            STACK stack,
            long amount
    );

    <STACK, ITEM, CAP> long getMaxTransferable(
            ResourceType<STACK, ITEM, CAP> resourceType,
            STACK stack
    );

    default boolean matchesCapabilityType(Object capability) {
        return getResourceLimit()
                .resourceIds()
                .getReferencedResourceTypes()
                .anyMatch(rt -> rt.matchesCapabilityType(capability));
    }

    default boolean matchesStack(Object stack) {
        return getResourceLimit().matchesStack(stack) && getExclusions().noneMatchStack(stack);
    }
}
