package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.InputItemMatcher;
import ca.teamdman.sfm.common.program.OutputItemMatcher;

import java.util.List;
import java.util.stream.Collectors;

public record Matchers(List<Matcher> matchers) implements ASTNode {
    public List<InputItemMatcher> createInputMatchers() {
        return matchers.stream().map(InputItemMatcher::new).collect(Collectors.toList());
    }

    public List<OutputItemMatcher> createOutputMatchers() {
        return matchers.stream().map(OutputItemMatcher::new).collect(Collectors.toList());
    }
}
