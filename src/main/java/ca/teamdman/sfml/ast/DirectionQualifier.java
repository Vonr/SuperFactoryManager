package ca.teamdman.sfml.ast;

import net.minecraft.core.Direction;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.stream.Stream;

public record DirectionQualifier(EnumSet<Direction> directions) implements ASTNode {

    public static final DirectionQualifier NULL_DIRECTION = new DirectionQualifier(EnumSet.noneOf(Direction.class));
    public static final DirectionQualifier EVERY_DIRECTION = new DirectionQualifier(EnumSet.allOf(Direction.class));

    public static Direction lookup(Side side) {
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
        if (this == EVERY_DIRECTION)
            return Stream.concat(directions.stream(), Stream.<Direction>builder().add(null).build());
        if (directions.isEmpty()) return Stream.<Direction>builder().add(null).build();
        return directions.stream();
    }
}
