package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.InputResourceTracker;
import ca.teamdman.sfm.common.program.OutputResourceTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record ResourceLimits(
        List<ResourceLimit<?, ?, ?>> resourceLimits,
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
