package ca.teamdman.sfml.ast;

import net.minecraft.core.Direction;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record DirectionQualifier(EnumSet<Direction> directions) implements ASTNode {
    public DirectionQualifier(Stream<Side> sides) {
        this(convert(sides));
    }

    private static EnumSet<Direction> convert(Stream<Side> sides) {
        var dirs = sides.map(DirectionQualifier::lookup).collect(Collectors.toSet());
        if (dirs.isEmpty())
            return EnumSet.noneOf(Direction.class);
        else
            return EnumSet.copyOf(dirs);
    }

    private static Direction lookup(Side side) {
        return switch (side) {
            case TOP -> Direction.UP;
            case BOTTOM -> Direction.DOWN;
            case NORTH -> Direction.NORTH;
            case SOUTH -> Direction.SOUTH;
            case EAST -> Direction.EAST;
            case WEST -> Direction.WEST;
        };
    }

    public static String directionToString(@Nullable Direction direction) {
        if (direction == null) return "";
        return switch (direction) {
            case UP -> "TOP";
            case DOWN -> "BOTTOM";
            case NORTH -> "NORTH";
            case SOUTH -> "SOUTH";
            case EAST -> "EAST";
            case WEST -> "WEST";
        };
    }

    public Stream<Direction> stream() {
        if (directions.isEmpty()) return Stream.<Direction>builder().add(null).build();
        return directions.stream();
    }
}
