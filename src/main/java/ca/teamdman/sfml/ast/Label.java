package ca.teamdman.sfml.ast;

public record Label(String NAME) implements ASTNode {
    @Override
    public String toString() {
        return getClass().getSimpleName() + " = '" + NAME + "'";
    }
}
