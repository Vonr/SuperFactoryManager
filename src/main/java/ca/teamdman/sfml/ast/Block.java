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
    public String toString() {
        var rtn = new StringBuilder();
        for (Statement statement : statements) {
            if (statement instanceof InputStatement ins) {
                rtn.append(ins.toStringPretty().strip());
            } else if (statement instanceof OutputStatement outs) {
                rtn.append(outs.toStringPretty().strip());
            } else {
                rtn.append(statement.toString().strip());
            }
            rtn.append("\n");
        }
        return rtn.toString().strip();
    }

    @Override
    public List<Statement> getStatements() {
        return statements;
    }
}
