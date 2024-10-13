// Generated from ./syntaxes/SFML.g4 by ANTLR 4.9.0-SNAPSHOT


import { ATN } from "antlr4ts/atn/ATN";
import { ATNDeserializer } from "antlr4ts/atn/ATNDeserializer";
import { FailedPredicateException } from "antlr4ts/FailedPredicateException";
import { NotNull } from "antlr4ts/Decorators";
import { NoViableAltException } from "antlr4ts/NoViableAltException";
import { Override } from "antlr4ts/Decorators";
import { Parser } from "antlr4ts/Parser";
import { ParserRuleContext } from "antlr4ts/ParserRuleContext";
import { ParserATNSimulator } from "antlr4ts/atn/ParserATNSimulator";
import { ParseTreeListener } from "antlr4ts/tree/ParseTreeListener";
import { ParseTreeVisitor } from "antlr4ts/tree/ParseTreeVisitor";
import { RecognitionException } from "antlr4ts/RecognitionException";
import { RuleContext } from "antlr4ts/RuleContext";
//import { RuleVersion } from "antlr4ts/RuleVersion";
import { TerminalNode } from "antlr4ts/tree/TerminalNode";
import { Token } from "antlr4ts/Token";
import { TokenStream } from "antlr4ts/TokenStream";
import { Vocabulary } from "antlr4ts/Vocabulary";
import { VocabularyImpl } from "antlr4ts/VocabularyImpl";

import * as Utils from "antlr4ts/misc/Utils";

import { SFMLListener } from "./SFMLListener";
import { SFMLVisitor } from "./SFMLVisitor";


export class SFMLParser extends Parser {
	public static readonly IF = 1;
	public static readonly THEN = 2;
	public static readonly ELSE = 3;
	public static readonly HAS = 4;
	public static readonly OVERALL = 5;
	public static readonly SOME = 6;
	public static readonly ONE = 7;
	public static readonly LONE = 8;
	public static readonly TRUE = 9;
	public static readonly FALSE = 10;
	public static readonly NOT = 11;
	public static readonly AND = 12;
	public static readonly OR = 13;
	public static readonly GT = 14;
	public static readonly GT_SYMBOL = 15;
	public static readonly LT = 16;
	public static readonly LT_SYMBOL = 17;
	public static readonly EQ = 18;
	public static readonly EQ_SYMBOL = 19;
	public static readonly LE = 20;
	public static readonly LE_SYMBOL = 21;
	public static readonly GE = 22;
	public static readonly GE_SYMBOL = 23;
	public static readonly MOVE = 24;
	public static readonly FROM = 25;
	public static readonly TO = 26;
	public static readonly INPUT = 27;
	public static readonly OUTPUT = 28;
	public static readonly WHERE = 29;
	public static readonly SLOTS = 30;
	public static readonly RETAIN = 31;
	public static readonly EACH = 32;
	public static readonly EXCEPT = 33;
	public static readonly FORGET = 34;
	public static readonly WITHOUT = 35;
	public static readonly WITH = 36;
	public static readonly TAG = 37;
	public static readonly HASHTAG = 38;
	public static readonly ROUND = 39;
	public static readonly ROBIN = 40;
	public static readonly BY = 41;
	public static readonly LABEL = 42;
	public static readonly BLOCK = 43;
	public static readonly TOP = 44;
	public static readonly BOTTOM = 45;
	public static readonly NORTH = 46;
	public static readonly EAST = 47;
	public static readonly SOUTH = 48;
	public static readonly WEST = 49;
	public static readonly SIDE = 50;
	public static readonly TICKS = 51;
	public static readonly TICK = 52;
	public static readonly SECONDS = 53;
	public static readonly REDSTONE = 54;
	public static readonly PULSE = 55;
	public static readonly DO = 56;
	public static readonly WORLD = 57;
	public static readonly PROGRAM = 58;
	public static readonly END = 59;
	public static readonly NAME = 60;
	public static readonly EVERY = 61;
	public static readonly COMMA = 62;
	public static readonly COLON = 63;
	public static readonly SLASH = 64;
	public static readonly DASH = 65;
	public static readonly LPAREN = 66;
	public static readonly RPAREN = 67;
	public static readonly IDENTIFIER = 68;
	public static readonly NUMBER = 69;
	public static readonly STRING = 70;
	public static readonly LINE_COMMENT = 71;
	public static readonly WS = 72;
	public static readonly UNUSED = 73;
	public static readonly RULE_program = 0;
	public static readonly RULE_name = 1;
	public static readonly RULE_trigger = 2;
	public static readonly RULE_interval = 3;
	public static readonly RULE_block = 4;
	public static readonly RULE_statement = 5;
	public static readonly RULE_forgetStatement = 6;
	public static readonly RULE_inputStatement = 7;
	public static readonly RULE_outputStatement = 8;
	public static readonly RULE_resourceExclusion = 9;
	public static readonly RULE_inputResourceLimits = 10;
	public static readonly RULE_outputResourceLimits = 11;
	public static readonly RULE_resourceLimits = 12;
	public static readonly RULE_resourceLimit = 13;
	public static readonly RULE_limit = 14;
	public static readonly RULE_quantity = 15;
	public static readonly RULE_retention = 16;
	public static readonly RULE_with = 17;
	public static readonly RULE_withClause = 18;
	public static readonly RULE_tagMatcher = 19;
	public static readonly RULE_sidequalifier = 20;
	public static readonly RULE_side = 21;
	public static readonly RULE_slotqualifier = 22;
	public static readonly RULE_rangeset = 23;
	public static readonly RULE_range = 24;
	public static readonly RULE_ifStatement = 25;
	public static readonly RULE_boolexpr = 26;
	public static readonly RULE_resourcecomparison = 27;
	public static readonly RULE_comparisonOp = 28;
	public static readonly RULE_setOp = 29;
	public static readonly RULE_labelAccess = 30;
	public static readonly RULE_roundrobin = 31;
	public static readonly RULE_label = 32;
	public static readonly RULE_resourceId = 33;
	public static readonly RULE_identifier = 34;
	public static readonly RULE_string = 35;
	public static readonly RULE_number = 36;
	// tslint:disable:no-trailing-whitespace
	public static readonly ruleNames: string[] = [
		"program", "name", "trigger", "interval", "block", "statement", "forgetStatement", 
		"inputStatement", "outputStatement", "resourceExclusion", "inputResourceLimits", 
		"outputResourceLimits", "resourceLimits", "resourceLimit", "limit", "quantity", 
		"retention", "with", "withClause", "tagMatcher", "sidequalifier", "side", 
		"slotqualifier", "rangeset", "range", "ifStatement", "boolexpr", "resourcecomparison", 
		"comparisonOp", "setOp", "labelAccess", "roundrobin", "label", "resourceId", 
		"identifier", "string", "number",
	];

	private static readonly _LITERAL_NAMES: Array<string | undefined> = [
		undefined, undefined, undefined, undefined, undefined, undefined, undefined, 
		undefined, undefined, undefined, undefined, undefined, undefined, undefined, 
		undefined, "'>'", undefined, "'<'", undefined, "'='", undefined, "'<='", 
		undefined, "'>='", undefined, undefined, undefined, undefined, undefined, 
		undefined, undefined, undefined, undefined, undefined, undefined, undefined, 
		undefined, undefined, "'#'", undefined, undefined, undefined, undefined, 
		undefined, undefined, undefined, undefined, undefined, undefined, undefined, 
		undefined, undefined, undefined, undefined, undefined, undefined, undefined, 
		undefined, undefined, undefined, undefined, undefined, "','", "':'", "'/'", 
		"'-'", "'('", "')'",
	];
	private static readonly _SYMBOLIC_NAMES: Array<string | undefined> = [
		undefined, "IF", "THEN", "ELSE", "HAS", "OVERALL", "SOME", "ONE", "LONE", 
		"TRUE", "FALSE", "NOT", "AND", "OR", "GT", "GT_SYMBOL", "LT", "LT_SYMBOL", 
		"EQ", "EQ_SYMBOL", "LE", "LE_SYMBOL", "GE", "GE_SYMBOL", "MOVE", "FROM", 
		"TO", "INPUT", "OUTPUT", "WHERE", "SLOTS", "RETAIN", "EACH", "EXCEPT", 
		"FORGET", "WITHOUT", "WITH", "TAG", "HASHTAG", "ROUND", "ROBIN", "BY", 
		"LABEL", "BLOCK", "TOP", "BOTTOM", "NORTH", "EAST", "SOUTH", "WEST", "SIDE", 
		"TICKS", "TICK", "SECONDS", "REDSTONE", "PULSE", "DO", "WORLD", "PROGRAM", 
		"END", "NAME", "EVERY", "COMMA", "COLON", "SLASH", "DASH", "LPAREN", "RPAREN", 
		"IDENTIFIER", "NUMBER", "STRING", "LINE_COMMENT", "WS", "UNUSED",
	];
	public static readonly VOCABULARY: Vocabulary = new VocabularyImpl(SFMLParser._LITERAL_NAMES, SFMLParser._SYMBOLIC_NAMES, []);

	// @Override
	// @NotNull
	public get vocabulary(): Vocabulary {
		return SFMLParser.VOCABULARY;
	}
	// tslint:enable:no-trailing-whitespace

	// @Override
	public get grammarFileName(): string { return "SFML.g4"; }

	// @Override
	public get ruleNames(): string[] { return SFMLParser.ruleNames; }

	// @Override
	public get serializedATN(): string { return SFMLParser._serializedATN; }

	protected createFailedPredicateException(predicate?: string, message?: string): FailedPredicateException {
		return new FailedPredicateException(this, predicate, message);
	}

