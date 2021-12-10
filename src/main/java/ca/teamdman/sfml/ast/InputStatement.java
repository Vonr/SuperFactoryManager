package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ItemInput;
import ca.teamdman.sfm.common.program.ProgramContext;

public class InputStatement extends Statement {
    private final Label LABEL;

    public InputStatement(Label label) {
        this.LABEL = label;
    }

    public String getLabel() {
        return LABEL.NAME();
    }

    @Override
    public void tick(ProgramContext context) {
        var inventories = context.getItemHandlersByLabel(getLabel());
        var input       = new ItemInput(inventories);
        context.addInput(input);
    }
}
