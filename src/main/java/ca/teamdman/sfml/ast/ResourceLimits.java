package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.IInputResourceTracker;
import ca.teamdman.sfm.common.program.IOutputResourceTracker;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record ResourceLimits(
        List<ResourceLimit> resourceLimitList,
        ResourceIdSet exclusions
) implements ASTNode, ToStringPretty {
    public List<IInputResourceTracker> createInputTrackers() {
        List<IInputResourceTracker> rtn = new ObjectArrayList<>(resourceLimitList.size());
        for (ResourceLimit rl : resourceLimitList) {
            rtn.add(rl.createInputTracker(exclusions));
        }
        return rtn;
    }

    public List<IOutputResourceTracker> createOutputTrackers() {
        List<IOutputResourceTracker> rtn = new ObjectArrayList<>(resourceLimitList.size());
        for (ResourceLimit rl : resourceLimitList) {
            rtn.add(rl.createOutputTracker(exclusions));
        }
        return rtn;
    }

    public ResourceLimits withDefaultLimit(Limit limit) {
        List<ResourceLimit> defaulted = new ObjectArrayList<>(this.resourceLimitList.size());
        for (ResourceLimit rl : this.resourceLimitList) {
            defaulted.add(rl.withDefaultLimit(limit));
        }
        return new ResourceLimits(
                defaulted,
                exclusions
        );
    }

    public ResourceLimits withExclusions(ResourceIdSet exclusions) {
        return new ResourceLimits(resourceLimitList, exclusions);
    }

    /**
     * See also: {@link ResourceIdSet#getReferencedResourceTypes()}
     */
    public Set<ResourceType<?,?,?>> getReferencedResourceTypes() {
        Set<ResourceType<?,?,?>> rtn = new HashSet<>(8);
        for (ResourceLimit resourceLimit : resourceLimitList) {
            for (var resourceId : resourceLimit.resourceIds().unsafeGetIdentifiers()) {
                rtn.add(resourceId.getResourceType());
            }
        }
        return rtn;
    }

    @Override
    public String toString() {
        String rtn = this.resourceLimitList.stream()
                .map(ResourceLimit::toString)
                .collect(Collectors.joining(",\n"));
        if (!exclusions.isEmpty()) {
            rtn += "\nEXCEPT\n" + exclusions.stream()
                    .map(ResourceIdentifier::toString)
                    .collect(Collectors.joining(",\n"));
        }
        return rtn;
    }

    public String toStringCondensed(Limit defaults) {
        String rtn = resourceLimitList.stream()
                .map(rl -> rl.toStringCondensed(defaults))
                .map(x -> resourceLimitList.size() == 1 ? x : x + ",")
                .collect(Collectors.joining("\n"));
        if (!exclusions.isEmpty()) {
            rtn += "\nEXCEPT\n" + exclusions.stream()
                    .map(ResourceIdentifier::toStringCondensed)
                    .collect(Collectors.joining(",\n"));
        }
        return rtn;
    }
}
