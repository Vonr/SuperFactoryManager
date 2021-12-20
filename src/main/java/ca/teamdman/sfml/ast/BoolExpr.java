package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;

import java.util.function.Predicate;

public record BoolExpr(
        Predicate<ProgramContext> pred
) implements Predicate<ProgramContext>, ASTNode {

    @Override
    public boolean test(ProgramContext context) {
        return pred.test(context);
    }
}
