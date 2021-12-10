package ca.teamdman.sfml.ast;

public class OutputStatement extends Statement {
    private final Label LABEL;

    public OutputStatement(Label label) {
        this.LABEL = label;
    }

    public Label getLabel() {
        return LABEL;
    }
}
