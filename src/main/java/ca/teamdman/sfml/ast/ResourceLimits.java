package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.InputResourceTracker;
import ca.teamdman.sfm.common.program.OutputResourceTracker;

import java.util.List;
import java.util.stream.Collectors;

public record ResourceLimits(
        List<ResourceLimit<?, ?, ?>> resourceLimits,
        ResourceIdSet exclusions
) implements ASTNode {
    public List<InputResourceTracker<?, ?, ?>> createInputTrackers() {
        return resourceLimits
                .stream()
                .map(lim -> new InputResourceTracker<>(lim, exclusions))
                .collect(Collectors.toList());
    }

    public List<OutputResourceTracker<?, ?, ?>> createOutputTrackers() {
        return resourceLimits
                .stream()
                .map(lim -> new OutputResourceTracker<>(lim, exclusions))
                .collect(Collectors.toList());
    }

    public ResourceLimits withDefaults(long quantity, long retention) {
        return new ResourceLimits(resourceLimits
                                          .stream()
                                          .map(il -> il.withDefaults(quantity, retention))
                                          .collect(Collectors.toList()), exclusions);
    }

    public ResourceLimits withExclusions(ResourceIdSet exclusions) {
        return new ResourceLimits(resourceLimits, exclusions);
    }
}
