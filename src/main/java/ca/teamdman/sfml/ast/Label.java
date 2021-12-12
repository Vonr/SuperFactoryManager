package ca.teamdman.sfml.ast;

public record Label(String name) implements ASTNode {
    @Override
    public String toString() {
        return getClass().getSimpleName() + " = '" + name + "'";
    }
}
