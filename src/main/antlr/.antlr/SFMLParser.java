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
		MOVE=1, FROM=2, TO=3, INPUT=4, OUTPUT=5, WHERE=6, SLOTS=7, RETAIN=8, EACH=9, 
		TOP=10, BOTTOM=11, NORTH=12, EAST=13, SOUTH=14, WEST=15, SIDE=16, SELF=17, 
		TICKS=18, SECONDS=19, EVERY=20, REDSTONE=21, PULSE=22, DO=23, WORLD=24, 
		PROGRAM=25, END=26, NAME=27, COMMA=28, COLON=29, DASH=30, IDENTIFIER=31, 
		NUMBER=32, STRING=33, LINE_COMMENT=34, WS=35;
	public static final int
		RULE_program = 0, RULE_name = 1, RULE_trigger = 2, RULE_interval = 3, 
		RULE_block = 4, RULE_statement = 5, RULE_inputstatement = 6, RULE_outputstatement = 7, 
		RULE_inputmatchers = 8, RULE_outputmatchers = 9, RULE_itemlimit = 10, 
		RULE_limit = 11, RULE_item = 12, RULE_quantity = 13, RULE_retention = 14, 
		RULE_sidequalifier = 15, RULE_side = 16, RULE_slotqualifier = 17, RULE_rangeset = 18, 
		RULE_range = 19, RULE_string = 20, RULE_number = 21, RULE_label = 22;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "name", "trigger", "interval", "block", "statement", "inputstatement", 
			"outputstatement", "inputmatchers", "outputmatchers", "itemlimit", "limit", 
			"item", "quantity", "retention", "sidequalifier", "side", "slotqualifier", 
			"rangeset", "range", "string", "number", "label"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, "','", "':'", "'-'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "MOVE", "FROM", "TO", "INPUT", "OUTPUT", "WHERE", "SLOTS", "RETAIN", 
			"EACH", "TOP", "BOTTOM", "NORTH", "EAST", "SOUTH", "WEST", "SIDE", "SELF", 
			"TICKS", "SECONDS", "EVERY", "REDSTONE", "PULSE", "DO", "WORLD", "PROGRAM", 
			"END", "NAME", "COMMA", "COLON", "DASH", "IDENTIFIER", "NUMBER", "STRING", 
			"LINE_COMMENT", "WS"
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
			setState(47);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NAME) {
				{
				setState(46);
				name();
				}
			}

			setState(52);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EVERY) {
				{
				{
				setState(49);
				trigger();
				}
				}
				setState(54);
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
			setState(55);
			match(NAME);
			setState(56);
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
			setState(71);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new TimerTriggerContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(58);
				match(EVERY);
				setState(59);
				interval();
				setState(60);
				match(DO);
				setState(61);
				block();
				setState(62);
				match(END);
				}
				break;
			case 2:
				_localctx = new PulseTriggerContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(64);
				match(EVERY);
				setState(65);
				match(REDSTONE);
				setState(66);
				match(PULSE);
				setState(67);
				match(DO);
				setState(68);
				block();
				setState(69);
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
			setState(79);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				_localctx = new TicksContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(73);
				number();
				setState(74);
				match(TICKS);
				}
				break;
			case 2:
				_localctx = new SecondsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(76);
				number();
				setState(77);
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
			setState(84);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INPUT || _la==OUTPUT) {
				{
				{
				setState(81);
				statement();
				}
				}
				setState(86);
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
			setState(89);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INPUT:
				_localctx = new InputStatementStatementContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(87);
				inputstatement();
				}
				break;
			case OUTPUT:
				_localctx = new OutputStatementStatementContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(88);
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
			setState(91);
			match(INPUT);
			setState(93);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RETAIN) | (1L << IDENTIFIER) | (1L << NUMBER))) != 0)) {
				{
				setState(92);
				inputmatchers();
				}
			}

			setState(95);
			match(FROM);
			setState(97);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EACH) {
				{
				setState(96);
				match(EACH);
				}
			}

			setState(100); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(99);
				label();
				}
				}
				setState(102); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(105);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TOP) | (1L << BOTTOM) | (1L << NORTH) | (1L << EAST) | (1L << SOUTH) | (1L << WEST))) != 0)) {
				{
				setState(104);
				sidequalifier();
				}
			}

			setState(108);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SLOTS) {
				{
				setState(107);
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
			setState(110);
			match(OUTPUT);
			setState(112);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RETAIN) | (1L << IDENTIFIER) | (1L << NUMBER))) != 0)) {
				{
				setState(111);
				outputmatchers();
				}
			}

			setState(114);
			match(TO);
			setState(116);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EACH) {
				{
				setState(115);
				match(EACH);
				}
			}

			setState(119); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(118);
				label();
				}
				}
				setState(121); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==IDENTIFIER );
			setState(124);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TOP) | (1L << BOTTOM) | (1L << NORTH) | (1L << EAST) | (1L << SOUTH) | (1L << WEST))) != 0)) {
				{
				setState(123);
				sidequalifier();
				}
			}

			setState(127);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SLOTS) {
				{
				setState(126);
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
		enterRule(_localctx, 16, RULE_inputmatchers);
		int _la;
		try {
			setState(146);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(129);
				itemlimit();
				setState(134);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(130);
					match(COMMA);
					setState(131);
					itemlimit();
					}
					}
					setState(136);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(137);
				limit();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(138);
				item();
				setState(143);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(139);
					match(COMMA);
					setState(140);
					item();
					}
					}
					setState(145);
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
		enterRule(_localctx, 18, RULE_outputmatchers);
		int _la;
		try {
			setState(165);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(148);
				itemlimit();
				setState(153);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(149);
					match(COMMA);
					setState(150);
					itemlimit();
					}
					}
					setState(155);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(156);
				limit();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(157);
				item();
				setState(162);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(158);
					match(COMMA);
					setState(159);
					item();
					}
					}
					setState(164);
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
		enterRule(_localctx, 20, RULE_itemlimit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(167);
			limit();
			setState(168);
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
			setState(175);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				_localctx = new QuantityRetentionLimitContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(170);
				quantity();
				setState(171);
				retention();
				}
				break;
			case 2:
				_localctx = new RetentionLimitContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(173);
				retention();
				}
				break;
			case 3:
				_localctx = new QuantityLimitContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(174);
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
			setState(177);
			match(IDENTIFIER);
			setState(180);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(178);
				match(COLON);
				setState(179);
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
			setState(182);
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
			setState(184);
			match(RETAIN);
			setState(185);
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
			setState(187);
			side();
			setState(192);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(188);
				match(COMMA);
				setState(189);
				side();
				}
				}
				setState(194);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(195);
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
			setState(197);
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
			setState(199);
			match(SLOTS);
			setState(200);
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
			setState(202);
			range();
			setState(207);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(203);
				match(COMMA);
				setState(204);
				range();
				}
				}
				setState(209);
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
			setState(210);
			number();
			setState(213);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DASH) {
				{
				setState(211);
				match(DASH);
				setState(212);
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
		enterRule(_localctx, 40, RULE_string);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(215);
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
		enterRule(_localctx, 42, RULE_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(217);
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
		enterRule(_localctx, 44, RULE_label);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(219);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3%\u00e0\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\3\2\5\2\62"+
		"\n\2\3\2\7\2\65\n\2\f\2\16\28\13\2\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4J\n\4\3\5\3\5\3\5\3\5\3\5\3\5\5\5R\n\5"+
		"\3\6\7\6U\n\6\f\6\16\6X\13\6\3\7\3\7\5\7\\\n\7\3\b\3\b\5\b`\n\b\3\b\3"+
		"\b\5\bd\n\b\3\b\6\bg\n\b\r\b\16\bh\3\b\5\bl\n\b\3\b\5\bo\n\b\3\t\3\t\5"+
		"\ts\n\t\3\t\3\t\5\tw\n\t\3\t\6\tz\n\t\r\t\16\t{\3\t\5\t\177\n\t\3\t\5"+
		"\t\u0082\n\t\3\n\3\n\3\n\7\n\u0087\n\n\f\n\16\n\u008a\13\n\3\n\3\n\3\n"+
		"\3\n\7\n\u0090\n\n\f\n\16\n\u0093\13\n\5\n\u0095\n\n\3\13\3\13\3\13\7"+
		"\13\u009a\n\13\f\13\16\13\u009d\13\13\3\13\3\13\3\13\3\13\7\13\u00a3\n"+
		"\13\f\13\16\13\u00a6\13\13\5\13\u00a8\n\13\3\f\3\f\3\f\3\r\3\r\3\r\3\r"+
		"\3\r\5\r\u00b2\n\r\3\16\3\16\3\16\5\16\u00b7\n\16\3\17\3\17\3\20\3\20"+
		"\3\20\3\21\3\21\3\21\7\21\u00c1\n\21\f\21\16\21\u00c4\13\21\3\21\3\21"+
		"\3\22\3\22\3\23\3\23\3\23\3\24\3\24\3\24\7\24\u00d0\n\24\f\24\16\24\u00d3"+
		"\13\24\3\25\3\25\3\25\5\25\u00d8\n\25\3\26\3\26\3\27\3\27\3\30\3\30\3"+
		"\30\2\2\31\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\2\3\3\2\f\21"+
		"\2\u00e6\2\61\3\2\2\2\49\3\2\2\2\6I\3\2\2\2\bQ\3\2\2\2\nV\3\2\2\2\f[\3"+
		"\2\2\2\16]\3\2\2\2\20p\3\2\2\2\22\u0094\3\2\2\2\24\u00a7\3\2\2\2\26\u00a9"+
		"\3\2\2\2\30\u00b1\3\2\2\2\32\u00b3\3\2\2\2\34\u00b8\3\2\2\2\36\u00ba\3"+
		"\2\2\2 \u00bd\3\2\2\2\"\u00c7\3\2\2\2$\u00c9\3\2\2\2&\u00cc\3\2\2\2(\u00d4"+
		"\3\2\2\2*\u00d9\3\2\2\2,\u00db\3\2\2\2.\u00dd\3\2\2\2\60\62\5\4\3\2\61"+
		"\60\3\2\2\2\61\62\3\2\2\2\62\66\3\2\2\2\63\65\5\6\4\2\64\63\3\2\2\2\65"+
		"8\3\2\2\2\66\64\3\2\2\2\66\67\3\2\2\2\67\3\3\2\2\28\66\3\2\2\29:\7\35"+
		"\2\2:;\5*\26\2;\5\3\2\2\2<=\7\26\2\2=>\5\b\5\2>?\7\31\2\2?@\5\n\6\2@A"+
		"\7\34\2\2AJ\3\2\2\2BC\7\26\2\2CD\7\27\2\2DE\7\30\2\2EF\7\31\2\2FG\5\n"+
		"\6\2GH\7\34\2\2HJ\3\2\2\2I<\3\2\2\2IB\3\2\2\2J\7\3\2\2\2KL\5,\27\2LM\7"+
		"\24\2\2MR\3\2\2\2NO\5,\27\2OP\7\25\2\2PR\3\2\2\2QK\3\2\2\2QN\3\2\2\2R"+
		"\t\3\2\2\2SU\5\f\7\2TS\3\2\2\2UX\3\2\2\2VT\3\2\2\2VW\3\2\2\2W\13\3\2\2"+
		"\2XV\3\2\2\2Y\\\5\16\b\2Z\\\5\20\t\2[Y\3\2\2\2[Z\3\2\2\2\\\r\3\2\2\2]"+
		"_\7\6\2\2^`\5\22\n\2_^\3\2\2\2_`\3\2\2\2`a\3\2\2\2ac\7\4\2\2bd\7\13\2"+
		"\2cb\3\2\2\2cd\3\2\2\2df\3\2\2\2eg\5.\30\2fe\3\2\2\2gh\3\2\2\2hf\3\2\2"+
		"\2hi\3\2\2\2ik\3\2\2\2jl\5 \21\2kj\3\2\2\2kl\3\2\2\2ln\3\2\2\2mo\5$\23"+
		"\2nm\3\2\2\2no\3\2\2\2o\17\3\2\2\2pr\7\7\2\2qs\5\24\13\2rq\3\2\2\2rs\3"+
		"\2\2\2st\3\2\2\2tv\7\5\2\2uw\7\13\2\2vu\3\2\2\2vw\3\2\2\2wy\3\2\2\2xz"+
		"\5.\30\2yx\3\2\2\2z{\3\2\2\2{y\3\2\2\2{|\3\2\2\2|~\3\2\2\2}\177\5 \21"+
		"\2~}\3\2\2\2~\177\3\2\2\2\177\u0081\3\2\2\2\u0080\u0082\5$\23\2\u0081"+
		"\u0080\3\2\2\2\u0081\u0082\3\2\2\2\u0082\21\3\2\2\2\u0083\u0088\5\26\f"+
		"\2\u0084\u0085\7\36\2\2\u0085\u0087\5\26\f\2\u0086\u0084\3\2\2\2\u0087"+
		"\u008a\3\2\2\2\u0088\u0086\3\2\2\2\u0088\u0089\3\2\2\2\u0089\u0095\3\2"+
		"\2\2\u008a\u0088\3\2\2\2\u008b\u0095\5\30\r\2\u008c\u0091\5\32\16\2\u008d"+
		"\u008e\7\36\2\2\u008e\u0090\5\32\16\2\u008f\u008d\3\2\2\2\u0090\u0093"+
		"\3\2\2\2\u0091\u008f\3\2\2\2\u0091\u0092\3\2\2\2\u0092\u0095\3\2\2\2\u0093"+
		"\u0091\3\2\2\2\u0094\u0083\3\2\2\2\u0094\u008b\3\2\2\2\u0094\u008c\3\2"+
		"\2\2\u0095\23\3\2\2\2\u0096\u009b\5\26\f\2\u0097\u0098\7\36\2\2\u0098"+
		"\u009a\5\26\f\2\u0099\u0097\3\2\2\2\u009a\u009d\3\2\2\2\u009b\u0099\3"+
		"\2\2\2\u009b\u009c\3\2\2\2\u009c\u00a8\3\2\2\2\u009d\u009b\3\2\2\2\u009e"+
		"\u00a8\5\30\r\2\u009f\u00a4\5\32\16\2\u00a0\u00a1\7\36\2\2\u00a1\u00a3"+
		"\5\32\16\2\u00a2\u00a0\3\2\2\2\u00a3\u00a6\3\2\2\2\u00a4\u00a2\3\2\2\2"+
		"\u00a4\u00a5\3\2\2\2\u00a5\u00a8\3\2\2\2\u00a6\u00a4\3\2\2\2\u00a7\u0096"+
		"\3\2\2\2\u00a7\u009e\3\2\2\2\u00a7\u009f\3\2\2\2\u00a8\25\3\2\2\2\u00a9"+
		"\u00aa\5\30\r\2\u00aa\u00ab\5\32\16\2\u00ab\27\3\2\2\2\u00ac\u00ad\5\34"+
		"\17\2\u00ad\u00ae\5\36\20\2\u00ae\u00b2\3\2\2\2\u00af\u00b2\5\36\20\2"+
		"\u00b0\u00b2\5\34\17\2\u00b1\u00ac\3\2\2\2\u00b1\u00af\3\2\2\2\u00b1\u00b0"+
		"\3\2\2\2\u00b2\31\3\2\2\2\u00b3\u00b6\7!\2\2\u00b4\u00b5\7\37\2\2\u00b5"+
		"\u00b7\7!\2\2\u00b6\u00b4\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7\33\3\2\2\2"+
		"\u00b8\u00b9\5,\27\2\u00b9\35\3\2\2\2\u00ba\u00bb\7\n\2\2\u00bb\u00bc"+
		"\5,\27\2\u00bc\37\3\2\2\2\u00bd\u00c2\5\"\22\2\u00be\u00bf\7\36\2\2\u00bf"+
		"\u00c1\5\"\22\2\u00c0\u00be\3\2\2\2\u00c1\u00c4\3\2\2\2\u00c2\u00c0\3"+
		"\2\2\2\u00c2\u00c3\3\2\2\2\u00c3\u00c5\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c5"+
		"\u00c6\7\22\2\2\u00c6!\3\2\2\2\u00c7\u00c8\t\2\2\2\u00c8#\3\2\2\2\u00c9"+
		"\u00ca\7\t\2\2\u00ca\u00cb\5&\24\2\u00cb%\3\2\2\2\u00cc\u00d1\5(\25\2"+
		"\u00cd\u00ce\7\36\2\2\u00ce\u00d0\5(\25\2\u00cf\u00cd\3\2\2\2\u00d0\u00d3"+
		"\3\2\2\2\u00d1\u00cf\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2\'\3\2\2\2\u00d3"+
		"\u00d1\3\2\2\2\u00d4\u00d7\5,\27\2\u00d5\u00d6\7 \2\2\u00d6\u00d8\5,\27"+
		"\2\u00d7\u00d5\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8)\3\2\2\2\u00d9\u00da"+
		"\7#\2\2\u00da+\3\2\2\2\u00db\u00dc\7\"\2\2\u00dc-\3\2\2\2\u00dd\u00de"+
		"\7!\2\2\u00de/\3\2\2\2\35\61\66IQV[_chknrv{~\u0081\u0088\u0091\u0094\u009b"+
		"\u00a4\u00a7\u00b1\u00b6\u00c2\u00d1\u00d7";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}