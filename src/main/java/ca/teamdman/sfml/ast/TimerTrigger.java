package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;

public class TimerTrigger extends Trigger {
    private final Interval INTERVAL;

    public TimerTrigger(Block block, Interval interval) {
        super(block);
        INTERVAL = interval;
    }

    @Override
    public boolean shouldTick(ProgramContext context) {
        return context
                       .getManager()
                       .getLevel()
                       .getGameTime() % INTERVAL.getTicks() == 0;
    }
}
