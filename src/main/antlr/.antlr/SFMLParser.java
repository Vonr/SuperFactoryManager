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
		IF=1, THEN=2, MOVE=3, FROM=4, TO=5, INPUT=6, OUTPUT=7, WHERE=8, SLOTS=9, 
		RETAIN=10, EACH=11, TOP=12, BOTTOM=13, NORTH=14, EAST=15, SOUTH=16, WEST=17, 
		SIDE=18, SELF=19, TICKS=20, SECONDS=21, EVERY=22, REDSTONE=23, PULSE=24, 
		DO=25, WORLD=26, PROGRAM=27, END=28, NAME=29, COMMA=30, COLON=31, DASH=32, 
		IDENTIFIER=33, NUMBER=34, STRING=35, LINE_COMMENT=36, WS=37;
	public static final int
		RULE_program = 0, RULE_name = 1, RULE_trigger = 2, RULE_interval = 3, 
		RULE_block = 4, RULE_statement = 5, RULE_inputstatement = 6, RULE_outputstatement = 7, 
		RULE_ifstatement = 8, RULE_condition = 9, RULE_inputmatchers = 10, RULE_outputmatchers = 11, 
		RULE_itemlimit = 12, RULE_limit = 13, RULE_item = 14, RULE_quantity = 15, 
		RULE_retention = 16, RULE_sidequalifier = 17, RULE_side = 18, RULE_slotqualifier = 19, 
		RULE_rangeset = 20, RULE_range = 21, RULE_string = 22, RULE_number = 23, 
		RULE_label = 24;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "name", "trigger", "interval", "block", "statement", "inputstatement", 
			"outputstatement", "ifstatement", "condition", "inputmatchers", "outputmatchers", 
			"itemlimit", "limit", "item", "quantity", "retention", "sidequalifier", 
			"side", "slotqualifier", "rangeset", "range", "string", "number", "label"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, "','", "':'", "'-'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "IF", "THEN", "MOVE", "FROM", "TO", "INPUT", "OUTPUT", "WHERE", 
			"SLOTS", "RETAIN", "EACH", "TOP", "BOTTOM", "NORTH", "EAST", "SOUTH", 
			"WEST", "SIDE", "SELF", "TICKS", "SECONDS", "EVERY", "REDSTONE", "PULSE", 
			"DO", "WORLD", "PROGRAM", "END", "NAME", "COMMA", "COLON", "DASH", "IDENTIFIER", 
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
			setState(51);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NAME) {
				{
				setState(50);
				name();
				}
			}

			setState(56);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EVERY) {
				{
				{
				setState(53);
				trigger();
				}
				}
				setState(58);
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
			setState(59);
			match(NAME);
			setState(60);
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
			setState(75);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new TimerTriggerContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(62);
				match(EVERY);
				setState(63);
				interval();
				setState(64);
				match(DO);
				setState(65);
				block();
				setState(66);
				match(END);
				}
				break;
			case 2:
				_localctx = new PulseTriggerContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(68);
				match(EVERY);
				setState(69);
				match(REDSTONE);
				setState(70);
				match(PULSE);
				setState(71);
				match(DO);
				setState(72);
				block();
				setState(73);
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
			setState(83);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				_localctx = new TicksContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(77);
				number();
				setState(78);
				match(TICKS);
				}
				break;
			case 2:
				_localctx = new SecondsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(80);
				number();
				setState(81);
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
			setState(88);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IF) | (1L << INPUT) | (1L << OUTPUT))) != 0)) {
				{
				{
				setState(85);
				statement();
				}
				}
				setState(90);
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
			setState(94);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INPUT:
				_localctx = new InputStatementStatementContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(91);
				inputstatement();
				}
				break;
			case OUTPUT:
				_localctx = new OutputStatementStatementContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(92);
				outputstatement();
				}
				break;
			case IF:
				_localctx = new IfStatementStatementContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(93);
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
		public InputmatchersContext inputmatchers() {
			return getRuleContext(InputmatchersContext.class,0);
		}
		public TerminalNode EACH() { return getToken(SFMLParser.EACH, 0); }
		public List<LabelContext> label() {
			return getRuleContexts(LabelContext.class);
		}
		public LabelContext label(int i) {
			return getRuleContext(LabelContext.class,i);
		}
		public SidequalifierContext sidequalifier() {
			return getRuleContext(SidequalifierContext.class,0);
		}
		public SlotqualifierContext slotqualifier() {
			return getRuleContext(SlotqualifierContext.class,0);
		}
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
			setState(96);
			match(INPUT);
			setState(98);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RETAIN) | (1L << IDENTIFIER) | (1L << NUMBER))) != 0)) {
				{
				setState(97);
				inputmatchers();
				}
			}

			setState(100);
			match(FROM);
			setState(102);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EACH) {
				{
				setState(101);
				match(EACH);
				}
			}

			setState(105); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(104);
				label();
				}
				}
				setState(107); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(110);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TOP) | (1L << BOTTOM) | (1L << NORTH) | (1L << EAST) | (1L << SOUTH) | (1L << WEST))) != 0)) {
				{
				setState(109);
				sidequalifier();
				}
			}

			setState(113);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SLOTS) {
				{
				setState(112);
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

	public static class OutputstatementContext extends ParserRuleContext {
		public TerminalNode OUTPUT() { return getToken(SFMLParser.OUTPUT, 0); }
		public TerminalNode TO() { return getToken(SFMLParser.TO, 0); }
		public OutputmatchersContext outputmatchers() {
			return getRuleContext(OutputmatchersContext.class,0);
		}
		public TerminalNode EACH() { return getToken(SFMLParser.EACH, 0); }
		public List<LabelContext> label() {
			return getRuleContexts(LabelContext.class);
		}
		public LabelContext label(int i) {
			return getRuleContext(LabelContext.class,i);
		}
		public SidequalifierContext sidequalifier() {
			return getRuleContext(SidequalifierContext.class,0);
		}
		public SlotqualifierContext slotqualifier() {
			return getRuleContext(SlotqualifierContext.class,0);
		}
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
			setState(115);
			match(OUTPUT);
			setState(117);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RETAIN) | (1L << IDENTIFIER) | (1L << NUMBER))) != 0)) {
				{
				setState(116);
				outputmatchers();
				}
			}

			setState(119);
			match(TO);
			setState(121);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EACH) {
				{
				setState(120);
				match(EACH);
				}
			}

			setState(124); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(123);
				label();
				}
				}
				setState(126); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(129);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TOP) | (1L << BOTTOM) | (1L << NORTH) | (1L << EAST) | (1L << SOUTH) | (1L << WEST))) != 0)) {
				{
				setState(128);
				sidequalifier();
				}
			}

			setState(132);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SLOTS) {
				{
				setState(131);
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

	public static class IfstatementContext extends ParserRuleContext {
		public TerminalNode IF() { return getToken(SFMLParser.IF, 0); }
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public TerminalNode THEN() { return getToken(SFMLParser.THEN, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public TerminalNode END() { return getToken(SFMLParser.END, 0); }
		public IfstatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifstatement; }
	}

	public final IfstatementContext ifstatement() throws RecognitionException {
		IfstatementContext _localctx = new IfstatementContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_ifstatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(134);
			match(IF);
			setState(135);
			condition();
			setState(136);
			match(THEN);
			setState(137);
			block();
			setState(138);
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

	public static class ConditionContext extends ParserRuleContext {
		public ConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition; }
	}

	public final ConditionContext condition() throws RecognitionException {
		ConditionContext _localctx = new ConditionContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
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
		public LimitContext limit() {
			return getRuleContext(LimitContext.class,0);
		}
		public List<ItemContext> item() {
			return getRuleContexts(ItemContext.class);
		}
		public ItemContext item(int i) {
			return getRuleContext(ItemContext.class,i);
		}
		public InputmatchersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inputmatchers; }
	}

	public final InputmatchersContext inputmatchers() throws RecognitionException {
		InputmatchersContext _localctx = new InputmatchersContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_inputmatchers);
		int _la;
		try {
			setState(159);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(142);
				itemlimit();
				setState(147);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(143);
					match(COMMA);
					setState(144);
					itemlimit();
					}
					}
					setState(149);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(150);
				limit();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(151);
				item();
				setState(156);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(152);
					match(COMMA);
					setState(153);
					item();
					}
					}
					setState(158);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
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

	public static class OutputmatchersContext extends ParserRuleContext {
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
		public LimitContext limit() {
			return getRuleContext(LimitContext.class,0);
		}
		public List<ItemContext> item() {
			return getRuleContexts(ItemContext.class);
		}
		public ItemContext item(int i) {
			return getRuleContext(ItemContext.class,i);
		}
		public OutputmatchersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outputmatchers; }
	}

	public final OutputmatchersContext outputmatchers() throws RecognitionException {
		OutputmatchersContext _localctx = new OutputmatchersContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_outputmatchers);
		int _la;
		try {
			setState(178);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(161);
				itemlimit();
				setState(166);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(162);
					match(COMMA);
					setState(163);
					itemlimit();
					}
					}
					setState(168);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(169);
				limit();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(170);
				item();
				setState(175);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(171);
					match(COMMA);
					setState(172);
					item();
					}
					}
					setState(177);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
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
		public LimitContext limit() {
			return getRuleContext(LimitContext.class,0);
		}
		public ItemContext item() {
			return getRuleContext(ItemContext.class,0);
		}
		public ItemlimitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_itemlimit; }
	}

	public final ItemlimitContext itemlimit() throws RecognitionException {
		ItemlimitContext _localctx = new ItemlimitContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_itemlimit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180);
			limit();
			setState(181);
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
		enterRule(_localctx, 26, RULE_limit);
		try {
			setState(188);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				_localctx = new QuantityRetentionLimitContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(183);
				quantity();
				setState(184);
				retention();
				}
				break;
			case 2:
				_localctx = new RetentionLimitContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(186);
				retention();
				}
				break;
			case 3:
				_localctx = new QuantityLimitContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(187);
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
		enterRule(_localctx, 28, RULE_item);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(190);
			match(IDENTIFIER);
			setState(193);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(191);
				match(COLON);
				setState(192);
				match(IDENTIFIER);
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
		enterRule(_localctx, 30, RULE_quantity);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195);
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
		enterRule(_localctx, 32, RULE_retention);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(197);
			match(RETAIN);
			setState(198);
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
		enterRule(_localctx, 34, RULE_sidequalifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(200);
			side();
			setState(205);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(201);
				match(COMMA);
				setState(202);
				side();
				}
				}
				setState(207);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(208);
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
		enterRule(_localctx, 36, RULE_side);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(210);
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
		enterRule(_localctx, 38, RULE_slotqualifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(212);
			match(SLOTS);
			setState(213);
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
		enterRule(_localctx, 40, RULE_rangeset);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(215);
			range();
			setState(220);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(216);
				match(COMMA);
				setState(217);
				range();
				}
				}
				setState(222);
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
		enterRule(_localctx, 42, RULE_range);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(223);
			number();
			setState(226);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DASH) {
				{
				setState(224);
				match(DASH);
				setState(225);
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

	public static class StringContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(SFMLParser.STRING, 0); }
		public StringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string; }
	}

	public final StringContext string() throws RecognitionException {
		StringContext _localctx = new StringContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_string);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(228);
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
		enterRule(_localctx, 46, RULE_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(230);
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

	public static class LabelContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SFMLParser.IDENTIFIER, 0); }
		public LabelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label; }
	}

	public final LabelContext label() throws RecognitionException {
		LabelContext _localctx = new LabelContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_label);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(232);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\'\u00ed\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\3\2\5\2\66\n\2\3\2\7\29\n\2\f\2\16\2<\13\2\3\3\3\3\3\3\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4N\n\4\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\5\5V\n\5\3\6\7\6Y\n\6\f\6\16\6\\\13\6\3\7\3\7\3\7\5\7a\n"+
		"\7\3\b\3\b\5\be\n\b\3\b\3\b\5\bi\n\b\3\b\6\bl\n\b\r\b\16\bm\3\b\5\bq\n"+
		"\b\3\b\5\bt\n\b\3\t\3\t\5\tx\n\t\3\t\3\t\5\t|\n\t\3\t\6\t\177\n\t\r\t"+
		"\16\t\u0080\3\t\5\t\u0084\n\t\3\t\5\t\u0087\n\t\3\n\3\n\3\n\3\n\3\n\3"+
		"\n\3\13\3\13\3\f\3\f\3\f\7\f\u0094\n\f\f\f\16\f\u0097\13\f\3\f\3\f\3\f"+
		"\3\f\7\f\u009d\n\f\f\f\16\f\u00a0\13\f\5\f\u00a2\n\f\3\r\3\r\3\r\7\r\u00a7"+
		"\n\r\f\r\16\r\u00aa\13\r\3\r\3\r\3\r\3\r\7\r\u00b0\n\r\f\r\16\r\u00b3"+
		"\13\r\5\r\u00b5\n\r\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\5\17\u00bf"+
		"\n\17\3\20\3\20\3\20\5\20\u00c4\n\20\3\21\3\21\3\22\3\22\3\22\3\23\3\23"+
		"\3\23\7\23\u00ce\n\23\f\23\16\23\u00d1\13\23\3\23\3\23\3\24\3\24\3\25"+
		"\3\25\3\25\3\26\3\26\3\26\7\26\u00dd\n\26\f\26\16\26\u00e0\13\26\3\27"+
		"\3\27\3\27\5\27\u00e5\n\27\3\30\3\30\3\31\3\31\3\32\3\32\3\32\2\2\33\2"+
		"\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\2\3\3\2\16\23\2\u00f2"+
		"\2\65\3\2\2\2\4=\3\2\2\2\6M\3\2\2\2\bU\3\2\2\2\nZ\3\2\2\2\f`\3\2\2\2\16"+
		"b\3\2\2\2\20u\3\2\2\2\22\u0088\3\2\2\2\24\u008e\3\2\2\2\26\u00a1\3\2\2"+
		"\2\30\u00b4\3\2\2\2\32\u00b6\3\2\2\2\34\u00be\3\2\2\2\36\u00c0\3\2\2\2"+
		" \u00c5\3\2\2\2\"\u00c7\3\2\2\2$\u00ca\3\2\2\2&\u00d4\3\2\2\2(\u00d6\3"+
		"\2\2\2*\u00d9\3\2\2\2,\u00e1\3\2\2\2.\u00e6\3\2\2\2\60\u00e8\3\2\2\2\62"+
		"\u00ea\3\2\2\2\64\66\5\4\3\2\65\64\3\2\2\2\65\66\3\2\2\2\66:\3\2\2\2\67"+
		"9\5\6\4\28\67\3\2\2\29<\3\2\2\2:8\3\2\2\2:;\3\2\2\2;\3\3\2\2\2<:\3\2\2"+
		"\2=>\7\37\2\2>?\5.\30\2?\5\3\2\2\2@A\7\30\2\2AB\5\b\5\2BC\7\33\2\2CD\5"+
		"\n\6\2DE\7\36\2\2EN\3\2\2\2FG\7\30\2\2GH\7\31\2\2HI\7\32\2\2IJ\7\33\2"+
		"\2JK\5\n\6\2KL\7\36\2\2LN\3\2\2\2M@\3\2\2\2MF\3\2\2\2N\7\3\2\2\2OP\5\60"+
		"\31\2PQ\7\26\2\2QV\3\2\2\2RS\5\60\31\2ST\7\27\2\2TV\3\2\2\2UO\3\2\2\2"+
		"UR\3\2\2\2V\t\3\2\2\2WY\5\f\7\2XW\3\2\2\2Y\\\3\2\2\2ZX\3\2\2\2Z[\3\2\2"+
		"\2[\13\3\2\2\2\\Z\3\2\2\2]a\5\16\b\2^a\5\20\t\2_a\5\22\n\2`]\3\2\2\2`"+
		"^\3\2\2\2`_\3\2\2\2a\r\3\2\2\2bd\7\b\2\2ce\5\26\f\2dc\3\2\2\2de\3\2\2"+
		"\2ef\3\2\2\2fh\7\6\2\2gi\7\r\2\2hg\3\2\2\2hi\3\2\2\2ik\3\2\2\2jl\5\62"+
		"\32\2kj\3\2\2\2lm\3\2\2\2mk\3\2\2\2mn\3\2\2\2np\3\2\2\2oq\5$\23\2po\3"+
		"\2\2\2pq\3\2\2\2qs\3\2\2\2rt\5(\25\2sr\3\2\2\2st\3\2\2\2t\17\3\2\2\2u"+
		"w\7\t\2\2vx\5\30\r\2wv\3\2\2\2wx\3\2\2\2xy\3\2\2\2y{\7\7\2\2z|\7\r\2\2"+
		"{z\3\2\2\2{|\3\2\2\2|~\3\2\2\2}\177\5\62\32\2~}\3\2\2\2\177\u0080\3\2"+
		"\2\2\u0080~\3\2\2\2\u0080\u0081\3\2\2\2\u0081\u0083\3\2\2\2\u0082\u0084"+
		"\5$\23\2\u0083\u0082\3\2\2\2\u0083\u0084\3\2\2\2\u0084\u0086\3\2\2\2\u0085"+
		"\u0087\5(\25\2\u0086\u0085\3\2\2\2\u0086\u0087\3\2\2\2\u0087\21\3\2\2"+
		"\2\u0088\u0089\7\3\2\2\u0089\u008a\5\24\13\2\u008a\u008b\7\4\2\2\u008b"+
		"\u008c\5\n\6\2\u008c\u008d\7\36\2\2\u008d\23\3\2\2\2\u008e\u008f\3\2\2"+
		"\2\u008f\25\3\2\2\2\u0090\u0095\5\32\16\2\u0091\u0092\7 \2\2\u0092\u0094"+
		"\5\32\16\2\u0093\u0091\3\2\2\2\u0094\u0097\3\2\2\2\u0095\u0093\3\2\2\2"+
		"\u0095\u0096\3\2\2\2\u0096\u00a2\3\2\2\2\u0097\u0095\3\2\2\2\u0098\u00a2"+
		"\5\34\17\2\u0099\u009e\5\36\20\2\u009a\u009b\7 \2\2\u009b\u009d\5\36\20"+
		"\2\u009c\u009a\3\2\2\2\u009d\u00a0\3\2\2\2\u009e\u009c\3\2\2\2\u009e\u009f"+
		"\3\2\2\2\u009f\u00a2\3\2\2\2\u00a0\u009e\3\2\2\2\u00a1\u0090\3\2\2\2\u00a1"+
		"\u0098\3\2\2\2\u00a1\u0099\3\2\2\2\u00a2\27\3\2\2\2\u00a3\u00a8\5\32\16"+
		"\2\u00a4\u00a5\7 \2\2\u00a5\u00a7\5\32\16\2\u00a6\u00a4\3\2\2\2\u00a7"+
		"\u00aa\3\2\2\2\u00a8\u00a6\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9\u00b5\3\2"+
		"\2\2\u00aa\u00a8\3\2\2\2\u00ab\u00b5\5\34\17\2\u00ac\u00b1\5\36\20\2\u00ad"+
		"\u00ae\7 \2\2\u00ae\u00b0\5\36\20\2\u00af\u00ad\3\2\2\2\u00b0\u00b3\3"+
		"\2\2\2\u00b1\u00af\3\2\2\2\u00b1\u00b2\3\2\2\2\u00b2\u00b5\3\2\2\2\u00b3"+
		"\u00b1\3\2\2\2\u00b4\u00a3\3\2\2\2\u00b4\u00ab\3\2\2\2\u00b4\u00ac\3\2"+
		"\2\2\u00b5\31\3\2\2\2\u00b6\u00b7\5\34\17\2\u00b7\u00b8\5\36\20\2\u00b8"+
		"\33\3\2\2\2\u00b9\u00ba\5 \21\2\u00ba\u00bb\5\"\22\2\u00bb\u00bf\3\2\2"+
		"\2\u00bc\u00bf\5\"\22\2\u00bd\u00bf\5 \21\2\u00be\u00b9\3\2\2\2\u00be"+
		"\u00bc\3\2\2\2\u00be\u00bd\3\2\2\2\u00bf\35\3\2\2\2\u00c0\u00c3\7#\2\2"+
		"\u00c1\u00c2\7!\2\2\u00c2\u00c4\7#\2\2\u00c3\u00c1\3\2\2\2\u00c3\u00c4"+
		"\3\2\2\2\u00c4\37\3\2\2\2\u00c5\u00c6\5\60\31\2\u00c6!\3\2\2\2\u00c7\u00c8"+
		"\7\f\2\2\u00c8\u00c9\5\60\31\2\u00c9#\3\2\2\2\u00ca\u00cf\5&\24\2\u00cb"+
		"\u00cc\7 \2\2\u00cc\u00ce\5&\24\2\u00cd\u00cb\3\2\2\2\u00ce\u00d1\3\2"+
		"\2\2\u00cf\u00cd\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00d2\3\2\2\2\u00d1"+
		"\u00cf\3\2\2\2\u00d2\u00d3\7\24\2\2\u00d3%\3\2\2\2\u00d4\u00d5\t\2\2\2"+
		"\u00d5\'\3\2\2\2\u00d6\u00d7\7\13\2\2\u00d7\u00d8\5*\26\2\u00d8)\3\2\2"+
		"\2\u00d9\u00de\5,\27\2\u00da\u00db\7 \2\2\u00db\u00dd\5,\27\2\u00dc\u00da"+
		"\3\2\2\2\u00dd\u00e0\3\2\2\2\u00de\u00dc\3\2\2\2\u00de\u00df\3\2\2\2\u00df"+
		"+\3\2\2\2\u00e0\u00de\3\2\2\2\u00e1\u00e4\5\60\31\2\u00e2\u00e3\7\"\2"+
		"\2\u00e3\u00e5\5\60\31\2\u00e4\u00e2\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5"+
		"-\3\2\2\2\u00e6\u00e7\7%\2\2\u00e7/\3\2\2\2\u00e8\u00e9\7$\2\2\u00e9\61"+
		"\3\2\2\2\u00ea\u00eb\7#\2\2\u00eb\63\3\2\2\2\35\65:MUZ`dhmpsw{\u0080\u0083"+
		"\u0086\u0095\u009e\u00a1\u00a8\u00b1\u00b4\u00be\u00c3\u00cf\u00de\u00e4";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}