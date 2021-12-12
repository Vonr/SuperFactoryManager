package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;

public record TimerTrigger(
        Interval interval,
        Block block
) implements Trigger {
    @Override
    public boolean shouldTick(ProgramContext context) {
        return context
                       .getManager()
                       .getLevel()
                       .getGameTime() % interval.getTicks() == 0;
    }

    @Override
    public void tick(ProgramContext context) {
        block.tick(context);
    }
}
