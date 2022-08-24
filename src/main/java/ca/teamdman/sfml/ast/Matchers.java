package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.InputResourceMatcher;
import ca.teamdman.sfm.common.program.OutputResourceMatcher;

import java.util.List;
import java.util.stream.Collectors;

public record Matchers<STACK, CAP>(List<ResourceLimit<STACK, CAP>> resourceLimits) implements ASTNode {
    public List<InputResourceMatcher<STACK, CAP>> createInputMatchers() {
        return resourceLimits.stream().map(InputResourceMatcher::new).collect(Collectors.toList());
    }

    public List<OutputResourceMatcher<STACK, CAP>> createOutputMatchers() {
        return resourceLimits.stream().map(OutputResourceMatcher<STACK, CAP>::new).collect(Collectors.toList());
    }

    public Matchers<STACK, CAP> withDefaults(int quantity, int retention) {
        return new Matchers<>(resourceLimits
                                      .stream()
                                      .map(il -> il.withDefaults(quantity, retention))
                                      .collect(Collectors.toList()));
    }
}
