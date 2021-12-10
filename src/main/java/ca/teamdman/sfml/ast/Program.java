package ca.teamdman.sfml.ast;

import java.util.List;

public class Program implements ASTNode {
    private final List<Trigger> TRIGGERS;

    public Program(List<Trigger> triggers) {
        this.TRIGGERS = triggers;
    }
}
