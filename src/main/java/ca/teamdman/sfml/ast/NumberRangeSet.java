package ca.teamdman.sfml.ast;

import java.util.List;

public record NumberRangeSet(List<NumberRange> ranges) implements ASTNode {
    public boolean contains(int value) {
        return ranges.stream().anyMatch(r -> r.contains(value));
    }
}
