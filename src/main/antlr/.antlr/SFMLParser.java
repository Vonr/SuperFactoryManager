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
		MOVE=1, FROM=2, TO=3, INPUT=4, OUTPUT=5, WHERE=6, SLOT=7, RETAIN=8, EACH=9, 
		TOP=10, BOTTOM=11, NORTH=12, EAST=13, SOUTH=14, WEST=15, SIDE=16, TICKS=17, 
		SECONDS=18, EVERY=19, REDSTONE=20, PULSE=21, DO=22, WORLD=23, PROGRAM=24, 
		END=25, NAME=26, COMMA=27, COLON=28, IDENTIFIER=29, NUMBER=30, STRING=31, 
		LINE_COMMENT=32, WS=33;
	public static final int
		RULE_program = 0, RULE_name = 1, RULE_trigger = 2, RULE_interval = 3, 
		RULE_block = 4, RULE_statement = 5, RULE_inputstatement = 6, RULE_outputstatement = 7, 
		RULE_inputmatchers = 8, RULE_outputmatchers = 9, RULE_itemlimit = 10, 
		RULE_limit = 11, RULE_item = 12, RULE_quantity = 13, RULE_retention = 14, 
		RULE_sidequalifier = 15, RULE_side = 16, RULE_string = 17, RULE_number = 18, 
		RULE_label = 19;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "name", "trigger", "interval", "block", "statement", "inputstatement", 
			"outputstatement", "inputmatchers", "outputmatchers", "itemlimit", "limit", 
			"item", "quantity", "retention", "sidequalifier", "side", "string", "number", 
			"label"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, "','", "':'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "MOVE", "FROM", "TO", "INPUT", "OUTPUT", "WHERE", "SLOT", "RETAIN", 
			"EACH", "TOP", "BOTTOM", "NORTH", "EAST", "SOUTH", "WEST", "SIDE", "TICKS", 
			"SECONDS", "EVERY", "REDSTONE", "PULSE", "DO", "WORLD", "PROGRAM", "END", 
			"NAME", "COMMA", "COLON", "IDENTIFIER", "NUMBER", "STRING", "LINE_COMMENT", 
			"WS"
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
			setState(41);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NAME) {
				{
				setState(40);
				name();
				}
			}

			setState(46);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EVERY) {
				{
				{
				setState(43);
				trigger();
				}
				}
				setState(48);
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
			setState(49);
			match(NAME);
			setState(50);
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
			setState(65);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new TimerTriggerContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(52);
				match(EVERY);
				setState(53);
				interval();
				setState(54);
				match(DO);
				setState(55);
				block();
				setState(56);
				match(END);
				}
				break;
			case 2:
				_localctx = new PulseTriggerContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(58);
				match(EVERY);
				setState(59);
				match(REDSTONE);
				setState(60);
				match(PULSE);
				setState(61);
				match(DO);
				setState(62);
				block();
				setState(63);
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
			setState(73);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				_localctx = new TicksContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(67);
				number();
				setState(68);
				match(TICKS);
				}
				break;
			case 2:
				_localctx = new SecondsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(70);
				number();
				setState(71);
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
			setState(78);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INPUT || _la==OUTPUT) {
				{
				{
				setState(75);
				statement();
				}
				}
				setState(80);
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

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_statement);
		try {
			setState(83);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INPUT:
				_localctx = new InputStatementStatementContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(81);
				inputstatement();
				}
				break;
			case OUTPUT:
				_localctx = new OutputStatementStatementContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(82);
				outputstatement();
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
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public InputmatchersContext inputmatchers() {
			return getRuleContext(InputmatchersContext.class,0);
		}
		public TerminalNode EACH() { return getToken(SFMLParser.EACH, 0); }
		public SidequalifierContext sidequalifier() {
			return getRuleContext(SidequalifierContext.class,0);
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
			setState(85);
			match(INPUT);
			setState(87);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RETAIN) | (1L << IDENTIFIER) | (1L << NUMBER))) != 0)) {
				{
				setState(86);
				inputmatchers();
				}
			}

			setState(89);
			match(FROM);
			setState(91);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EACH) {
				{
				setState(90);
				match(EACH);
				}
			}

			setState(93);
			label();
			setState(95);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TOP) | (1L << BOTTOM) | (1L << NORTH) | (1L << EAST) | (1L << SOUTH) | (1L << WEST))) != 0)) {
				{
				setState(94);
				sidequalifier();
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
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public OutputmatchersContext outputmatchers() {
			return getRuleContext(OutputmatchersContext.class,0);
		}
		public TerminalNode EACH() { return getToken(SFMLParser.EACH, 0); }
		public SidequalifierContext sidequalifier() {
			return getRuleContext(SidequalifierContext.class,0);
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
			setState(97);
			match(OUTPUT);
			setState(99);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RETAIN) | (1L << IDENTIFIER) | (1L << NUMBER))) != 0)) {
				{
				setState(98);
				outputmatchers();
				}
			}

			setState(101);
			match(TO);
			setState(103);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EACH) {
				{
				setState(102);
				match(EACH);
				}
			}

			setState(105);
			label();
			setState(107);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TOP) | (1L << BOTTOM) | (1L << NORTH) | (1L << EAST) | (1L << SOUTH) | (1L << WEST))) != 0)) {
				{
				setState(106);
				sidequalifier();
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
		public ItemContext item() {
			return getRuleContext(ItemContext.class,0);
		}
		public InputmatchersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inputmatchers; }
	}

	public final InputmatchersContext inputmatchers() throws RecognitionException {
		InputmatchersContext _localctx = new InputmatchersContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_inputmatchers);
		int _la;
		try {
			setState(119);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(109);
				itemlimit();
				setState(114);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(110);
					match(COMMA);
					setState(111);
					itemlimit();
					}
					}
					setState(116);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(117);
				limit();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(118);
				item();
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
		public ItemContext item() {
			return getRuleContext(ItemContext.class,0);
		}
		public OutputmatchersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outputmatchers; }
	}

	public final OutputmatchersContext outputmatchers() throws RecognitionException {
		OutputmatchersContext _localctx = new OutputmatchersContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_outputmatchers);
		int _la;
		try {
			setState(131);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(121);
				itemlimit();
				setState(126);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(122);
					match(COMMA);
					setState(123);
					itemlimit();
					}
					}
					setState(128);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(129);
				limit();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(130);
				item();
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
		enterRule(_localctx, 20, RULE_itemlimit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(133);
			limit();
			setState(134);
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
		enterRule(_localctx, 22, RULE_limit);
		try {
			setState(141);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				_localctx = new QuantityRetentionLimitContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(136);
				quantity();
				setState(137);
				retention();
				}
				break;
			case 2:
				_localctx = new RetentionLimitContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(139);
				retention();
				}
				break;
			case 3:
				_localctx = new QuantityLimitContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(140);
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
		enterRule(_localctx, 24, RULE_item);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			match(IDENTIFIER);
			setState(146);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(144);
				match(COLON);
				setState(145);
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
		enterRule(_localctx, 26, RULE_quantity);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(148);
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
			setState(150);
			match(RETAIN);
			setState(151);
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
			setState(153);
			side();
			setState(158);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(154);
				match(COMMA);
				setState(155);
				side();
				}
				}
				setState(160);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(161);
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
			setState(163);
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

	public static class StringContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(SFMLParser.STRING, 0); }
		public StringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_string; }
	}

	public final StringContext string() throws RecognitionException {
		StringContext _localctx = new StringContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_string);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(165);
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
		enterRule(_localctx, 36, RULE_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(167);
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
		enterRule(_localctx, 38, RULE_label);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3#\u00ae\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\3\2\5\2,\n\2\3\2\7\2/\n\2\f\2\16\2\62\13"+
		"\2\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4"+
		"D\n\4\3\5\3\5\3\5\3\5\3\5\3\5\5\5L\n\5\3\6\7\6O\n\6\f\6\16\6R\13\6\3\7"+
		"\3\7\5\7V\n\7\3\b\3\b\5\bZ\n\b\3\b\3\b\5\b^\n\b\3\b\3\b\5\bb\n\b\3\t\3"+
		"\t\5\tf\n\t\3\t\3\t\5\tj\n\t\3\t\3\t\5\tn\n\t\3\n\3\n\3\n\7\ns\n\n\f\n"+
		"\16\nv\13\n\3\n\3\n\5\nz\n\n\3\13\3\13\3\13\7\13\177\n\13\f\13\16\13\u0082"+
		"\13\13\3\13\3\13\5\13\u0086\n\13\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\5\r\u0090"+
		"\n\r\3\16\3\16\3\16\5\16\u0095\n\16\3\17\3\17\3\20\3\20\3\20\3\21\3\21"+
		"\3\21\7\21\u009f\n\21\f\21\16\21\u00a2\13\21\3\21\3\21\3\22\3\22\3\23"+
		"\3\23\3\24\3\24\3\25\3\25\3\25\2\2\26\2\4\6\b\n\f\16\20\22\24\26\30\32"+
		"\34\36 \"$&(\2\3\3\2\f\21\2\u00af\2+\3\2\2\2\4\63\3\2\2\2\6C\3\2\2\2\b"+
		"K\3\2\2\2\nP\3\2\2\2\fU\3\2\2\2\16W\3\2\2\2\20c\3\2\2\2\22y\3\2\2\2\24"+
		"\u0085\3\2\2\2\26\u0087\3\2\2\2\30\u008f\3\2\2\2\32\u0091\3\2\2\2\34\u0096"+
		"\3\2\2\2\36\u0098\3\2\2\2 \u009b\3\2\2\2\"\u00a5\3\2\2\2$\u00a7\3\2\2"+
		"\2&\u00a9\3\2\2\2(\u00ab\3\2\2\2*,\5\4\3\2+*\3\2\2\2+,\3\2\2\2,\60\3\2"+
		"\2\2-/\5\6\4\2.-\3\2\2\2/\62\3\2\2\2\60.\3\2\2\2\60\61\3\2\2\2\61\3\3"+
		"\2\2\2\62\60\3\2\2\2\63\64\7\34\2\2\64\65\5$\23\2\65\5\3\2\2\2\66\67\7"+
		"\25\2\2\678\5\b\5\289\7\30\2\29:\5\n\6\2:;\7\33\2\2;D\3\2\2\2<=\7\25\2"+
		"\2=>\7\26\2\2>?\7\27\2\2?@\7\30\2\2@A\5\n\6\2AB\7\33\2\2BD\3\2\2\2C\66"+
		"\3\2\2\2C<\3\2\2\2D\7\3\2\2\2EF\5&\24\2FG\7\23\2\2GL\3\2\2\2HI\5&\24\2"+
		"IJ\7\24\2\2JL\3\2\2\2KE\3\2\2\2KH\3\2\2\2L\t\3\2\2\2MO\5\f\7\2NM\3\2\2"+
		"\2OR\3\2\2\2PN\3\2\2\2PQ\3\2\2\2Q\13\3\2\2\2RP\3\2\2\2SV\5\16\b\2TV\5"+
		"\20\t\2US\3\2\2\2UT\3\2\2\2V\r\3\2\2\2WY\7\6\2\2XZ\5\22\n\2YX\3\2\2\2"+
		"YZ\3\2\2\2Z[\3\2\2\2[]\7\4\2\2\\^\7\13\2\2]\\\3\2\2\2]^\3\2\2\2^_\3\2"+
		"\2\2_a\5(\25\2`b\5 \21\2a`\3\2\2\2ab\3\2\2\2b\17\3\2\2\2ce\7\7\2\2df\5"+
		"\24\13\2ed\3\2\2\2ef\3\2\2\2fg\3\2\2\2gi\7\5\2\2hj\7\13\2\2ih\3\2\2\2"+
		"ij\3\2\2\2jk\3\2\2\2km\5(\25\2ln\5 \21\2ml\3\2\2\2mn\3\2\2\2n\21\3\2\2"+
		"\2ot\5\26\f\2pq\7\35\2\2qs\5\26\f\2rp\3\2\2\2sv\3\2\2\2tr\3\2\2\2tu\3"+
		"\2\2\2uz\3\2\2\2vt\3\2\2\2wz\5\30\r\2xz\5\32\16\2yo\3\2\2\2yw\3\2\2\2"+
		"yx\3\2\2\2z\23\3\2\2\2{\u0080\5\26\f\2|}\7\35\2\2}\177\5\26\f\2~|\3\2"+
		"\2\2\177\u0082\3\2\2\2\u0080~\3\2\2\2\u0080\u0081\3\2\2\2\u0081\u0086"+
		"\3\2\2\2\u0082\u0080\3\2\2\2\u0083\u0086\5\30\r\2\u0084\u0086\5\32\16"+
		"\2\u0085{\3\2\2\2\u0085\u0083\3\2\2\2\u0085\u0084\3\2\2\2\u0086\25\3\2"+
		"\2\2\u0087\u0088\5\30\r\2\u0088\u0089\5\32\16\2\u0089\27\3\2\2\2\u008a"+
		"\u008b\5\34\17\2\u008b\u008c\5\36\20\2\u008c\u0090\3\2\2\2\u008d\u0090"+
		"\5\36\20\2\u008e\u0090\5\34\17\2\u008f\u008a\3\2\2\2\u008f\u008d\3\2\2"+
		"\2\u008f\u008e\3\2\2\2\u0090\31\3\2\2\2\u0091\u0094\7\37\2\2\u0092\u0093"+
		"\7\36\2\2\u0093\u0095\7\37\2\2\u0094\u0092\3\2\2\2\u0094\u0095\3\2\2\2"+
		"\u0095\33\3\2\2\2\u0096\u0097\5&\24\2\u0097\35\3\2\2\2\u0098\u0099\7\n"+
		"\2\2\u0099\u009a\5&\24\2\u009a\37\3\2\2\2\u009b\u00a0\5\"\22\2\u009c\u009d"+
		"\7\35\2\2\u009d\u009f\5\"\22\2\u009e\u009c\3\2\2\2\u009f\u00a2\3\2\2\2"+
		"\u00a0\u009e\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\u00a3\3\2\2\2\u00a2\u00a0"+
		"\3\2\2\2\u00a3\u00a4\7\22\2\2\u00a4!\3\2\2\2\u00a5\u00a6\t\2\2\2\u00a6"+
		"#\3\2\2\2\u00a7\u00a8\7!\2\2\u00a8%\3\2\2\2\u00a9\u00aa\7 \2\2\u00aa\'"+
		"\3\2\2\2\u00ab\u00ac\7\37\2\2\u00ac)\3\2\2\2\25+\60CKPUY]aeimty\u0080"+
		"\u0085\u008f\u0094\u00a0";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}