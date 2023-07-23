package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;

import java.util.List;
import java.util.function.Predicate;

public record IfStatement(
        BoolExpr condition,
        Block trueBlock,
        Block falseBlock
) implements ASTNode, Statement {
    @Override
    public void tick(ProgramContext context) {
        Predicate<ProgramContext> condition = this.condition;
        if (context.getExecutionPolicy() == ProgramContext.ExecutionPolicy.EXPLORE_BRANCHES) {
            condition = ctx -> {
                int conditionIndex = ctx.getProgram().getConditionIndex(this);
                return (ctx.getExplorationBranchIndex() & (1 << conditionIndex)) != 0;
            };
        }

        if (condition.test(context)) {
            context.pushPath(new ProgramContext.Branch(this, true));
            trueBlock.tick(context);
        } else {
            context.pushPath(new ProgramContext.Branch(this, false));
            falseBlock.tick(context);
        }

    }

    @Override
    public String toString() {
        return "IF " + condition + " THEN " + trueBlock.toString().indent(1) + " ELSE " + falseBlock
                .toString()
                .indent(1) + " END";
    }

    @Override
    public List<Statement> getStatements() {
        return List.of(trueBlock, falseBlock);
    }
}
