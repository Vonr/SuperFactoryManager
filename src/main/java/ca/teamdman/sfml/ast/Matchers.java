package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.InputResourceMatcher;
import ca.teamdman.sfm.common.program.OutputResourceMatcher;

import java.util.List;
import java.util.stream.Collectors;

public record Matchers<STACK>(List<ResourceLimit<STACK>> resourceLimits) implements ASTNode {
    public List<InputResourceMatcher<STACK>> createInputMatchers() {
        return resourceLimits.stream().map(InputResourceMatcher::new).collect(Collectors.toList());
    }

    public List<OutputResourceMatcher<STACK>> createOutputMatchers() {
        return resourceLimits.stream().map(OutputResourceMatcher<STACK>::new).collect(Collectors.toList());
    }

    public Matchers<STACK> withDefaults(int quantity, int retention) {
        return new Matchers<>(resourceLimits
                                      .stream()
                                      .map(il -> il.withDefaults(quantity, retention))
                                      .collect(Collectors.toList()));
    }
}
