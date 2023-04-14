package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;

public interface Trigger extends Statement {
    boolean shouldTick(ProgramContext context);
}
