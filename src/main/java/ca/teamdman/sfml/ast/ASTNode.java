package ca.teamdman.sfml.ast;

import java.util.List;

public interface ASTNode {
    default List<Statement> getStatements() {
        return List.of();
    }
}