	constructor(input: TokenStream) {
		super(input);
		this._interp = new ParserATNSimulator(SFMLParser._ATN, this);
	}
	// @RuleVersion(0)
	public program(): ProgramContext {
		let _localctx: ProgramContext = new ProgramContext(this._ctx, this.state);
		this.enterRule(_localctx, 0, SFMLParser.RULE_program);
		let _la: number;
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 75;
			this._errHandler.sync(this);
			_la = this._input.LA(1);
			if (_la === SFMLParser.NAME) {
				{
				this.state = 74;
				this.name();
				}
			}

			this.state = 80;
			this._errHandler.sync(this);
			_la = this._input.LA(1);
			while (_la === SFMLParser.EVERY) {
				{
				{
				this.state = 77;
				this.trigger();
				}
				}
				this.state = 82;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
			}
			this.state = 83;
			this.match(SFMLParser.EOF);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public name(): NameContext {
		let _localctx: NameContext = new NameContext(this._ctx, this.state);
		this.enterRule(_localctx, 2, SFMLParser.RULE_name);
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 85;
			this.match(SFMLParser.NAME);
			this.state = 86;
			this.string();
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public trigger(): TriggerContext {
		let _localctx: TriggerContext = new TriggerContext(this._ctx, this.state);
		this.enterRule(_localctx, 4, SFMLParser.RULE_trigger);
		try {
			this.state = 101;
			this._errHandler.sync(this);
			switch ( this.interpreter.adaptivePredict(this._input, 2, this._ctx) ) {
			case 1:
				_localctx = new TimerTriggerContext(_localctx);
				this.enterOuterAlt(_localctx, 1);
				{
				this.state = 88;
				this.match(SFMLParser.EVERY);
				this.state = 89;
				this.interval();
				this.state = 90;
				this.match(SFMLParser.DO);
				this.state = 91;
				this.block();
				this.state = 92;
				this.match(SFMLParser.END);
				}
				break;

			case 2:
				_localctx = new PulseTriggerContext(_localctx);
				this.enterOuterAlt(_localctx, 2);
				{
				this.state = 94;
				this.match(SFMLParser.EVERY);
				this.state = 95;
				this.match(SFMLParser.REDSTONE);
				this.state = 96;
				this.match(SFMLParser.PULSE);
				this.state = 97;
				this.match(SFMLParser.DO);
				this.state = 98;
				this.block();
				this.state = 99;
				this.match(SFMLParser.END);
				}
				break;
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public interval(): IntervalContext {
		let _localctx: IntervalContext = new IntervalContext(this._ctx, this.state);
		this.enterRule(_localctx, 6, SFMLParser.RULE_interval);
		try {
			this.state = 110;
			this._errHandler.sync(this);
			switch ( this.interpreter.adaptivePredict(this._input, 3, this._ctx) ) {
			case 1:
				_localctx = new TickContext(_localctx);
				this.enterOuterAlt(_localctx, 1);
				{
				this.state = 103;
				this.match(SFMLParser.TICK);
				}
				break;

			case 2:
				_localctx = new TicksContext(_localctx);
				this.enterOuterAlt(_localctx, 2);
				{
				this.state = 104;
				this.number();
				this.state = 105;
				this.match(SFMLParser.TICKS);
				}
				break;

			case 3:
				_localctx = new SecondsContext(_localctx);
				this.enterOuterAlt(_localctx, 3);
				{
				this.state = 107;
				this.number();
				this.state = 108;
				this.match(SFMLParser.SECONDS);
				}
				break;
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public block(): BlockContext {
		let _localctx: BlockContext = new BlockContext(this._ctx, this.state);
		this.enterRule(_localctx, 8, SFMLParser.RULE_block);
		let _la: number;
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 115;
			this._errHandler.sync(this);
			_la = this._input.LA(1);
			while ((((_la) & ~0x1F) === 0 && ((1 << _la) & ((1 << SFMLParser.IF) | (1 << SFMLParser.FROM) | (1 << SFMLParser.TO) | (1 << SFMLParser.INPUT) | (1 << SFMLParser.OUTPUT))) !== 0) || _la === SFMLParser.FORGET) {
				{
				{
				this.state = 112;
				this.statement();
				}
				}
				this.state = 117;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
			}
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public statement(): StatementContext {
		let _localctx: StatementContext = new StatementContext(this._ctx, this.state);
		this.enterRule(_localctx, 10, SFMLParser.RULE_statement);
		try {
			this.state = 122;
			this._errHandler.sync(this);
			switch (this._input.LA(1)) {
			case SFMLParser.FROM:
			case SFMLParser.INPUT:
				this.enterOuterAlt(_localctx, 1);
				{
				this.state = 118;
				this.inputStatement();
				}
				break;
			case SFMLParser.TO:
			case SFMLParser.OUTPUT:
				this.enterOuterAlt(_localctx, 2);
				{
				this.state = 119;
				this.outputStatement();
				}
				break;
			case SFMLParser.IF:
				this.enterOuterAlt(_localctx, 3);
				{
				this.state = 120;
				this.ifStatement();
				}
				break;
			case SFMLParser.FORGET:
				this.enterOuterAlt(_localctx, 4);
				{
				this.state = 121;
				this.forgetStatement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public forgetStatement(): ForgetStatementContext {
		let _localctx: ForgetStatementContext = new ForgetStatementContext(this._ctx, this.state);
		this.enterRule(_localctx, 12, SFMLParser.RULE_forgetStatement);
		let _la: number;
		try {
			let _alt: number;
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 124;
			this.match(SFMLParser.FORGET);
			this.state = 126;
			this._errHandler.sync(this);
			_la = this._input.LA(1);
			if (((((_la - 54)) & ~0x1F) === 0 && ((1 << (_la - 54)) & ((1 << (SFMLParser.REDSTONE - 54)) | (1 << (SFMLParser.IDENTIFIER - 54)) | (1 << (SFMLParser.STRING - 54)))) !== 0)) {
				{
				this.state = 125;
				this.label();
				}
			}

			this.state = 132;
			this._errHandler.sync(this);
			_alt = this.interpreter.adaptivePredict(this._input, 7, this._ctx);
			while (_alt !== 2 && _alt !== ATN.INVALID_ALT_NUMBER) {
				if (_alt === 1) {
					{
					{
					this.state = 128;
					this.match(SFMLParser.COMMA);
					this.state = 129;
					this.label();
					}
					}
				}
				this.state = 134;
				this._errHandler.sync(this);
				_alt = this.interpreter.adaptivePredict(this._input, 7, this._ctx);
			}
			this.state = 136;
			this._errHandler.sync(this);
			_la = this._input.LA(1);
			if (_la === SFMLParser.COMMA) {
				{
				this.state = 135;
				this.match(SFMLParser.COMMA);
				}
			}

			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public inputStatement(): InputStatementContext {
		let _localctx: InputStatementContext = new InputStatementContext(this._ctx, this.state);
		this.enterRule(_localctx, 14, SFMLParser.RULE_inputStatement);
		let _la: number;
		try {
			this.state = 162;
			this._errHandler.sync(this);
			switch (this._input.LA(1)) {
			case SFMLParser.INPUT:
				this.enterOuterAlt(_localctx, 1);
				{
				this.state = 138;
				this.match(SFMLParser.INPUT);
				this.state = 140;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
				if (((((_la - 31)) & ~0x1F) === 0 && ((1 << (_la - 31)) & ((1 << (SFMLParser.RETAIN - 31)) | (1 << (SFMLParser.WITHOUT - 31)) | (1 << (SFMLParser.WITH - 31)) | (1 << (SFMLParser.REDSTONE - 31)))) !== 0) || ((((_la - 68)) & ~0x1F) === 0 && ((1 << (_la - 68)) & ((1 << (SFMLParser.IDENTIFIER - 68)) | (1 << (SFMLParser.NUMBER - 68)) | (1 << (SFMLParser.STRING - 68)))) !== 0)) {
					{
					this.state = 139;
					this.inputResourceLimits();
					}
				}

				this.state = 143;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
				if (_la === SFMLParser.EXCEPT) {
					{
					this.state = 142;
					this.resourceExclusion();
					}
				}

				this.state = 145;
				this.match(SFMLParser.FROM);
				this.state = 147;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
				if (_la === SFMLParser.EACH) {
					{
					this.state = 146;
					this.match(SFMLParser.EACH);
					}
				}

				this.state = 149;
				this.labelAccess();
				}
				break;
			case SFMLParser.FROM:
				this.enterOuterAlt(_localctx, 2);
				{
				this.state = 150;
				this.match(SFMLParser.FROM);
				this.state = 152;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
				if (_la === SFMLParser.EACH) {
					{
					this.state = 151;
					this.match(SFMLParser.EACH);
					}
				}

				this.state = 154;
				this.labelAccess();
				this.state = 155;
				this.match(SFMLParser.INPUT);
				this.state = 157;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
				if (((((_la - 31)) & ~0x1F) === 0 && ((1 << (_la - 31)) & ((1 << (SFMLParser.RETAIN - 31)) | (1 << (SFMLParser.WITHOUT - 31)) | (1 << (SFMLParser.WITH - 31)) | (1 << (SFMLParser.REDSTONE - 31)))) !== 0) || ((((_la - 68)) & ~0x1F) === 0 && ((1 << (_la - 68)) & ((1 << (SFMLParser.IDENTIFIER - 68)) | (1 << (SFMLParser.NUMBER - 68)) | (1 << (SFMLParser.STRING - 68)))) !== 0)) {
					{
					this.state = 156;
					this.inputResourceLimits();
					}
				}

				this.state = 160;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
				if (_la === SFMLParser.EXCEPT) {
					{
					this.state = 159;
					this.resourceExclusion();
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public outputStatement(): OutputStatementContext {
		let _localctx: OutputStatementContext = new OutputStatementContext(this._ctx, this.state);
		this.enterRule(_localctx, 16, SFMLParser.RULE_outputStatement);
		let _la: number;
		try {
			this.state = 188;
			this._errHandler.sync(this);
			switch (this._input.LA(1)) {
			case SFMLParser.OUTPUT:
				this.enterOuterAlt(_localctx, 1);
				{
				this.state = 164;
				this.match(SFMLParser.OUTPUT);
				this.state = 166;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
				if (((((_la - 31)) & ~0x1F) === 0 && ((1 << (_la - 31)) & ((1 << (SFMLParser.RETAIN - 31)) | (1 << (SFMLParser.WITHOUT - 31)) | (1 << (SFMLParser.WITH - 31)) | (1 << (SFMLParser.REDSTONE - 31)))) !== 0) || ((((_la - 68)) & ~0x1F) === 0 && ((1 << (_la - 68)) & ((1 << (SFMLParser.IDENTIFIER - 68)) | (1 << (SFMLParser.NUMBER - 68)) | (1 << (SFMLParser.STRING - 68)))) !== 0)) {
					{
					this.state = 165;
					this.outputResourceLimits();
					}
				}

				this.state = 169;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
				if (_la === SFMLParser.EXCEPT) {
					{
					this.state = 168;
					this.resourceExclusion();
					}
				}

				this.state = 171;
				this.match(SFMLParser.TO);
				this.state = 173;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
				if (_la === SFMLParser.EACH) {
					{
					this.state = 172;
					this.match(SFMLParser.EACH);
					}
				}

				this.state = 175;
				this.labelAccess();
				}
				break;
			case SFMLParser.TO:
				this.enterOuterAlt(_localctx, 2);
				{
				this.state = 176;
				this.match(SFMLParser.TO);
				this.state = 178;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
				if (_la === SFMLParser.EACH) {
					{
					this.state = 177;
					this.match(SFMLParser.EACH);
					}
				}

				this.state = 180;
				this.labelAccess();
				this.state = 181;
				this.match(SFMLParser.OUTPUT);
				this.state = 183;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
				if (((((_la - 31)) & ~0x1F) === 0 && ((1 << (_la - 31)) & ((1 << (SFMLParser.RETAIN - 31)) | (1 << (SFMLParser.WITHOUT - 31)) | (1 << (SFMLParser.WITH - 31)) | (1 << (SFMLParser.REDSTONE - 31)))) !== 0) || ((((_la - 68)) & ~0x1F) === 0 && ((1 << (_la - 68)) & ((1 << (SFMLParser.IDENTIFIER - 68)) | (1 << (SFMLParser.NUMBER - 68)) | (1 << (SFMLParser.STRING - 68)))) !== 0)) {
					{
					this.state = 182;
					this.outputResourceLimits();
					}
				}

				this.state = 186;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
				if (_la === SFMLParser.EXCEPT) {
					{
					this.state = 185;
					this.resourceExclusion();
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public resourceExclusion(): ResourceExclusionContext {
		let _localctx: ResourceExclusionContext = new ResourceExclusionContext(this._ctx, this.state);
		this.enterRule(_localctx, 18, SFMLParser.RULE_resourceExclusion);
		let _la: number;
		try {
			let _alt: number;
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 190;
			this.match(SFMLParser.EXCEPT);
			this.state = 191;
			this.resourceId();
			this.state = 196;
			this._errHandler.sync(this);
			_alt = this.interpreter.adaptivePredict(this._input, 23, this._ctx);
			while (_alt !== 2 && _alt !== ATN.INVALID_ALT_NUMBER) {
				if (_alt === 1) {
					{
					{
					this.state = 192;
					this.match(SFMLParser.COMMA);
					this.state = 193;
					this.resourceId();
					}
					}
				}
				this.state = 198;
				this._errHandler.sync(this);
				_alt = this.interpreter.adaptivePredict(this._input, 23, this._ctx);
			}
			this.state = 200;
			this._errHandler.sync(this);
			_la = this._input.LA(1);
			if (_la === SFMLParser.COMMA) {
				{
				this.state = 199;
				this.match(SFMLParser.COMMA);
				}
			}

			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public inputResourceLimits(): InputResourceLimitsContext {
		let _localctx: InputResourceLimitsContext = new InputResourceLimitsContext(this._ctx, this.state);
		this.enterRule(_localctx, 20, SFMLParser.RULE_inputResourceLimits);
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 202;
			this.resourceLimits();
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public outputResourceLimits(): OutputResourceLimitsContext {
		let _localctx: OutputResourceLimitsContext = new OutputResourceLimitsContext(this._ctx, this.state);
		this.enterRule(_localctx, 22, SFMLParser.RULE_outputResourceLimits);
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 204;
			this.resourceLimits();
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public resourceLimits(): ResourceLimitsContext {
		let _localctx: ResourceLimitsContext = new ResourceLimitsContext(this._ctx, this.state);
		this.enterRule(_localctx, 24, SFMLParser.RULE_resourceLimits);
		let _la: number;
		try {
			let _alt: number;
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 206;
			this.resourceLimit();
			this.state = 211;
			this._errHandler.sync(this);
			_alt = this.interpreter.adaptivePredict(this._input, 25, this._ctx);
			while (_alt !== 2 && _alt !== ATN.INVALID_ALT_NUMBER) {
				if (_alt === 1) {
					{
					{
					this.state = 207;
					this.match(SFMLParser.COMMA);
					this.state = 208;
					this.resourceLimit();
					}
					}
				}
				this.state = 213;
				this._errHandler.sync(this);
				_alt = this.interpreter.adaptivePredict(this._input, 25, this._ctx);
			}
			this.state = 215;
			this._errHandler.sync(this);
			_la = this._input.LA(1);
			if (_la === SFMLParser.COMMA) {
				{
				this.state = 214;
				this.match(SFMLParser.COMMA);
				}
			}

			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public resourceLimit(): ResourceLimitContext {
		let _localctx: ResourceLimitContext = new ResourceLimitContext(this._ctx, this.state);
		this.enterRule(_localctx, 26, SFMLParser.RULE_resourceLimit);
		let _la: number;
		try {
			this.state = 229;
			this._errHandler.sync(this);
			switch ( this.interpreter.adaptivePredict(this._input, 30, this._ctx) ) {
			case 1:
				this.enterOuterAlt(_localctx, 1);
				{
				this.state = 218;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
				if (_la === SFMLParser.RETAIN || _la === SFMLParser.NUMBER) {
					{
					this.state = 217;
					this.limit();
					}
				}

				this.state = 220;
				this.resourceId();
				this.state = 222;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
				if (_la === SFMLParser.WITHOUT || _la === SFMLParser.WITH) {
					{
					this.state = 221;
					this.with();
					}
				}

				}
				break;

			case 2:
				this.enterOuterAlt(_localctx, 2);
				{
				this.state = 224;
				this.limit();
				this.state = 226;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
				if (_la === SFMLParser.WITHOUT || _la === SFMLParser.WITH) {
					{
					this.state = 225;
					this.with();
					}
				}

				}
				break;

			case 3:
				this.enterOuterAlt(_localctx, 3);
				{
				this.state = 228;
				this.with();
				}
				break;
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public limit(): LimitContext {
		let _localctx: LimitContext = new LimitContext(this._ctx, this.state);
		this.enterRule(_localctx, 28, SFMLParser.RULE_limit);
		try {
			this.state = 236;
			this._errHandler.sync(this);
			switch ( this.interpreter.adaptivePredict(this._input, 31, this._ctx) ) {
			case 1:
				_localctx = new QuantityRetentionLimitContext(_localctx);
				this.enterOuterAlt(_localctx, 1);
				{
				this.state = 231;
				this.quantity();
				this.state = 232;
				this.retention();
				}
				break;

			case 2:
				_localctx = new RetentionLimitContext(_localctx);
				this.enterOuterAlt(_localctx, 2);
				{
				this.state = 234;
				this.retention();
				}
				break;

			case 3:
				_localctx = new QuantityLimitContext(_localctx);
				this.enterOuterAlt(_localctx, 3);
				{
				this.state = 235;
				this.quantity();
				}
				break;
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public quantity(): QuantityContext {
		let _localctx: QuantityContext = new QuantityContext(this._ctx, this.state);
		this.enterRule(_localctx, 30, SFMLParser.RULE_quantity);
		let _la: number;
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 238;
			this.number();
			this.state = 240;
			this._errHandler.sync(this);
			_la = this._input.LA(1);
			if (_la === SFMLParser.EACH) {
				{
				this.state = 239;
				this.match(SFMLParser.EACH);
				}
			}

			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public retention(): RetentionContext {
		let _localctx: RetentionContext = new RetentionContext(this._ctx, this.state);
		this.enterRule(_localctx, 32, SFMLParser.RULE_retention);
		let _la: number;
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 242;
			this.match(SFMLParser.RETAIN);
			this.state = 243;
			this.number();
			this.state = 245;
			this._errHandler.sync(this);
			_la = this._input.LA(1);
			if (_la === SFMLParser.EACH) {
				{
				this.state = 244;
				this.match(SFMLParser.EACH);
				}
			}

			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public with(): WithContext {
		let _localctx: WithContext = new WithContext(this._ctx, this.state);
		this.enterRule(_localctx, 34, SFMLParser.RULE_with);
		try {
			this.state = 251;
			this._errHandler.sync(this);
			switch (this._input.LA(1)) {
			case SFMLParser.WITH:
				this.enterOuterAlt(_localctx, 1);
				{
				this.state = 247;
				this.match(SFMLParser.WITH);
				this.state = 248;
				this.withClause(0);
				}
				break;
			case SFMLParser.WITHOUT:
				this.enterOuterAlt(_localctx, 2);
				{
				this.state = 249;
				this.match(SFMLParser.WITHOUT);
				this.state = 250;
				this.withClause(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}

	public withClause(): WithClauseContext;
	public withClause(_p: number): WithClauseContext;
	// @RuleVersion(0)
	public withClause(_p?: number): WithClauseContext {
		if (_p === undefined) {
			_p = 0;
		}

		let _parentctx: ParserRuleContext = this._ctx;
		let _parentState: number = this.state;
		let _localctx: WithClauseContext = new WithClauseContext(this._ctx, _parentState);
		let _prevctx: WithClauseContext = _localctx;
		let _startState: number = 36;
		this.enterRecursionRule(_localctx, 36, SFMLParser.RULE_withClause, _p);
		let _la: number;
		try {
			let _alt: number;
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 268;
			this._errHandler.sync(this);
			switch (this._input.LA(1)) {
			case SFMLParser.LPAREN:
				{
				_localctx = new WithParenContext(_localctx);
				this._ctx = _localctx;
				_prevctx = _localctx;

				this.state = 254;
				this.match(SFMLParser.LPAREN);
				this.state = 255;
				this.withClause(0);
				this.state = 256;
				this.match(SFMLParser.RPAREN);
				}
				break;
			case SFMLParser.NOT:
				{
				_localctx = new WithNegationContext(_localctx);
				this._ctx = _localctx;
				_prevctx = _localctx;
				this.state = 258;
				this.match(SFMLParser.NOT);
				this.state = 259;
				this.withClause(4);
				}
				break;
			case SFMLParser.TAG:
			case SFMLParser.HASHTAG:
				{
				_localctx = new WithTagContext(_localctx);
				this._ctx = _localctx;
				_prevctx = _localctx;
				this.state = 265;
				this._errHandler.sync(this);
				switch (this._input.LA(1)) {
				case SFMLParser.TAG:
					{
					this.state = 260;
					this.match(SFMLParser.TAG);
					this.state = 262;
					this._errHandler.sync(this);
					_la = this._input.LA(1);
					if (_la === SFMLParser.HASHTAG) {
						{
						this.state = 261;
						this.match(SFMLParser.HASHTAG);
						}
					}

					}
					break;
				case SFMLParser.HASHTAG:
					{
					this.state = 264;
					this.match(SFMLParser.HASHTAG);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				this.state = 267;
				this.tagMatcher();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			this._ctx._stop = this._input.tryLT(-1);
			this.state = 278;
			this._errHandler.sync(this);
			_alt = this.interpreter.adaptivePredict(this._input, 39, this._ctx);
			while (_alt !== 2 && _alt !== ATN.INVALID_ALT_NUMBER) {
				if (_alt === 1) {
					if (this._parseListeners != null) {
						this.triggerExitRuleEvent();
					}
					_prevctx = _localctx;
					{
					this.state = 276;
					this._errHandler.sync(this);
					switch ( this.interpreter.adaptivePredict(this._input, 38, this._ctx) ) {
					case 1:
						{
						_localctx = new WithConjunctionContext(new WithClauseContext(_parentctx, _parentState));
						this.pushNewRecursionContext(_localctx, _startState, SFMLParser.RULE_withClause);
						this.state = 270;
						if (!(this.precpred(this._ctx, 3))) {
							throw this.createFailedPredicateException("this.precpred(this._ctx, 3)");
						}
						this.state = 271;
						this.match(SFMLParser.AND);
						this.state = 272;
						this.withClause(4);
						}
						break;

					case 2:
						{
						_localctx = new WithDisjunctionContext(new WithClauseContext(_parentctx, _parentState));
						this.pushNewRecursionContext(_localctx, _startState, SFMLParser.RULE_withClause);
						this.state = 273;
						if (!(this.precpred(this._ctx, 2))) {
							throw this.createFailedPredicateException("this.precpred(this._ctx, 2)");
						}
						this.state = 274;
						this.match(SFMLParser.OR);
						this.state = 275;
						this.withClause(3);
						}
						break;
					}
					}
				}
				this.state = 280;
				this._errHandler.sync(this);
				_alt = this.interpreter.adaptivePredict(this._input, 39, this._ctx);
			}
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public tagMatcher(): TagMatcherContext {
		let _localctx: TagMatcherContext = new TagMatcherContext(this._ctx, this.state);
		this.enterRule(_localctx, 38, SFMLParser.RULE_tagMatcher);
		try {
			let _alt: number;
			this.state = 299;
			this._errHandler.sync(this);
			switch ( this.interpreter.adaptivePredict(this._input, 42, this._ctx) ) {
			case 1:
				this.enterOuterAlt(_localctx, 1);
				{
				this.state = 281;
				this.identifier();
				this.state = 282;
				this.match(SFMLParser.COLON);
				this.state = 283;
				this.identifier();
				this.state = 288;
				this._errHandler.sync(this);
				_alt = this.interpreter.adaptivePredict(this._input, 40, this._ctx);
				while (_alt !== 2 && _alt !== ATN.INVALID_ALT_NUMBER) {
					if (_alt === 1) {
						{
						{
						this.state = 284;
						this.match(SFMLParser.SLASH);
						this.state = 285;
						this.identifier();
						}
						}
					}
					this.state = 290;
					this._errHandler.sync(this);
					_alt = this.interpreter.adaptivePredict(this._input, 40, this._ctx);
				}
				}
				break;

			case 2:
				this.enterOuterAlt(_localctx, 2);
				{
				this.state = 291;
				this.identifier();
				this.state = 296;
				this._errHandler.sync(this);
				_alt = this.interpreter.adaptivePredict(this._input, 41, this._ctx);
				while (_alt !== 2 && _alt !== ATN.INVALID_ALT_NUMBER) {
					if (_alt === 1) {
						{
						{
						this.state = 292;
						this.match(SFMLParser.SLASH);
						this.state = 293;
						this.identifier();
						}
						}
					}
					this.state = 298;
					this._errHandler.sync(this);
					_alt = this.interpreter.adaptivePredict(this._input, 41, this._ctx);
				}
				}
				break;
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public sidequalifier(): SidequalifierContext {
		let _localctx: SidequalifierContext = new SidequalifierContext(this._ctx, this.state);
		this.enterRule(_localctx, 40, SFMLParser.RULE_sidequalifier);
		let _la: number;
		try {
			this.state = 313;
			this._errHandler.sync(this);
			switch (this._input.LA(1)) {
			case SFMLParser.EACH:
				_localctx = new EachSideContext(_localctx);
				this.enterOuterAlt(_localctx, 1);
				{
				this.state = 301;
				this.match(SFMLParser.EACH);
				this.state = 302;
				this.match(SFMLParser.SIDE);
				}
				break;
			case SFMLParser.TOP:
			case SFMLParser.BOTTOM:
			case SFMLParser.NORTH:
			case SFMLParser.EAST:
			case SFMLParser.SOUTH:
			case SFMLParser.WEST:
				_localctx = new ListedSidesContext(_localctx);
				this.enterOuterAlt(_localctx, 2);
				{
				this.state = 303;
				this.side();
				this.state = 308;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
				while (_la === SFMLParser.COMMA) {
					{
					{
					this.state = 304;
					this.match(SFMLParser.COMMA);
					this.state = 305;
					this.side();
					}
					}
					this.state = 310;
					this._errHandler.sync(this);
					_la = this._input.LA(1);
				}
				this.state = 311;
				this.match(SFMLParser.SIDE);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public side(): SideContext {
		let _localctx: SideContext = new SideContext(this._ctx, this.state);
		this.enterRule(_localctx, 42, SFMLParser.RULE_side);
		let _la: number;
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 315;
			_la = this._input.LA(1);
			if (!(((((_la - 44)) & ~0x1F) === 0 && ((1 << (_la - 44)) & ((1 << (SFMLParser.TOP - 44)) | (1 << (SFMLParser.BOTTOM - 44)) | (1 << (SFMLParser.NORTH - 44)) | (1 << (SFMLParser.EAST - 44)) | (1 << (SFMLParser.SOUTH - 44)) | (1 << (SFMLParser.WEST - 44)))) !== 0))) {
			this._errHandler.recoverInline(this);
			} else {
				if (this._input.LA(1) === Token.EOF) {
					this.matchedEOF = true;
				}

				this._errHandler.reportMatch(this);
				this.consume();
			}
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public slotqualifier(): SlotqualifierContext {
		let _localctx: SlotqualifierContext = new SlotqualifierContext(this._ctx, this.state);
		this.enterRule(_localctx, 44, SFMLParser.RULE_slotqualifier);
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 317;
			this.match(SFMLParser.SLOTS);
			this.state = 318;
			this.rangeset();
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public rangeset(): RangesetContext {
		let _localctx: RangesetContext = new RangesetContext(this._ctx, this.state);
		this.enterRule(_localctx, 46, SFMLParser.RULE_rangeset);
		let _la: number;
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 320;
			this.range();
			this.state = 325;
			this._errHandler.sync(this);
			_la = this._input.LA(1);
			while (_la === SFMLParser.COMMA) {
				{
				{
				this.state = 321;
				this.match(SFMLParser.COMMA);
				this.state = 322;
				this.range();
				}
				}
				this.state = 327;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
			}
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public range(): RangeContext {
		let _localctx: RangeContext = new RangeContext(this._ctx, this.state);
		this.enterRule(_localctx, 48, SFMLParser.RULE_range);
		let _la: number;
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 328;
			this.number();
			this.state = 331;
			this._errHandler.sync(this);
			_la = this._input.LA(1);
			if (_la === SFMLParser.DASH) {
				{
				this.state = 329;
				this.match(SFMLParser.DASH);
				this.state = 330;
				this.number();
				}
			}

			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public ifStatement(): IfStatementContext {
		let _localctx: IfStatementContext = new IfStatementContext(this._ctx, this.state);
		this.enterRule(_localctx, 50, SFMLParser.RULE_ifStatement);
		let _la: number;
		try {
			let _alt: number;
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 333;
			this.match(SFMLParser.IF);
			this.state = 334;
			this.boolexpr(0);
			this.state = 335;
			this.match(SFMLParser.THEN);
			this.state = 336;
			this.block();
			this.state = 345;
			this._errHandler.sync(this);
			_alt = this.interpreter.adaptivePredict(this._input, 47, this._ctx);
			while (_alt !== 2 && _alt !== ATN.INVALID_ALT_NUMBER) {
				if (_alt === 1) {
					{
					{
					this.state = 337;
					this.match(SFMLParser.ELSE);
					this.state = 338;
					this.match(SFMLParser.IF);
					this.state = 339;
					this.boolexpr(0);
					this.state = 340;
					this.match(SFMLParser.THEN);
					this.state = 341;
					this.block();
					}
					}
				}
				this.state = 347;
				this._errHandler.sync(this);
				_alt = this.interpreter.adaptivePredict(this._input, 47, this._ctx);
			}
			this.state = 350;
			this._errHandler.sync(this);
			_la = this._input.LA(1);
			if (_la === SFMLParser.ELSE) {
				{
				this.state = 348;
				this.match(SFMLParser.ELSE);
				this.state = 349;
				this.block();
				}
			}

			this.state = 352;
			this.match(SFMLParser.END);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}

	public boolexpr(): BoolexprContext;
	public boolexpr(_p: number): BoolexprContext;
	// @RuleVersion(0)
	public boolexpr(_p?: number): BoolexprContext {
		if (_p === undefined) {
			_p = 0;
		}

		let _parentctx: ParserRuleContext = this._ctx;
		let _parentState: number = this.state;
		let _localctx: BoolexprContext = new BoolexprContext(this._ctx, _parentState);
		let _prevctx: BoolexprContext = _localctx;
		let _startState: number = 52;
		this.enterRecursionRule(_localctx, 52, SFMLParser.RULE_boolexpr, _p);
		let _la: number;
		try {
			let _alt: number;
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 376;
			this._errHandler.sync(this);
			switch ( this.interpreter.adaptivePredict(this._input, 51, this._ctx) ) {
			case 1:
				{
				_localctx = new BooleanTrueContext(_localctx);
				this._ctx = _localctx;
				_prevctx = _localctx;

				this.state = 355;
				this.match(SFMLParser.TRUE);
				}
				break;

			case 2:
				{
				_localctx = new BooleanFalseContext(_localctx);
				this._ctx = _localctx;
				_prevctx = _localctx;
				this.state = 356;
				this.match(SFMLParser.FALSE);
				}
				break;

			case 3:
				{
				_localctx = new BooleanParenContext(_localctx);
				this._ctx = _localctx;
				_prevctx = _localctx;
				this.state = 357;
				this.match(SFMLParser.LPAREN);
				this.state = 358;
				this.boolexpr(0);
				this.state = 359;
				this.match(SFMLParser.RPAREN);
				}
				break;

			case 4:
				{
				_localctx = new BooleanNegationContext(_localctx);
				this._ctx = _localctx;
				_prevctx = _localctx;
				this.state = 361;
				this.match(SFMLParser.NOT);
				this.state = 362;
				this.boolexpr(5);
				}
				break;

			case 5:
				{
				_localctx = new BooleanHasContext(_localctx);
				this._ctx = _localctx;
				_prevctx = _localctx;
				this.state = 364;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
				if ((((_la) & ~0x1F) === 0 && ((1 << _la) & ((1 << SFMLParser.OVERALL) | (1 << SFMLParser.SOME) | (1 << SFMLParser.ONE) | (1 << SFMLParser.LONE))) !== 0) || _la === SFMLParser.EACH || _la === SFMLParser.EVERY) {
					{
					this.state = 363;
					this.setOp();
					}
				}

				this.state = 366;
				this.labelAccess();
				this.state = 367;
				this.match(SFMLParser.HAS);
				this.state = 368;
				this.resourcecomparison();
				}
				break;

			case 6:
				{
				_localctx = new BooleanRedstoneContext(_localctx);
				this._ctx = _localctx;
				_prevctx = _localctx;
				this.state = 370;
				this.match(SFMLParser.REDSTONE);
				this.state = 374;
				this._errHandler.sync(this);
				switch ( this.interpreter.adaptivePredict(this._input, 50, this._ctx) ) {
				case 1:
					{
					this.state = 371;
					this.comparisonOp();
					this.state = 372;
					this.number();
					}
					break;
				}
				}
				break;
			}
			this._ctx._stop = this._input.tryLT(-1);
			this.state = 386;
			this._errHandler.sync(this);
			_alt = this.interpreter.adaptivePredict(this._input, 53, this._ctx);
			while (_alt !== 2 && _alt !== ATN.INVALID_ALT_NUMBER) {
				if (_alt === 1) {
					if (this._parseListeners != null) {
						this.triggerExitRuleEvent();
					}
					_prevctx = _localctx;
					{
					this.state = 384;
					this._errHandler.sync(this);
					switch ( this.interpreter.adaptivePredict(this._input, 52, this._ctx) ) {
					case 1:
						{
						_localctx = new BooleanConjunctionContext(new BoolexprContext(_parentctx, _parentState));
						this.pushNewRecursionContext(_localctx, _startState, SFMLParser.RULE_boolexpr);
						this.state = 378;
						if (!(this.precpred(this._ctx, 4))) {
							throw this.createFailedPredicateException("this.precpred(this._ctx, 4)");
						}
						this.state = 379;
						this.match(SFMLParser.AND);
						this.state = 380;
						this.boolexpr(5);
						}
						break;

					case 2:
						{
						_localctx = new BooleanDisjunctionContext(new BoolexprContext(_parentctx, _parentState));
						this.pushNewRecursionContext(_localctx, _startState, SFMLParser.RULE_boolexpr);
						this.state = 381;
						if (!(this.precpred(this._ctx, 3))) {
							throw this.createFailedPredicateException("this.precpred(this._ctx, 3)");
						}
						this.state = 382;
						this.match(SFMLParser.OR);
						this.state = 383;
						this.boolexpr(4);
						}
						break;
					}
					}
				}
				this.state = 388;
				this._errHandler.sync(this);
				_alt = this.interpreter.adaptivePredict(this._input, 53, this._ctx);
			}
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public resourcecomparison(): ResourcecomparisonContext {
		let _localctx: ResourcecomparisonContext = new ResourcecomparisonContext(this._ctx, this.state);
		this.enterRule(_localctx, 54, SFMLParser.RULE_resourcecomparison);
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 389;
			this.comparisonOp();
			this.state = 390;
			this.number();
			this.state = 392;
			this._errHandler.sync(this);
			switch ( this.interpreter.adaptivePredict(this._input, 54, this._ctx) ) {
			case 1:
				{
				this.state = 391;
				this.resourceId();
				}
				break;
			}
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public comparisonOp(): ComparisonOpContext {
		let _localctx: ComparisonOpContext = new ComparisonOpContext(this._ctx, this.state);
		this.enterRule(_localctx, 56, SFMLParser.RULE_comparisonOp);
		let _la: number;
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 394;
			_la = this._input.LA(1);
			if (!((((_la) & ~0x1F) === 0 && ((1 << _la) & ((1 << SFMLParser.GT) | (1 << SFMLParser.GT_SYMBOL) | (1 << SFMLParser.LT) | (1 << SFMLParser.LT_SYMBOL) | (1 << SFMLParser.EQ) | (1 << SFMLParser.EQ_SYMBOL) | (1 << SFMLParser.LE) | (1 << SFMLParser.LE_SYMBOL) | (1 << SFMLParser.GE) | (1 << SFMLParser.GE_SYMBOL))) !== 0))) {
			this._errHandler.recoverInline(this);
			} else {
				if (this._input.LA(1) === Token.EOF) {
					this.matchedEOF = true;
				}

				this._errHandler.reportMatch(this);
				this.consume();
			}
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public setOp(): SetOpContext {
		let _localctx: SetOpContext = new SetOpContext(this._ctx, this.state);
		this.enterRule(_localctx, 58, SFMLParser.RULE_setOp);
		let _la: number;
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 396;
			_la = this._input.LA(1);
			if (!((((_la) & ~0x1F) === 0 && ((1 << _la) & ((1 << SFMLParser.OVERALL) | (1 << SFMLParser.SOME) | (1 << SFMLParser.ONE) | (1 << SFMLParser.LONE))) !== 0) || _la === SFMLParser.EACH || _la === SFMLParser.EVERY)) {
			this._errHandler.recoverInline(this);
			} else {
				if (this._input.LA(1) === Token.EOF) {
					this.matchedEOF = true;
				}

				this._errHandler.reportMatch(this);
				this.consume();
			}
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public labelAccess(): LabelAccessContext {
		let _localctx: LabelAccessContext = new LabelAccessContext(this._ctx, this.state);
		this.enterRule(_localctx, 60, SFMLParser.RULE_labelAccess);
		let _la: number;
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 398;
			this.label();
			this.state = 403;
			this._errHandler.sync(this);
			_la = this._input.LA(1);
			while (_la === SFMLParser.COMMA) {
				{
				{
				this.state = 399;
				this.match(SFMLParser.COMMA);
				this.state = 400;
				this.label();
				}
				}
				this.state = 405;
				this._errHandler.sync(this);
				_la = this._input.LA(1);
			}
			this.state = 407;
			this._errHandler.sync(this);
			_la = this._input.LA(1);
			if (_la === SFMLParser.ROUND) {
				{
				this.state = 406;
				this.roundrobin();
				}
			}

			this.state = 410;
			this._errHandler.sync(this);
			_la = this._input.LA(1);
			if (((((_la - 32)) & ~0x1F) === 0 && ((1 << (_la - 32)) & ((1 << (SFMLParser.EACH - 32)) | (1 << (SFMLParser.TOP - 32)) | (1 << (SFMLParser.BOTTOM - 32)) | (1 << (SFMLParser.NORTH - 32)) | (1 << (SFMLParser.EAST - 32)) | (1 << (SFMLParser.SOUTH - 32)) | (1 << (SFMLParser.WEST - 32)))) !== 0)) {
				{
				this.state = 409;
				this.sidequalifier();
				}
			}

			this.state = 413;
			this._errHandler.sync(this);
			_la = this._input.LA(1);
			if (_la === SFMLParser.SLOTS) {
				{
				this.state = 412;
				this.slotqualifier();
				}
			}

			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public roundrobin(): RoundrobinContext {
		let _localctx: RoundrobinContext = new RoundrobinContext(this._ctx, this.state);
		this.enterRule(_localctx, 62, SFMLParser.RULE_roundrobin);
		let _la: number;
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 415;
			this.match(SFMLParser.ROUND);
			this.state = 416;
			this.match(SFMLParser.ROBIN);
			this.state = 417;
			this.match(SFMLParser.BY);
			this.state = 418;
			_la = this._input.LA(1);
			if (!(_la === SFMLParser.LABEL || _la === SFMLParser.BLOCK)) {
			this._errHandler.recoverInline(this);
			} else {
				if (this._input.LA(1) === Token.EOF) {
					this.matchedEOF = true;
				}

				this._errHandler.reportMatch(this);
				this.consume();
			}
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public label(): LabelContext {
		let _localctx: LabelContext = new LabelContext(this._ctx, this.state);
		this.enterRule(_localctx, 64, SFMLParser.RULE_label);
		try {
			this.state = 422;
			this._errHandler.sync(this);
			switch (this._input.LA(1)) {
			case SFMLParser.REDSTONE:
			case SFMLParser.IDENTIFIER:
				_localctx = new RawLabelContext(_localctx);
				this.enterOuterAlt(_localctx, 1);
				{
				{
				this.state = 420;
				this.identifier();
				}
				}
				break;
			case SFMLParser.STRING:
				_localctx = new StringLabelContext(_localctx);
				this.enterOuterAlt(_localctx, 2);
				{
				this.state = 421;
				this.string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public resourceId(): ResourceIdContext {
		let _localctx: ResourceIdContext = new ResourceIdContext(this._ctx, this.state);
		this.enterRule(_localctx, 66, SFMLParser.RULE_resourceId);
		try {
			this.state = 444;
			this._errHandler.sync(this);
			switch (this._input.LA(1)) {
			case SFMLParser.REDSTONE:
			case SFMLParser.IDENTIFIER:
				_localctx = new ResourceContext(_localctx);
				this.enterOuterAlt(_localctx, 1);
				{
				{
				this.state = 424;
				this.identifier();
				}
				this.state = 441;
				this._errHandler.sync(this);
				switch ( this.interpreter.adaptivePredict(this._input, 65, this._ctx) ) {
				case 1:
					{
					this.state = 425;
					this.match(SFMLParser.COLON);
					this.state = 427;
					this._errHandler.sync(this);
					switch ( this.interpreter.adaptivePredict(this._input, 60, this._ctx) ) {
					case 1:
						{
						this.state = 426;
						this.identifier();
						}
						break;
					}
					this.state = 439;
					this._errHandler.sync(this);
					switch ( this.interpreter.adaptivePredict(this._input, 64, this._ctx) ) {
					case 1:
						{
						this.state = 429;
						this.match(SFMLParser.COLON);
						this.state = 431;
						this._errHandler.sync(this);
						switch ( this.interpreter.adaptivePredict(this._input, 61, this._ctx) ) {
						case 1:
							{
							this.state = 430;
							this.identifier();
							}
							break;
						}
						this.state = 437;
						this._errHandler.sync(this);
						switch ( this.interpreter.adaptivePredict(this._input, 63, this._ctx) ) {
						case 1:
							{
							this.state = 433;
							this.match(SFMLParser.COLON);
							this.state = 435;
							this._errHandler.sync(this);
							switch ( this.interpreter.adaptivePredict(this._input, 62, this._ctx) ) {
							case 1:
								{
								this.state = 434;
								this.identifier();
								}
								break;
							}
							}
							break;
						}
						}
						break;
					}
					}
					break;
				}
				}
				break;
			case SFMLParser.STRING:
				_localctx = new StringResourceContext(_localctx);
				this.enterOuterAlt(_localctx, 2);
				{
				this.state = 443;
				this.string();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public identifier(): IdentifierContext {
		let _localctx: IdentifierContext = new IdentifierContext(this._ctx, this.state);
		this.enterRule(_localctx, 68, SFMLParser.RULE_identifier);
		let _la: number;
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 446;
			_la = this._input.LA(1);
			if (!(_la === SFMLParser.REDSTONE || _la === SFMLParser.IDENTIFIER)) {
			this._errHandler.recoverInline(this);
			} else {
				if (this._input.LA(1) === Token.EOF) {
					this.matchedEOF = true;
				}

				this._errHandler.reportMatch(this);
				this.consume();
			}
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public string(): StringContext {
		let _localctx: StringContext = new StringContext(this._ctx, this.state);
		this.enterRule(_localctx, 70, SFMLParser.RULE_string);
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 448;
			this.match(SFMLParser.STRING);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}
	// @RuleVersion(0)
	public number(): NumberContext {
		let _localctx: NumberContext = new NumberContext(this._ctx, this.state);
		this.enterRule(_localctx, 72, SFMLParser.RULE_number);
		try {
			this.enterOuterAlt(_localctx, 1);
			{
			this.state = 450;
			this.match(SFMLParser.NUMBER);
			}
		}
		catch (re) {
			if (re instanceof RecognitionException) {
				_localctx.exception = re;
				this._errHandler.reportError(this, re);
				this._errHandler.recover(this, re);
			} else {
				throw re;
			}
		}
		finally {
			this.exitRule();
		}
		return _localctx;
	}

	public sempred(_localctx: RuleContext, ruleIndex: number, predIndex: number): boolean {
		switch (ruleIndex) {
		case 18:
			return this.withClause_sempred(_localctx as WithClauseContext, predIndex);

		case 26:
			return this.boolexpr_sempred(_localctx as BoolexprContext, predIndex);
		}
		return true;
	}
	private withClause_sempred(_localctx: WithClauseContext, predIndex: number): boolean {
		switch (predIndex) {
		case 0:
			return this.precpred(this._ctx, 3);

		case 1:
			return this.precpred(this._ctx, 2);
		}
		return true;
	}
	private boolexpr_sempred(_localctx: BoolexprContext, predIndex: number): boolean {
		switch (predIndex) {
		case 2:
			return this.precpred(this._ctx, 4);

		case 3:
			return this.precpred(this._ctx, 3);
		}
		return true;
	}

	public static readonly _serializedATN: string =
		"\x03\uC91D\uCABA\u058D\uAFBA\u4F53\u0607\uEA8B\uC241\x03K\u01C7\x04\x02" +
		"\t\x02\x04\x03\t\x03\x04\x04\t\x04\x04\x05\t\x05\x04\x06\t\x06\x04\x07" +
		"\t\x07\x04\b\t\b\x04\t\t\t\x04\n\t\n\x04\v\t\v\x04\f\t\f\x04\r\t\r\x04" +
		"\x0E\t\x0E\x04\x0F\t\x0F\x04\x10\t\x10\x04\x11\t\x11\x04\x12\t\x12\x04" +
		"\x13\t\x13\x04\x14\t\x14\x04\x15\t\x15\x04\x16\t\x16\x04\x17\t\x17\x04" +
		"\x18\t\x18\x04\x19\t\x19\x04\x1A\t\x1A\x04\x1B\t\x1B\x04\x1C\t\x1C\x04" +
		"\x1D\t\x1D\x04\x1E\t\x1E\x04\x1F\t\x1F\x04 \t \x04!\t!\x04\"\t\"\x04#" +
		"\t#\x04$\t$\x04%\t%\x04&\t&\x03\x02\x05\x02N\n\x02\x03\x02\x07\x02Q\n" +
		"\x02\f\x02\x0E\x02T\v\x02\x03\x02\x03\x02\x03\x03\x03\x03\x03\x03\x03" +
		"\x04\x03\x04\x03\x04\x03\x04\x03\x04\x03\x04\x03\x04\x03\x04\x03\x04\x03" +
		"\x04\x03\x04\x03\x04\x03\x04\x05\x04h\n\x04\x03\x05\x03\x05\x03\x05\x03" +
		"\x05\x03\x05\x03\x05\x03\x05\x05\x05q\n\x05\x03\x06\x07\x06t\n\x06\f\x06" +
		"\x0E\x06w\v\x06\x03\x07\x03\x07\x03\x07\x03\x07\x05\x07}\n\x07\x03\b\x03" +
		"\b\x05\b\x81\n\b\x03\b\x03\b\x07\b\x85\n\b\f\b\x0E\b\x88\v\b\x03\b\x05" +
		"\b\x8B\n\b\x03\t\x03\t\x05\t\x8F\n\t\x03\t\x05\t\x92\n\t\x03\t\x03\t\x05" +
		"\t\x96\n\t\x03\t\x03\t\x03\t\x05\t\x9B\n\t\x03\t\x03\t\x03\t\x05\t\xA0" +
		"\n\t\x03\t\x05\t\xA3\n\t\x05\t\xA5\n\t\x03\n\x03\n\x05\n\xA9\n\n\x03\n" +
		"\x05\n\xAC\n\n\x03\n\x03\n\x05\n\xB0\n\n\x03\n\x03\n\x03\n\x05\n\xB5\n" +
		"\n\x03\n\x03\n\x03\n\x05\n\xBA\n\n\x03\n\x05\n\xBD\n\n\x05\n\xBF\n\n\x03" +
		"\v\x03\v\x03\v\x03\v\x07\v\xC5\n\v\f\v\x0E\v\xC8\v\v\x03\v\x05\v\xCB\n" +
		"\v\x03\f\x03\f\x03\r\x03\r\x03\x0E\x03\x0E\x03\x0E\x07\x0E\xD4\n\x0E\f" +
		"\x0E\x0E\x0E\xD7\v\x0E\x03\x0E\x05\x0E\xDA\n\x0E\x03\x0F\x05\x0F\xDD\n" +
		"\x0F\x03\x0F\x03\x0F\x05\x0F\xE1\n\x0F\x03\x0F\x03\x0F\x05\x0F\xE5\n\x0F" +
		"\x03\x0F\x05\x0F\xE8\n\x0F\x03\x10\x03\x10\x03\x10\x03\x10\x03\x10\x05" +
		"\x10\xEF\n\x10\x03\x11\x03\x11\x05\x11\xF3\n\x11\x03\x12\x03\x12\x03\x12" +
		"\x05\x12\xF8\n\x12\x03\x13\x03\x13\x03\x13\x03\x13\x05\x13\xFE\n\x13\x03" +
		"\x14\x03\x14\x03\x14\x03\x14\x03\x14\x03\x14\x03\x14\x03\x14\x03\x14\x05" +
		"\x14\u0109\n\x14\x03\x14\x05\x14\u010C\n\x14\x03\x14\x05\x14\u010F\n\x14" +
		"\x03\x14\x03\x14\x03\x14\x03\x14\x03\x14\x03\x14\x07\x14\u0117\n\x14\f" +
		"\x14\x0E\x14\u011A\v\x14\x03\x15\x03\x15\x03\x15\x03\x15\x03\x15\x07\x15" +
		"\u0121\n\x15\f\x15\x0E\x15\u0124\v\x15\x03\x15\x03\x15\x03\x15\x07\x15" +
		"\u0129\n\x15\f\x15\x0E\x15\u012C\v\x15\x05\x15\u012E\n\x15\x03\x16\x03" +
		"\x16\x03\x16\x03\x16\x03\x16\x07\x16\u0135\n\x16\f\x16\x0E\x16\u0138\v" +
		"\x16\x03\x16\x03\x16\x05\x16\u013C\n\x16\x03\x17\x03\x17\x03\x18\x03\x18" +
		"\x03\x18\x03\x19\x03\x19\x03\x19\x07\x19\u0146\n\x19\f\x19\x0E\x19\u0149" +
		"\v\x19\x03\x1A\x03\x1A\x03\x1A\x05\x1A\u014E\n\x1A\x03\x1B\x03\x1B\x03" +
		"\x1B\x03\x1B\x03\x1B\x03\x1B\x03\x1B\x03\x1B\x03\x1B\x03\x1B\x07\x1B\u015A" +
		"\n\x1B\f\x1B\x0E\x1B\u015D\v\x1B\x03\x1B\x03\x1B\x05\x1B\u0161\n\x1B\x03" +
		"\x1B\x03\x1B\x03\x1C\x03\x1C\x03\x1C\x03\x1C\x03\x1C\x03\x1C\x03\x1C\x03" +
		"\x1C\x03\x1C\x03\x1C\x05\x1C\u016F\n\x1C\x03\x1C\x03\x1C\x03\x1C\x03\x1C" +
		"\x03\x1C\x03\x1C\x03\x1C\x03\x1C\x05\x1C\u0179\n\x1C\x05\x1C\u017B\n\x1C" +
		"\x03\x1C\x03\x1C\x03\x1C\x03\x1C\x03\x1C\x03\x1C\x07\x1C\u0183\n\x1C\f" +
		"\x1C\x0E\x1C\u0186\v\x1C\x03\x1D\x03\x1D\x03\x1D\x05\x1D\u018B\n\x1D\x03" +
		"\x1E\x03\x1E\x03\x1F\x03\x1F\x03 \x03 \x03 \x07 \u0194\n \f \x0E \u0197" +
		"\v \x03 \x05 \u019A\n \x03 \x05 \u019D\n \x03 \x05 \u01A0\n \x03!\x03" +
		"!\x03!\x03!\x03!\x03\"\x03\"\x05\"\u01A9\n\"\x03#\x03#\x03#\x05#\u01AE" +
		"\n#\x03#\x03#\x05#\u01B2\n#\x03#\x03#\x05#\u01B6\n#\x05#\u01B8\n#\x05" +
		"#\u01BA\n#\x05#\u01BC\n#\x03#\x05#\u01BF\n#\x03$\x03$\x03%\x03%\x03&\x03" +
		"&\x03&\x02\x02\x04&6\'\x02\x02\x04\x02\x06\x02\b\x02\n\x02\f\x02\x0E\x02" +
		"\x10\x02\x12\x02\x14\x02\x16\x02\x18\x02\x1A\x02\x1C\x02\x1E\x02 \x02" +
		"\"\x02$\x02&\x02(\x02*\x02,\x02.\x020\x022\x024\x026\x028\x02:\x02<\x02" +
		">\x02@\x02B\x02D\x02F\x02H\x02J\x02\x02\x07\x03\x02.3\x03\x02\x10\x19" +
		"\x05\x02\x07\n\"\"??\x03\x02,-\x04\x0288FF\x02\u01EE\x02M\x03\x02\x02" +
		"\x02\x04W\x03\x02\x02\x02\x06g\x03\x02\x02\x02\bp\x03\x02\x02\x02\nu\x03" +
		"\x02\x02\x02\f|\x03\x02\x02\x02\x0E~\x03\x02\x02\x02\x10\xA4\x03\x02\x02" +
		"\x02\x12\xBE\x03\x02\x02\x02\x14\xC0\x03\x02\x02\x02\x16\xCC\x03\x02\x02" +
		"\x02\x18\xCE\x03\x02\x02\x02\x1A\xD0\x03\x02\x02\x02\x1C\xE7\x03\x02\x02" +
		"\x02\x1E\xEE\x03\x02\x02\x02 \xF0\x03\x02\x02\x02\"\xF4\x03\x02\x02\x02" +
		"$\xFD\x03\x02\x02\x02&\u010E\x03\x02\x02\x02(\u012D\x03\x02\x02\x02*\u013B" +
		"\x03\x02\x02\x02,\u013D\x03\x02\x02\x02.\u013F\x03\x02\x02\x020\u0142" +
		"\x03\x02\x02\x022\u014A\x03\x02\x02\x024\u014F\x03\x02\x02\x026\u017A" +
		"\x03\x02\x02\x028\u0187\x03\x02\x02\x02:\u018C\x03\x02\x02\x02<\u018E" +
		"\x03\x02\x02\x02>\u0190\x03\x02\x02\x02@\u01A1\x03\x02\x02\x02B\u01A8" +
		"\x03\x02\x02\x02D\u01BE\x03\x02\x02\x02F\u01C0\x03\x02\x02\x02H\u01C2" +
		"\x03\x02\x02\x02J\u01C4\x03\x02\x02\x02LN\x05\x04\x03\x02ML\x03\x02\x02" +
		"\x02MN\x03\x02\x02\x02NR\x03\x02\x02\x02OQ\x05\x06\x04\x02PO\x03\x02\x02" +
		"\x02QT\x03\x02\x02\x02RP\x03\x02\x02\x02RS\x03\x02\x02\x02SU\x03\x02\x02" +
		"\x02TR\x03\x02\x02\x02UV\x07\x02\x02\x03V\x03\x03\x02\x02\x02WX\x07>\x02" +
		"\x02XY\x05H%\x02Y\x05\x03\x02\x02\x02Z[\x07?\x02\x02[\\\x05\b\x05\x02" +
		"\\]\x07:\x02\x02]^\x05\n\x06\x02^_\x07=\x02\x02_h\x03\x02\x02\x02`a\x07" +
		"?\x02\x02ab\x078\x02\x02bc\x079\x02\x02cd\x07:\x02\x02de\x05\n\x06\x02" +
		"ef\x07=\x02\x02fh\x03\x02\x02\x02gZ\x03\x02\x02\x02g`\x03\x02\x02\x02" +
		"h\x07\x03\x02\x02\x02iq\x076\x02\x02jk\x05J&\x02kl\x075\x02\x02lq\x03" +
		"\x02\x02\x02mn\x05J&\x02no\x077\x02\x02oq\x03\x02\x02\x02pi\x03\x02\x02" +
		"\x02pj\x03\x02\x02\x02pm\x03\x02\x02\x02q\t\x03\x02\x02\x02rt\x05\f\x07" +
		"\x02sr\x03\x02\x02\x02tw\x03\x02\x02\x02us\x03\x02\x02\x02uv\x03\x02\x02" +
		"\x02v\v\x03\x02\x02\x02wu\x03\x02\x02\x02x}\x05\x10\t\x02y}\x05\x12\n" +
		"\x02z}\x054\x1B\x02{}\x05\x0E\b\x02|x\x03\x02\x02\x02|y\x03\x02\x02\x02" +
		"|z\x03\x02\x02\x02|{\x03\x02\x02\x02}\r\x03\x02\x02\x02~\x80\x07$\x02" +
		"\x02\x7F\x81\x05B\"\x02\x80\x7F\x03\x02\x02\x02\x80\x81\x03\x02\x02\x02" +
		"\x81\x86\x03\x02\x02\x02\x82\x83\x07@\x02\x02\x83\x85\x05B\"\x02\x84\x82" +
		"\x03\x02\x02\x02\x85\x88\x03\x02\x02\x02\x86\x84\x03\x02\x02\x02\x86\x87" +
		"\x03\x02\x02\x02\x87\x8A\x03\x02\x02\x02\x88\x86\x03\x02\x02\x02\x89\x8B" +
		"\x07@\x02\x02\x8A\x89\x03\x02\x02\x02\x8A\x8B\x03\x02\x02\x02\x8B\x0F" +
		"\x03\x02\x02\x02\x8C\x8E\x07\x1D\x02\x02\x8D\x8F\x05\x16\f\x02\x8E\x8D" +
		"\x03\x02\x02\x02\x8E\x8F\x03\x02\x02\x02\x8F\x91\x03\x02\x02\x02\x90\x92" +
		"\x05\x14\v\x02\x91\x90\x03\x02\x02\x02\x91\x92\x03\x02\x02\x02\x92\x93" +
		"\x03\x02\x02\x02\x93\x95\x07\x1B\x02\x02\x94\x96\x07\"\x02\x02\x95\x94" +
		"\x03\x02\x02\x02\x95\x96\x03\x02\x02\x02\x96\x97\x03\x02\x02\x02\x97\xA5" +
		"\x05> \x02\x98\x9A\x07\x1B\x02\x02\x99\x9B\x07\"\x02\x02\x9A\x99\x03\x02" +
		"\x02\x02\x9A\x9B\x03\x02\x02\x02\x9B\x9C\x03\x02\x02\x02\x9C\x9D\x05>" +
		" \x02\x9D\x9F\x07\x1D\x02\x02\x9E\xA0\x05\x16\f\x02\x9F\x9E\x03\x02\x02" +
		"\x02\x9F\xA0\x03\x02\x02\x02\xA0\xA2\x03\x02\x02\x02\xA1\xA3\x05\x14\v" +
		"\x02\xA2\xA1\x03\x02\x02\x02\xA2\xA3\x03\x02\x02\x02\xA3\xA5\x03\x02\x02" +
		"\x02\xA4\x8C\x03\x02\x02\x02\xA4\x98\x03\x02\x02\x02\xA5\x11\x03\x02\x02" +
		"\x02\xA6\xA8\x07\x1E\x02\x02\xA7\xA9\x05\x18\r\x02\xA8\xA7\x03\x02\x02" +
		"\x02\xA8\xA9\x03\x02\x02\x02\xA9\xAB\x03\x02\x02\x02\xAA\xAC\x05\x14\v" +
		"\x02\xAB\xAA\x03\x02\x02\x02\xAB\xAC\x03\x02\x02\x02\xAC\xAD\x03\x02\x02" +
		"\x02\xAD\xAF\x07\x1C\x02\x02\xAE\xB0\x07\"\x02\x02\xAF\xAE\x03\x02\x02" +
		"\x02\xAF\xB0\x03\x02\x02\x02\xB0\xB1\x03\x02\x02\x02\xB1\xBF\x05> \x02" +
		"\xB2\xB4\x07\x1C\x02\x02\xB3\xB5\x07\"\x02\x02\xB4\xB3\x03\x02\x02\x02" +
		"\xB4\xB5\x03\x02\x02\x02\xB5\xB6\x03\x02\x02\x02\xB6\xB7\x05> \x02\xB7" +
		"\xB9\x07\x1E\x02\x02\xB8\xBA\x05\x18\r\x02\xB9\xB8\x03\x02\x02\x02\xB9" +
		"\xBA\x03\x02\x02\x02\xBA\xBC\x03\x02\x02\x02\xBB\xBD\x05\x14\v\x02\xBC" +
		"\xBB\x03\x02\x02\x02\xBC\xBD\x03\x02\x02\x02\xBD\xBF\x03\x02\x02\x02\xBE" +
		"\xA6\x03\x02\x02\x02\xBE\xB2\x03\x02\x02\x02\xBF\x13\x03\x02\x02\x02\xC0" +
		"\xC1\x07#\x02\x02\xC1\xC6\x05D#\x02\xC2\xC3\x07@\x02\x02\xC3\xC5\x05D" +
		"#\x02\xC4\xC2\x03\x02\x02\x02\xC5\xC8\x03\x02\x02\x02\xC6\xC4\x03\x02" +
		"\x02\x02\xC6\xC7\x03\x02\x02\x02\xC7\xCA\x03\x02\x02\x02\xC8\xC6\x03\x02" +
		"\x02\x02\xC9\xCB\x07@\x02\x02\xCA\xC9\x03\x02\x02\x02\xCA\xCB\x03\x02" +
		"\x02\x02\xCB\x15\x03\x02\x02\x02\xCC\xCD\x05\x1A\x0E\x02\xCD\x17\x03\x02" +
		"\x02\x02\xCE\xCF\x05\x1A\x0E\x02\xCF\x19\x03\x02\x02\x02\xD0\xD5\x05\x1C" +
		"\x0F\x02\xD1\xD2\x07@\x02\x02\xD2\xD4\x05\x1C\x0F\x02\xD3\xD1\x03\x02" +
		"\x02\x02\xD4\xD7\x03\x02\x02\x02\xD5\xD3\x03\x02\x02\x02\xD5\xD6\x03\x02" +
		"\x02\x02\xD6\xD9\x03\x02\x02\x02\xD7\xD5\x03\x02\x02\x02\xD8\xDA\x07@" +
		"\x02\x02\xD9\xD8\x03\x02\x02\x02\xD9\xDA\x03\x02\x02\x02\xDA\x1B\x03\x02" +
		"\x02\x02\xDB\xDD\x05\x1E\x10\x02\xDC\xDB\x03\x02\x02\x02\xDC\xDD\x03\x02" +
		"\x02\x02\xDD\xDE\x03\x02\x02\x02\xDE\xE0\x05D#\x02\xDF\xE1\x05$\x13\x02" +
		"\xE0\xDF\x03\x02\x02\x02\xE0\xE1\x03\x02\x02\x02\xE1\xE8\x03\x02\x02\x02" +
		"\xE2\xE4\x05\x1E\x10\x02\xE3\xE5\x05$\x13\x02\xE4\xE3\x03\x02\x02\x02" +
		"\xE4\xE5\x03\x02\x02\x02\xE5\xE8\x03\x02\x02\x02\xE6\xE8\x05$\x13\x02" +
		"\xE7\xDC\x03\x02\x02\x02\xE7\xE2\x03\x02\x02\x02\xE7\xE6\x03\x02\x02\x02" +
		"\xE8\x1D\x03\x02\x02\x02\xE9\xEA\x05 \x11\x02\xEA\xEB\x05\"\x12\x02\xEB" +
		"\xEF\x03\x02\x02\x02\xEC\xEF\x05\"\x12\x02\xED\xEF\x05 \x11\x02\xEE\xE9" +
		"\x03\x02\x02\x02\xEE\xEC\x03\x02\x02\x02\xEE\xED\x03\x02\x02\x02\xEF\x1F" +
		"\x03\x02\x02\x02\xF0\xF2\x05J&\x02\xF1\xF3\x07\"\x02\x02\xF2\xF1\x03\x02" +
		"\x02\x02\xF2\xF3\x03\x02\x02\x02\xF3!\x03\x02\x02\x02\xF4\xF5\x07!\x02" +
		"\x02\xF5\xF7\x05J&\x02\xF6\xF8\x07\"\x02\x02\xF7\xF6\x03\x02\x02\x02\xF7" +
		"\xF8\x03\x02\x02\x02\xF8#\x03\x02\x02\x02\xF9\xFA\x07&\x02\x02\xFA\xFE" +
		"\x05&\x14\x02\xFB\xFC\x07%\x02\x02\xFC\xFE\x05&\x14\x02\xFD\xF9\x03\x02" +
		"\x02\x02\xFD\xFB\x03\x02\x02\x02\xFE%\x03\x02\x02\x02\xFF\u0100\b\x14" +
		"\x01\x02\u0100\u0101\x07D\x02\x02\u0101\u0102\x05&\x14\x02\u0102\u0103" +
		"\x07E\x02\x02\u0103\u010F\x03\x02\x02\x02\u0104\u0105\x07\r\x02\x02\u0105" +
		"\u010F\x05&\x14\x06\u0106\u0108\x07\'\x02\x02\u0107\u0109\x07(\x02\x02" +
		"\u0108\u0107\x03\x02\x02\x02\u0108\u0109\x03\x02\x02\x02\u0109\u010C\x03" +
		"\x02\x02\x02\u010A\u010C\x07(\x02\x02\u010B\u0106\x03\x02\x02\x02\u010B" +
		"\u010A\x03\x02\x02\x02\u010C\u010D\x03\x02\x02\x02\u010D\u010F\x05(\x15" +
		"\x02\u010E\xFF\x03\x02\x02\x02\u010E\u0104\x03\x02\x02\x02\u010E\u010B" +
		"\x03\x02\x02\x02\u010F\u0118\x03\x02\x02\x02\u0110\u0111\f\x05\x02\x02" +
		"\u0111\u0112\x07\x0E\x02\x02\u0112\u0117\x05&\x14\x06\u0113\u0114\f\x04" +
		"\x02\x02\u0114\u0115\x07\x0F\x02\x02\u0115\u0117\x05&\x14\x05\u0116\u0110" +
		"\x03\x02\x02\x02\u0116\u0113\x03\x02\x02\x02\u0117\u011A\x03\x02\x02\x02" +
		"\u0118\u0116\x03\x02\x02\x02\u0118\u0119\x03\x02\x02\x02\u0119\'\x03\x02" +
		"\x02\x02\u011A\u0118\x03\x02\x02\x02\u011B\u011C\x05F$\x02\u011C\u011D" +
		"\x07A\x02\x02\u011D\u0122\x05F$\x02\u011E\u011F\x07B\x02\x02\u011F\u0121" +
		"\x05F$\x02\u0120\u011E\x03\x02\x02\x02\u0121\u0124\x03\x02\x02\x02\u0122" +
		"\u0120\x03\x02\x02\x02\u0122\u0123\x03\x02\x02\x02\u0123\u012E\x03\x02" +
		"\x02\x02\u0124\u0122\x03\x02\x02\x02\u0125\u012A\x05F$\x02\u0126\u0127" +
		"\x07B\x02\x02\u0127\u0129\x05F$\x02\u0128\u0126\x03\x02\x02\x02\u0129" +
		"\u012C\x03\x02\x02\x02\u012A\u0128\x03\x02\x02\x02\u012A\u012B\x03\x02" +
		"\x02\x02\u012B\u012E\x03\x02\x02\x02\u012C\u012A\x03\x02\x02\x02\u012D" +
		"\u011B\x03\x02\x02\x02\u012D\u0125\x03\x02\x02\x02\u012E)\x03\x02\x02" +
		"\x02\u012F\u0130\x07\"\x02\x02\u0130\u013C\x074\x02\x02\u0131\u0136\x05" +
		",\x17\x02\u0132\u0133\x07@\x02\x02\u0133\u0135\x05,\x17\x02\u0134\u0132" +
		"\x03\x02\x02\x02\u0135\u0138\x03\x02\x02\x02\u0136\u0134\x03\x02\x02\x02" +
		"\u0136\u0137\x03\x02\x02\x02\u0137\u0139\x03\x02\x02\x02\u0138\u0136\x03" +
		"\x02\x02\x02\u0139\u013A\x074\x02\x02\u013A\u013C\x03\x02\x02\x02\u013B" +
		"\u012F\x03\x02\x02\x02\u013B\u0131\x03\x02\x02\x02\u013C+\x03\x02\x02" +
		"\x02\u013D\u013E\t\x02\x02\x02\u013E-\x03\x02\x02\x02\u013F\u0140\x07" +
		" \x02\x02\u0140\u0141\x050\x19\x02\u0141/\x03\x02\x02\x02\u0142\u0147" +
		"\x052\x1A\x02\u0143\u0144\x07@\x02\x02\u0144\u0146\x052\x1A\x02\u0145" +
		"\u0143\x03\x02\x02\x02\u0146\u0149\x03\x02\x02\x02\u0147\u0145\x03\x02" +
		"\x02\x02\u0147\u0148\x03\x02\x02\x02\u01481\x03\x02\x02\x02\u0149\u0147" +
		"\x03\x02\x02\x02\u014A\u014D\x05J&\x02\u014B\u014C\x07C\x02\x02\u014C" +
		"\u014E\x05J&\x02\u014D\u014B\x03\x02\x02\x02\u014D\u014E\x03\x02\x02\x02" +
		"\u014E3\x03\x02\x02\x02\u014F\u0150\x07\x03\x02\x02\u0150\u0151\x056\x1C" +
		"\x02\u0151\u0152\x07\x04\x02\x02\u0152\u015B\x05\n\x06\x02\u0153\u0154" +
		"\x07\x05\x02\x02\u0154\u0155\x07\x03\x02\x02\u0155\u0156\x056\x1C\x02" +
		"\u0156\u0157\x07\x04\x02\x02\u0157\u0158\x05\n\x06\x02\u0158\u015A\x03" +
		"\x02\x02\x02\u0159\u0153\x03\x02\x02\x02\u015A\u015D\x03\x02\x02\x02\u015B" +
		"\u0159\x03\x02\x02\x02\u015B\u015C\x03\x02\x02\x02\u015C\u0160\x03\x02" +
		"\x02\x02\u015D\u015B\x03\x02\x02\x02\u015E\u015F\x07\x05\x02\x02\u015F" +
		"\u0161\x05\n\x06\x02\u0160\u015E\x03\x02\x02\x02\u0160\u0161\x03\x02\x02" +
		"\x02\u0161\u0162\x03\x02\x02\x02\u0162\u0163\x07=\x02\x02\u01635\x03\x02" +
		"\x02\x02\u0164\u0165\b\x1C\x01\x02\u0165\u017B\x07\v\x02\x02\u0166\u017B" +
		"\x07\f\x02\x02\u0167\u0168\x07D\x02\x02\u0168\u0169\x056\x1C\x02\u0169" +
		"\u016A\x07E\x02\x02\u016A\u017B\x03\x02\x02\x02\u016B\u016C\x07\r\x02" +
		"\x02\u016C\u017B\x056\x1C\x07\u016D\u016F\x05<\x1F\x02\u016E\u016D\x03" +
		"\x02\x02\x02\u016E\u016F\x03\x02\x02\x02\u016F\u0170\x03\x02\x02\x02\u0170" +
		"\u0171\x05> \x02\u0171\u0172\x07\x06\x02\x02\u0172\u0173\x058\x1D\x02" +
		"\u0173\u017B\x03\x02\x02\x02\u0174\u0178\x078\x02\x02\u0175\u0176\x05" +
		":\x1E\x02\u0176\u0177\x05J&\x02\u0177\u0179\x03\x02\x02\x02\u0178\u0175" +
		"\x03\x02\x02\x02\u0178\u0179\x03\x02\x02\x02\u0179\u017B\x03\x02\x02\x02" +
		"\u017A\u0164\x03\x02\x02\x02\u017A\u0166\x03\x02\x02\x02\u017A\u0167\x03" +
		"\x02\x02\x02\u017A\u016B\x03\x02\x02\x02\u017A\u016E\x03\x02\x02\x02\u017A" +
		"\u0174\x03\x02\x02\x02\u017B\u0184\x03\x02\x02\x02\u017C\u017D\f\x06\x02" +
		"\x02\u017D\u017E\x07\x0E\x02\x02\u017E\u0183\x056\x1C\x07\u017F\u0180" +
		"\f\x05\x02\x02\u0180\u0181\x07\x0F\x02\x02\u0181\u0183\x056\x1C\x06\u0182" +
		"\u017C\x03\x02\x02\x02\u0182\u017F\x03\x02\x02\x02\u0183\u0186\x03\x02" +
		"\x02\x02\u0184\u0182\x03\x02\x02\x02\u0184\u0185\x03\x02\x02\x02\u0185" +
		"7\x03\x02\x02\x02\u0186\u0184\x03\x02\x02\x02\u0187\u0188\x05:\x1E\x02" +
		"\u0188\u018A\x05J&\x02\u0189\u018B\x05D#\x02\u018A\u0189\x03\x02\x02\x02" +
		"\u018A\u018B\x03\x02\x02\x02\u018B9\x03\x02\x02\x02\u018C\u018D\t\x03" +
		"\x02\x02\u018D;\x03\x02\x02\x02\u018E\u018F\t\x04\x02\x02\u018F=\x03\x02" +
		"\x02\x02\u0190\u0195\x05B\"\x02\u0191\u0192\x07@\x02\x02\u0192\u0194\x05" +
		"B\"\x02\u0193\u0191\x03\x02\x02\x02\u0194\u0197\x03\x02\x02\x02\u0195" +
		"\u0193\x03\x02\x02\x02\u0195\u0196\x03\x02\x02\x02\u0196\u0199\x03\x02" +
		"\x02\x02\u0197\u0195\x03\x02\x02\x02\u0198\u019A\x05@!\x02\u0199\u0198" +
		"\x03\x02\x02\x02\u0199\u019A\x03\x02\x02\x02\u019A\u019C\x03\x02\x02\x02" +
		"\u019B\u019D\x05*\x16\x02\u019C\u019B\x03\x02\x02\x02\u019C\u019D\x03" +
		"\x02\x02\x02\u019D\u019F\x03\x02\x02\x02\u019E\u01A0\x05.\x18\x02\u019F" +
		"\u019E\x03\x02\x02\x02\u019F\u01A0\x03\x02\x02\x02\u01A0?\x03\x02\x02" +
		"\x02\u01A1\u01A2\x07)\x02\x02\u01A2\u01A3\x07*\x02\x02\u01A3\u01A4\x07" +
		"+\x02\x02\u01A4\u01A5\t\x05\x02\x02\u01A5A\x03\x02\x02\x02\u01A6\u01A9" +
		"\x05F$\x02\u01A7\u01A9\x05H%\x02\u01A8\u01A6\x03\x02\x02\x02\u01A8\u01A7" +
		"\x03\x02\x02\x02\u01A9C\x03\x02\x02\x02\u01AA\u01BB\x05F$\x02\u01AB\u01AD" +
		"\x07A\x02\x02\u01AC\u01AE\x05F$\x02\u01AD\u01AC\x03\x02\x02\x02\u01AD" +
		"\u01AE\x03\x02\x02\x02\u01AE\u01B9\x03\x02\x02\x02\u01AF\u01B1\x07A\x02" +
		"\x02\u01B0\u01B2\x05F$\x02\u01B1\u01B0\x03\x02\x02\x02\u01B1\u01B2\x03" +
		"\x02\x02\x02\u01B2\u01B7\x03\x02\x02\x02\u01B3\u01B5\x07A\x02\x02\u01B4" +
		"\u01B6\x05F$\x02\u01B5\u01B4\x03\x02\x02\x02\u01B5\u01B6\x03\x02\x02\x02" +
		"\u01B6\u01B8\x03\x02\x02\x02\u01B7\u01B3\x03\x02\x02\x02\u01B7\u01B8\x03" +
		"\x02\x02\x02\u01B8\u01BA\x03\x02\x02\x02\u01B9\u01AF\x03\x02\x02\x02\u01B9" +
		"\u01BA\x03\x02\x02\x02\u01BA\u01BC\x03\x02\x02\x02\u01BB\u01AB\x03\x02" +
		"\x02\x02\u01BB\u01BC\x03\x02\x02\x02\u01BC\u01BF\x03\x02\x02\x02\u01BD" +
		"\u01BF\x05H%\x02\u01BE\u01AA\x03\x02\x02\x02\u01BE\u01BD\x03\x02\x02\x02" +
		"\u01BFE\x03\x02\x02\x02\u01C0\u01C1\t\x06\x02\x02\u01C1G\x03\x02\x02\x02" +
		"\u01C2\u01C3\x07H\x02\x02\u01C3I\x03\x02\x02\x02\u01C4\u01C5\x07G\x02" +
		"\x02\u01C5K\x03\x02\x02\x02EMRgpu|\x80\x86\x8A\x8E\x91\x95\x9A\x9F\xA2" +
		"\xA4\xA8\xAB\xAF\xB4\xB9\xBC\xBE\xC6\xCA\xD5\xD9\xDC\xE0\xE4\xE7\xEE\xF2" +
		"\xF7\xFD\u0108\u010B\u010E\u0116\u0118\u0122\u012A\u012D\u0136\u013B\u0147" +
		"\u014D\u015B\u0160\u016E\u0178\u017A\u0182\u0184\u018A\u0195\u0199\u019C" +
		"\u019F\u01A8\u01AD\u01B1\u01B5\u01B7\u01B9\u01BB\u01BE";
	public static __ATN: ATN;
	public static get _ATN(): ATN {
		if (!SFMLParser.__ATN) {
			SFMLParser.__ATN = new ATNDeserializer().deserialize(Utils.toCharArray(SFMLParser._serializedATN));
		}

		return SFMLParser.__ATN;
	}

}

export class ProgramContext extends ParserRuleContext {
	public EOF(): TerminalNode { return this.getToken(SFMLParser.EOF, 0); }
	public name(): NameContext | undefined {
		return this.tryGetRuleContext(0, NameContext);
	}
	public trigger(): TriggerContext[];
	public trigger(i: number): TriggerContext;
	public trigger(i?: number): TriggerContext | TriggerContext[] {
		if (i === undefined) {
			return this.getRuleContexts(TriggerContext);
		} else {
			return this.getRuleContext(i, TriggerContext);
		}
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_program; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterProgram) {
			listener.enterProgram(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitProgram) {
			listener.exitProgram(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitProgram) {
			return visitor.visitProgram(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class NameContext extends ParserRuleContext {
	public NAME(): TerminalNode { return this.getToken(SFMLParser.NAME, 0); }
	public string(): StringContext {
		return this.getRuleContext(0, StringContext);
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_name; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterName) {
			listener.enterName(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitName) {
			listener.exitName(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitName) {
			return visitor.visitName(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class TriggerContext extends ParserRuleContext {
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_trigger; }
	public copyFrom(ctx: TriggerContext): void {
		super.copyFrom(ctx);
	}
}
export class TimerTriggerContext extends TriggerContext {
	public EVERY(): TerminalNode { return this.getToken(SFMLParser.EVERY, 0); }
	public interval(): IntervalContext {
		return this.getRuleContext(0, IntervalContext);
	}
	public DO(): TerminalNode { return this.getToken(SFMLParser.DO, 0); }
	public block(): BlockContext {
		return this.getRuleContext(0, BlockContext);
	}
	public END(): TerminalNode { return this.getToken(SFMLParser.END, 0); }
	constructor(ctx: TriggerContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterTimerTrigger) {
			listener.enterTimerTrigger(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitTimerTrigger) {
			listener.exitTimerTrigger(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitTimerTrigger) {
			return visitor.visitTimerTrigger(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}
export class PulseTriggerContext extends TriggerContext {
	public EVERY(): TerminalNode { return this.getToken(SFMLParser.EVERY, 0); }
	public REDSTONE(): TerminalNode { return this.getToken(SFMLParser.REDSTONE, 0); }
	public PULSE(): TerminalNode { return this.getToken(SFMLParser.PULSE, 0); }
	public DO(): TerminalNode { return this.getToken(SFMLParser.DO, 0); }
	public block(): BlockContext {
		return this.getRuleContext(0, BlockContext);
	}
	public END(): TerminalNode { return this.getToken(SFMLParser.END, 0); }
	constructor(ctx: TriggerContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterPulseTrigger) {
			listener.enterPulseTrigger(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitPulseTrigger) {
			listener.exitPulseTrigger(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitPulseTrigger) {
			return visitor.visitPulseTrigger(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class IntervalContext extends ParserRuleContext {
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_interval; }
	public copyFrom(ctx: IntervalContext): void {
		super.copyFrom(ctx);
	}
}
export class TickContext extends IntervalContext {
	public TICK(): TerminalNode { return this.getToken(SFMLParser.TICK, 0); }
	constructor(ctx: IntervalContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterTick) {
			listener.enterTick(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitTick) {
			listener.exitTick(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitTick) {
			return visitor.visitTick(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}
export class TicksContext extends IntervalContext {
	public number(): NumberContext {
		return this.getRuleContext(0, NumberContext);
	}
	public TICKS(): TerminalNode { return this.getToken(SFMLParser.TICKS, 0); }
	constructor(ctx: IntervalContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterTicks) {
			listener.enterTicks(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitTicks) {
			listener.exitTicks(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitTicks) {
			return visitor.visitTicks(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}
export class SecondsContext extends IntervalContext {
	public number(): NumberContext {
		return this.getRuleContext(0, NumberContext);
	}
	public SECONDS(): TerminalNode { return this.getToken(SFMLParser.SECONDS, 0); }
	constructor(ctx: IntervalContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterSeconds) {
			listener.enterSeconds(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitSeconds) {
			listener.exitSeconds(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitSeconds) {
			return visitor.visitSeconds(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class BlockContext extends ParserRuleContext {
	public statement(): StatementContext[];
	public statement(i: number): StatementContext;
	public statement(i?: number): StatementContext | StatementContext[] {
		if (i === undefined) {
			return this.getRuleContexts(StatementContext);
		} else {
			return this.getRuleContext(i, StatementContext);
		}
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_block; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterBlock) {
			listener.enterBlock(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitBlock) {
			listener.exitBlock(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitBlock) {
			return visitor.visitBlock(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class StatementContext extends ParserRuleContext {
	public inputStatement(): InputStatementContext | undefined {
		return this.tryGetRuleContext(0, InputStatementContext);
	}
	public outputStatement(): OutputStatementContext | undefined {
		return this.tryGetRuleContext(0, OutputStatementContext);
	}
	public ifStatement(): IfStatementContext | undefined {
		return this.tryGetRuleContext(0, IfStatementContext);
	}
	public forgetStatement(): ForgetStatementContext | undefined {
		return this.tryGetRuleContext(0, ForgetStatementContext);
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_statement; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterStatement) {
			listener.enterStatement(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitStatement) {
			listener.exitStatement(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitStatement) {
			return visitor.visitStatement(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class ForgetStatementContext extends ParserRuleContext {
	public FORGET(): TerminalNode { return this.getToken(SFMLParser.FORGET, 0); }
	public label(): LabelContext[];
	public label(i: number): LabelContext;
	public label(i?: number): LabelContext | LabelContext[] {
		if (i === undefined) {
			return this.getRuleContexts(LabelContext);
		} else {
			return this.getRuleContext(i, LabelContext);
		}
	}
	public COMMA(): TerminalNode[];
	public COMMA(i: number): TerminalNode;
	public COMMA(i?: number): TerminalNode | TerminalNode[] {
		if (i === undefined) {
			return this.getTokens(SFMLParser.COMMA);
		} else {
			return this.getToken(SFMLParser.COMMA, i);
		}
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_forgetStatement; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterForgetStatement) {
			listener.enterForgetStatement(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitForgetStatement) {
			listener.exitForgetStatement(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitForgetStatement) {
			return visitor.visitForgetStatement(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class InputStatementContext extends ParserRuleContext {
	public INPUT(): TerminalNode { return this.getToken(SFMLParser.INPUT, 0); }
	public FROM(): TerminalNode { return this.getToken(SFMLParser.FROM, 0); }
	public labelAccess(): LabelAccessContext {
		return this.getRuleContext(0, LabelAccessContext);
	}
	public inputResourceLimits(): InputResourceLimitsContext | undefined {
		return this.tryGetRuleContext(0, InputResourceLimitsContext);
	}
	public resourceExclusion(): ResourceExclusionContext | undefined {
		return this.tryGetRuleContext(0, ResourceExclusionContext);
	}
	public EACH(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.EACH, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_inputStatement; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterInputStatement) {
			listener.enterInputStatement(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitInputStatement) {
			listener.exitInputStatement(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitInputStatement) {
			return visitor.visitInputStatement(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class OutputStatementContext extends ParserRuleContext {
	public OUTPUT(): TerminalNode { return this.getToken(SFMLParser.OUTPUT, 0); }
	public TO(): TerminalNode { return this.getToken(SFMLParser.TO, 0); }
	public labelAccess(): LabelAccessContext {
		return this.getRuleContext(0, LabelAccessContext);
	}
	public outputResourceLimits(): OutputResourceLimitsContext | undefined {
		return this.tryGetRuleContext(0, OutputResourceLimitsContext);
	}
	public resourceExclusion(): ResourceExclusionContext | undefined {
		return this.tryGetRuleContext(0, ResourceExclusionContext);
	}
	public EACH(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.EACH, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_outputStatement; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterOutputStatement) {
			listener.enterOutputStatement(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitOutputStatement) {
			listener.exitOutputStatement(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitOutputStatement) {
			return visitor.visitOutputStatement(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class ResourceExclusionContext extends ParserRuleContext {
	public EXCEPT(): TerminalNode { return this.getToken(SFMLParser.EXCEPT, 0); }
	public resourceId(): ResourceIdContext[];
	public resourceId(i: number): ResourceIdContext;
	public resourceId(i?: number): ResourceIdContext | ResourceIdContext[] {
		if (i === undefined) {
			return this.getRuleContexts(ResourceIdContext);
		} else {
			return this.getRuleContext(i, ResourceIdContext);
		}
	}
	public COMMA(): TerminalNode[];
	public COMMA(i: number): TerminalNode;
	public COMMA(i?: number): TerminalNode | TerminalNode[] {
		if (i === undefined) {
			return this.getTokens(SFMLParser.COMMA);
		} else {
			return this.getToken(SFMLParser.COMMA, i);
		}
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_resourceExclusion; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterResourceExclusion) {
			listener.enterResourceExclusion(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitResourceExclusion) {
			listener.exitResourceExclusion(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitResourceExclusion) {
			return visitor.visitResourceExclusion(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class InputResourceLimitsContext extends ParserRuleContext {
	public resourceLimits(): ResourceLimitsContext {
		return this.getRuleContext(0, ResourceLimitsContext);
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_inputResourceLimits; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterInputResourceLimits) {
			listener.enterInputResourceLimits(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitInputResourceLimits) {
			listener.exitInputResourceLimits(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitInputResourceLimits) {
			return visitor.visitInputResourceLimits(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class OutputResourceLimitsContext extends ParserRuleContext {
	public resourceLimits(): ResourceLimitsContext {
		return this.getRuleContext(0, ResourceLimitsContext);
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_outputResourceLimits; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterOutputResourceLimits) {
			listener.enterOutputResourceLimits(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitOutputResourceLimits) {
			listener.exitOutputResourceLimits(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitOutputResourceLimits) {
			return visitor.visitOutputResourceLimits(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class ResourceLimitsContext extends ParserRuleContext {
	public resourceLimit(): ResourceLimitContext[];
	public resourceLimit(i: number): ResourceLimitContext;
	public resourceLimit(i?: number): ResourceLimitContext | ResourceLimitContext[] {
		if (i === undefined) {
			return this.getRuleContexts(ResourceLimitContext);
		} else {
			return this.getRuleContext(i, ResourceLimitContext);
		}
	}
	public COMMA(): TerminalNode[];
	public COMMA(i: number): TerminalNode;
	public COMMA(i?: number): TerminalNode | TerminalNode[] {
		if (i === undefined) {
			return this.getTokens(SFMLParser.COMMA);
		} else {
			return this.getToken(SFMLParser.COMMA, i);
		}
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_resourceLimits; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterResourceLimits) {
			listener.enterResourceLimits(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitResourceLimits) {
			listener.exitResourceLimits(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitResourceLimits) {
			return visitor.visitResourceLimits(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class ResourceLimitContext extends ParserRuleContext {
	public resourceId(): ResourceIdContext | undefined {
		return this.tryGetRuleContext(0, ResourceIdContext);
	}
	public limit(): LimitContext | undefined {
		return this.tryGetRuleContext(0, LimitContext);
	}
	public with(): WithContext | undefined {
		return this.tryGetRuleContext(0, WithContext);
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_resourceLimit; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterResourceLimit) {
			listener.enterResourceLimit(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitResourceLimit) {
			listener.exitResourceLimit(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitResourceLimit) {
			return visitor.visitResourceLimit(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class LimitContext extends ParserRuleContext {
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_limit; }
	public copyFrom(ctx: LimitContext): void {
		super.copyFrom(ctx);
	}
}
export class QuantityRetentionLimitContext extends LimitContext {
	public quantity(): QuantityContext {
		return this.getRuleContext(0, QuantityContext);
	}
	public retention(): RetentionContext {
		return this.getRuleContext(0, RetentionContext);
	}
	constructor(ctx: LimitContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterQuantityRetentionLimit) {
			listener.enterQuantityRetentionLimit(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitQuantityRetentionLimit) {
			listener.exitQuantityRetentionLimit(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitQuantityRetentionLimit) {
			return visitor.visitQuantityRetentionLimit(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}
export class RetentionLimitContext extends LimitContext {
	public retention(): RetentionContext {
		return this.getRuleContext(0, RetentionContext);
	}
	constructor(ctx: LimitContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterRetentionLimit) {
			listener.enterRetentionLimit(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitRetentionLimit) {
			listener.exitRetentionLimit(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitRetentionLimit) {
			return visitor.visitRetentionLimit(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}
export class QuantityLimitContext extends LimitContext {
	public quantity(): QuantityContext {
		return this.getRuleContext(0, QuantityContext);
	}
	constructor(ctx: LimitContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterQuantityLimit) {
			listener.enterQuantityLimit(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitQuantityLimit) {
			listener.exitQuantityLimit(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitQuantityLimit) {
			return visitor.visitQuantityLimit(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class QuantityContext extends ParserRuleContext {
	public number(): NumberContext {
		return this.getRuleContext(0, NumberContext);
	}
	public EACH(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.EACH, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_quantity; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterQuantity) {
			listener.enterQuantity(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitQuantity) {
			listener.exitQuantity(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitQuantity) {
			return visitor.visitQuantity(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class RetentionContext extends ParserRuleContext {
	public RETAIN(): TerminalNode { return this.getToken(SFMLParser.RETAIN, 0); }
	public number(): NumberContext {
		return this.getRuleContext(0, NumberContext);
	}
	public EACH(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.EACH, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_retention; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterRetention) {
			listener.enterRetention(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitRetention) {
			listener.exitRetention(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitRetention) {
			return visitor.visitRetention(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class WithContext extends ParserRuleContext {
	public WITH(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.WITH, 0); }
	public withClause(): WithClauseContext {
		return this.getRuleContext(0, WithClauseContext);
	}
	public WITHOUT(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.WITHOUT, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_with; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterWith) {
			listener.enterWith(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitWith) {
			listener.exitWith(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitWith) {
			return visitor.visitWith(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class WithClauseContext extends ParserRuleContext {
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_withClause; }
	public copyFrom(ctx: WithClauseContext): void {
		super.copyFrom(ctx);
	}
}
export class WithParenContext extends WithClauseContext {
	public LPAREN(): TerminalNode { return this.getToken(SFMLParser.LPAREN, 0); }
	public withClause(): WithClauseContext {
		return this.getRuleContext(0, WithClauseContext);
	}
	public RPAREN(): TerminalNode { return this.getToken(SFMLParser.RPAREN, 0); }
	constructor(ctx: WithClauseContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterWithParen) {
			listener.enterWithParen(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitWithParen) {
			listener.exitWithParen(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitWithParen) {
			return visitor.visitWithParen(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}
export class WithNegationContext extends WithClauseContext {
	public NOT(): TerminalNode { return this.getToken(SFMLParser.NOT, 0); }
	public withClause(): WithClauseContext {
		return this.getRuleContext(0, WithClauseContext);
	}
	constructor(ctx: WithClauseContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterWithNegation) {
			listener.enterWithNegation(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitWithNegation) {
			listener.exitWithNegation(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitWithNegation) {
			return visitor.visitWithNegation(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}
export class WithConjunctionContext extends WithClauseContext {
	public withClause(): WithClauseContext[];
	public withClause(i: number): WithClauseContext;
	public withClause(i?: number): WithClauseContext | WithClauseContext[] {
		if (i === undefined) {
			return this.getRuleContexts(WithClauseContext);
		} else {
			return this.getRuleContext(i, WithClauseContext);
		}
	}
	public AND(): TerminalNode { return this.getToken(SFMLParser.AND, 0); }
	constructor(ctx: WithClauseContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterWithConjunction) {
			listener.enterWithConjunction(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitWithConjunction) {
			listener.exitWithConjunction(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitWithConjunction) {
			return visitor.visitWithConjunction(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}
export class WithDisjunctionContext extends WithClauseContext {
	public withClause(): WithClauseContext[];
	public withClause(i: number): WithClauseContext;
	public withClause(i?: number): WithClauseContext | WithClauseContext[] {
		if (i === undefined) {
			return this.getRuleContexts(WithClauseContext);
		} else {
			return this.getRuleContext(i, WithClauseContext);
		}
	}
	public OR(): TerminalNode { return this.getToken(SFMLParser.OR, 0); }
	constructor(ctx: WithClauseContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterWithDisjunction) {
			listener.enterWithDisjunction(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitWithDisjunction) {
			listener.exitWithDisjunction(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitWithDisjunction) {
			return visitor.visitWithDisjunction(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}
export class WithTagContext extends WithClauseContext {
	public tagMatcher(): TagMatcherContext {
		return this.getRuleContext(0, TagMatcherContext);
	}
	public TAG(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.TAG, 0); }
	public HASHTAG(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.HASHTAG, 0); }
	constructor(ctx: WithClauseContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterWithTag) {
			listener.enterWithTag(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitWithTag) {
			listener.exitWithTag(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitWithTag) {
			return visitor.visitWithTag(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class TagMatcherContext extends ParserRuleContext {
	public identifier(): IdentifierContext[];
	public identifier(i: number): IdentifierContext;
	public identifier(i?: number): IdentifierContext | IdentifierContext[] {
		if (i === undefined) {
			return this.getRuleContexts(IdentifierContext);
		} else {
			return this.getRuleContext(i, IdentifierContext);
		}
	}
	public COLON(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.COLON, 0); }
	public SLASH(): TerminalNode[];
	public SLASH(i: number): TerminalNode;
	public SLASH(i?: number): TerminalNode | TerminalNode[] {
		if (i === undefined) {
			return this.getTokens(SFMLParser.SLASH);
		} else {
			return this.getToken(SFMLParser.SLASH, i);
		}
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_tagMatcher; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterTagMatcher) {
			listener.enterTagMatcher(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitTagMatcher) {
			listener.exitTagMatcher(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitTagMatcher) {
			return visitor.visitTagMatcher(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class SidequalifierContext extends ParserRuleContext {
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_sidequalifier; }
	public copyFrom(ctx: SidequalifierContext): void {
		super.copyFrom(ctx);
	}
}
export class EachSideContext extends SidequalifierContext {
	public EACH(): TerminalNode { return this.getToken(SFMLParser.EACH, 0); }
	public SIDE(): TerminalNode { return this.getToken(SFMLParser.SIDE, 0); }
	constructor(ctx: SidequalifierContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterEachSide) {
			listener.enterEachSide(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitEachSide) {
			listener.exitEachSide(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitEachSide) {
			return visitor.visitEachSide(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}
export class ListedSidesContext extends SidequalifierContext {
	public side(): SideContext[];
	public side(i: number): SideContext;
	public side(i?: number): SideContext | SideContext[] {
		if (i === undefined) {
			return this.getRuleContexts(SideContext);
		} else {
			return this.getRuleContext(i, SideContext);
		}
	}
	public SIDE(): TerminalNode { return this.getToken(SFMLParser.SIDE, 0); }
	public COMMA(): TerminalNode[];
	public COMMA(i: number): TerminalNode;
	public COMMA(i?: number): TerminalNode | TerminalNode[] {
		if (i === undefined) {
			return this.getTokens(SFMLParser.COMMA);
		} else {
			return this.getToken(SFMLParser.COMMA, i);
		}
	}
	constructor(ctx: SidequalifierContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterListedSides) {
			listener.enterListedSides(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitListedSides) {
			listener.exitListedSides(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitListedSides) {
			return visitor.visitListedSides(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class SideContext extends ParserRuleContext {
	public TOP(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.TOP, 0); }
	public BOTTOM(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.BOTTOM, 0); }
	public NORTH(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.NORTH, 0); }
	public EAST(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.EAST, 0); }
	public SOUTH(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.SOUTH, 0); }
	public WEST(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.WEST, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_side; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterSide) {
			listener.enterSide(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitSide) {
			listener.exitSide(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitSide) {
			return visitor.visitSide(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class SlotqualifierContext extends ParserRuleContext {
	public SLOTS(): TerminalNode { return this.getToken(SFMLParser.SLOTS, 0); }
	public rangeset(): RangesetContext {
		return this.getRuleContext(0, RangesetContext);
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_slotqualifier; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterSlotqualifier) {
			listener.enterSlotqualifier(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitSlotqualifier) {
			listener.exitSlotqualifier(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitSlotqualifier) {
			return visitor.visitSlotqualifier(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class RangesetContext extends ParserRuleContext {
	public range(): RangeContext[];
	public range(i: number): RangeContext;
	public range(i?: number): RangeContext | RangeContext[] {
		if (i === undefined) {
			return this.getRuleContexts(RangeContext);
		} else {
			return this.getRuleContext(i, RangeContext);
		}
	}
	public COMMA(): TerminalNode[];
	public COMMA(i: number): TerminalNode;
	public COMMA(i?: number): TerminalNode | TerminalNode[] {
		if (i === undefined) {
			return this.getTokens(SFMLParser.COMMA);
		} else {
			return this.getToken(SFMLParser.COMMA, i);
		}
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_rangeset; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterRangeset) {
			listener.enterRangeset(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitRangeset) {
			listener.exitRangeset(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitRangeset) {
			return visitor.visitRangeset(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class RangeContext extends ParserRuleContext {
	public number(): NumberContext[];
	public number(i: number): NumberContext;
	public number(i?: number): NumberContext | NumberContext[] {
		if (i === undefined) {
			return this.getRuleContexts(NumberContext);
		} else {
			return this.getRuleContext(i, NumberContext);
		}
	}
	public DASH(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.DASH, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_range; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterRange) {
			listener.enterRange(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitRange) {
			listener.exitRange(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitRange) {
			return visitor.visitRange(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class IfStatementContext extends ParserRuleContext {
	public IF(): TerminalNode[];
	public IF(i: number): TerminalNode;
	public IF(i?: number): TerminalNode | TerminalNode[] {
		if (i === undefined) {
			return this.getTokens(SFMLParser.IF);
		} else {
			return this.getToken(SFMLParser.IF, i);
		}
	}
	public boolexpr(): BoolexprContext[];
	public boolexpr(i: number): BoolexprContext;
	public boolexpr(i?: number): BoolexprContext | BoolexprContext[] {
		if (i === undefined) {
			return this.getRuleContexts(BoolexprContext);
		} else {
			return this.getRuleContext(i, BoolexprContext);
		}
	}
	public THEN(): TerminalNode[];
	public THEN(i: number): TerminalNode;
	public THEN(i?: number): TerminalNode | TerminalNode[] {
		if (i === undefined) {
			return this.getTokens(SFMLParser.THEN);
		} else {
			return this.getToken(SFMLParser.THEN, i);
		}
	}
	public block(): BlockContext[];
	public block(i: number): BlockContext;
	public block(i?: number): BlockContext | BlockContext[] {
		if (i === undefined) {
			return this.getRuleContexts(BlockContext);
		} else {
			return this.getRuleContext(i, BlockContext);
		}
	}
	public END(): TerminalNode { return this.getToken(SFMLParser.END, 0); }
	public ELSE(): TerminalNode[];
	public ELSE(i: number): TerminalNode;
	public ELSE(i?: number): TerminalNode | TerminalNode[] {
		if (i === undefined) {
			return this.getTokens(SFMLParser.ELSE);
		} else {
			return this.getToken(SFMLParser.ELSE, i);
		}
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_ifStatement; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterIfStatement) {
			listener.enterIfStatement(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitIfStatement) {
			listener.exitIfStatement(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitIfStatement) {
			return visitor.visitIfStatement(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class BoolexprContext extends ParserRuleContext {
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_boolexpr; }
	public copyFrom(ctx: BoolexprContext): void {
		super.copyFrom(ctx);
	}
}
export class BooleanTrueContext extends BoolexprContext {
	public TRUE(): TerminalNode { return this.getToken(SFMLParser.TRUE, 0); }
	constructor(ctx: BoolexprContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterBooleanTrue) {
			listener.enterBooleanTrue(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitBooleanTrue) {
			listener.exitBooleanTrue(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitBooleanTrue) {
			return visitor.visitBooleanTrue(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}
export class BooleanFalseContext extends BoolexprContext {
	public FALSE(): TerminalNode { return this.getToken(SFMLParser.FALSE, 0); }
	constructor(ctx: BoolexprContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterBooleanFalse) {
			listener.enterBooleanFalse(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitBooleanFalse) {
			listener.exitBooleanFalse(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitBooleanFalse) {
			return visitor.visitBooleanFalse(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}
export class BooleanParenContext extends BoolexprContext {
	public LPAREN(): TerminalNode { return this.getToken(SFMLParser.LPAREN, 0); }
	public boolexpr(): BoolexprContext {
		return this.getRuleContext(0, BoolexprContext);
	}
	public RPAREN(): TerminalNode { return this.getToken(SFMLParser.RPAREN, 0); }
	constructor(ctx: BoolexprContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterBooleanParen) {
			listener.enterBooleanParen(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitBooleanParen) {
			listener.exitBooleanParen(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitBooleanParen) {
			return visitor.visitBooleanParen(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}
export class BooleanNegationContext extends BoolexprContext {
	public NOT(): TerminalNode { return this.getToken(SFMLParser.NOT, 0); }
	public boolexpr(): BoolexprContext {
		return this.getRuleContext(0, BoolexprContext);
	}
	constructor(ctx: BoolexprContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterBooleanNegation) {
			listener.enterBooleanNegation(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitBooleanNegation) {
			listener.exitBooleanNegation(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitBooleanNegation) {
			return visitor.visitBooleanNegation(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}
export class BooleanConjunctionContext extends BoolexprContext {
	public boolexpr(): BoolexprContext[];
	public boolexpr(i: number): BoolexprContext;
	public boolexpr(i?: number): BoolexprContext | BoolexprContext[] {
		if (i === undefined) {
			return this.getRuleContexts(BoolexprContext);
		} else {
			return this.getRuleContext(i, BoolexprContext);
		}
	}
	public AND(): TerminalNode { return this.getToken(SFMLParser.AND, 0); }
	constructor(ctx: BoolexprContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterBooleanConjunction) {
			listener.enterBooleanConjunction(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitBooleanConjunction) {
			listener.exitBooleanConjunction(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitBooleanConjunction) {
			return visitor.visitBooleanConjunction(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}
export class BooleanDisjunctionContext extends BoolexprContext {
	public boolexpr(): BoolexprContext[];
	public boolexpr(i: number): BoolexprContext;
	public boolexpr(i?: number): BoolexprContext | BoolexprContext[] {
		if (i === undefined) {
			return this.getRuleContexts(BoolexprContext);
		} else {
			return this.getRuleContext(i, BoolexprContext);
		}
	}
	public OR(): TerminalNode { return this.getToken(SFMLParser.OR, 0); }
	constructor(ctx: BoolexprContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterBooleanDisjunction) {
			listener.enterBooleanDisjunction(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitBooleanDisjunction) {
			listener.exitBooleanDisjunction(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitBooleanDisjunction) {
			return visitor.visitBooleanDisjunction(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}
export class BooleanHasContext extends BoolexprContext {
	public labelAccess(): LabelAccessContext {
		return this.getRuleContext(0, LabelAccessContext);
	}
	public HAS(): TerminalNode { return this.getToken(SFMLParser.HAS, 0); }
	public resourcecomparison(): ResourcecomparisonContext {
		return this.getRuleContext(0, ResourcecomparisonContext);
	}
	public setOp(): SetOpContext | undefined {
		return this.tryGetRuleContext(0, SetOpContext);
	}
	constructor(ctx: BoolexprContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterBooleanHas) {
			listener.enterBooleanHas(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitBooleanHas) {
			listener.exitBooleanHas(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitBooleanHas) {
			return visitor.visitBooleanHas(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}
export class BooleanRedstoneContext extends BoolexprContext {
	public REDSTONE(): TerminalNode { return this.getToken(SFMLParser.REDSTONE, 0); }
	public comparisonOp(): ComparisonOpContext | undefined {
		return this.tryGetRuleContext(0, ComparisonOpContext);
	}
	public number(): NumberContext | undefined {
		return this.tryGetRuleContext(0, NumberContext);
	}
	constructor(ctx: BoolexprContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterBooleanRedstone) {
			listener.enterBooleanRedstone(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitBooleanRedstone) {
			listener.exitBooleanRedstone(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitBooleanRedstone) {
			return visitor.visitBooleanRedstone(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class ResourcecomparisonContext extends ParserRuleContext {
	public comparisonOp(): ComparisonOpContext {
		return this.getRuleContext(0, ComparisonOpContext);
	}
	public number(): NumberContext {
		return this.getRuleContext(0, NumberContext);
	}
	public resourceId(): ResourceIdContext | undefined {
		return this.tryGetRuleContext(0, ResourceIdContext);
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_resourcecomparison; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterResourcecomparison) {
			listener.enterResourcecomparison(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitResourcecomparison) {
			listener.exitResourcecomparison(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitResourcecomparison) {
			return visitor.visitResourcecomparison(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class ComparisonOpContext extends ParserRuleContext {
	public GT(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.GT, 0); }
	public LT(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.LT, 0); }
	public EQ(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.EQ, 0); }
	public LE(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.LE, 0); }
	public GE(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.GE, 0); }
	public GT_SYMBOL(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.GT_SYMBOL, 0); }
	public LT_SYMBOL(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.LT_SYMBOL, 0); }
	public EQ_SYMBOL(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.EQ_SYMBOL, 0); }
	public LE_SYMBOL(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.LE_SYMBOL, 0); }
	public GE_SYMBOL(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.GE_SYMBOL, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_comparisonOp; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterComparisonOp) {
			listener.enterComparisonOp(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitComparisonOp) {
			listener.exitComparisonOp(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitComparisonOp) {
			return visitor.visitComparisonOp(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class SetOpContext extends ParserRuleContext {
	public OVERALL(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.OVERALL, 0); }
	public SOME(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.SOME, 0); }
	public EVERY(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.EVERY, 0); }
	public EACH(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.EACH, 0); }
	public ONE(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.ONE, 0); }
	public LONE(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.LONE, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_setOp; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterSetOp) {
			listener.enterSetOp(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitSetOp) {
			listener.exitSetOp(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitSetOp) {
			return visitor.visitSetOp(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class LabelAccessContext extends ParserRuleContext {
	public label(): LabelContext[];
	public label(i: number): LabelContext;
	public label(i?: number): LabelContext | LabelContext[] {
		if (i === undefined) {
			return this.getRuleContexts(LabelContext);
		} else {
			return this.getRuleContext(i, LabelContext);
		}
	}
	public COMMA(): TerminalNode[];
	public COMMA(i: number): TerminalNode;
	public COMMA(i?: number): TerminalNode | TerminalNode[] {
		if (i === undefined) {
			return this.getTokens(SFMLParser.COMMA);
		} else {
			return this.getToken(SFMLParser.COMMA, i);
		}
	}
	public roundrobin(): RoundrobinContext | undefined {
		return this.tryGetRuleContext(0, RoundrobinContext);
	}
	public sidequalifier(): SidequalifierContext | undefined {
		return this.tryGetRuleContext(0, SidequalifierContext);
	}
	public slotqualifier(): SlotqualifierContext | undefined {
		return this.tryGetRuleContext(0, SlotqualifierContext);
	}
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_labelAccess; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterLabelAccess) {
			listener.enterLabelAccess(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitLabelAccess) {
			listener.exitLabelAccess(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitLabelAccess) {
			return visitor.visitLabelAccess(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class RoundrobinContext extends ParserRuleContext {
	public ROUND(): TerminalNode { return this.getToken(SFMLParser.ROUND, 0); }
	public ROBIN(): TerminalNode { return this.getToken(SFMLParser.ROBIN, 0); }
	public BY(): TerminalNode { return this.getToken(SFMLParser.BY, 0); }
	public LABEL(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.LABEL, 0); }
	public BLOCK(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.BLOCK, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_roundrobin; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterRoundrobin) {
			listener.enterRoundrobin(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitRoundrobin) {
			listener.exitRoundrobin(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitRoundrobin) {
			return visitor.visitRoundrobin(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class LabelContext extends ParserRuleContext {
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_label; }
	public copyFrom(ctx: LabelContext): void {
		super.copyFrom(ctx);
	}
}
export class RawLabelContext extends LabelContext {
	public identifier(): IdentifierContext | undefined {
		return this.tryGetRuleContext(0, IdentifierContext);
	}
	constructor(ctx: LabelContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterRawLabel) {
			listener.enterRawLabel(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitRawLabel) {
			listener.exitRawLabel(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitRawLabel) {
			return visitor.visitRawLabel(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}
export class StringLabelContext extends LabelContext {
	public string(): StringContext {
		return this.getRuleContext(0, StringContext);
	}
	constructor(ctx: LabelContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterStringLabel) {
			listener.enterStringLabel(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitStringLabel) {
			listener.exitStringLabel(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitStringLabel) {
			return visitor.visitStringLabel(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class ResourceIdContext extends ParserRuleContext {
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_resourceId; }
	public copyFrom(ctx: ResourceIdContext): void {
		super.copyFrom(ctx);
	}
}
export class ResourceContext extends ResourceIdContext {
	public identifier(): IdentifierContext[];
	public identifier(i: number): IdentifierContext;
	public identifier(i?: number): IdentifierContext | IdentifierContext[] {
		if (i === undefined) {
			return this.getRuleContexts(IdentifierContext);
		} else {
			return this.getRuleContext(i, IdentifierContext);
		}
	}
	public COLON(): TerminalNode[];
	public COLON(i: number): TerminalNode;
	public COLON(i?: number): TerminalNode | TerminalNode[] {
		if (i === undefined) {
			return this.getTokens(SFMLParser.COLON);
		} else {
			return this.getToken(SFMLParser.COLON, i);
		}
	}
	constructor(ctx: ResourceIdContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterResource) {
			listener.enterResource(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitResource) {
			listener.exitResource(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitResource) {
			return visitor.visitResource(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}
export class StringResourceContext extends ResourceIdContext {
	public string(): StringContext {
		return this.getRuleContext(0, StringContext);
	}
	constructor(ctx: ResourceIdContext) {
		super(ctx.parent, ctx.invokingState);
		this.copyFrom(ctx);
	}
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterStringResource) {
			listener.enterStringResource(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitStringResource) {
			listener.exitStringResource(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitStringResource) {
			return visitor.visitStringResource(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class IdentifierContext extends ParserRuleContext {
	public IDENTIFIER(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.IDENTIFIER, 0); }
	public REDSTONE(): TerminalNode | undefined { return this.tryGetToken(SFMLParser.REDSTONE, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_identifier; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterIdentifier) {
			listener.enterIdentifier(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitIdentifier) {
			listener.exitIdentifier(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitIdentifier) {
			return visitor.visitIdentifier(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class StringContext extends ParserRuleContext {
	public STRING(): TerminalNode { return this.getToken(SFMLParser.STRING, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_string; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterString) {
			listener.enterString(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitString) {
			listener.exitString(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitString) {
			return visitor.visitString(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


export class NumberContext extends ParserRuleContext {
	public NUMBER(): TerminalNode { return this.getToken(SFMLParser.NUMBER, 0); }
	constructor(parent: ParserRuleContext | undefined, invokingState: number) {
		super(parent, invokingState);
	}
	// @Override
	public get ruleIndex(): number { return SFMLParser.RULE_number; }
	// @Override
	public enterRule(listener: SFMLListener): void {
		if (listener.enterNumber) {
			listener.enterNumber(this);
		}
	}
	// @Override
	public exitRule(listener: SFMLListener): void {
		if (listener.exitNumber) {
			listener.exitNumber(this);
		}
	}
	// @Override
	public accept<Result>(visitor: SFMLVisitor<Result>): Result {
		if (visitor.visitNumber) {
			return visitor.visitNumber(this);
		} else {
			return visitor.visitChildren(this);
		}
	}
}


