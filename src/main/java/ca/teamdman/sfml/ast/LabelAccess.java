package ca.teamdman.sfml.ast;

import java.util.List;

public record LabelAccess(
        List<Label> labels,
        DirectionQualifier directions,
        NumberRangeSet slots
) implements ASTNode {
}
