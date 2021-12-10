package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfml.ast.Program;

public class ProgramExecutor {
    private final Program            PROGRAM;
    private final ManagerBlockEntity MANAGER;

    public ProgramExecutor(Program program, ManagerBlockEntity manager) {
        this.PROGRAM = program;
        this.MANAGER = manager;
    }

    public void tick() {
        var context = new ProgramContext(MANAGER);
        if (MANAGER
                    .getLevel()
                    .getGameTime() % 20 == 0) {
            MANAGER
                    .getDisk()
                    .ifPresent(PROGRAM::addWarnings);
        }
        PROGRAM.tick(context);
    }
}
