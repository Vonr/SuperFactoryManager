package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Block implements ASTNode {
    private final List<Statement> STATEMENTS = new ArrayList<>();

    public Block(Collection<Statement> statements) {
        this.STATEMENTS.addAll(statements);
    }


    public void tick(ProgramContext context) {
        for (Statement statement : STATEMENTS) {
            statement.tick(context);
        }
    }
}
