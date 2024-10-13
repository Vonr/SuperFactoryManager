package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.IInputResourceTracker;
import ca.teamdman.sfm.common.program.IOutputResourceTracker;
import ca.teamdman.sfm.common.resourcetype.ResourceType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record ResourceLimits(
        List<ResourceLimit> resourceLimitList,
        ResourceIdSet exclusions
) implements ASTNode {
    public List<IInputResourceTracker> createInputTrackers() {
        List<IInputResourceTracker> rtn = new ArrayList<>();
        resourceLimitList.stream().map(rl -> rl.createInputTracker(exclusions)).forEach(rtn::add);
        return rtn;
    }

    public List<IOutputResourceTracker> createOutputTrackers() {
        List<IOutputResourceTracker> rtn = new ArrayList<>();
        resourceLimitList.stream().map(rl -> rl.createOutputTracker(exclusions)).forEach(rtn::add);
        return rtn;
    }

    public ResourceLimits withDefaultLimit(Limit limit) {
        return new ResourceLimits(
                resourceLimitList.stream().map(il -> il.withDefaultLimit(limit)).toList(),
                exclusions
        );
    }

    public ResourceLimits withExclusions(ResourceIdSet exclusions) {
        return new ResourceLimits(resourceLimitList, exclusions);
    }

    public Set<ResourceType<?,?,?>> getReferencedResourceTypes() {
        Set<ResourceType<?,?,?>> rtn = new HashSet<>(8);
        for (ResourceLimit resourceLimit : resourceLimitList) {
            rtn.addAll(resourceLimit.resourceIds().getReferencedResourceTypes());
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

    public String toStringPretty(Limit defaults) {
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
