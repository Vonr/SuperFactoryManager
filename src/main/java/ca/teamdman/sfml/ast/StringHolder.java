package ca.teamdman.sfml.ast;

public class StringHolder implements ASTNode {
    private final String VALUE;

    public StringHolder(String value) {
        this.VALUE = value;
    }

    public String getValue() {
        return VALUE;
    }
}
