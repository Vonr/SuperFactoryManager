package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.InputResourceTracker;
import ca.teamdman.sfm.common.program.OutputResourceTracker;

import java.util.List;
import java.util.stream.Collectors;

public record ResourceLimits(List<ResourceLimit<?, ?, ?>> resourceLimits) implements ASTNode {
    public List<InputResourceTracker<?, ?, ?>> createInputTrackers() {
        return resourceLimits.stream().map(InputResourceTracker::new).collect(Collectors.toList());
    }

    public List<OutputResourceTracker<?, ?, ?>> createOutputTrackers() {
        return resourceLimits.stream().map(OutputResourceTracker::new).collect(Collectors.toList());
    }

    public ResourceLimits withDefaults(long quantity, long retention) {
        return new ResourceLimits(resourceLimits
                                          .stream()
                                          .map(il -> il.withDefaults(quantity, retention))
                                          .collect(Collectors.toList()));
    }
}
