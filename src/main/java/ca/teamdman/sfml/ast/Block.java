package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;

import java.util.List;

public record Block(List<Statement> statements) implements Statement {
    @Override
    public void tick(ProgramContext context) {
        for (Statement statement : statements) {
            statement.tick(context);
        }
    }

    @Override
    public List<Statement> getStatements() {
        return statements;
    }
}
