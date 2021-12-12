package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ItemMatcher;

import java.util.List;
import java.util.stream.Collectors;

public record Matchers(List<Matcher> matchers) implements ASTNode {
    public List<ItemMatcher> createMatchers() {
        return matchers.stream().map(ItemMatcher::new).collect(Collectors.toList());
    }
}
