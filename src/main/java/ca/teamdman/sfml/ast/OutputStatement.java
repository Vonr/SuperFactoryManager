package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ItemOutput;
import ca.teamdman.sfm.common.program.ProgramContext;

public class OutputStatement extends Statement {
    private final Label LABEL;

    public OutputStatement(Label label) {
        this.LABEL = label;
    }

    public String getLabel() {
        return LABEL.NAME();
    }

    @Override
    public void tick(ProgramContext context) {
        var inventories = context.getItemHandlersByLabel(getLabel());
        var output      = new ItemOutput(inventories);
        output.tick(context);
    }
}
