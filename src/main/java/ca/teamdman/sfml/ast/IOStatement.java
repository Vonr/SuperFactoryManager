package ca.teamdman.sfml.ast;

public interface IOStatement extends Statement, PrettyStatement {
    LabelAccess labelAccess();
    ResourceLimits resourceLimits();
    boolean each();
}
