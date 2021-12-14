package ca.teamdman.sfml.ast;

import ca.teamdman.sfml.SFMLBaseVisitor;
import ca.teamdman.sfml.SFMLParser;
import org.antlr.v4.runtime.tree.TerminalNode;

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
        if (ctx == null) return new StringHolder("");
        return visitString(ctx.string());
    }

    @Override
    public ItemIdentifier visitItem(SFMLParser.ItemContext ctx) {
        var params = ctx.IDENTIFIER().stream().map(TerminalNode::getText).collect(Collectors.toList());
        if (params.size() == 1) return new ItemIdentifier("minecraft", params.get(0));
        return new ItemIdentifier(params.get(0), params.get(1));
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
        var labels   = ctx.label().stream().map(this::visitLabel).collect(Collectors.toList());
        var matchers = visitInputmatchers(ctx.inputmatchers());
        var sides    = visitSidequalifier(ctx.sidequalifier());
        var each     = ctx.EACH() != null;
        var slots    = visitSlotqualifier(ctx.slotqualifier());
        return new InputStatement(labels, matchers, sides, each, slots);
    }

    @Override
    public OutputStatement visitOutputstatement(SFMLParser.OutputstatementContext ctx) {
        var labels   = ctx.label().stream().map(this::visitLabel).collect(Collectors.toList());
        var matchers = visitOutputmatchers(ctx.outputmatchers());
        var sides    = visitSidequalifier(ctx.sidequalifier());
        var each     = ctx.EACH() != null;
        var slots    = visitSlotqualifier(ctx.slotqualifier());
        return new OutputStatement(labels, matchers, sides, each, slots);
    }

    @Override
    public Limit visitQuantityRetentionLimit(SFMLParser.QuantityRetentionLimitContext ctx) {
        var quantity = visitQuantity(ctx.quantity());
        var retain   = visitRetention(ctx.retention());
        return new Limit(quantity.value(), retain.value());
    }

    @Override
    public Matchers visitInputmatchers(SFMLParser.InputmatchersContext ctx) {
        if (ctx == null) return new Matchers(List.of(new ItemLimit(new Limit(Integer.MAX_VALUE, 0))));
        if (ctx.limit() != null) {
            var limit = (Limit) this.visit(ctx.limit());
            limit = limit.withDefaults(Integer.MAX_VALUE, 0);
            return new Matchers(List.of(new ItemLimit(limit)));
        } else if (ctx.item() != null) {
            var items = ctx
                    .item()
                    .stream()
                    .map(this::visitItem)
                    .map(item -> new ItemLimit(new Limit(Integer.MAX_VALUE, 0), item))
                    .collect(Collectors.toList());
            return new Matchers(items);
        } else {
            var itemLimits = ctx.itemlimit().stream()
                    .map(this::visitItemlimit)
                    .map(il -> il.withDefaults(Integer.MAX_VALUE, 0))
                    .collect(Collectors.toList());
            return new Matchers(itemLimits);
        }
    }

    @Override
    public ItemLimit visitItemlimit(SFMLParser.ItemlimitContext ctx) {
        var limit = (Limit) visit(ctx.limit());
        var item  = (ItemIdentifier) visitItem(ctx.item());
        return new ItemLimit(limit, item);
    }

    @Override
    public NumberRangeSet visitSlotqualifier(SFMLParser.SlotqualifierContext ctx) {
        return visitRangeset(ctx == null ? null : ctx.rangeset());
    }

    @Override
    public NumberRangeSet visitRangeset(SFMLParser.RangesetContext ctx) {
        if (ctx == null) return new NumberRangeSet(List.of(new NumberRange(Integer.MIN_VALUE, Integer.MAX_VALUE)));
        return new NumberRangeSet(ctx.range().stream().map(this::visitRange).collect(Collectors.toList()));
    }

    @Override
    public NumberRange visitRange(SFMLParser.RangeContext ctx) {
        var iter  = ctx.number().stream().map(this::visitNumber).mapToInt(Number::value).iterator();
        var start = iter.next();
        if (iter.hasNext()) {
            var end = iter.next();
            return new NumberRange(start, end);
        } else {
            return new NumberRange(start, start);
        }
    }

    @Override
    public Matchers visitOutputmatchers(SFMLParser.OutputmatchersContext ctx) {
        if (ctx == null) return new Matchers(List.of(new ItemLimit(new Limit(Integer.MAX_VALUE, Integer.MAX_VALUE))));
        if (ctx.limit() != null) {
            var limit = (Limit) this.visit(ctx.limit());
            limit = limit.withDefaults(Integer.MAX_VALUE, 0);
            return new Matchers(List.of(new ItemLimit(limit)));
        } else if (ctx.item() != null) {
            var items = ctx
                    .item()
                    .stream()
                    .map(this::visitItem)
                    .map(item -> new ItemLimit(new Limit(Integer.MAX_VALUE, Integer.MAX_VALUE), item))
                    .collect(Collectors.toList());
            return new Matchers(items);
        } else {
            var itemLimits = ctx.itemlimit().stream()
                    .map(this::visitItemlimit)
                    .map(il -> il.withDefaults(Integer.MAX_VALUE, Integer.MAX_VALUE))
                    .collect(Collectors.toList());
            return new Matchers(itemLimits);
        }
    }

    @Override
    public Limit visitRetentionLimit(SFMLParser.RetentionLimitContext ctx) {
        var retain = visitRetention(ctx.retention());
        return new Limit(-1, retain.value());
    }

    @Override
    public Limit visitQuantityLimit(SFMLParser.QuantityLimitContext ctx) {
        var quantity = visitQuantity(ctx.quantity());
        return new Limit(quantity.value(), -1);
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
