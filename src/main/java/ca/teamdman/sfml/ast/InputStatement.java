package ca.teamdman.sfml.ast;

public class InputStatement extends Statement {
    private final Label LABEL;

    public InputStatement(Label label) {
        this.LABEL = label;
    }

    public Label getLabel() {
        return LABEL;
    }
}
