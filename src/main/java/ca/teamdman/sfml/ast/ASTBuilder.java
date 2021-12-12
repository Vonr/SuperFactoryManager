package ca.teamdman.sfml.ast;

import ca.teamdman.sfml.SFMLBaseVisitor;
import ca.teamdman.sfml.SFMLParser;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ASTBuilder extends SFMLBaseVisitor<ASTNode> {
    private final Set<Label> LABELS = new HashSet<>();

    private void trackLabel(Label label) {
        LABELS.add(label);
    }

    public Set<Label> getLabels() {
        return LABELS;
    }

    @Override
    public StringHolder visitName(SFMLParser.NameContext ctx) {
        return visitString(ctx.string());
    }

    @Override
    public StringHolder visitString(SFMLParser.StringContext ctx) {
        var content = ctx.getText();
        return new StringHolder(content.substring(1, content.length() - 1));
    }

    @Override
    public Label visitLabel(SFMLParser.LabelContext ctx) {
        var label = new Label(ctx.getText());
        trackLabel(label);
        return label;
    }

    @Override
    public Program visitProgram(SFMLParser.ProgramContext ctx) {
        var name = visitName(ctx.name());
        var triggers = ctx
                .trigger()
                .stream()
                .map(this::visit)
                .map(Trigger.class::cast)
                .collect(Collectors.toList());
        var labels = getLabels()
                .stream()
                .map(Label::name)
                .collect(Collectors.toSet());
        return new Program(name.value(), triggers, labels);
    }

    @Override
    public ASTNode visitTimerTrigger(SFMLParser.TimerTriggerContext ctx) {
        var time = (Interval) visit(ctx.interval());
        if (time.getSeconds() < 1) throw new IllegalArgumentException("Minimum trigger interval is 1 second.");
        var block = visitBlock(ctx.block());
        return new TimerTrigger(time, block);
    }

    @Override
    public Number visitNumber(SFMLParser.NumberContext ctx) {
        return new Number(Integer.parseInt(ctx.getText()));
    }

    @Override
    public Interval visitTicks(SFMLParser.TicksContext ctx) {
        var num = visitNumber(ctx.number());
        return Interval.fromTicks(num.value());
    }

    @Override
    public Interval visitSeconds(SFMLParser.SecondsContext ctx) {
        var num = visitNumber(ctx.number());
        return Interval.fromSeconds(num.value());
    }

    @Override
    public InputStatement visitInputStatementStatement(SFMLParser.InputStatementStatementContext ctx) {
        return (InputStatement) visit(ctx.inputstatement());
    }

    @Override
    public OutputStatement visitOutputStatementStatement(SFMLParser.OutputStatementStatementContext ctx) {
        return (OutputStatement) visit(ctx.outputstatement());
    }

    @Override
    public InputStatement visitInputstatement(SFMLParser.InputstatementContext ctx) {
        var label    = visitLabel(ctx.label());
        var matchers = visitInputmatchers(ctx.inputmatchers());
        var sides    = visitSidequalifier(ctx.sidequalifier());
        return new InputStatement(label, matchers, sides);
    }

    @Override
    public OutputStatement visitOutputstatement(SFMLParser.OutputstatementContext ctx) {
        var label    = visitLabel(ctx.label());
        var matchers = visitOutputmatchers(ctx.outputmatchers());
        var sides    = visitSidequalifier(ctx.sidequalifier());
        return new OutputStatement(label, matchers, sides);
    }

    @Override
    public Matcher visitQuantityRetentionMatcher(SFMLParser.QuantityRetentionMatcherContext ctx) {
        var quantity = visitQuantity(ctx.quantity());
        var retain   = visitRetention(ctx.retention());
        return new Matcher(quantity.value(), retain.value());
    }

    @Override
    public Matchers visitInputmatchers(SFMLParser.InputmatchersContext ctx) {
        if (ctx == null) return new Matchers(List.of(new Matcher(Integer.MAX_VALUE, 0)));
        List<Matcher> matchers = ctx
                .matcher()
                .stream()
                .map(this::visit)
                .map(Matcher.class::cast)
                .map(m -> setDefaults(m, Integer.MAX_VALUE, 0))
                .collect(Collectors.toList());
        return new Matchers(matchers);
    }

    private Matcher setDefaults(Matcher matcher, int quantity, int retention) {
        if (matcher.quantity() < 0 && matcher.retention() < 0)
            return new Matcher(quantity, retention);
        else if (matcher.quantity() < 0)
            return new Matcher(quantity, matcher.retention());
        else if (matcher.retention() < 0)
            return new Matcher(matcher.quantity(), retention);
        return matcher;
    }

    @Override
    public Matchers visitOutputmatchers(SFMLParser.OutputmatchersContext ctx) {
        if (ctx == null) return new Matchers(List.of(new Matcher(Integer.MAX_VALUE, Integer.MAX_VALUE)));
        List<Matcher> matchers = ctx
                .matcher()
                .stream()
                .map(this::visit)
                .map(Matcher.class::cast)
                .map(m -> setDefaults(m, Integer.MAX_VALUE, Integer.MAX_VALUE))
                .collect(Collectors.toList());
        return new Matchers(matchers);
    }

    @Override
    public Matcher visitRetentionMatcher(SFMLParser.RetentionMatcherContext ctx) {
        var retain = visitRetention(ctx.retention());
        return new Matcher(-1, retain.value());
    }

    @Override
    public Matcher visitQuantityMatcher(SFMLParser.QuantityMatcherContext ctx) {
        var quantity = visitQuantity(ctx.quantity());
        return new Matcher(quantity.value(), -1);
    }

    @Override
    public Number visitRetention(SFMLParser.RetentionContext ctx) {
        if (ctx == null) return new Number(-1);
        return visitNumber(ctx.number());
    }

    @Override
    public Number visitQuantity(SFMLParser.QuantityContext ctx) {
        if (ctx == null) return new Number(Integer.MAX_VALUE);
        return visitNumber(ctx.number());
    }

    @Override
    public DirectionQualifier visitSidequalifier(SFMLParser.SidequalifierContext ctx) {
        if (ctx == null) return new DirectionQualifier(Stream.empty());
        var sides = ctx.side().stream().map(this::visitSide);
        return new DirectionQualifier(sides);
    }

    @Override
    public Side visitSide(SFMLParser.SideContext ctx) {
        return Side.valueOf(ctx.getText().toUpperCase(Locale.ROOT));
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
