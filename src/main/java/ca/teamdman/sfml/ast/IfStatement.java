package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.program.ProgramContext;

import java.util.List;
import java.util.function.Predicate;

public record IfStatement(
        BoolExpr condition,
        Block trueBlock,
        Block falseBlock
) implements ASTNode, Statement, ShortStatement {
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
            context.getLogger().debug(x -> x.accept(
                    Constants.LocalizationKeys.LOG_PROGRAM_TICK_IF_STATEMENT_WAS_TRUE.get(this.condition.sourceCode())));
            trueBlock.tick(context);
        } else {
            context.pushPath(new ProgramContext.Branch(this, false));
            context.getLogger().debug(x -> x.accept(
                    Constants.LocalizationKeys.LOG_PROGRAM_TICK_IF_STATEMENT_WAS_FALSE.get(this.condition.sourceCode())));
            falseBlock.tick(context);
        }
    }

    @Override
    public String toString() {
        var rtn = "IF " + condition + " THEN\n" + trueBlock.toString().strip().indent(1).stripTrailing();
        if (!falseBlock.getStatements().isEmpty()) {
            rtn += "\nELSE\n" + falseBlock.toString().strip().indent(1);
        }
        rtn += "\nEND";
        return rtn.strip();
    }

    @Override
    public List<Statement> getStatements() {
        return List.of(trueBlock, falseBlock);
    }

    @Override
    public String toStringShort() {
        return condition.toString();
    }
}
