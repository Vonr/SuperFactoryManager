package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.program.ProgramContext;

import java.util.List;

public class Program implements ASTNode {
    private final List<Trigger> TRIGGERS;

    public Program(List<Trigger> triggers) {
        this.TRIGGERS = triggers;
    }

    public void tick(ProgramContext context) {
        for (Trigger t : TRIGGERS) {
            if (t.shouldTick(context)) {
                var start = System.nanoTime();
                t.tick(context);
                var end  = System.nanoTime();
                var diff = end - start;
                SFM.LOGGER.info("Took {}ms ({}us)", diff / 1000000, diff);
            }
        }
    }
}
