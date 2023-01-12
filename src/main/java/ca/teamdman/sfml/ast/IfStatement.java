package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;

import java.util.List;

public record IfStatement(
        List<BoolExpr> expressions,
        List<Block> blocks
) implements ASTNode, Statement {
    public IfStatement {
        // if there is an "else" statement with no condition
        if (expressions.size() < blocks.size()) {
            expressions.add(new BoolExpr(__ -> true));
        }
        // there can only be 1 "else" statement without a condition
        assert expressions.size() == blocks.size();
    }

    @Override
    public void tick(ProgramContext context) {
        var exprIter  = expressions.iterator();
        var blockIter = blocks.iterator();
        while (exprIter.hasNext()) {
            var expr  = exprIter.next();
            var block = blockIter.next();
            if (expr.test(context)) {
                block.tick(context);
                break; // ensure only 1 block is evaluated
            }
        }
    }
}
