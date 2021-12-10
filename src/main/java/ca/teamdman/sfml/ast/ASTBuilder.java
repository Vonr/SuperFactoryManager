package ca.teamdman.sfml.ast;

import ca.teamdman.sfml.SFMLBaseVisitor;
import ca.teamdman.sfml.SFMLParser;

import java.util.stream.Collectors;

public class ASTBuilder extends SFMLBaseVisitor<ASTNode> {
    @Override
    public World visitWorld(SFMLParser.WorldContext worldContext) {
        var labels = worldContext
                .label()
                .stream()
                .map(this::visitLabel)
                .collect(Collectors.toList());

        return new World(labels);
    }

    @Override
    public Label visitLabel(SFMLParser.LabelContext ctx) {
        return new Label(ctx.getText());
    }

    @Override
    public Start visitStart(SFMLParser.StartContext ctx) {
        var world   = visitWorld(ctx.world());
        var program = visitProgram(ctx.program());
        return new Start(world, program);
    }

    @Override
    public Program visitProgram(SFMLParser.ProgramContext ctx) {
        var triggers = ctx
                .trigger()
                .stream()
                .map(this::visit)
                .map(Trigger.class::cast)
                .collect(Collectors.toList());
        return new Program(triggers);
    }

    @Override
    public ASTNode visitTimerTrigger(SFMLParser.TimerTriggerContext ctx) {
        var time  = (Interval) visit(ctx.interval());
        var block = visitBlock(ctx.block());
        return new TimerTrigger(block, time);
    }

    @Override
    public Number visitNumber(SFMLParser.NumberContext ctx) {
        return new Number(Integer.parseInt(ctx.getText()));
    }

    @Override
    public Interval visitTicks(SFMLParser.TicksContext ctx) {
        var num = visitNumber(ctx.number());
        return Interval.fromTicks(num.getValue());
    }

    @Override
    public Interval visitSeconds(SFMLParser.SecondsContext ctx) {
        var num = visitNumber(ctx.number());
        return Interval.fromSeconds(num.getValue());
    }

    @Override
    public InputStatement visitInputStatement(SFMLParser.InputStatementContext ctx) {
        var label = visitLabel(ctx.label());
        return new InputStatement(label);
    }

    @Override
    public OutputStatement visitOutputStatement(SFMLParser.OutputStatementContext ctx) {
        var label = visitLabel(ctx.label());
        return new OutputStatement(label);
    }

    @Override
    public Block visitBlock(SFMLParser.BlockContext ctx) {
        var statements = ctx
                .statement()
                .stream()
                .map(this::visit)
                .map(Statement.class::cast)
                .collect(Collectors.toList());
        return new Block(statements);
    }

}
