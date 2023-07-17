package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;

public record RedstoneTrigger(
        Block block
) implements Trigger {
    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public void tick(ProgramContext context) {
        for (int i = 0; i < context.getRedstonePulses(); i++) {
            block.tick(context);
        }
    }

    @Override
    public boolean shouldTick(ProgramContext context) {
        if (context.getExecutionPolicy() == ProgramContext.ExecutionPolicy.EXPLORE_BRANCHES) return true;
        return context.getManager().getUnprocessedRedstonePulseCount() > 0;
    }
}
