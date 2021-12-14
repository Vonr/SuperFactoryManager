// Generated from d:\Repos\Minecraft\Forge\SuperFactoryManager\src\main\antlr\SFML.g by ANTLR 4.8
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SFMLLexer extends Lexer {
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
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"MOVE", "FROM", "TO", "INPUT", "OUTPUT", "WHERE", "SLOTS", "RETAIN", 
			"EACH", "TOP", "BOTTOM", "NORTH", "EAST", "SOUTH", "WEST", "SIDE", "SELF", 
			"TICKS", "SECONDS", "EVERY", "REDSTONE", "PULSE", "DO", "WORLD", "PROGRAM", 
			"END", "NAME", "COMMA", "COLON", "DASH", "IDENTIFIER", "NUMBER", "STRING", 
			"LINE_COMMENT", "WS", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", 
			"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", 
			"Y", "Z"
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


	public SFMLLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SFML.g"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2%\u0181\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13"+
		"\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3"+
		"\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3"+
		"\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3"+
		"\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3"+
		"\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\34\3\34\3"+
		"\34\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \7 \u0120\n \f \16 \u0123"+
		"\13 \3!\6!\u0126\n!\r!\16!\u0127\3\"\3\"\3\"\3\"\7\"\u012e\n\"\f\"\16"+
		"\"\u0131\13\"\3\"\3\"\3#\3#\3#\3#\7#\u0139\n#\f#\16#\u013c\13#\3#\3#\5"+
		"#\u0140\n#\3#\5#\u0143\n#\3#\3#\3$\6$\u0148\n$\r$\16$\u0149\3$\3$\3%\3"+
		"%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3"+
		"\60\3\61\3\61\3\62\3\62\3\63\3\63\3\64\3\64\3\65\3\65\3\66\3\66\3\67\3"+
		"\67\38\38\39\39\3:\3:\3;\3;\3<\3<\3=\3=\3>\3>\2\2?\3\3\5\4\7\5\t\6\13"+
		"\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'"+
		"\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I\2K\2"+
		"M\2O\2Q\2S\2U\2W\2Y\2[\2]\2_\2a\2c\2e\2g\2i\2k\2m\2o\2q\2s\2u\2w\2y\2"+
		"{\2\3\2\"\5\2C\\aac|\6\2\62;C\\aac|\3\2\62;\3\2$$\4\2\f\f\17\17\5\2\13"+
		"\f\17\17\"\"\4\2CCcc\4\2DDdd\4\2EEee\4\2FFff\4\2GGgg\4\2HHhh\4\2IIii\4"+
		"\2JJjj\4\2KKkk\4\2LLll\4\2MMmm\4\2NNnn\4\2OOoo\4\2PPpp\4\2QQqq\4\2RRr"+
		"r\4\2SSss\4\2TTtt\4\2UUuu\4\2VVvv\4\2WWww\4\2XXxx\4\2YYyy\4\2ZZzz\4\2"+
		"[[{{\4\2\\\\||\2\u016e\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2"+
		"\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25"+
		"\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2"+
		"\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2"+
		"\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3"+
		"\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2"+
		"\2\2E\3\2\2\2\2G\3\2\2\2\3}\3\2\2\2\5\u0082\3\2\2\2\7\u0087\3\2\2\2\t"+
		"\u008a\3\2\2\2\13\u0090\3\2\2\2\r\u0097\3\2\2\2\17\u009d\3\2\2\2\21\u00a3"+
		"\3\2\2\2\23\u00aa\3\2\2\2\25\u00af\3\2\2\2\27\u00b3\3\2\2\2\31\u00ba\3"+
		"\2\2\2\33\u00c0\3\2\2\2\35\u00c5\3\2\2\2\37\u00cb\3\2\2\2!\u00d0\3\2\2"+
		"\2#\u00d5\3\2\2\2%\u00da\3\2\2\2\'\u00e0\3\2\2\2)\u00e8\3\2\2\2+\u00ee"+
		"\3\2\2\2-\u00f7\3\2\2\2/\u00fd\3\2\2\2\61\u0100\3\2\2\2\63\u0106\3\2\2"+
		"\2\65\u010e\3\2\2\2\67\u0112\3\2\2\29\u0117\3\2\2\2;\u0119\3\2\2\2=\u011b"+
		"\3\2\2\2?\u011d\3\2\2\2A\u0125\3\2\2\2C\u0129\3\2\2\2E\u0134\3\2\2\2G"+
		"\u0147\3\2\2\2I\u014d\3\2\2\2K\u014f\3\2\2\2M\u0151\3\2\2\2O\u0153\3\2"+
		"\2\2Q\u0155\3\2\2\2S\u0157\3\2\2\2U\u0159\3\2\2\2W\u015b\3\2\2\2Y\u015d"+
		"\3\2\2\2[\u015f\3\2\2\2]\u0161\3\2\2\2_\u0163\3\2\2\2a\u0165\3\2\2\2c"+
		"\u0167\3\2\2\2e\u0169\3\2\2\2g\u016b\3\2\2\2i\u016d\3\2\2\2k\u016f\3\2"+
		"\2\2m\u0171\3\2\2\2o\u0173\3\2\2\2q\u0175\3\2\2\2s\u0177\3\2\2\2u\u0179"+
		"\3\2\2\2w\u017b\3\2\2\2y\u017d\3\2\2\2{\u017f\3\2\2\2}~\5a\61\2~\177\5"+
		"e\63\2\177\u0080\5s:\2\u0080\u0081\5Q)\2\u0081\4\3\2\2\2\u0082\u0083\5"+
		"S*\2\u0083\u0084\5k\66\2\u0084\u0085\5e\63\2\u0085\u0086\5a\61\2\u0086"+
		"\6\3\2\2\2\u0087\u0088\5o8\2\u0088\u0089\5e\63\2\u0089\b\3\2\2\2\u008a"+
		"\u008b\5Y-\2\u008b\u008c\5c\62\2\u008c\u008d\5g\64\2\u008d\u008e\5q9\2"+
		"\u008e\u008f\5o8\2\u008f\n\3\2\2\2\u0090\u0091\5e\63\2\u0091\u0092\5q"+
		"9\2\u0092\u0093\5o8\2\u0093\u0094\5g\64\2\u0094\u0095\5q9\2\u0095\u0096"+
		"\5o8\2\u0096\f\3\2\2\2\u0097\u0098\5u;\2\u0098\u0099\5W,\2\u0099\u009a"+
		"\5Q)\2\u009a\u009b\5k\66\2\u009b\u009c\5Q)\2\u009c\16\3\2\2\2\u009d\u009e"+
		"\5m\67\2\u009e\u009f\5_\60\2\u009f\u00a0\5e\63\2\u00a0\u00a1\5o8\2\u00a1"+
		"\u00a2\5m\67\2\u00a2\20\3\2\2\2\u00a3\u00a4\5k\66\2\u00a4\u00a5\5Q)\2"+
		"\u00a5\u00a6\5o8\2\u00a6\u00a7\5I%\2\u00a7\u00a8\5Y-\2\u00a8\u00a9\5c"+
		"\62\2\u00a9\22\3\2\2\2\u00aa\u00ab\5Q)\2\u00ab\u00ac\5I%\2\u00ac\u00ad"+
		"\5M\'\2\u00ad\u00ae\5W,\2\u00ae\24\3\2\2\2\u00af\u00b0\5o8\2\u00b0\u00b1"+
		"\5e\63\2\u00b1\u00b2\5g\64\2\u00b2\26\3\2\2\2\u00b3\u00b4\5K&\2\u00b4"+
		"\u00b5\5e\63\2\u00b5\u00b6\5o8\2\u00b6\u00b7\5o8\2\u00b7\u00b8\5e\63\2"+
		"\u00b8\u00b9\5a\61\2\u00b9\30\3\2\2\2\u00ba\u00bb\5c\62\2\u00bb\u00bc"+
		"\5e\63\2\u00bc\u00bd\5k\66\2\u00bd\u00be\5o8\2\u00be\u00bf\5W,\2\u00bf"+
		"\32\3\2\2\2\u00c0\u00c1\5Q)\2\u00c1\u00c2\5I%\2\u00c2\u00c3\5m\67\2\u00c3"+
		"\u00c4\5o8\2\u00c4\34\3\2\2\2\u00c5\u00c6\5m\67\2\u00c6\u00c7\5e\63\2"+
		"\u00c7\u00c8\5q9\2\u00c8\u00c9\5o8\2\u00c9\u00ca\5W,\2\u00ca\36\3\2\2"+
		"\2\u00cb\u00cc\5u;\2\u00cc\u00cd\5Q)\2\u00cd\u00ce\5m\67\2\u00ce\u00cf"+
		"\5o8\2\u00cf \3\2\2\2\u00d0\u00d1\5m\67\2\u00d1\u00d2\5Y-\2\u00d2\u00d3"+
		"\5O(\2\u00d3\u00d4\5Q)\2\u00d4\"\3\2\2\2\u00d5\u00d6\5m\67\2\u00d6\u00d7"+
		"\5Q)\2\u00d7\u00d8\5_\60\2\u00d8\u00d9\5S*\2\u00d9$\3\2\2\2\u00da\u00db"+
		"\5o8\2\u00db\u00dc\5Y-\2\u00dc\u00dd\5M\'\2\u00dd\u00de\5]/\2\u00de\u00df"+
		"\5m\67\2\u00df&\3\2\2\2\u00e0\u00e1\5m\67\2\u00e1\u00e2\5Q)\2\u00e2\u00e3"+
		"\5M\'\2\u00e3\u00e4\5e\63\2\u00e4\u00e5\5c\62\2\u00e5\u00e6\5O(\2\u00e6"+
		"\u00e7\5m\67\2\u00e7(\3\2\2\2\u00e8\u00e9\5Q)\2\u00e9\u00ea\5s:\2\u00ea"+
		"\u00eb\5Q)\2\u00eb\u00ec\5k\66\2\u00ec\u00ed\5y=\2\u00ed*\3\2\2\2\u00ee"+
		"\u00ef\5k\66\2\u00ef\u00f0\5Q)\2\u00f0\u00f1\5O(\2\u00f1\u00f2\5m\67\2"+
		"\u00f2\u00f3\5o8\2\u00f3\u00f4\5e\63\2\u00f4\u00f5\5c\62\2\u00f5\u00f6"+
		"\5Q)\2\u00f6,\3\2\2\2\u00f7\u00f8\5g\64\2\u00f8\u00f9\5q9\2\u00f9\u00fa"+
		"\5_\60\2\u00fa\u00fb\5m\67\2\u00fb\u00fc\5Q)\2\u00fc.\3\2\2\2\u00fd\u00fe"+
		"\5O(\2\u00fe\u00ff\5e\63\2\u00ff\60\3\2\2\2\u0100\u0101\5u;\2\u0101\u0102"+
		"\5e\63\2\u0102\u0103\5k\66\2\u0103\u0104\5_\60\2\u0104\u0105\5O(\2\u0105"+
		"\62\3\2\2\2\u0106\u0107\5g\64\2\u0107\u0108\5k\66\2\u0108\u0109\5e\63"+
		"\2\u0109\u010a\5U+\2\u010a\u010b\5k\66\2\u010b\u010c\5I%\2\u010c\u010d"+
		"\5a\61\2\u010d\64\3\2\2\2\u010e\u010f\5Q)\2\u010f\u0110\5c\62\2\u0110"+
		"\u0111\5O(\2\u0111\66\3\2\2\2\u0112\u0113\5c\62\2\u0113\u0114\5I%\2\u0114"+
		"\u0115\5a\61\2\u0115\u0116\5Q)\2\u01168\3\2\2\2\u0117\u0118\7.\2\2\u0118"+
		":\3\2\2\2\u0119\u011a\7<\2\2\u011a<\3\2\2\2\u011b\u011c\7/\2\2\u011c>"+
		"\3\2\2\2\u011d\u0121\t\2\2\2\u011e\u0120\t\3\2\2\u011f\u011e\3\2\2\2\u0120"+
		"\u0123\3\2\2\2\u0121\u011f\3\2\2\2\u0121\u0122\3\2\2\2\u0122@\3\2\2\2"+
		"\u0123\u0121\3\2\2\2\u0124\u0126\t\4\2\2\u0125\u0124\3\2\2\2\u0126\u0127"+
		"\3\2\2\2\u0127\u0125\3\2\2\2\u0127\u0128\3\2\2\2\u0128B\3\2\2\2\u0129"+
		"\u012f\7$\2\2\u012a\u012e\n\5\2\2\u012b\u012c\7^\2\2\u012c\u012e\7$\2"+
		"\2\u012d\u012a\3\2\2\2\u012d\u012b\3\2\2\2\u012e\u0131\3\2\2\2\u012f\u012d"+
		"\3\2\2\2\u012f\u0130\3\2\2\2\u0130\u0132\3\2\2\2\u0131\u012f\3\2\2\2\u0132"+
		"\u0133\7$\2\2\u0133D\3\2\2\2\u0134\u0135\7/\2\2\u0135\u0136\7/\2\2\u0136"+
		"\u013a\3\2\2\2\u0137\u0139\n\6\2\2\u0138\u0137\3\2\2\2\u0139\u013c\3\2"+
		"\2\2\u013a\u0138\3\2\2\2\u013a\u013b\3\2\2\2\u013b\u0142\3\2\2\2\u013c"+
		"\u013a\3\2\2\2\u013d\u0143\7\2\2\3\u013e\u0140\7\17\2\2\u013f\u013e\3"+
		"\2\2\2\u013f\u0140\3\2\2\2\u0140\u0141\3\2\2\2\u0141\u0143\7\f\2\2\u0142"+
		"\u013d\3\2\2\2\u0142\u013f\3\2\2\2\u0143\u0144\3\2\2\2\u0144\u0145\b#"+
		"\2\2\u0145F\3\2\2\2\u0146\u0148\t\7\2\2\u0147\u0146\3\2\2\2\u0148\u0149"+
		"\3\2\2\2\u0149\u0147\3\2\2\2\u0149\u014a\3\2\2\2\u014a\u014b\3\2\2\2\u014b"+
		"\u014c\b$\3\2\u014cH\3\2\2\2\u014d\u014e\t\b\2\2\u014eJ\3\2\2\2\u014f"+
		"\u0150\t\t\2\2\u0150L\3\2\2\2\u0151\u0152\t\n\2\2\u0152N\3\2\2\2\u0153"+
		"\u0154\t\13\2\2\u0154P\3\2\2\2\u0155\u0156\t\f\2\2\u0156R\3\2\2\2\u0157"+
		"\u0158\t\r\2\2\u0158T\3\2\2\2\u0159\u015a\t\16\2\2\u015aV\3\2\2\2\u015b"+
		"\u015c\t\17\2\2\u015cX\3\2\2\2\u015d\u015e\t\20\2\2\u015eZ\3\2\2\2\u015f"+
		"\u0160\t\21\2\2\u0160\\\3\2\2\2\u0161\u0162\t\22\2\2\u0162^\3\2\2\2\u0163"+
		"\u0164\t\23\2\2\u0164`\3\2\2\2\u0165\u0166\t\24\2\2\u0166b\3\2\2\2\u0167"+
		"\u0168\t\25\2\2\u0168d\3\2\2\2\u0169\u016a\t\26\2\2\u016af\3\2\2\2\u016b"+
		"\u016c\t\27\2\2\u016ch\3\2\2\2\u016d\u016e\t\30\2\2\u016ej\3\2\2\2\u016f"+
		"\u0170\t\31\2\2\u0170l\3\2\2\2\u0171\u0172\t\32\2\2\u0172n\3\2\2\2\u0173"+
		"\u0174\t\33\2\2\u0174p\3\2\2\2\u0175\u0176\t\34\2\2\u0176r\3\2\2\2\u0177"+
		"\u0178\t\35\2\2\u0178t\3\2\2\2\u0179\u017a\t\36\2\2\u017av\3\2\2\2\u017b"+
		"\u017c\t\37\2\2\u017cx\3\2\2\2\u017d\u017e\t \2\2\u017ez\3\2\2\2\u017f"+
		"\u0180\t!\2\2\u0180|\3\2\2\2\13\2\u0121\u0127\u012d\u012f\u013a\u013f"+
		"\u0142\u0149\4\b\2\2\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}