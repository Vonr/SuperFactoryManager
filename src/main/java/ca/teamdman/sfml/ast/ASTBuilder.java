package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.SFMConfig;
import ca.teamdman.sfml.SFMLBaseVisitor;
import ca.teamdman.sfml.SFMLParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ASTBuilder extends SFMLBaseVisitor<ASTNode> {
    private final Set<Label> USED_LABELS = new HashSet<>();
    private final Set<ResourceIdentifier<?, ?, ?>> USED_RESOURCES = new HashSet<>();
    private final List<Pair<ASTNode, ParserRuleContext>> AST_NODE_CONTEXTS = new LinkedList<>();

    public List<Pair<ASTNode, ParserRuleContext>> getNodesUnderCursor(int cursorPos) {
        return AST_NODE_CONTEXTS
                .stream()
                .filter(pair -> pair.b != null)
                .filter(pair -> pair.b.start.getStartIndex() <= cursorPos && pair.b.stop.getStopIndex() >= cursorPos)
                .collect(Collectors.toList());
    }

    public Optional<ASTNode> getNodeAtIndex(int index) {
        if (index < 0 || index >= AST_NODE_CONTEXTS.size()) return Optional.empty();
        return Optional.ofNullable(AST_NODE_CONTEXTS.get(index).a);
    }

    public int getIndexForNode(ASTNode node) {
        return AST_NODE_CONTEXTS
                .stream()
                .filter(pair -> pair.a == node)
                .map(AST_NODE_CONTEXTS::indexOf)
                .findFirst()
                .orElse(-1);
    }

    @Override
    public StringHolder visitName(@Nullable SFMLParser.NameContext ctx) {
        if (ctx == null) return new StringHolder("");
        StringHolder name = visitString(ctx.string());
        AST_NODE_CONTEXTS.add(new Pair<>(name, ctx));
        return name;
    }

    @Override
    public ASTNode visitResource(SFMLParser.ResourceContext ctx) {
        var str = ctx
                .children
                .stream()
                .map(ParseTree::getText)
                .collect(Collectors.joining())
                .replaceAll("::", ":*:")
                .replaceAll(":$", ":*")
                .replaceAll("\\*", ".*");
        var rtn = ResourceIdentifier.fromString(str);
        USED_RESOURCES.add(rtn);
        rtn.assertValid();
        AST_NODE_CONTEXTS.add(new Pair<>(rtn, ctx));
        return rtn;
    }

    @Override
    public ResourceIdentifier<?, ?, ?> visitStringResource(SFMLParser.StringResourceContext ctx) {
        var rtn = ResourceIdentifier.fromString(visitString(ctx.string()).value());
        USED_RESOURCES.add(rtn);
        rtn.assertValid();
        AST_NODE_CONTEXTS.add(new Pair<>(rtn, ctx));
        return rtn;
    }

    @Override
    public StringHolder visitString(SFMLParser.StringContext ctx) {
        var content = ctx.getText();
        StringHolder str = new StringHolder(content.substring(1, content.length() - 1));
        AST_NODE_CONTEXTS.add(new Pair<>(str, ctx));
        return str;
    }

    @Override
    public Label visitRawLabel(SFMLParser.RawLabelContext ctx) {
        var label = new Label(ctx.getText());
        if (label.name().length() > Program.MAX_LABEL_LENGTH) {
            throw new IllegalArgumentException("Label name cannot be longer than "
                                               + Program.MAX_LABEL_LENGTH
                                               + " characters.");
        }
        USED_LABELS.add(label);
        AST_NODE_CONTEXTS.add(new Pair<>(label, ctx));
        return label;
    }

    @Override
    public Label visitStringLabel(SFMLParser.StringLabelContext ctx) {
        var label = new Label(visitString(ctx.string()).value());
        if (label.name().length() > Program.MAX_LABEL_LENGTH) {
            throw new IllegalArgumentException("Label name cannot be longer than "
                                               + Program.MAX_LABEL_LENGTH
                                               + " characters.");
        }
        USED_LABELS.add(label);
        AST_NODE_CONTEXTS.add(new Pair<>(label, ctx));
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
        var labels = USED_LABELS
                .stream()
                .map(Label::name)
                .collect(Collectors.toSet());
        Program program = new Program(name.value(), triggers, labels, USED_RESOURCES);
        AST_NODE_CONTEXTS.add(new Pair<>(program, ctx));
        return program;
    }

    @Override
    public ASTNode visitTimerTrigger(SFMLParser.TimerTriggerContext ctx) {
        // create timer trigger
        var time = (Interval) visit(ctx.interval());
        var block = visitBlock(ctx.block());
        TimerTrigger timerTrigger = new TimerTrigger(time, block);

        // get default min interval
        int minInterval = timerTrigger.usesOnlyForgeEnergyResourceIO()
                          ? SFMConfig.getOrDefault(SFMConfig.COMMON.timerTriggerMinimumIntervalInTicksWhenOnlyForgeEnergyIO)
                          : SFMConfig.getOrDefault(SFMConfig.COMMON.timerTriggerMinimumIntervalInTicks);

        // validate interval
        if (time.getTicks() < minInterval) {
            throw new IllegalArgumentException("Minimum trigger interval is " + minInterval + " ticks.");
        }

        AST_NODE_CONTEXTS.add(new Pair<>(timerTrigger, ctx));
        return timerTrigger;
    }

    @Override
    public ASTNode visitBooleanRedstone(SFMLParser.BooleanRedstoneContext ctx) {
        ComparisonOperator comp = ComparisonOperator.GREATER_OR_EQUAL;
        Number num = new Number(0);
        if (ctx.comparisonOp() != null && ctx.number() != null) {
            comp = visitComparisonOp(ctx.comparisonOp());
            num = visitNumber(ctx.number());
        }

        ComparisonOperator finalComp = comp;
        if (num.value() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Redstone signal strength cannot be greater than " + Integer.MAX_VALUE);
        }
        //noinspection ExtractMethodRecommender
        int finalNum = (int) num.value();
        //noinspection DataFlowIssue // if the program is ticking, level shouldn't be null
        BoolExpr boolExpr = new BoolExpr(
                programContext -> finalComp.test(
                        (long) programContext
                                .getManager()
                                .getLevel()
                                .getBestNeighborSignal(programContext
                                                               .getManager()
                                                               .getBlockPos()),
                        (long) finalNum
                ),
                ctx.getText()
        );
        AST_NODE_CONTEXTS.add(new Pair<>(boolExpr, ctx));
        return boolExpr;
    }

    @Override
    public ASTNode visitPulseTrigger(SFMLParser.PulseTriggerContext ctx) {
        var block = visitBlock(ctx.block());
        RedstoneTrigger redstoneTrigger = new RedstoneTrigger(block);
        AST_NODE_CONTEXTS.add(new Pair<>(redstoneTrigger, ctx));
        return redstoneTrigger;
    }

    @Override
    public Number visitNumber(SFMLParser.NumberContext ctx) {
        Number number = new Number(Long.parseLong(ctx.getText()));
        AST_NODE_CONTEXTS.add(new Pair<>(number, ctx));
        return number;
    }

    @Override
    public Interval visitTick(SFMLParser.TickContext ctx) {
        Interval interval = Interval.fromTicks(1);
        AST_NODE_CONTEXTS.add(new Pair<>(interval, ctx));
        return interval;
    }

    @Override
    public Interval visitTicks(SFMLParser.TicksContext ctx) {
        var num = visitNumber(ctx.number());
        if (num.value() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("interval cannot be greater than " + Integer.MAX_VALUE + " ticks.");
        }
        Interval interval = Interval.fromTicks((int) num.value());
        AST_NODE_CONTEXTS.add(new Pair<>(interval, ctx));
        return interval;
    }

    @Override
    public Interval visitSeconds(SFMLParser.SecondsContext ctx) {
        var num = visitNumber(ctx.number());
        if (num.value() > Integer.MAX_VALUE / 20) {
            throw new IllegalArgumentException("interval cannot be greater than " + Integer.MAX_VALUE + " ticks.");
        }
        Interval interval = Interval.fromSeconds((int) num.value());
        AST_NODE_CONTEXTS.add(new Pair<>(interval, ctx));
        return interval;
    }

    @Override
    public InputStatement visitInputStatementStatement(SFMLParser.InputStatementStatementContext ctx) {
        InputStatement input = (InputStatement) visit(ctx.inputstatement());
        AST_NODE_CONTEXTS.add(new Pair<>(input, ctx));
        return input;
    }

    @Override
    public OutputStatement visitOutputStatementStatement(SFMLParser.OutputStatementStatementContext ctx) {
        OutputStatement output = (OutputStatement) visit(ctx.outputstatement());
        AST_NODE_CONTEXTS.add(new Pair<>(output, ctx));
        return output;
    }

    @Override
    public InputStatement visitInputstatement(SFMLParser.InputstatementContext ctx) {
        var labelAccess = visitLabelaccess(ctx.labelaccess());
        var matchers = visitInputmatchers(ctx.inputmatchers());
        var exclusions = visitResourceexclusion(ctx.resourceexclusion());
        var each = ctx.EACH() != null;
        InputStatement inputStatement = new InputStatement(labelAccess, matchers.withExclusions(exclusions), each);
        AST_NODE_CONTEXTS.add(new Pair<>(inputStatement, ctx));
        return inputStatement;
    }

    @Override
    public OutputStatement visitOutputstatement(SFMLParser.OutputstatementContext ctx) {
        var labelAccess = visitLabelaccess(ctx.labelaccess());
        var matchers = visitOutputmatchers(ctx.outputmatchers());
        var exclusions = visitResourceexclusion(ctx.resourceexclusion());
        var each = ctx.EACH() != null;
        OutputStatement outputStatement = new OutputStatement(labelAccess, matchers.withExclusions(exclusions), each);
        AST_NODE_CONTEXTS.add(new Pair<>(outputStatement, ctx));
        return outputStatement;
    }

    @Override
    public LabelAccess visitLabelaccess(SFMLParser.LabelaccessContext ctx) {
        var directionQualifierCtx = ctx.sidequalifier();
        DirectionQualifier directionQualifier;
        if (directionQualifierCtx == null) {
            directionQualifier = DirectionQualifier.NULL_DIRECTION;
        } else {
            directionQualifier = (DirectionQualifier) visit(directionQualifierCtx);
        }
        LabelAccess labelAccess = new LabelAccess(
                ctx.label().stream().map(this::visit).map(Label.class::cast).collect(Collectors.toList()),
                directionQualifier,
                visitSlotqualifier(ctx.slotqualifier()),
                visitRoundrobin(ctx.roundrobin())
        );
        AST_NODE_CONTEXTS.add(new Pair<>(labelAccess, ctx));
        return labelAccess;
    }

    @Override
    public RoundRobin visitRoundrobin(@Nullable SFMLParser.RoundrobinContext ctx) {
        if (ctx == null) return RoundRobin.disabled();
        return ctx.BLOCK() != null
               ? new RoundRobin(RoundRobin.Behaviour.BY_BLOCK)
               : new RoundRobin(RoundRobin.Behaviour.BY_LABEL);
    }

    @Override
    public IfStatement visitIfstatement(SFMLParser.IfstatementContext ctx) {
        var conditions = ctx
                .boolexpr()
                .stream()
                .map(this::visit)
                .map(BoolExpr.class::cast)
                .collect(Collectors.toCollection(ArrayDeque::new));
        var blocks = ctx.block().stream()
                .map(this::visitBlock)
                .collect(Collectors.toCollection(ArrayDeque::new));

        IfStatement nestedStatement;
        if (conditions.size() < blocks.size()) {
            Block elseBlock = blocks.removeLast();
            Block ifBlock = blocks.removeLast();
            nestedStatement = new IfStatement(
                    conditions.removeLast(),
                    ifBlock,
                    elseBlock
            );
        } else {
            nestedStatement = new IfStatement(
                    conditions.removeLast(),
                    blocks.removeLast(),
                    new Block(List.of())
            );
        }
        while (!blocks.isEmpty()) {
            nestedStatement = new IfStatement(
                    conditions.removeLast(),
                    blocks.removeLast(),
                    new Block(List.of(nestedStatement))
            );
        }
        if (!conditions.isEmpty()) {
            throw new IllegalStateException("If statement construction failed to consume all conditions");
        }

        AST_NODE_CONTEXTS.add(new Pair<>(nestedStatement, ctx));
        return nestedStatement;
    }

    @Override
    public IfStatement visitIfStatementStatement(SFMLParser.IfStatementStatementContext ctx) {
        IfStatement ifStatement = visitIfstatement(ctx.ifstatement());
        AST_NODE_CONTEXTS.add(new Pair<>(ifStatement, ctx));
        return ifStatement;
    }

    @Override
    public BoolExpr visitBooleanTrue(SFMLParser.BooleanTrueContext ctx) {
        BoolExpr boolExpr = new BoolExpr(__ -> true, "TRUE");
        AST_NODE_CONTEXTS.add(new Pair<>(boolExpr, ctx));
        return boolExpr;
    }

    @Override
    public BoolExpr visitBooleanHas(SFMLParser.BooleanHasContext ctx) {
        var setOp = visitSetOp(ctx.setOp());
        var labelAccess = visitLabelaccess(ctx.labelaccess());
        var comparison = visitResourcecomparison(ctx.resourcecomparison());
        BoolExpr booleanExpression = comparison.toBooleanExpression(
                setOp,
                labelAccess,
                setOp.name().toUpperCase() + " " + labelAccess + " HAS " + comparison
        );
        AST_NODE_CONTEXTS.add(new Pair<>(booleanExpression, ctx));
        return booleanExpression;
    }

    @Override
    public SetOperator visitSetOp(@Nullable SFMLParser.SetOpContext ctx) {
        if (ctx == null) return SetOperator.OVERALL;
        SetOperator from = SetOperator.from(ctx.getText());
        AST_NODE_CONTEXTS.add(new Pair<>(from, ctx));
        return from;
    }

    @Override
    public ResourceComparer<?, ?, ?> visitResourcecomparison(SFMLParser.ResourcecomparisonContext ctx) {
        ComparisonOperator op = visitComparisonOp(ctx.comparisonOp());
        Number num = visitNumber(ctx.number());
        ResourceQuantity quantity = new ResourceQuantity(num, ResourceQuantity.IdExpansionBehaviour.NO_EXPAND);

        ResourceIdentifier<?, ?, ?> item;
        if (ctx.resourceid() == null) {
            item = ResourceIdentifier.MATCH_ALL;
        } else {
            item = (ResourceIdentifier<?, ?, ?>) visit(ctx.resourceid());
        }

        ResourceComparer<?, ?, ?> resourceComparer = new ResourceComparer<>(op, quantity, item);
        AST_NODE_CONTEXTS.add(new Pair<>(resourceComparer, ctx));
        return resourceComparer;
    }

    @Override
    public ComparisonOperator visitComparisonOp(SFMLParser.ComparisonOpContext ctx) {
        ComparisonOperator from = ComparisonOperator.from(ctx.getText());
        AST_NODE_CONTEXTS.add(new Pair<>(from, ctx));
        return from;
    }

    @Override
    public BoolExpr visitBooleanConjunction(SFMLParser.BooleanConjunctionContext ctx) {
        var left = (BoolExpr) visit(ctx.boolexpr(0));
        var right = (BoolExpr) visit(ctx.boolexpr(1));
        BoolExpr boolExpr = new BoolExpr(left.and(right), left.sourceCode() + " AND " + right.sourceCode());
        AST_NODE_CONTEXTS.add(new Pair<>(boolExpr, ctx));
        return boolExpr;
    }

    @Override
    public BoolExpr visitBooleanDisjunction(SFMLParser.BooleanDisjunctionContext ctx) {
        var left = (BoolExpr) visit(ctx.boolexpr(0));
        var right = (BoolExpr) visit(ctx.boolexpr(1));
        BoolExpr boolExpr = new BoolExpr(left.or(right), left.sourceCode() + " OR " + right.sourceCode());
        AST_NODE_CONTEXTS.add(new Pair<>(boolExpr, ctx));
        return boolExpr;
    }

    @Override
    public BoolExpr visitBooleanFalse(SFMLParser.BooleanFalseContext ctx) {
        BoolExpr boolExpr = new BoolExpr(__ -> false, "FALSE");
        AST_NODE_CONTEXTS.add(new Pair<>(boolExpr, ctx));
        return boolExpr;
    }

    @Override
    public BoolExpr visitBooleanParen(SFMLParser.BooleanParenContext ctx) {
        BoolExpr expr = (BoolExpr) visit(ctx.boolexpr());
        AST_NODE_CONTEXTS.add(new Pair<>(expr, ctx));
        return expr;
    }

    @Override
    public BoolExpr visitBooleanNegation(SFMLParser.BooleanNegationContext ctx) {
        var x = (BoolExpr) visit(ctx.boolexpr());
        BoolExpr boolExpr = new BoolExpr(x.negate(), "NOT " + x.sourceCode());
        AST_NODE_CONTEXTS.add(new Pair<>(boolExpr, ctx));
        return boolExpr;
    }

    @Override
    public Limit visitQuantityRetentionLimit(SFMLParser.QuantityRetentionLimitContext ctx) {
        var quantity = visitQuantity(ctx.quantity());
        var retain = visitRetention(ctx.retention());
        Limit limit = new Limit(quantity, retain);
        AST_NODE_CONTEXTS.add(new Pair<>(limit, ctx));
        return limit;
    }

    @Override
    public ResourceIdSet visitResourceexclusion(@Nullable SFMLParser.ResourceexclusionContext ctx) {
        if (ctx == null) return ResourceIdSet.EMPTY;
        ResourceIdSet resourceIdSet = new ResourceIdSet(ctx
                                                                .resourceid()
                                                                .stream()
                                                                .map(this::visit)
                                                                .map(ResourceIdentifier.class::cast)
                                                                .collect(HashSet::new, HashSet::add, HashSet::addAll));
        AST_NODE_CONTEXTS.add(new Pair<>(resourceIdSet, ctx));
        return resourceIdSet;
    }


    private void assertResourceLimitDoesntExpandHuge(ResourceLimits limits) {
        if (limits.createInputTrackers().size() > 512) {
            throw new IllegalArgumentException("Resource limit expands to more than 512 trackers, this is likely a mistake where the \"EACH\" keyword is being used. The code: " + limits);
        }
    }

    @Override
    public ResourceLimits visitInputmatchers(@Nullable SFMLParser.InputmatchersContext ctx) {
        if (ctx == null) {
            return new ResourceLimits(List.of(ResourceLimit.TAKE_ALL_LEAVE_NONE), ResourceIdSet.EMPTY);
        }
        ResourceLimits resourceLimits = ((ResourceLimits) visit(ctx.movement())).withDefaults(Limit.MAX_QUANTITY_NO_RETENTION);
        assertResourceLimitDoesntExpandHuge(resourceLimits);
        AST_NODE_CONTEXTS.add(new Pair<>(resourceLimits, ctx));
        return resourceLimits;
    }


    @Override
    public ResourceLimits visitOutputmatchers(@Nullable SFMLParser.OutputmatchersContext ctx) {
        if (ctx == null) {
            return new ResourceLimits(List.of(ResourceLimit.ACCEPT_ALL_WITHOUT_RESTRAINT), ResourceIdSet.EMPTY);
        }
        ResourceLimits resourceLimits = ((ResourceLimits) visit(ctx.movement())).withDefaults(Limit.MAX_QUANTITY_MAX_RETENTION);
        assertResourceLimitDoesntExpandHuge(resourceLimits);
        AST_NODE_CONTEXTS.add(new Pair<>(resourceLimits, ctx));
        return resourceLimits;
    }

    @Override
    public ResourceLimits visitResourceLimitMovement(SFMLParser.ResourceLimitMovementContext ctx) {
        ResourceLimits resourceLimits = new ResourceLimits(
                ctx.resourcelimit().stream()
                        .map(this::visitResourcelimit)
                        .collect(Collectors.toList()),
                ResourceIdSet.EMPTY
        );
        AST_NODE_CONTEXTS.add(new Pair<>(resourceLimits, ctx));
        return resourceLimits;
    }

    @Override
    public ResourceLimits visitLimitMovement(SFMLParser.LimitMovementContext ctx) {
        ResourceLimits resourceLimits = new ResourceLimits(
                List.of(new ResourceLimit<>(
                        ResourceIdentifier.MATCH_ALL, (Limit) this.visit(ctx.limit())
                )),
                ResourceIdSet.EMPTY
        );
        AST_NODE_CONTEXTS.add(new Pair<>(resourceLimits, ctx));
        return resourceLimits;
    }

    @Override
    public ResourceLimit<?, ?, ?> visitResourcelimit(SFMLParser.ResourcelimitContext ctx) {

        var res = (ResourceIdentifier<?, ?, ?>) visit(ctx.resourceid());

        if (ctx.limit() == null)
            return new ResourceLimit<>(res, Limit.UNSET);

        var limit = (Limit) visit(ctx.limit());
        ResourceLimit<?, ?, ?> resourceLimit = new ResourceLimit<>(res, limit);
        AST_NODE_CONTEXTS.add(new Pair<>(resourceLimit, ctx));
        return resourceLimit;
    }

    @Override
    public NumberRangeSet visitSlotqualifier(@Nullable SFMLParser.SlotqualifierContext ctx) {
        NumberRangeSet numberRangeSet = visitRangeset(ctx == null ? null : ctx.rangeset());
        AST_NODE_CONTEXTS.add(new Pair<>(numberRangeSet, ctx));
        return numberRangeSet;
    }

    @Override
    public ASTNode visitForgetStatementStatement(SFMLParser.ForgetStatementStatementContext ctx) {
        ForgetStatement statement = (ForgetStatement) visit(ctx.forgetstatement());
        AST_NODE_CONTEXTS.add(new Pair<>(statement, ctx));
        return statement;
    }

    @Override
    public ForgetStatement visitForgetstatement(SFMLParser.ForgetstatementContext ctx) {
        List<Label> labels = ctx
                .label()
                .stream()
                .map(this::visit)
                .map(Label.class::cast)
                .collect(Collectors.toList());
        if (labels.isEmpty()) {
            labels = USED_LABELS.stream().toList();
        }
        return new ForgetStatement(labels);
    }

    @Override
    public NumberRangeSet visitRangeset(@Nullable SFMLParser.RangesetContext ctx) {
        if (ctx == null) return NumberRangeSet.MAX_RANGE;
        NumberRangeSet numberRangeSet = new NumberRangeSet(ctx
                                                                   .range()
                                                                   .stream()
                                                                   .map(this::visitRange)
                                                                   .toArray(NumberRange[]::new));
        AST_NODE_CONTEXTS.add(new Pair<>(numberRangeSet, ctx));
        return numberRangeSet;
    }

    @Override
    public NumberRange visitRange(SFMLParser.RangeContext ctx) {
        var iter = ctx.number().stream().map(this::visitNumber).mapToLong(Number::value).iterator();
        var start = iter.next();
        if (iter.hasNext()) {
            var end = iter.next();
            NumberRange numberRange = new NumberRange(start, end);
            AST_NODE_CONTEXTS.add(new Pair<>(numberRange, ctx));
            return numberRange;
        } else {
            NumberRange numberRange = new NumberRange(start, start);
            AST_NODE_CONTEXTS.add(new Pair<>(numberRange, ctx));
            return numberRange;
        }
    }


    @Override
    public Limit visitRetentionLimit(SFMLParser.RetentionLimitContext ctx) {
        var retain = visitRetention(ctx.retention());
        Limit limit = new Limit(ResourceQuantity.UNSET, retain);
        AST_NODE_CONTEXTS.add(new Pair<>(limit, ctx));
        return limit;
    }

    @Override
    public Limit visitQuantityLimit(SFMLParser.QuantityLimitContext ctx) {
        var quantity = visitQuantity(ctx.quantity());
        Limit limit = new Limit(quantity, ResourceQuantity.UNSET);
        AST_NODE_CONTEXTS.add(new Pair<>(limit, ctx));
        return limit;
    }

    @Override
    public ResourceQuantity visitRetention(@Nullable SFMLParser.RetentionContext ctx) {
        if (ctx == null)
            return ResourceQuantity.UNSET;
        ResourceQuantity quantity = new ResourceQuantity(
                visitNumber(ctx.number()),
                ctx.EACH() != null
                ? ResourceQuantity.IdExpansionBehaviour.EXPAND
                : ResourceQuantity.IdExpansionBehaviour.NO_EXPAND
        );
        AST_NODE_CONTEXTS.add(new Pair<>(quantity, ctx));
        return quantity;
    }

    @Override
    public ResourceQuantity visitQuantity(@Nullable SFMLParser.QuantityContext ctx) {
        if (ctx == null) return ResourceQuantity.MAX_QUANTITY;
        ResourceQuantity quantity = new ResourceQuantity(
                visitNumber(ctx.number()),
                ctx.EACH() != null
                ? ResourceQuantity.IdExpansionBehaviour.EXPAND
                : ResourceQuantity.IdExpansionBehaviour.NO_EXPAND
        );
        AST_NODE_CONTEXTS.add(new Pair<>(quantity, ctx));
        return quantity;
    }

    @Override
    public DirectionQualifier visitEachSide(SFMLParser.EachSideContext ctx) {
        var rtn = DirectionQualifier.EVERY_DIRECTION;
        AST_NODE_CONTEXTS.add(new Pair<>(rtn, ctx));
        return rtn;
    }

    @Override
    public DirectionQualifier visitListedSides(SFMLParser.ListedSidesContext ctx) {
        DirectionQualifier directionQualifier = new DirectionQualifier(
                EnumSet.copyOf(ctx.side().stream()
                                       .map(this::visitSide)
                                       .map(DirectionQualifier::lookup)
                                       .toList())
        );
        AST_NODE_CONTEXTS.add(new Pair<>(directionQualifier, ctx));
        return directionQualifier;
    }

    @Override
    public Side visitSide(SFMLParser.SideContext ctx) {
        Side side = Side.valueOf(ctx.getText().toUpperCase(Locale.ROOT));
        AST_NODE_CONTEXTS.add(new Pair<>(side, ctx));
        return side;
    }

    @Override
    public Block visitBlock(@Nullable SFMLParser.BlockContext ctx) {
        if (ctx == null) return new Block(Collections.emptyList());
        var statements = ctx
                .statement()
                .stream()
                .map(this::visit)
                .map(Statement.class::cast)
                .collect(Collectors.toList());
        Block block = new Block(statements);
        AST_NODE_CONTEXTS.add(new Pair<>(block, ctx));
        return block;
    }
}
