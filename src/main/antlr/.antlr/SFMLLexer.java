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
		MOVE=1, FROM=2, TO=3, INPUT=4, OUTPUT=5, WHERE=6, SLOT=7, RETAIN=8, EACH=9, 
		TOP=10, BOTTOM=11, NORTH=12, EAST=13, SOUTH=14, WEST=15, SIDE=16, TICKS=17, 
		SECONDS=18, EVERY=19, REDSTONE=20, PULSE=21, DO=22, WORLD=23, PROGRAM=24, 
		END=25, NAME=26, COMMA=27, COLON=28, IDENTIFIER=29, NUMBER=30, STRING=31, 
		LINE_COMMENT=32, WS=33;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"MOVE", "FROM", "TO", "INPUT", "OUTPUT", "WHERE", "SLOT", "RETAIN", "EACH", 
			"TOP", "BOTTOM", "NORTH", "EAST", "SOUTH", "WEST", "SIDE", "TICKS", "SECONDS", 
			"EVERY", "REDSTONE", "PULSE", "DO", "WORLD", "PROGRAM", "END", "NAME", 
			"COMMA", "COLON", "IDENTIFIER", "NUMBER", "STRING", "LINE_COMMENT", "WS", 
			"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", 
			"O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2#\u0175\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\3\2\3"+
		"\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3"+
		"\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3"+
		"\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\21\3"+
		"\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3"+
		"\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3"+
		"\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3"+
		"\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3"+
		"\36\7\36\u0114\n\36\f\36\16\36\u0117\13\36\3\37\6\37\u011a\n\37\r\37\16"+
		"\37\u011b\3 \3 \3 \3 \7 \u0122\n \f \16 \u0125\13 \3 \3 \3!\3!\3!\3!\7"+
		"!\u012d\n!\f!\16!\u0130\13!\3!\3!\5!\u0134\n!\3!\5!\u0137\n!\3!\3!\3\""+
		"\6\"\u013c\n\"\r\"\16\"\u013d\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'"+
		"\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60\3\61\3\61\3"+
		"\62\3\62\3\63\3\63\3\64\3\64\3\65\3\65\3\66\3\66\3\67\3\67\38\38\39\3"+
		"9\3:\3:\3;\3;\3<\3<\2\2=\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f"+
		"\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63"+
		"\33\65\34\67\359\36;\37= ?!A\"C#E\2G\2I\2K\2M\2O\2Q\2S\2U\2W\2Y\2[\2]"+
		"\2_\2a\2c\2e\2g\2i\2k\2m\2o\2q\2s\2u\2w\2\3\2\"\5\2C\\aac|\6\2\62;C\\"+
		"aac|\3\2\62;\3\2$$\4\2\f\f\17\17\5\2\13\f\17\17\"\"\4\2CCcc\4\2DDdd\4"+
		"\2EEee\4\2FFff\4\2GGgg\4\2HHhh\4\2IIii\4\2JJjj\4\2KKkk\4\2LLll\4\2MMm"+
		"m\4\2NNnn\4\2OOoo\4\2PPpp\4\2QQqq\4\2RRrr\4\2SSss\4\2TTtt\4\2UUuu\4\2"+
		"VVvv\4\2WWww\4\2XXxx\4\2YYyy\4\2ZZzz\4\2[[{{\4\2\\\\||\2\u0162\2\3\3\2"+
		"\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17"+
		"\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2"+
		"\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3"+
		"\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3"+
		"\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2"+
		"=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\3y\3\2\2\2\5~\3\2\2\2\7\u0083"+
		"\3\2\2\2\t\u0086\3\2\2\2\13\u008c\3\2\2\2\r\u0093\3\2\2\2\17\u0099\3\2"+
		"\2\2\21\u009e\3\2\2\2\23\u00a5\3\2\2\2\25\u00aa\3\2\2\2\27\u00ae\3\2\2"+
		"\2\31\u00b5\3\2\2\2\33\u00bb\3\2\2\2\35\u00c0\3\2\2\2\37\u00c6\3\2\2\2"+
		"!\u00cb\3\2\2\2#\u00d0\3\2\2\2%\u00d6\3\2\2\2\'\u00de\3\2\2\2)\u00e4\3"+
		"\2\2\2+\u00ed\3\2\2\2-\u00f3\3\2\2\2/\u00f6\3\2\2\2\61\u00fc\3\2\2\2\63"+
		"\u0104\3\2\2\2\65\u0108\3\2\2\2\67\u010d\3\2\2\29\u010f\3\2\2\2;\u0111"+
		"\3\2\2\2=\u0119\3\2\2\2?\u011d\3\2\2\2A\u0128\3\2\2\2C\u013b\3\2\2\2E"+
		"\u0141\3\2\2\2G\u0143\3\2\2\2I\u0145\3\2\2\2K\u0147\3\2\2\2M\u0149\3\2"+
		"\2\2O\u014b\3\2\2\2Q\u014d\3\2\2\2S\u014f\3\2\2\2U\u0151\3\2\2\2W\u0153"+
		"\3\2\2\2Y\u0155\3\2\2\2[\u0157\3\2\2\2]\u0159\3\2\2\2_\u015b\3\2\2\2a"+
		"\u015d\3\2\2\2c\u015f\3\2\2\2e\u0161\3\2\2\2g\u0163\3\2\2\2i\u0165\3\2"+
		"\2\2k\u0167\3\2\2\2m\u0169\3\2\2\2o\u016b\3\2\2\2q\u016d\3\2\2\2s\u016f"+
		"\3\2\2\2u\u0171\3\2\2\2w\u0173\3\2\2\2yz\5]/\2z{\5a\61\2{|\5o8\2|}\5M"+
		"\'\2}\4\3\2\2\2~\177\5O(\2\177\u0080\5g\64\2\u0080\u0081\5a\61\2\u0081"+
		"\u0082\5]/\2\u0082\6\3\2\2\2\u0083\u0084\5k\66\2\u0084\u0085\5a\61\2\u0085"+
		"\b\3\2\2\2\u0086\u0087\5U+\2\u0087\u0088\5_\60\2\u0088\u0089\5c\62\2\u0089"+
		"\u008a\5m\67\2\u008a\u008b\5k\66\2\u008b\n\3\2\2\2\u008c\u008d\5a\61\2"+
		"\u008d\u008e\5m\67\2\u008e\u008f\5k\66\2\u008f\u0090\5c\62\2\u0090\u0091"+
		"\5m\67\2\u0091\u0092\5k\66\2\u0092\f\3\2\2\2\u0093\u0094\5q9\2\u0094\u0095"+
		"\5S*\2\u0095\u0096\5M\'\2\u0096\u0097\5g\64\2\u0097\u0098\5M\'\2\u0098"+
		"\16\3\2\2\2\u0099\u009a\5i\65\2\u009a\u009b\5[.\2\u009b\u009c\5a\61\2"+
		"\u009c\u009d\5k\66\2\u009d\20\3\2\2\2\u009e\u009f\5g\64\2\u009f\u00a0"+
		"\5M\'\2\u00a0\u00a1\5k\66\2\u00a1\u00a2\5E#\2\u00a2\u00a3\5U+\2\u00a3"+
		"\u00a4\5_\60\2\u00a4\22\3\2\2\2\u00a5\u00a6\5M\'\2\u00a6\u00a7\5E#\2\u00a7"+
		"\u00a8\5I%\2\u00a8\u00a9\5S*\2\u00a9\24\3\2\2\2\u00aa\u00ab\5k\66\2\u00ab"+
		"\u00ac\5a\61\2\u00ac\u00ad\5c\62\2\u00ad\26\3\2\2\2\u00ae\u00af\5G$\2"+
		"\u00af\u00b0\5a\61\2\u00b0\u00b1\5k\66\2\u00b1\u00b2\5k\66\2\u00b2\u00b3"+
		"\5a\61\2\u00b3\u00b4\5]/\2\u00b4\30\3\2\2\2\u00b5\u00b6\5_\60\2\u00b6"+
		"\u00b7\5a\61\2\u00b7\u00b8\5g\64\2\u00b8\u00b9\5k\66\2\u00b9\u00ba\5S"+
		"*\2\u00ba\32\3\2\2\2\u00bb\u00bc\5M\'\2\u00bc\u00bd\5E#\2\u00bd\u00be"+
		"\5i\65\2\u00be\u00bf\5k\66\2\u00bf\34\3\2\2\2\u00c0\u00c1\5i\65\2\u00c1"+
		"\u00c2\5a\61\2\u00c2\u00c3\5m\67\2\u00c3\u00c4\5k\66\2\u00c4\u00c5\5S"+
		"*\2\u00c5\36\3\2\2\2\u00c6\u00c7\5q9\2\u00c7\u00c8\5M\'\2\u00c8\u00c9"+
		"\5i\65\2\u00c9\u00ca\5k\66\2\u00ca \3\2\2\2\u00cb\u00cc\5i\65\2\u00cc"+
		"\u00cd\5U+\2\u00cd\u00ce\5K&\2\u00ce\u00cf\5M\'\2\u00cf\"\3\2\2\2\u00d0"+
		"\u00d1\5k\66\2\u00d1\u00d2\5U+\2\u00d2\u00d3\5I%\2\u00d3\u00d4\5Y-\2\u00d4"+
		"\u00d5\5i\65\2\u00d5$\3\2\2\2\u00d6\u00d7\5i\65\2\u00d7\u00d8\5M\'\2\u00d8"+
		"\u00d9\5I%\2\u00d9\u00da\5a\61\2\u00da\u00db\5_\60\2\u00db\u00dc\5K&\2"+
		"\u00dc\u00dd\5i\65\2\u00dd&\3\2\2\2\u00de\u00df\5M\'\2\u00df\u00e0\5o"+
		"8\2\u00e0\u00e1\5M\'\2\u00e1\u00e2\5g\64\2\u00e2\u00e3\5u;\2\u00e3(\3"+
		"\2\2\2\u00e4\u00e5\5g\64\2\u00e5\u00e6\5M\'\2\u00e6\u00e7\5K&\2\u00e7"+
		"\u00e8\5i\65\2\u00e8\u00e9\5k\66\2\u00e9\u00ea\5a\61\2\u00ea\u00eb\5_"+
		"\60\2\u00eb\u00ec\5M\'\2\u00ec*\3\2\2\2\u00ed\u00ee\5c\62\2\u00ee\u00ef"+
		"\5m\67\2\u00ef\u00f0\5[.\2\u00f0\u00f1\5i\65\2\u00f1\u00f2\5M\'\2\u00f2"+
		",\3\2\2\2\u00f3\u00f4\5K&\2\u00f4\u00f5\5a\61\2\u00f5.\3\2\2\2\u00f6\u00f7"+
		"\5q9\2\u00f7\u00f8\5a\61\2\u00f8\u00f9\5g\64\2\u00f9\u00fa\5[.\2\u00fa"+
		"\u00fb\5K&\2\u00fb\60\3\2\2\2\u00fc\u00fd\5c\62\2\u00fd\u00fe\5g\64\2"+
		"\u00fe\u00ff\5a\61\2\u00ff\u0100\5Q)\2\u0100\u0101\5g\64\2\u0101\u0102"+
		"\5E#\2\u0102\u0103\5]/\2\u0103\62\3\2\2\2\u0104\u0105\5M\'\2\u0105\u0106"+
		"\5_\60\2\u0106\u0107\5K&\2\u0107\64\3\2\2\2\u0108\u0109\5_\60\2\u0109"+
		"\u010a\5E#\2\u010a\u010b\5]/\2\u010b\u010c\5M\'\2\u010c\66\3\2\2\2\u010d"+
		"\u010e\7.\2\2\u010e8\3\2\2\2\u010f\u0110\7<\2\2\u0110:\3\2\2\2\u0111\u0115"+
		"\t\2\2\2\u0112\u0114\t\3\2\2\u0113\u0112\3\2\2\2\u0114\u0117\3\2\2\2\u0115"+
		"\u0113\3\2\2\2\u0115\u0116\3\2\2\2\u0116<\3\2\2\2\u0117\u0115\3\2\2\2"+
		"\u0118\u011a\t\4\2\2\u0119\u0118\3\2\2\2\u011a\u011b\3\2\2\2\u011b\u0119"+
		"\3\2\2\2\u011b\u011c\3\2\2\2\u011c>\3\2\2\2\u011d\u0123\7$\2\2\u011e\u0122"+
		"\n\5\2\2\u011f\u0120\7^\2\2\u0120\u0122\7$\2\2\u0121\u011e\3\2\2\2\u0121"+
		"\u011f\3\2\2\2\u0122\u0125\3\2\2\2\u0123\u0121\3\2\2\2\u0123\u0124\3\2"+
		"\2\2\u0124\u0126\3\2\2\2\u0125\u0123\3\2\2\2\u0126\u0127\7$\2\2\u0127"+
		"@\3\2\2\2\u0128\u0129\7/\2\2\u0129\u012a\7/\2\2\u012a\u012e\3\2\2\2\u012b"+
		"\u012d\n\6\2\2\u012c\u012b\3\2\2\2\u012d\u0130\3\2\2\2\u012e\u012c\3\2"+
		"\2\2\u012e\u012f\3\2\2\2\u012f\u0136\3\2\2\2\u0130\u012e\3\2\2\2\u0131"+
		"\u0137\7\2\2\3\u0132\u0134\7\17\2\2\u0133\u0132\3\2\2\2\u0133\u0134\3"+
		"\2\2\2\u0134\u0135\3\2\2\2\u0135\u0137\7\f\2\2\u0136\u0131\3\2\2\2\u0136"+
		"\u0133\3\2\2\2\u0137\u0138\3\2\2\2\u0138\u0139\b!\2\2\u0139B\3\2\2\2\u013a"+
		"\u013c\t\7\2\2\u013b\u013a\3\2\2\2\u013c\u013d\3\2\2\2\u013d\u013b\3\2"+
		"\2\2\u013d\u013e\3\2\2\2\u013e\u013f\3\2\2\2\u013f\u0140\b\"\3\2\u0140"+
		"D\3\2\2\2\u0141\u0142\t\b\2\2\u0142F\3\2\2\2\u0143\u0144\t\t\2\2\u0144"+
		"H\3\2\2\2\u0145\u0146\t\n\2\2\u0146J\3\2\2\2\u0147\u0148\t\13\2\2\u0148"+
		"L\3\2\2\2\u0149\u014a\t\f\2\2\u014aN\3\2\2\2\u014b\u014c\t\r\2\2\u014c"+
		"P\3\2\2\2\u014d\u014e\t\16\2\2\u014eR\3\2\2\2\u014f\u0150\t\17\2\2\u0150"+
		"T\3\2\2\2\u0151\u0152\t\20\2\2\u0152V\3\2\2\2\u0153\u0154\t\21\2\2\u0154"+
		"X\3\2\2\2\u0155\u0156\t\22\2\2\u0156Z\3\2\2\2\u0157\u0158\t\23\2\2\u0158"+
		"\\\3\2\2\2\u0159\u015a\t\24\2\2\u015a^\3\2\2\2\u015b\u015c\t\25\2\2\u015c"+
		"`\3\2\2\2\u015d\u015e\t\26\2\2\u015eb\3\2\2\2\u015f\u0160\t\27\2\2\u0160"+
		"d\3\2\2\2\u0161\u0162\t\30\2\2\u0162f\3\2\2\2\u0163\u0164\t\31\2\2\u0164"+
		"h\3\2\2\2\u0165\u0166\t\32\2\2\u0166j\3\2\2\2\u0167\u0168\t\33\2\2\u0168"+
		"l\3\2\2\2\u0169\u016a\t\34\2\2\u016an\3\2\2\2\u016b\u016c\t\35\2\2\u016c"+
		"p\3\2\2\2\u016d\u016e\t\36\2\2\u016er\3\2\2\2\u016f\u0170\t\37\2\2\u0170"+
		"t\3\2\2\2\u0171\u0172\t \2\2\u0172v\3\2\2\2\u0173\u0174\t!\2\2\u0174x"+
		"\3\2\2\2\13\2\u0115\u011b\u0121\u0123\u012e\u0133\u0136\u013d\4\b\2\2"+
		"\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}