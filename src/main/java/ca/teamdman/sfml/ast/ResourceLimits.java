package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.InputResourceTracker;
import ca.teamdman.sfm.common.program.OutputResourceTracker;
import ca.teamdman.sfm.common.resourcetype.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ResourceLimits(
        List<? extends ResourceLimit<?, ?, ?>> resourceLimits,
        ResourceIdSet exclusions
) implements ASTNode {
    public List<InputResourceTracker<?, ?, ?>> createInputTrackers() {
        List<InputResourceTracker<?, ?, ?>> rtn = new ArrayList<>();
        resourceLimits.forEach(rl -> rl.gatherInputTrackers(rtn::add, exclusions));
        return rtn;
    }

    public List<OutputResourceTracker<?, ?, ?>> createOutputTrackers() {
        List<OutputResourceTracker<?, ?, ?>> rtn = new ArrayList<>();
        resourceLimits.forEach(rl -> rl.gatherOutputTrackers(rtn::add, exclusions));
        return rtn;
    }

    public ResourceLimits withDefaultLimit(Limit limit) {
        return new ResourceLimits(resourceLimits.stream().map(il -> il.withDefaultLimit(limit)).toList(), exclusions);
    }

    public ResourceLimits withExclusions(ResourceIdSet exclusions) {
        return new ResourceLimits(resourceLimits, exclusions);
    }

    @SuppressWarnings("rawtypes")
    public Stream<ResourceType> getReferencedResourceTypes() {
        return resourceLimits()
                .stream()
                .map(ResourceLimit::resourceId)
                .map((ResourceIdentifier x) -> x.getResourceType())
                .distinct();
    }

    @Override
    public String toString() {
        String rtn = this.resourceLimits.stream()
                .map(ResourceLimit::toString)
                .collect(Collectors.joining(",\n"));
        if (!exclusions.resourceIds().isEmpty()) {
            rtn += "\nEXCEPT\n" + exclusions.resourceIds().stream()
                    .map(ResourceIdentifier::toString)
                    .collect(Collectors.joining(",\n"));
        }
        return rtn;
    }

    public String toStringPretty(Limit defaults) {
        String rtn = resourceLimits.stream()
                .map(rl -> rl.toStringCondensed(defaults))
                .map(x -> resourceLimits.size() == 1 ? x : x + ",")
                .collect(Collectors.joining("\n"));
        if (!exclusions.resourceIds().isEmpty()) {
            rtn += "\nEXCEPT\n" + exclusions.resourceIds().stream()
                    .map(ResourceIdentifier::toStringCondensed)
                    .collect(Collectors.joining(",\n"));
        }
        return rtn;
    }
}
