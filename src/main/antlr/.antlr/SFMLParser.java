// Generated from d:\Repos\Minecraft\Forge\SuperFactoryManager\src\main\antlr\SFML.g by ANTLR 4.9.2
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
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

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
		RULE_inputmatchers = 8, RULE_outputmatchers = 9, RULE_movement = 10, RULE_resourcelimit = 11, 
		RULE_limit = 12, RULE_quantity = 13, RULE_retention = 14, RULE_sidequalifier = 15, 
		RULE_side = 16, RULE_slotqualifier = 17, RULE_rangeset = 18, RULE_range = 19, 
		RULE_ifstatement = 20, RULE_boolexpr = 21, RULE_resourcecomparison = 22, 
		RULE_comparisonOp = 23, RULE_setOp = 24, RULE_labelaccess = 25, RULE_label = 26, 
		RULE_resourceid = 27, RULE_string = 28, RULE_number = 29;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "name", "trigger", "interval", "block", "statement", "inputstatement", 
			"outputstatement", "inputmatchers", "outputmatchers", "movement", "resourcelimit", 
			"limit", "quantity", "retention", "sidequalifier", "side", "slotqualifier", 
			"rangeset", "range", "ifstatement", "boolexpr", "resourcecomparison", 
			"comparisonOp", "setOp", "labelaccess", "label", "resourceid", "string", 
			"number"
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
		public TerminalNode EOF() { return getToken(SFMLParser.EOF, 0); }
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
			setState(69);
			match(EOF);
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
			setState(71);
			match(NAME);
			setState(72);
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
			setState(87);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new TimerTriggerContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(74);
				match(EVERY);
				setState(75);
				interval();
				setState(76);
				match(DO);
				setState(77);
				block();
				setState(78);
				match(END);
				}
				break;
			case 2:
				_localctx = new PulseTriggerContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(80);
				match(EVERY);
				setState(81);
				match(REDSTONE);
				setState(82);
				match(PULSE);
				setState(83);
				match(DO);
				setState(84);
				block();
				setState(85);
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
			setState(95);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				_localctx = new TicksContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(89);
				number();
				setState(90);
				match(TICKS);
				}
				break;
			case 2:
				_localctx = new SecondsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(92);
				number();
				setState(93);
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
			setState(100);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IF) | (1L << INPUT) | (1L << OUTPUT))) != 0)) {
				{
				{
				setState(97);
				statement();
				}
				}
				setState(102);
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
			setState(106);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INPUT:
				_localctx = new InputStatementStatementContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(103);
				inputstatement();
				}
				break;
			case OUTPUT:
				_localctx = new OutputStatementStatementContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(104);
				outputstatement();
				}
				break;
			case IF:
				_localctx = new IfStatementStatementContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(105);
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
			setState(108);
			match(INPUT);
			setState(110);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RETAIN) | (1L << IDENTIFIER) | (1L << NUMBER) | (1L << STRING))) != 0)) {
				{
				setState(109);
				inputmatchers();
				}
			}

			setState(112);
			match(FROM);
			setState(114);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EACH) {
				{
				setState(113);
				match(EACH);
				}
			}

			setState(116);
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
			setState(118);
			match(OUTPUT);
			setState(120);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RETAIN) | (1L << IDENTIFIER) | (1L << NUMBER) | (1L << STRING))) != 0)) {
				{
				setState(119);
				outputmatchers();
				}
			}

			setState(122);
			match(TO);
			setState(124);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EACH) {
				{
				setState(123);
				match(EACH);
				}
			}

			setState(126);
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
		public MovementContext movement() {
			return getRuleContext(MovementContext.class,0);
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
			setState(128);
			movement();
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
		public MovementContext movement() {
			return getRuleContext(MovementContext.class,0);
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
			setState(130);
			movement();
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

	public static class MovementContext extends ParserRuleContext {
		public MovementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_movement; }
	 
		public MovementContext() { }
		public void copyFrom(MovementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ResourceLimitMovementContext extends MovementContext {
		public List<ResourcelimitContext> resourcelimit() {
			return getRuleContexts(ResourcelimitContext.class);
		}
		public ResourcelimitContext resourcelimit(int i) {
			return getRuleContext(ResourcelimitContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SFMLParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SFMLParser.COMMA, i);
		}
		public ResourceLimitMovementContext(MovementContext ctx) { copyFrom(ctx); }
	}
	public static class LimitMovementContext extends MovementContext {
		public LimitContext limit() {
			return getRuleContext(LimitContext.class,0);
		}
		public LimitMovementContext(MovementContext ctx) { copyFrom(ctx); }
	}

	public final MovementContext movement() throws RecognitionException {
		MovementContext _localctx = new MovementContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_movement);
		int _la;
		try {
			setState(141);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				_localctx = new ResourceLimitMovementContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(132);
				resourcelimit();
				setState(137);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(133);
					match(COMMA);
					setState(134);
					resourcelimit();
					}
					}
					setState(139);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				_localctx = new LimitMovementContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(140);
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

	public static class ResourcelimitContext extends ParserRuleContext {
		public ResourceidContext resourceid() {
			return getRuleContext(ResourceidContext.class,0);
		}
		public LimitContext limit() {
			return getRuleContext(LimitContext.class,0);
		}
		public ResourcelimitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resourcelimit; }
	}

	public final ResourcelimitContext resourcelimit() throws RecognitionException {
		ResourcelimitContext _localctx = new ResourcelimitContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_resourcelimit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RETAIN || _la==NUMBER) {
				{
				setState(143);
				limit();
				}
			}

			setState(146);
			resourceid();
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
			setState(153);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				_localctx = new QuantityRetentionLimitContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(148);
				quantity();
				setState(149);
				retention();
				}
				break;
			case 2:
				_localctx = new RetentionLimitContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(151);
				retention();
				}
				break;
			case 3:
				_localctx = new QuantityLimitContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(152);
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
			setState(155);
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
			setState(157);
			match(RETAIN);
			setState(158);
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
			setState(160);
			side();
			setState(165);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(161);
				match(COMMA);
				setState(162);
				side();
				}
				}
				setState(167);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(168);
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
			setState(170);
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
			setState(172);
			match(SLOTS);
			setState(173);
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
			setState(175);
			range();
			setState(180);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(176);
				match(COMMA);
				setState(177);
				range();
				}
				}
				setState(182);
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
			setState(183);
			number();
			setState(186);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DASH) {
				{
				setState(184);
				match(DASH);
				setState(185);
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
			setState(188);
			match(IF);
			setState(189);
			boolexpr(0);
			setState(190);
			match(THEN);
			setState(191);
			block();
			setState(200);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(192);
					match(ELSE);
					setState(193);
					match(IF);
					setState(194);
					boolexpr(0);
					setState(195);
					match(THEN);
					setState(196);
					block();
					}
					} 
				}
				setState(202);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			}
			setState(205);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(203);
				match(ELSE);
				setState(204);
				block();
				}
			}

			setState(207);
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
		public ResourcecomparisonContext resourcecomparison() {
			return getRuleContext(ResourcecomparisonContext.class,0);
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
	public static class BooleanRedstoneContext extends BoolexprContext {
		public TerminalNode REDSTONE() { return getToken(SFMLParser.REDSTONE, 0); }
		public ComparisonOpContext comparisonOp() {
			return getRuleContext(ComparisonOpContext.class,0);
		}
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public BooleanRedstoneContext(BoolexprContext ctx) { copyFrom(ctx); }
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
			setState(231);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TRUE:
				{
				_localctx = new BooleanTrueContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(210);
				match(TRUE);
				}
				break;
			case FALSE:
				{
				_localctx = new BooleanFalseContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(211);
				match(FALSE);
				}
				break;
			case LPAREN:
				{
				_localctx = new BooleanParenContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(212);
				match(LPAREN);
				setState(213);
				boolexpr(0);
				setState(214);
				match(RPAREN);
				}
				break;
			case NOT:
				{
				_localctx = new BooleanNegationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(216);
				match(NOT);
				setState(217);
				boolexpr(5);
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
				setState(219);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << OVERALL) | (1L << SOME) | (1L << ONE) | (1L << LONE) | (1L << EVERY))) != 0)) {
					{
					setState(218);
					setOp();
					}
				}

				setState(221);
				labelaccess();
				setState(222);
				match(HAS);
				setState(223);
				resourcecomparison();
				}
				break;
			case REDSTONE:
				{
				_localctx = new BooleanRedstoneContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(225);
				match(REDSTONE);
				setState(229);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
				case 1:
					{
					setState(226);
					comparisonOp();
					setState(227);
					number();
					}
					break;
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(241);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(239);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
					case 1:
						{
						_localctx = new BooleanConjunctionContext(new BoolexprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_boolexpr);
						setState(233);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(234);
						match(AND);
						setState(235);
						boolexpr(5);
						}
						break;
					case 2:
						{
						_localctx = new BooleanDisjunctionContext(new BoolexprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_boolexpr);
						setState(236);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(237);
						match(OR);
						setState(238);
						boolexpr(4);
						}
						break;
					}
					} 
				}
				setState(243);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
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

	public static class ResourcecomparisonContext extends ParserRuleContext {
		public ComparisonOpContext comparisonOp() {
			return getRuleContext(ComparisonOpContext.class,0);
		}
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public ResourceidContext resourceid() {
			return getRuleContext(ResourceidContext.class,0);
		}
		public ResourcecomparisonContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resourcecomparison; }
	}

	public final ResourcecomparisonContext resourcecomparison() throws RecognitionException {
		ResourcecomparisonContext _localctx = new ResourcecomparisonContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_resourcecomparison);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(244);
			comparisonOp();
			setState(245);
			number();
			setState(246);
			resourceid();
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
			setState(248);
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
			setState(250);
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
			setState(252);
			label();
			setState(257);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(253);
				match(COMMA);
				setState(254);
				label();
				}
				}
				setState(259);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(261);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << TOP) | (1L << BOTTOM) | (1L << NORTH) | (1L << EAST) | (1L << SOUTH) | (1L << WEST))) != 0)) {
				{
				setState(260);
				sidequalifier();
				}
			}

			setState(264);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SLOTS) {
				{
				setState(263);
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
			setState(266);
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

	public static class ResourceidContext extends ParserRuleContext {
		public ResourceidContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_resourceid; }
	 
		public ResourceidContext() { }
		public void copyFrom(ResourceidContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class StringResourceContext extends ResourceidContext {
		public TerminalNode STRING() { return getToken(SFMLParser.STRING, 0); }
		public StringResourceContext(ResourceidContext ctx) { copyFrom(ctx); }
	}
	public static class MinecraftResourceContext extends ResourceidContext {
		public TerminalNode IDENTIFIER() { return getToken(SFMLParser.IDENTIFIER, 0); }
		public MinecraftResourceContext(ResourceidContext ctx) { copyFrom(ctx); }
	}
	public static class ExplicitResourceContext extends ResourceidContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(SFMLParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(SFMLParser.IDENTIFIER, i);
		}
		public List<TerminalNode> COLON() { return getTokens(SFMLParser.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(SFMLParser.COLON, i);
		}
		public ExplicitResourceContext(ResourceidContext ctx) { copyFrom(ctx); }
	}
	public static class ItemResourceContext extends ResourceidContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(SFMLParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(SFMLParser.IDENTIFIER, i);
		}
		public TerminalNode COLON() { return getToken(SFMLParser.COLON, 0); }
		public ItemResourceContext(ResourceidContext ctx) { copyFrom(ctx); }
	}

	public final ResourceidContext resourceid() throws RecognitionException {
		ResourceidContext _localctx = new ResourceidContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_resourceid);
		try {
			setState(278);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				_localctx = new ExplicitResourceContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(268);
				match(IDENTIFIER);
				setState(269);
				match(COLON);
				setState(270);
				match(IDENTIFIER);
				setState(271);
				match(COLON);
				setState(272);
				match(IDENTIFIER);
				}
				break;
			case 2:
				_localctx = new ItemResourceContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(273);
				match(IDENTIFIER);
				setState(274);
				match(COLON);
				setState(275);
				match(IDENTIFIER);
				}
				break;
			case 3:
				_localctx = new MinecraftResourceContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(276);
				match(IDENTIFIER);
				}
				break;
			case 4:
				_localctx = new StringResourceContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(277);
				match(STRING);
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
			setState(280);
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
			setState(282);
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
			return precpred(_ctx, 4);
		case 1:
			return precpred(_ctx, 3);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\39\u011f\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\3\2\5\2@"+
		"\n\2\3\2\7\2C\n\2\f\2\16\2F\13\2\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4Z\n\4\3\5\3\5\3\5\3\5\3\5\3\5\5"+
		"\5b\n\5\3\6\7\6e\n\6\f\6\16\6h\13\6\3\7\3\7\3\7\5\7m\n\7\3\b\3\b\5\bq"+
		"\n\b\3\b\3\b\5\bu\n\b\3\b\3\b\3\t\3\t\5\t{\n\t\3\t\3\t\5\t\177\n\t\3\t"+
		"\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\f\7\f\u008a\n\f\f\f\16\f\u008d\13\f\3"+
		"\f\5\f\u0090\n\f\3\r\5\r\u0093\n\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\5"+
		"\16\u009c\n\16\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\21\7\21\u00a6\n\21"+
		"\f\21\16\21\u00a9\13\21\3\21\3\21\3\22\3\22\3\23\3\23\3\23\3\24\3\24\3"+
		"\24\7\24\u00b5\n\24\f\24\16\24\u00b8\13\24\3\25\3\25\3\25\5\25\u00bd\n"+
		"\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\7\26\u00c9\n\26"+
		"\f\26\16\26\u00cc\13\26\3\26\3\26\5\26\u00d0\n\26\3\26\3\26\3\27\3\27"+
		"\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\5\27\u00de\n\27\3\27\3\27\3\27"+
		"\3\27\3\27\3\27\3\27\3\27\5\27\u00e8\n\27\5\27\u00ea\n\27\3\27\3\27\3"+
		"\27\3\27\3\27\3\27\7\27\u00f2\n\27\f\27\16\27\u00f5\13\27\3\30\3\30\3"+
		"\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\33\7\33\u0102\n\33\f\33\16\33"+
		"\u0105\13\33\3\33\5\33\u0108\n\33\3\33\5\33\u010b\n\33\3\34\3\34\3\35"+
		"\3\35\3\35\3\35\3\35\3\35\3\35\3\35\3\35\3\35\5\35\u0119\n\35\3\36\3\36"+
		"\3\37\3\37\3\37\2\3, \2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,."+
		"\60\62\64\668:<\2\5\3\2\37$\3\2\21\25\4\2\7\n//\2\u0124\2?\3\2\2\2\4I"+
		"\3\2\2\2\6Y\3\2\2\2\ba\3\2\2\2\nf\3\2\2\2\fl\3\2\2\2\16n\3\2\2\2\20x\3"+
		"\2\2\2\22\u0082\3\2\2\2\24\u0084\3\2\2\2\26\u008f\3\2\2\2\30\u0092\3\2"+
		"\2\2\32\u009b\3\2\2\2\34\u009d\3\2\2\2\36\u009f\3\2\2\2 \u00a2\3\2\2\2"+
		"\"\u00ac\3\2\2\2$\u00ae\3\2\2\2&\u00b1\3\2\2\2(\u00b9\3\2\2\2*\u00be\3"+
		"\2\2\2,\u00e9\3\2\2\2.\u00f6\3\2\2\2\60\u00fa\3\2\2\2\62\u00fc\3\2\2\2"+
		"\64\u00fe\3\2\2\2\66\u010c\3\2\2\28\u0118\3\2\2\2:\u011a\3\2\2\2<\u011c"+
		"\3\2\2\2>@\5\4\3\2?>\3\2\2\2?@\3\2\2\2@D\3\2\2\2AC\5\6\4\2BA\3\2\2\2C"+
		"F\3\2\2\2DB\3\2\2\2DE\3\2\2\2EG\3\2\2\2FD\3\2\2\2GH\7\2\2\3H\3\3\2\2\2"+
		"IJ\7.\2\2JK\5:\36\2K\5\3\2\2\2LM\7/\2\2MN\5\b\5\2NO\7*\2\2OP\5\n\6\2P"+
		"Q\7-\2\2QZ\3\2\2\2RS\7/\2\2ST\7(\2\2TU\7)\2\2UV\7*\2\2VW\5\n\6\2WX\7-"+
		"\2\2XZ\3\2\2\2YL\3\2\2\2YR\3\2\2\2Z\7\3\2\2\2[\\\5<\37\2\\]\7&\2\2]b\3"+
		"\2\2\2^_\5<\37\2_`\7\'\2\2`b\3\2\2\2a[\3\2\2\2a^\3\2\2\2b\t\3\2\2\2ce"+
		"\5\f\7\2dc\3\2\2\2eh\3\2\2\2fd\3\2\2\2fg\3\2\2\2g\13\3\2\2\2hf\3\2\2\2"+
		"im\5\16\b\2jm\5\20\t\2km\5*\26\2li\3\2\2\2lj\3\2\2\2lk\3\2\2\2m\r\3\2"+
		"\2\2np\7\31\2\2oq\5\22\n\2po\3\2\2\2pq\3\2\2\2qr\3\2\2\2rt\7\27\2\2su"+
		"\7\36\2\2ts\3\2\2\2tu\3\2\2\2uv\3\2\2\2vw\5\64\33\2w\17\3\2\2\2xz\7\32"+
		"\2\2y{\5\24\13\2zy\3\2\2\2z{\3\2\2\2{|\3\2\2\2|~\7\30\2\2}\177\7\36\2"+
		"\2~}\3\2\2\2~\177\3\2\2\2\177\u0080\3\2\2\2\u0080\u0081\5\64\33\2\u0081"+
		"\21\3\2\2\2\u0082\u0083\5\26\f\2\u0083\23\3\2\2\2\u0084\u0085\5\26\f\2"+
		"\u0085\25\3\2\2\2\u0086\u008b\5\30\r\2\u0087\u0088\7\60\2\2\u0088\u008a"+
		"\5\30\r\2\u0089\u0087\3\2\2\2\u008a\u008d\3\2\2\2\u008b\u0089\3\2\2\2"+
		"\u008b\u008c\3\2\2\2\u008c\u0090\3\2\2\2\u008d\u008b\3\2\2\2\u008e\u0090"+
		"\5\32\16\2\u008f\u0086\3\2\2\2\u008f\u008e\3\2\2\2\u0090\27\3\2\2\2\u0091"+
		"\u0093\5\32\16\2\u0092\u0091\3\2\2\2\u0092\u0093\3\2\2\2\u0093\u0094\3"+
		"\2\2\2\u0094\u0095\58\35\2\u0095\31\3\2\2\2\u0096\u0097\5\34\17\2\u0097"+
		"\u0098\5\36\20\2\u0098\u009c\3\2\2\2\u0099\u009c\5\36\20\2\u009a\u009c"+
		"\5\34\17\2\u009b\u0096\3\2\2\2\u009b\u0099\3\2\2\2\u009b\u009a\3\2\2\2"+
		"\u009c\33\3\2\2\2\u009d\u009e\5<\37\2\u009e\35\3\2\2\2\u009f\u00a0\7\35"+
		"\2\2\u00a0\u00a1\5<\37\2\u00a1\37\3\2\2\2\u00a2\u00a7\5\"\22\2\u00a3\u00a4"+
		"\7\60\2\2\u00a4\u00a6\5\"\22\2\u00a5\u00a3\3\2\2\2\u00a6\u00a9\3\2\2\2"+
		"\u00a7\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00aa\3\2\2\2\u00a9\u00a7"+
		"\3\2\2\2\u00aa\u00ab\7%\2\2\u00ab!\3\2\2\2\u00ac\u00ad\t\2\2\2\u00ad#"+
		"\3\2\2\2\u00ae\u00af\7\34\2\2\u00af\u00b0\5&\24\2\u00b0%\3\2\2\2\u00b1"+
		"\u00b6\5(\25\2\u00b2\u00b3\7\60\2\2\u00b3\u00b5\5(\25\2\u00b4\u00b2\3"+
		"\2\2\2\u00b5\u00b8\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7"+
		"\'\3\2\2\2\u00b8\u00b6\3\2\2\2\u00b9\u00bc\5<\37\2\u00ba\u00bb\7\62\2"+
		"\2\u00bb\u00bd\5<\37\2\u00bc\u00ba\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd)"+
		"\3\2\2\2\u00be\u00bf\7\3\2\2\u00bf\u00c0\5,\27\2\u00c0\u00c1\7\4\2\2\u00c1"+
		"\u00ca\5\n\6\2\u00c2\u00c3\7\5\2\2\u00c3\u00c4\7\3\2\2\u00c4\u00c5\5,"+
		"\27\2\u00c5\u00c6\7\4\2\2\u00c6\u00c7\5\n\6\2\u00c7\u00c9\3\2\2\2\u00c8"+
		"\u00c2\3\2\2\2\u00c9\u00cc\3\2\2\2\u00ca\u00c8\3\2\2\2\u00ca\u00cb\3\2"+
		"\2\2\u00cb\u00cf\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cd\u00ce\7\5\2\2\u00ce"+
		"\u00d0\5\n\6\2\u00cf\u00cd\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00d1\3\2"+
		"\2\2\u00d1\u00d2\7-\2\2\u00d2+\3\2\2\2\u00d3\u00d4\b\27\1\2\u00d4\u00ea"+
		"\7\f\2\2\u00d5\u00ea\7\r\2\2\u00d6\u00d7\7\63\2\2\u00d7\u00d8\5,\27\2"+
		"\u00d8\u00d9\7\64\2\2\u00d9\u00ea\3\2\2\2\u00da\u00db\7\16\2\2\u00db\u00ea"+
		"\5,\27\7\u00dc\u00de\5\62\32\2\u00dd\u00dc\3\2\2\2\u00dd\u00de\3\2\2\2"+
		"\u00de\u00df\3\2\2\2\u00df\u00e0\5\64\33\2\u00e0\u00e1\7\6\2\2\u00e1\u00e2"+
		"\5.\30\2\u00e2\u00ea\3\2\2\2\u00e3\u00e7\7(\2\2\u00e4\u00e5\5\60\31\2"+
		"\u00e5\u00e6\5<\37\2\u00e6\u00e8\3\2\2\2\u00e7\u00e4\3\2\2\2\u00e7\u00e8"+
		"\3\2\2\2\u00e8\u00ea\3\2\2\2\u00e9\u00d3\3\2\2\2\u00e9\u00d5\3\2\2\2\u00e9"+
		"\u00d6\3\2\2\2\u00e9\u00da\3\2\2\2\u00e9\u00dd\3\2\2\2\u00e9\u00e3\3\2"+
		"\2\2\u00ea\u00f3\3\2\2\2\u00eb\u00ec\f\6\2\2\u00ec\u00ed\7\17\2\2\u00ed"+
		"\u00f2\5,\27\7\u00ee\u00ef\f\5\2\2\u00ef\u00f0\7\20\2\2\u00f0\u00f2\5"+
		",\27\6\u00f1\u00eb\3\2\2\2\u00f1\u00ee\3\2\2\2\u00f2\u00f5\3\2\2\2\u00f3"+
		"\u00f1\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4-\3\2\2\2\u00f5\u00f3\3\2\2\2"+
		"\u00f6\u00f7\5\60\31\2\u00f7\u00f8\5<\37\2\u00f8\u00f9\58\35\2\u00f9/"+
		"\3\2\2\2\u00fa\u00fb\t\3\2\2\u00fb\61\3\2\2\2\u00fc\u00fd\t\4\2\2\u00fd"+
		"\63\3\2\2\2\u00fe\u0103\5\66\34\2\u00ff\u0100\7\60\2\2\u0100\u0102\5\66"+
		"\34\2\u0101\u00ff\3\2\2\2\u0102\u0105\3\2\2\2\u0103\u0101\3\2\2\2\u0103"+
		"\u0104\3\2\2\2\u0104\u0107\3\2\2\2\u0105\u0103\3\2\2\2\u0106\u0108\5 "+
		"\21\2\u0107\u0106\3\2\2\2\u0107\u0108\3\2\2\2\u0108\u010a\3\2\2\2\u0109"+
		"\u010b\5$\23\2\u010a\u0109\3\2\2\2\u010a\u010b\3\2\2\2\u010b\65\3\2\2"+
		"\2\u010c\u010d\7\65\2\2\u010d\67\3\2\2\2\u010e\u010f\7\65\2\2\u010f\u0110"+
		"\7\61\2\2\u0110\u0111\7\65\2\2\u0111\u0112\7\61\2\2\u0112\u0119\7\65\2"+
		"\2\u0113\u0114\7\65\2\2\u0114\u0115\7\61\2\2\u0115\u0119\7\65\2\2\u0116"+
		"\u0119\7\65\2\2\u0117\u0119\7\67\2\2\u0118\u010e\3\2\2\2\u0118\u0113\3"+
		"\2\2\2\u0118\u0116\3\2\2\2\u0118\u0117\3\2\2\2\u01199\3\2\2\2\u011a\u011b"+
		"\7\67\2\2\u011b;\3\2\2\2\u011c\u011d\7\66\2\2\u011d=\3\2\2\2\36?DYafl"+
		"ptz~\u008b\u008f\u0092\u009b\u00a7\u00b6\u00bc\u00ca\u00cf\u00dd\u00e7"+
		"\u00e9\u00f1\u00f3\u0103\u0107\u010a\u0118";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}