package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;

public record BoolNegation(BoolExpr inner) implements BoolExpr {
    @Override
    public boolean test(ProgramContext programContext) {
        return !inner.test(programContext);
    }

    @Override
    public String toString() {
        return "NOT " + inner;
    }
}
