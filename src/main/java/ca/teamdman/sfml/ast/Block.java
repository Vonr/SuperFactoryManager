package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;

import java.util.List;

public record Block(List<Statement> statements) implements ASTNode {
    public void tick(ProgramContext context) {
        for (Statement statement : statements) {
            statement.tick(context);
        }
    }
}
