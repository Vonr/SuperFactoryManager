package ca.teamdman.sfml.ast;

import net.minecraft.core.Direction;

import java.util.EnumSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record DirectionQualifier(EnumSet<Direction> directions) implements ASTNode {
    public DirectionQualifier(Stream<ASTSide> sides) {
        this(convert(sides));
    }

    private static EnumSet<Direction> convert(Stream<ASTSide> sides) {
        var dirs = sides.map(DirectionQualifier::lookup).collect(Collectors.toSet());
        if (dirs.isEmpty())
            return EnumSet.noneOf(Direction.class);
        else
            return EnumSet.copyOf(dirs);
    }

    private static Direction lookup(ASTSide side) {
        return switch (side) {
            case TOP -> Direction.UP;
            case BOTTOM -> Direction.DOWN;
            case NORTH -> Direction.NORTH;
            case SOUTH -> Direction.SOUTH;
            case EAST -> Direction.EAST;
            case WEST -> Direction.WEST;
        };
    }
}
