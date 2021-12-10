package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfml.ast.Start;

public class ProgramExecutor {
    private final Start              START;
    private final ManagerBlockEntity MANAGER;

    public ProgramExecutor(Start start, ManagerBlockEntity manager) {
        this.START   = start;
        this.MANAGER = manager;
    }

    public void tick() {
        var context = new ProgramContext(MANAGER);

        START
                .getProgram()
                .tick(context);
    }
}
