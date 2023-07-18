package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.InputResourceTracker;
import ca.teamdman.sfm.common.program.OutputResourceTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public ResourceLimits withDefaults(Limit limit) {
        return new ResourceLimits(resourceLimits.stream().map(il -> il.withDefaults(limit)).toList(), exclusions);
    }

    public ResourceLimits withExclusions(ResourceIdSet exclusions) {
        return new ResourceLimits(resourceLimits, exclusions);
    }

    @Override
    public String toString() {
        return resourceLimits.stream()
                .map(ResourceLimit::toString)
                .collect(Collectors.joining(",\n"));
    }

    public String toStringPretty(Limit defaults) {
        return resourceLimits.stream()
                .map(rl -> rl.toStringCondensed(defaults))
                .map(x -> x + ",")
                .collect(Collectors.joining("\n"));
    }
}
