package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;

import java.util.HashMap;
import java.util.function.BiPredicate;

public record WhereStatement(
        BiPredicate<ProgramContext, HashMap<Object, Long>> pred,
        String sourceCode
) implements BiPredicate<ProgramContext, HashMap<Object, Long>>, ASTNode {
    public static final WhereStatement ALWAYS_TRUE = new WhereStatement((__, ___) -> true, "");

    public boolean test(ProgramContext context, HashMap<Object, Long> resourceTable) {
        return pred.test(context, resourceTable);
    }

    public WhereStatement negate() {
        return new WhereStatement(pred.negate(), "NOT " + sourceCode);
    }

    @Override
    public String toString() {
        return sourceCode;
    }
}
