package ca.teamdman.sfml.ast;

public class TimerTrigger extends Trigger {
    private final Interval INTERVAL;

    public TimerTrigger(Block block, Interval interval) {
        super(block);
        INTERVAL = interval;
    }

    @Override
    public boolean shouldTick() {
        return false;
    }
}
