package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;

import java.util.function.Predicate;

public record BoolExpr(
        Predicate<ProgramContext> pred,
        String sourceCode
) implements Predicate<ProgramContext>, ASTNode {
    public static final BoolExpr TRUE = new BoolExpr(ctx -> true, "TRUE");
    public static final BoolExpr FALSE = new BoolExpr(ctx -> false, "FALSE");

    @Override
    public boolean test(ProgramContext context) {
        return pred.test(context);
    }

    @Override
    public BoolExpr negate() {
        return new BoolExpr(pred.negate(), "NOT " + sourceCode);
    }

    @Override
    public String toString() {
        return sourceCode;
    }
}
