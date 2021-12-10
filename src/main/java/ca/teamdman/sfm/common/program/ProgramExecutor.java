package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfml.ast.Program;

public class ProgramExecutor {
    private final Program            Program;
    private final ManagerBlockEntity MANAGER;

    public ProgramExecutor(Program program, ManagerBlockEntity manager) {
        this.Program = program;
        this.MANAGER = manager;
    }

    public void tick() {
        var context = new ProgramContext(MANAGER);
        Program.tick(context);
    }
}
