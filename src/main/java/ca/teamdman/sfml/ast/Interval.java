package ca.teamdman.sfml.ast;

public class Interval implements ASTNode {
    private final int TICKS;

    private Interval(int ticks) {
        this.TICKS = ticks;
    }

    public static Interval fromTicks(int ticks) {
        return new Interval(ticks);
    }

    public static Interval fromSeconds(int seconds) {
        return new Interval(seconds * 20);
    }

    public int getTicks() {
        return TICKS;
    }

    public int getSeconds() {
        return TICKS / 20;
    }
}
