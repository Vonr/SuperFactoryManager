package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.InputItemMatcher;
import ca.teamdman.sfm.common.program.OutputItemMatcher;

import java.util.List;
import java.util.stream.Collectors;

public record Matchers(List<ResourceLimit> resourceLimits) implements ASTNode {
    public List<InputItemMatcher> createInputMatchers() {
        return resourceLimits.stream().map(InputItemMatcher::new).collect(Collectors.toList());
    }

    public List<OutputItemMatcher> createOutputMatchers() {
        return resourceLimits.stream().map(OutputItemMatcher::new).collect(Collectors.toList());
    }

    public Matchers withDefaults(int quantity, int retention) {
        return new Matchers(resourceLimits
                                    .stream()
                                    .map(il -> il.withDefaults(quantity, retention))
                                    .collect(Collectors.toList()));
    }
}
