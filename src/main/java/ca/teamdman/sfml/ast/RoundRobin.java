package ca.teamdman.sfml.ast;

public class RoundRobin implements ASTNode {
    private final Behaviour behaviour;
    private int nextIndex = 0;

    public RoundRobin(Behaviour behaviour) {
        this.behaviour = behaviour;
    }

    public static RoundRobin disabled() {
        return new RoundRobin(Behaviour.UNMODIFIED);
    }

    public Behaviour getBehaviour() {
        return behaviour;
    }

    public int next(int length) {
        // this never exists long enough to roll over
        return nextIndex++ % length;
    }

    @Override
    public String toString() {
        return switch (behaviour) {
            case UNMODIFIED -> "NOT ROUND ROBIN";
            case BY_BLOCK -> "ROUND ROBIN BY BLOCK";
            case BY_LABEL -> "ROUND ROBIN BY LABEL";
        };
    }

    public boolean isEnabled() {
        return behaviour != Behaviour.UNMODIFIED;
    }

    public enum Behaviour {
        UNMODIFIED,
        BY_BLOCK,
        BY_LABEL
    }
}
