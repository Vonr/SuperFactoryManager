package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;

import java.util.List;

public record IfStatement(
        List<BoolExpr> expressions,
        List<Block> blocks
) implements ASTNode, Statement {
    public IfStatement {
        if (expressions.size() < blocks.size()) {
            expressions.add(new BoolExpr(__ -> true));
        }
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
            }
        }
    }
}
