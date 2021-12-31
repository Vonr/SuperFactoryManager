// Generated from d:\Repos\Minecraft\Forge\SuperFactoryManager\src\main\antlr\SFML.g by ANTLR 4.8
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SFMLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		IF=1, THEN=2, ELSE=3, HAS=4, OVERALL=5, SOME=6, ONE=7, LONE=8, NO=9, TRUE=10, 
		FALSE=11, NOT=12, AND=13, OR=14, GT=15, LT=16, EQ=17, LE=18, GE=19, MOVE=20, 
		FROM=21, TO=22, INPUT=23, OUTPUT=24, WHERE=25, SLOTS=26, RETAIN=27, EACH=28, 
		TOP=29, BOTTOM=30, NORTH=31, EAST=32, SOUTH=33, WEST=34, SIDE=35, TICKS=36, 
		SECONDS=37, REDSTONE=38, PULSE=39, DO=40, WORLD=41, PROGRAM=42, END=43, 
		NAME=44, EVERY=45, COMMA=46, COLON=47, DASH=48, LPAREN=49, RPAREN=50, 
		IDENTIFIER=51, NUMBER=52, STRING=53, LINE_COMMENT=54, WS=55;
	public static final int
		RULE_program = 0, RULE_name = 1, RULE_trigger = 2, RULE_interval = 3, 
		RULE_block = 4, RULE_statement = 5, RULE_inputstatement = 6, RULE_outputstatement = 7, 
		RULE_inputmatchers = 8, RULE_outputmatchers = 9, RULE_itemmovement = 10, 
		RULE_itemlimit = 11, RULE_limit = 12, RULE_quantity = 13, RULE_retention = 14, 
		RULE_sidequalifier = 15, RULE_side = 16, RULE_slotqualifier = 17, RULE_rangeset = 18, 
		RULE_range = 19, RULE_ifstatement = 20, RULE_boolexpr = 21, RULE_itemcomparison = 22, 
		RULE_comparisonOp = 23, RULE_setOp = 24, RULE_labelaccess = 25, RULE_label = 26, 
		RULE_item = 27, RULE_string = 28, RULE_number = 29;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "name", "trigger", "interval", "block", "statement", "inputstatement", 
			"outputstatement", "inputmatchers", "outputmatchers", "itemmovement", 
			"itemlimit", "limit", "quantity", "retention", "sidequalifier", "side", 
			"slotqualifier", "rangeset", "range", "ifstatement", "boolexpr", "itemcomparison", 
			"comparisonOp", "setOp", "labelaccess", "label", "item", "string", "number"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, "','", "':'", 
			"'-'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "IF", "THEN", "ELSE", "HAS", "OVERALL", "SOME", "ONE", "LONE", 
			"NO", "TRUE", "FALSE", "NOT", "AND", "OR", "GT", "LT", "EQ", "LE", "GE", 
			"MOVE", "FROM", "TO", "INPUT", "OUTPUT", "WHERE", "SLOTS", "RETAIN", 
			"EACH", "TOP", "BOTTOM", "NORTH", "EAST", "SOUTH", "WEST", "SIDE", "TICKS", 
			"SECONDS", "REDSTONE", "PULSE", "DO", "WORLD", "PROGRAM", "END", "NAME", 
			"EVERY", "COMMA", "COLON", "DASH", "LPAREN", "RPAREN", "IDENTIFIER", 
			"NUMBER", "STRING", "LINE_COMMENT", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "SFML.g"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SFMLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ProgramContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public List<TriggerContext> trigger() {
			return getRuleContexts(TriggerContext.class);
		}
		public TriggerContext trigger(int i) {
			return getRuleContext(TriggerContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(61);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NAME) {
				{
				setState(60);
				name();
				}
			}

			setState(66);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EVERY) {
				{
				{
				setState(63);
				trigger();
				}
				}
				setState(68);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NameContext extends ParserRuleContext {
		public TerminalNode NAME() { return getToken(SFMLParser.NAME, 0); }
		public StringContext string() {
			return getRuleContext(StringContext.class,0);
		}
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			match(NAME);
			setState(70);
			string();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TriggerContext extends ParserRuleContext {
		public TriggerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_trigger; }
	 
		public TriggerContext() { }
		public void copyFrom(TriggerContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PulseTriggerContext extends TriggerContext {
		public TerminalNode EVERY() { return getToken(SFMLParser.EVERY, 0); }
		public TerminalNode REDSTONE() { return getToken(SFMLParser.REDSTONE, 0); }
		public TerminalNode PULSE() { return getToken(SFMLParser.PULSE, 0); }
		public TerminalNode DO() { return getToken(SFMLParser.DO, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public TerminalNode END() { return getToken(SFMLParser.END, 0); }
		public PulseTriggerContext(TriggerContext ctx) { copyFrom(ctx); }
	}
	public static class TimerTriggerContext extends TriggerContext {
		public TerminalNode EVERY() { return getToken(SFMLParser.EVERY, 0); }
		public IntervalContext interval() {
			return getRuleContext(IntervalContext.class,0);
		}
		public TerminalNode DO() { return getToken(SFMLParser.DO, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public TerminalNode END() { return getToken(SFMLParser.END, 0); }
		public TimerTriggerContext(TriggerContext ctx) { copyFrom(ctx); }
	}

	public final TriggerContext trigger() throws RecognitionException {
		TriggerContext _localctx = new TriggerContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_trigger);
		try {
			setState(85);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new TimerTriggerContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(72);
				match(EVERY);
				setState(73);
				interval();
				setState(74);
				match(DO);
				setState(75);
				block();
				setState(76);
				match(END);
				}
				break;
			case 2:
				_localctx = new PulseTriggerContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(78);
				match(EVERY);
				setState(79);
				match(REDSTONE);
				setState(80);
				match(PULSE);
				setState(81);
				match(DO);
				setState(82);
				block();
				setState(83);
				match(END);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntervalContext extends ParserRuleContext {
		public IntervalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interval; }
	 
		public IntervalContext() { }
		public void copyFrom(IntervalContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class TicksContext extends IntervalContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public TerminalNode TICKS() { return getToken(SFMLParser.TICKS, 0); }
		public TicksContext(IntervalContext ctx) { copyFrom(ctx); }
	}
	public static class SecondsContext extends IntervalContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public TerminalNode SECONDS() { return getToken(SFMLParser.SECONDS, 0); }
		public SecondsContext(IntervalContext ctx) { copyFrom(ctx); }
	}

	public final IntervalContext interval() throws RecognitionException {
		IntervalContext _localctx = new IntervalContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_interval);
		try {
			setState(93);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				_localctx = new TicksContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(87);
				number();
				setState(88);
				match(TICKS);
				}
				break;
			case 2:
				_localctx = new SecondsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(90);
				number();
				setState(91);
				match(SECONDS);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(98);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IF) | (1L << INPUT) | (1L << OUTPUT))) != 0)) {
				{
				{
				setState(95);
				statement();
				}
				}
				setState(100);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
	 
		public StatementContext() { }
		public void copyFrom(StatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class OutputStatementStatementContext extends StatementContext {
		public OutputstatementContext outputstatement() {
			return getRuleContext(OutputstatementContext.class,0);
		}
		public OutputStatementStatementContext(StatementContext ctx) { copyFrom(ctx); }
	}
	public static class InputStatementStatementContext extends StatementContext {
		public InputstatementContext inputstatement() {
			return getRuleContext(InputstatementContext.class,0);
		}
		public InputStatementStatementContext(StatementContext ctx) { copyFrom(ctx); }
	}
	public static class IfStatementStatementContext extends StatementContext {
		public IfstatementContext ifstatement() {
			return getRuleContext(IfstatementContext.class,0);
		}
		public IfStatementStatementContext(StatementContext ctx) { copyFrom(ctx); }
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_statement);
		try {
			setState(104);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INPUT:
				_localctx = new InputStatementStatementContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(101);
				inputstatement();
				}
				break;
			case OUTPUT:
				_localctx = new OutputStatementStatementContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(102);
				outputstatement();
				}
				break;
			case IF:
				_localctx = new IfStatementStatementContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(103);
				ifstatement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InputstatementContext extends ParserRuleContext {
		public TerminalNode INPUT() { return getToken(SFMLParser.INPUT, 0); }
		public TerminalNode FROM() { return getToken(SFMLParser.FROM, 0); }
		public LabelaccessContext labelaccess() {
			return getRuleContext(LabelaccessContext.class,0);
		}
		public InputmatchersContext inputmatchers() {
			return getRuleContext(InputmatchersContext.class,0);
		}
		public TerminalNode EACH() { return getToken(SFMLParser.EACH, 0); }
		public InputstatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inputstatement; }
	}

	public final InputstatementContext inputstatement() throws RecognitionException {
		InputstatementContext _localctx = new InputstatementContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_inputstatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106);
			match(INPUT);
			setState(108);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RETAIN) | (1L << IDENTIFIER) | (1L << NUMBER))) != 0)) {
				{
				setState(107);
				inputmatchers();
				}
			}

			setState(110);
			match(FROM);
			setState(112);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EACH) {
				{
				setState(111);
				match(EACH);
				}
			}

			setState(114);
			labelaccess();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OutputstatementContext extends ParserRuleContext {
		public TerminalNode OUTPUT() { return getToken(SFMLParser.OUTPUT, 0); }
		public TerminalNode TO() { return getToken(SFMLParser.TO, 0); }
		public LabelaccessContext labelaccess() {
			return getRuleContext(LabelaccessContext.class,0);
		}
		public OutputmatchersContext outputmatchers() {
			return getRuleContext(OutputmatchersContext.class,0);
		}
		public TerminalNode EACH() { return getToken(SFMLParser.EACH, 0); }
		public OutputstatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outputstatement; }
	}

	public final OutputstatementContext outputstatement() throws RecognitionException {
		OutputstatementContext _localctx = new OutputstatementContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_outputstatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(116);
			match(OUTPUT);
			setState(118);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RETAIN) | (1L << IDENTIFIER) | (1L << NUMBER))) != 0)) {
				{
				setState(117);
				outputmatchers();
				}
			}

			setState(120);
			match(TO);
			setState(122);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EACH) {
				{
				setState(121);
				match(EACH);
				}
			}

			setState(124);
			labelaccess();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InputmatchersContext extends ParserRuleContext {
		public ItemmovementContext itemmovement() {
			return getRuleContext(ItemmovementContext.class,0);
		}
		public InputmatchersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inputmatchers; }
	}

	public final InputmatchersContext inputmatchers() throws RecognitionException {
		InputmatchersContext _localctx = new InputmatchersContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_inputmatchers);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			itemmovement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OutputmatchersContext extends ParserRuleContext {
		public ItemmovementContext itemmovement() {
			return getRuleContext(ItemmovementContext.class,0);
		}
		public OutputmatchersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outputmatchers; }
	}

	public final OutputmatchersContext outputmatchers() throws RecognitionException {
		OutputmatchersContext _localctx = new OutputmatchersContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_outputmatchers);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
			itemmovement();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ItemmovementContext extends ParserRuleContext {
		public ItemmovementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_itemmovement; }
	 
		public ItemmovementContext() { }
		public void copyFrom(ItemmovementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LimitMovementContext extends ItemmovementContext {
		public LimitContext limit() {
			return getRuleContext(LimitContext.class,0);
		}
		public LimitMovementContext(ItemmovementContext ctx) { copyFrom(ctx); }
	}
	public static class ItemLimitMovementContext extends ItemmovementContext {
		public List<ItemlimitContext> itemlimit() {
			return getRuleContexts(ItemlimitContext.class);
		}
		public ItemlimitContext itemlimit(int i) {
			return getRuleContext(ItemlimitContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SFMLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SFMLParser.COMMA, i);
		}
		public ItemLimitMovementContext(ItemmovementContext ctx) { copyFrom(ctx); }
	}

	public final ItemmovementContext itemmovement() throws RecognitionException {
		ItemmovementContext _localctx = new ItemmovementContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_itemmovement);
		int _la;
		try {
			setState(139);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				_localctx = new ItemLimitMovementContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(130);
				itemlimit();
				setState(135);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(131);
					match(COMMA);
					setState(132);
					itemlimit();
					}
					}
					setState(137);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				_localctx = new LimitMovementContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(138);
				limit();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ItemlimitContext extends ParserRuleContext {
		public ItemContext item() {
			return getRuleContext(ItemContext.class,0);
		}
		public LimitContext limit() {
			return getRuleContext(LimitContext.class,0);
		}
		public ItemlimitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_itemlimit; }
	}

	public final ItemlimitContext itemlimit() throws RecognitionException {
		ItemlimitContext _localctx = new ItemlimitContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_itemlimit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(142);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RETAIN || _la==NUMBER) {
				{
				setState(141);
				limit();
				}
			}

			setState(144);
			item();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LimitContext extends ParserRuleContext {
		public LimitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_limit; }
	 
		public LimitContext() { }
		public void copyFrom(LimitContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RetentionLimitContext extends LimitContext {
		public RetentionContext retention() {
			return getRuleContext(RetentionContext.class,0);
		}
		public RetentionLimitContext(LimitContext ctx) { copyFrom(ctx); }
	}
	public static class QuantityRetentionLimitContext extends LimitContext {
		public QuantityContext quantity() {
			return getRuleContext(QuantityContext.class,0);
		}
		public RetentionContext retention() {
			return getRuleContext(RetentionContext.class,0);
		}
		public QuantityRetentionLimitContext(LimitContext ctx) { copyFrom(ctx); }
	}
	public static class QuantityLimitContext extends LimitContext {
		public QuantityContext quantity() {
			return getRuleContext(QuantityContext.class,0);
		}
		public QuantityLimitContext(LimitContext ctx) { copyFrom(ctx); }
	}

	public final LimitContext limit() throws RecognitionException {
		LimitContext _localctx = new LimitContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_limit);
		try {
			setState(151);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				_localctx = new QuantityRetentionLimitContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(146);
				quantity();
				setState(147);
				retention();
				}
				break;
			case 2:
				_localctx = new RetentionLimitContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(149);
				retention();
				}
				break;
			case 3:
				_localctx = new QuantityLimitContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(150);
				quantity();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class QuantityContext extends ParserRuleContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public QuantityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_quantity; }
	}

	public final QuantityContext quantity() throws RecognitionException {
		QuantityContext _localctx = new QuantityContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_quantity);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(153);
			number();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RetentionContext extends ParserRuleContext {
		public TerminalNode RETAIN() { return getToken(SFMLParser.RETAIN, 0); }
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public RetentionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_retention; }
	}

	public final RetentionContext retention() throws RecognitionException {
		RetentionContext _localctx = new RetentionContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_retention);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(155);
			match(RETAIN);
			setState(156);
			number();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SidequalifierContext extends ParserRuleContext {
		public List<SideContext> side() {
			return getRuleContexts(SideContext.class);
		}
		public SideContext side(int i) {
			return getRuleContext(SideContext.class,i);
		}
		public TerminalNode SIDE() { return getToken(SFMLParser.SIDE, 0); }
		public List<TerminalNode> COMMA() { return getTokens(SFMLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SFMLParser.COMMA, i);
		}
		public SidequalifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sidequalifier; }
	}

	public final SidequalifierContext sidequalifier() throws RecognitionException {
		SidequalifierContext _localctx = new SidequalifierContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_sidequalifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(158);
			side();
			setState(163);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(159);
				match(COMMA);
				setState(160);
				side();
				}
				}
				setState(165);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(166);
			match(SIDE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SideContext extends ParserRuleContext {
		public TerminalNode TOP() { return getToken(SFMLParser.TOP, 0); }
		public TerminalNode BOTTOM() { return getToken(SFMLParser.BOTTOM, 0); }
		public TerminalNode NORTH() { return getToken(SFMLParser.NORTH, 0); }
		public TerminalNode EAST() { return getToken(SFMLParser.EAST, 0); }
		public TerminalNode SOUTH() { return getToken(SFMLParser.SOUTH, 0); }
		public TerminalNode WEST() { return getToken(SFMLParser.WEST, 0); }
		public SideContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_side; }
	}

	public final SideContext side() throws RecognitionException {
		SideContext _localctx = new SideContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_side);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(168);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TOP) | (1L << BOTTOM) | (1L << NORTH) | (1L << EAST) | (1L << SOUTH) | (1L << WEST))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SlotqualifierContext extends ParserRuleContext {
		public TerminalNode SLOTS() { return getToken(SFMLParser.SLOTS, 0); }
		public RangesetContext rangeset() {
			return getRuleContext(RangesetContext.class,0);
		}
		public SlotqualifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_slotqualifier; }
	}

	public final SlotqualifierContext slotqualifier() throws RecognitionException {
		SlotqualifierContext _localctx = new SlotqualifierContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_slotqualifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			match(SLOTS);
			setState(171);
			rangeset();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RangesetContext extends ParserRuleContext {
		public List<RangeContext> range() {
			return getRuleContexts(RangeContext.class);
		}
		public RangeContext range(int i) {
			return getRuleContext(RangeContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SFMLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SFMLParser.COMMA, i);
		}
		public RangesetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rangeset; }
	}

	public final RangesetContext rangeset() throws RecognitionException {
		RangesetContext _localctx = new RangesetContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_rangeset);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(173);
			range();
			setState(178);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(174);
				match(COMMA);
				setState(175);
				range();
				}
				}
				setState(180);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RangeContext extends ParserRuleContext {
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
		}
		public TerminalNode DASH() { return getToken(SFMLParser.DASH, 0); }
		public RangeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_range; }
	}

	public final RangeContext range() throws RecognitionException {
		RangeContext _localctx = new RangeContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_range);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(181);
			number();
			setState(184);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DASH) {
				{
				setState(182);
				match(DASH);
				setState(183);
				number();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IfstatementContext extends ParserRuleContext {
		public List<TerminalNode> IF() { return getTokens(SFMLParser.IF); }
		public TerminalNode IF(int i) {
			return getToken(SFMLParser.IF, i);
		}
		public List<BoolexprContext> boolexpr() {
			return getRuleContexts(BoolexprContext.class);
		}
		public BoolexprContext boolexpr(int i) {
			return getRuleContext(BoolexprContext.class,i);
		}
		public List<TerminalNode> THEN() { return getTokens(SFMLParser.THEN); }
		public TerminalNode THEN(int i) {
			return getToken(SFMLParser.THEN, i);
		}
		public List<BlockContext> block() {
			return getRuleContexts(BlockContext.class);
		}
		public BlockContext block(int i) {
			return getRuleContext(BlockContext.class,i);
		}
		public TerminalNode END() { return getToken(SFMLParser.END, 0); }
		public List<TerminalNode> ELSE() { return getTokens(SFMLParser.ELSE); }
		public TerminalNode ELSE(int i) {
			return getToken(SFMLParser.ELSE, i);
		}
		public IfstatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifstatement; }
	}

	public final IfstatementContext ifstatement() throws RecognitionException {
		IfstatementContext _localctx = new IfstatementContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_ifstatement);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(186);
			match(IF);
			setState(187);
			boolexpr(0);
			setState(188);
			match(THEN);
			setState(189);
			block();
			setState(198);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(190);
					match(ELSE);
					setState(191);
					match(IF);
					setState(192);
					boolexpr(0);
					setState(193);
					match(THEN);
					setState(194);
					block();
					}
					} 
				}
				setState(200);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			}
			setState(203);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(201);
				match(ELSE);
				setState(202);
				block();
				}
			}

			setState(205);
			match(END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BoolexprContext extends ParserRuleContext {
		public BoolexprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolexpr; }
	 
		public BoolexprContext() { }
		public void copyFrom(BoolexprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class BooleanHasContext extends BoolexprContext {
		public LabelaccessContext labelaccess() {
			return getRuleContext(LabelaccessContext.class,0);
		}
		public TerminalNode HAS() { return getToken(SFMLParser.HAS, 0); }
		public ItemcomparisonContext itemcomparison() {
			return getRuleContext(ItemcomparisonContext.class,0);
		}
		public SetOpContext setOp() {
			return getRuleContext(SetOpContext.class,0);
		}
		public BooleanHasContext(BoolexprContext ctx) { copyFrom(ctx); }
	}
	public static class BooleanConjunctionContext extends BoolexprContext {
		public List<BoolexprContext> boolexpr() {
			return getRuleContexts(BoolexprContext.class);
		}
		public BoolexprContext boolexpr(int i) {
			return getRuleContext(BoolexprContext.class,i);
		}
		public TerminalNode AND() { return getToken(SFMLParser.AND, 0); }
		public BooleanConjunctionContext(BoolexprContext ctx) { copyFrom(ctx); }
	}
	public static class BooleanDisjunctionContext extends BoolexprContext {
		public List<BoolexprContext> boolexpr() {
			return getRuleContexts(BoolexprContext.class);
		}
		public BoolexprContext boolexpr(int i) {
			return getRuleContext(BoolexprContext.class,i);
		}
		public TerminalNode OR() { return getToken(SFMLParser.OR, 0); }
		public BooleanDisjunctionContext(BoolexprContext ctx) { copyFrom(ctx); }
	}
	public static class BooleanFalseContext extends BoolexprContext {
		public TerminalNode FALSE() { return getToken(SFMLParser.FALSE, 0); }
		public BooleanFalseContext(BoolexprContext ctx) { copyFrom(ctx); }
	}
	public static class BooleanParenContext extends BoolexprContext {
		public TerminalNode LPAREN() { return getToken(SFMLParser.LPAREN, 0); }
		public BoolexprContext boolexpr() {
			return getRuleContext(BoolexprContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(SFMLParser.RPAREN, 0); }
		public BooleanParenContext(BoolexprContext ctx) { copyFrom(ctx); }
	}
	public static class BooleanNegationContext extends BoolexprContext {
		public TerminalNode NOT() { return getToken(SFMLParser.NOT, 0); }
		public BoolexprContext boolexpr() {
			return getRuleContext(BoolexprContext.class,0);
		}
		public BooleanNegationContext(BoolexprContext ctx) { copyFrom(ctx); }
	}
	public static class BooleanTrueContext extends BoolexprContext {
		public TerminalNode TRUE() { return getToken(SFMLParser.TRUE, 0); }
		public BooleanTrueContext(BoolexprContext ctx) { copyFrom(ctx); }
	}

	public final BoolexprContext boolexpr() throws RecognitionException {
		return boolexpr(0);
	}

	private BoolexprContext boolexpr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		BoolexprContext _localctx = new BoolexprContext(_ctx, _parentState);
		BoolexprContext _prevctx = _localctx;
		int _startState = 42;
		enterRecursionRule(_localctx, 42, RULE_boolexpr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(223);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TRUE:
				{
				_localctx = new BooleanTrueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(208);
				match(TRUE);
				}
				break;
			case FALSE:
				{
				_localctx = new BooleanFalseContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(209);
				match(FALSE);
				}
				break;
			case LPAREN:
				{
				_localctx = new BooleanParenContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(210);
				match(LPAREN);
				setState(211);
				boolexpr(0);
				setState(212);
				match(RPAREN);
				}
				break;
			case NOT:
				{
				_localctx = new BooleanNegationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(214);
				match(NOT);
				setState(215);
				boolexpr(4);
				}
				break;
			case OVERALL:
			case SOME:
			case ONE:
			case LONE:
			case EVERY:
			case IDENTIFIER:
				{
				_localctx = new BooleanHasContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(217);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OVERALL) | (1L << SOME) | (1L << ONE) | (1L << LONE) | (1L << EVERY))) != 0)) {
					{
					setState(216);
					setOp();
					}
				}

				setState(219);
				labelaccess();
				setState(220);
				match(HAS);
				setState(221);
				itemcomparison();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(233);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(231);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
					case 1:
						{
						_localctx = new BooleanConjunctionContext(new BoolexprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_boolexpr);
						setState(225);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(226);
						match(AND);
						setState(227);
						boolexpr(4);
						}
						break;
					case 2:
						{
						_localctx = new BooleanDisjunctionContext(new BoolexprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_boolexpr);
						setState(228);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(229);
						match(OR);
						setState(230);
						boolexpr(3);
						}
						break;
					}
					} 
				}
				setState(235);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ItemcomparisonContext extends ParserRuleContext {
		public ComparisonOpContext comparisonOp() {
			return getRuleContext(ComparisonOpContext.class,0);
		}
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public ItemContext item() {
			return getRuleContext(ItemContext.class,0);
		}
		public ItemcomparisonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_itemcomparison; }
	}

	public final ItemcomparisonContext itemcomparison() throws RecognitionException {
		ItemcomparisonContext _localctx = new ItemcomparisonContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_itemcomparison);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(236);
			comparisonOp();
			setState(237);
			number();
			setState(238);
			item();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ComparisonOpContext extends ParserRuleContext {
		public TerminalNode GT() { return getToken(SFMLParser.GT, 0); }
		public TerminalNode LT() { return getToken(SFMLParser.LT, 0); }
		public TerminalNode EQ() { return getToken(SFMLParser.EQ, 0); }
		public TerminalNode LE() { return getToken(SFMLParser.LE, 0); }
		public TerminalNode GE() { return getToken(SFMLParser.GE, 0); }
		public ComparisonOpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparisonOp; }
	}

	public final ComparisonOpContext comparisonOp() throws RecognitionException {
		ComparisonOpContext _localctx = new ComparisonOpContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_comparisonOp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(240);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GT) | (1L << LT) | (1L << EQ) | (1L << LE) | (1L << GE))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SetOpContext extends ParserRuleContext {
		public TerminalNode OVERALL() { return getToken(SFMLParser.OVERALL, 0); }
		public TerminalNode SOME() { return getToken(SFMLParser.SOME, 0); }
		public TerminalNode EVERY() { return getToken(SFMLParser.EVERY, 0); }
		public TerminalNode ONE() { return getToken(SFMLParser.ONE, 0); }
		public TerminalNode LONE() { return getToken(SFMLParser.LONE, 0); }
		public SetOpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_setOp; }
	}

	public final SetOpContext setOp() throws RecognitionException {
		SetOpContext _localctx = new SetOpContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_setOp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(242);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OVERALL) | (1L << SOME) | (1L << ONE) | (1L << LONE) | (1L << EVERY))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LabelaccessContext extends ParserRuleContext {
		public List<LabelContext> label() {
			return getRuleContexts(LabelContext.class);
		}
		public LabelContext label(int i) {
			return getRuleContext(LabelContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SFMLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SFMLParser.COMMA, i);
		}
		public SidequalifierContext sidequalifier() {
			return getRuleContext(SidequalifierContext.class,0);
		}
		public SlotqualifierContext slotqualifier() {
			return getRuleContext(SlotqualifierContext.class,0);
		}
		public LabelaccessContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_labelaccess; }
	}

	public final LabelaccessContext labelaccess() throws RecognitionException {
		LabelaccessContext _localctx = new LabelaccessContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_labelaccess);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(244);
			label();
			setState(249);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(245);
				match(COMMA);
				setState(246);
				label();
				}
				}
				setState(251);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(253);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TOP) | (1L << BOTTOM) | (1L << NORTH) | (1L << EAST) | (1L << SOUTH) | (1L << WEST))) != 0)) {
				{
				setState(252);
				sidequalifier();
				}
			}

			setState(256);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SLOTS) {
				{
				setState(255);
				slotqualifier();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LabelContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SFMLParser.IDENTIFIER, 0); }
		public LabelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label; }
	}

	public final LabelContext label() throws RecognitionException {
		LabelContext _localctx = new LabelContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_label);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(258);
			match(IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ItemContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(SFMLParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(SFMLParser.IDENTIFIER, i);
		}
		public TerminalNode COLON() { return getToken(SFMLParser.COLON, 0); }
		public ItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_item; }
	}

	public final ItemContext item() throws RecognitionException {
		ItemContext _localctx = new ItemContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_item);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(260);
			match(IDENTIFIER);
			setState(263);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				{
				setState(261);
				match(COLON);
				setState(262);
				match(IDENTIFIER);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StringContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(SFMLParser.STRING, 0); }
		public StringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string; }
	}

	public final StringContext string() throws RecognitionException {
		StringContext _localctx = new StringContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_string);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(265);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumberContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(SFMLParser.NUMBER, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(267);
			match(NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 21:
			return boolexpr_sempred((BoolexprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean boolexpr_sempred(BoolexprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 3);
		case 1:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\39\u0110\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\3\2\5\2@"+
		"\n\2\3\2\7\2C\n\2\f\2\16\2F\13\2\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4X\n\4\3\5\3\5\3\5\3\5\3\5\3\5\5\5`\n\5\3"+
		"\6\7\6c\n\6\f\6\16\6f\13\6\3\7\3\7\3\7\5\7k\n\7\3\b\3\b\5\bo\n\b\3\b\3"+
		"\b\5\bs\n\b\3\b\3\b\3\t\3\t\5\ty\n\t\3\t\3\t\5\t}\n\t\3\t\3\t\3\n\3\n"+
		"\3\13\3\13\3\f\3\f\3\f\7\f\u0088\n\f\f\f\16\f\u008b\13\f\3\f\5\f\u008e"+
		"\n\f\3\r\5\r\u0091\n\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\5\16\u009a\n\16"+
		"\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\21\7\21\u00a4\n\21\f\21\16\21\u00a7"+
		"\13\21\3\21\3\21\3\22\3\22\3\23\3\23\3\23\3\24\3\24\3\24\7\24\u00b3\n"+
		"\24\f\24\16\24\u00b6\13\24\3\25\3\25\3\25\5\25\u00bb\n\25\3\26\3\26\3"+
		"\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\7\26\u00c7\n\26\f\26\16\26\u00ca"+
		"\13\26\3\26\3\26\5\26\u00ce\n\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3"+
		"\27\3\27\3\27\3\27\3\27\5\27\u00dc\n\27\3\27\3\27\3\27\3\27\5\27\u00e2"+
		"\n\27\3\27\3\27\3\27\3\27\3\27\3\27\7\27\u00ea\n\27\f\27\16\27\u00ed\13"+
		"\27\3\30\3\30\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\33\7\33\u00fa"+
		"\n\33\f\33\16\33\u00fd\13\33\3\33\5\33\u0100\n\33\3\33\5\33\u0103\n\33"+
		"\3\34\3\34\3\35\3\35\3\35\5\35\u010a\n\35\3\36\3\36\3\37\3\37\3\37\2\3"+
		", \2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<\2\5"+
		"\3\2\37$\3\2\21\25\4\2\7\n//\2\u0111\2?\3\2\2\2\4G\3\2\2\2\6W\3\2\2\2"+
		"\b_\3\2\2\2\nd\3\2\2\2\fj\3\2\2\2\16l\3\2\2\2\20v\3\2\2\2\22\u0080\3\2"+
		"\2\2\24\u0082\3\2\2\2\26\u008d\3\2\2\2\30\u0090\3\2\2\2\32\u0099\3\2\2"+
		"\2\34\u009b\3\2\2\2\36\u009d\3\2\2\2 \u00a0\3\2\2\2\"\u00aa\3\2\2\2$\u00ac"+
		"\3\2\2\2&\u00af\3\2\2\2(\u00b7\3\2\2\2*\u00bc\3\2\2\2,\u00e1\3\2\2\2."+
		"\u00ee\3\2\2\2\60\u00f2\3\2\2\2\62\u00f4\3\2\2\2\64\u00f6\3\2\2\2\66\u0104"+
		"\3\2\2\28\u0106\3\2\2\2:\u010b\3\2\2\2<\u010d\3\2\2\2>@\5\4\3\2?>\3\2"+
		"\2\2?@\3\2\2\2@D\3\2\2\2AC\5\6\4\2BA\3\2\2\2CF\3\2\2\2DB\3\2\2\2DE\3\2"+
		"\2\2E\3\3\2\2\2FD\3\2\2\2GH\7.\2\2HI\5:\36\2I\5\3\2\2\2JK\7/\2\2KL\5\b"+
		"\5\2LM\7*\2\2MN\5\n\6\2NO\7-\2\2OX\3\2\2\2PQ\7/\2\2QR\7(\2\2RS\7)\2\2"+
		"ST\7*\2\2TU\5\n\6\2UV\7-\2\2VX\3\2\2\2WJ\3\2\2\2WP\3\2\2\2X\7\3\2\2\2"+
		"YZ\5<\37\2Z[\7&\2\2[`\3\2\2\2\\]\5<\37\2]^\7\'\2\2^`\3\2\2\2_Y\3\2\2\2"+
		"_\\\3\2\2\2`\t\3\2\2\2ac\5\f\7\2ba\3\2\2\2cf\3\2\2\2db\3\2\2\2de\3\2\2"+
		"\2e\13\3\2\2\2fd\3\2\2\2gk\5\16\b\2hk\5\20\t\2ik\5*\26\2jg\3\2\2\2jh\3"+
		"\2\2\2ji\3\2\2\2k\r\3\2\2\2ln\7\31\2\2mo\5\22\n\2nm\3\2\2\2no\3\2\2\2"+
		"op\3\2\2\2pr\7\27\2\2qs\7\36\2\2rq\3\2\2\2rs\3\2\2\2st\3\2\2\2tu\5\64"+
		"\33\2u\17\3\2\2\2vx\7\32\2\2wy\5\24\13\2xw\3\2\2\2xy\3\2\2\2yz\3\2\2\2"+
		"z|\7\30\2\2{}\7\36\2\2|{\3\2\2\2|}\3\2\2\2}~\3\2\2\2~\177\5\64\33\2\177"+
		"\21\3\2\2\2\u0080\u0081\5\26\f\2\u0081\23\3\2\2\2\u0082\u0083\5\26\f\2"+
		"\u0083\25\3\2\2\2\u0084\u0089\5\30\r\2\u0085\u0086\7\60\2\2\u0086\u0088"+
		"\5\30\r\2\u0087\u0085\3\2\2\2\u0088\u008b\3\2\2\2\u0089\u0087\3\2\2\2"+
		"\u0089\u008a\3\2\2\2\u008a\u008e\3\2\2\2\u008b\u0089\3\2\2\2\u008c\u008e"+
		"\5\32\16\2\u008d\u0084\3\2\2\2\u008d\u008c\3\2\2\2\u008e\27\3\2\2\2\u008f"+
		"\u0091\5\32\16\2\u0090\u008f\3\2\2\2\u0090\u0091\3\2\2\2\u0091\u0092\3"+
		"\2\2\2\u0092\u0093\58\35\2\u0093\31\3\2\2\2\u0094\u0095\5\34\17\2\u0095"+
		"\u0096\5\36\20\2\u0096\u009a\3\2\2\2\u0097\u009a\5\36\20\2\u0098\u009a"+
		"\5\34\17\2\u0099\u0094\3\2\2\2\u0099\u0097\3\2\2\2\u0099\u0098\3\2\2\2"+
		"\u009a\33\3\2\2\2\u009b\u009c\5<\37\2\u009c\35\3\2\2\2\u009d\u009e\7\35"+
		"\2\2\u009e\u009f\5<\37\2\u009f\37\3\2\2\2\u00a0\u00a5\5\"\22\2\u00a1\u00a2"+
		"\7\60\2\2\u00a2\u00a4\5\"\22\2\u00a3\u00a1\3\2\2\2\u00a4\u00a7\3\2\2\2"+
		"\u00a5\u00a3\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\u00a8\3\2\2\2\u00a7\u00a5"+
		"\3\2\2\2\u00a8\u00a9\7%\2\2\u00a9!\3\2\2\2\u00aa\u00ab\t\2\2\2\u00ab#"+
		"\3\2\2\2\u00ac\u00ad\7\34\2\2\u00ad\u00ae\5&\24\2\u00ae%\3\2\2\2\u00af"+
		"\u00b4\5(\25\2\u00b0\u00b1\7\60\2\2\u00b1\u00b3\5(\25\2\u00b2\u00b0\3"+
		"\2\2\2\u00b3\u00b6\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5"+
		"\'\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b7\u00ba\5<\37\2\u00b8\u00b9\7\62\2"+
		"\2\u00b9\u00bb\5<\37\2\u00ba\u00b8\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb)"+
		"\3\2\2\2\u00bc\u00bd\7\3\2\2\u00bd\u00be\5,\27\2\u00be\u00bf\7\4\2\2\u00bf"+
		"\u00c8\5\n\6\2\u00c0\u00c1\7\5\2\2\u00c1\u00c2\7\3\2\2\u00c2\u00c3\5,"+
		"\27\2\u00c3\u00c4\7\4\2\2\u00c4\u00c5\5\n\6\2\u00c5\u00c7\3\2\2\2\u00c6"+
		"\u00c0\3\2\2\2\u00c7\u00ca\3\2\2\2\u00c8\u00c6\3\2\2\2\u00c8\u00c9\3\2"+
		"\2\2\u00c9\u00cd\3\2\2\2\u00ca\u00c8\3\2\2\2\u00cb\u00cc\7\5\2\2\u00cc"+
		"\u00ce\5\n\6\2\u00cd\u00cb\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce\u00cf\3\2"+
		"\2\2\u00cf\u00d0\7-\2\2\u00d0+\3\2\2\2\u00d1\u00d2\b\27\1\2\u00d2\u00e2"+
		"\7\f\2\2\u00d3\u00e2\7\r\2\2\u00d4\u00d5\7\63\2\2\u00d5\u00d6\5,\27\2"+
		"\u00d6\u00d7\7\64\2\2\u00d7\u00e2\3\2\2\2\u00d8\u00d9\7\16\2\2\u00d9\u00e2"+
		"\5,\27\6\u00da\u00dc\5\62\32\2\u00db\u00da\3\2\2\2\u00db\u00dc\3\2\2\2"+
		"\u00dc\u00dd\3\2\2\2\u00dd\u00de\5\64\33\2\u00de\u00df\7\6\2\2\u00df\u00e0"+
		"\5.\30\2\u00e0\u00e2\3\2\2\2\u00e1\u00d1\3\2\2\2\u00e1\u00d3\3\2\2\2\u00e1"+
		"\u00d4\3\2\2\2\u00e1\u00d8\3\2\2\2\u00e1\u00db\3\2\2\2\u00e2\u00eb\3\2"+
		"\2\2\u00e3\u00e4\f\5\2\2\u00e4\u00e5\7\17\2\2\u00e5\u00ea\5,\27\6\u00e6"+
		"\u00e7\f\4\2\2\u00e7\u00e8\7\20\2\2\u00e8\u00ea\5,\27\5\u00e9\u00e3\3"+
		"\2\2\2\u00e9\u00e6\3\2\2\2\u00ea\u00ed\3\2\2\2\u00eb\u00e9\3\2\2\2\u00eb"+
		"\u00ec\3\2\2\2\u00ec-\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ee\u00ef\5\60\31"+
		"\2\u00ef\u00f0\5<\37\2\u00f0\u00f1\58\35\2\u00f1/\3\2\2\2\u00f2\u00f3"+
		"\t\3\2\2\u00f3\61\3\2\2\2\u00f4\u00f5\t\4\2\2\u00f5\63\3\2\2\2\u00f6\u00fb"+
		"\5\66\34\2\u00f7\u00f8\7\60\2\2\u00f8\u00fa\5\66\34\2\u00f9\u00f7\3\2"+
		"\2\2\u00fa\u00fd\3\2\2\2\u00fb\u00f9\3\2\2\2\u00fb\u00fc\3\2\2\2\u00fc"+
		"\u00ff\3\2\2\2\u00fd\u00fb\3\2\2\2\u00fe\u0100\5 \21\2\u00ff\u00fe\3\2"+
		"\2\2\u00ff\u0100\3\2\2\2\u0100\u0102\3\2\2\2\u0101\u0103\5$\23\2\u0102"+
		"\u0101\3\2\2\2\u0102\u0103\3\2\2\2\u0103\65\3\2\2\2\u0104\u0105\7\65\2"+
		"\2\u0105\67\3\2\2\2\u0106\u0109\7\65\2\2\u0107\u0108\7\61\2\2\u0108\u010a"+
		"\7\65\2\2\u0109\u0107\3\2\2\2\u0109\u010a\3\2\2\2\u010a9\3\2\2\2\u010b"+
		"\u010c\7\67\2\2\u010c;\3\2\2\2\u010d\u010e\7\66\2\2\u010e=\3\2\2\2\35"+
		"?DW_djnrx|\u0089\u008d\u0090\u0099\u00a5\u00b4\u00ba\u00c8\u00cd\u00db"+
		"\u00e1\u00e9\u00eb\u00fb\u00ff\u0102\u0109";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}