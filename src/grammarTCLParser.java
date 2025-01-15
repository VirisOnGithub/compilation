package src;// Generated from grammarTCL.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class grammarTCLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, PRINT=9, 
		SEMICOL=10, NEWLINE=11, INT=12, BOOL=13, OR=14, AND=15, MUL=16, DIV=17, 
		MODULO=18, ADD=19, SUB=20, SUPEQ=21, INFEQ=22, SUP=23, INF=24, EQUALS=25, 
		DIFF=26, NOT=27, ASSIGN=28, BASE_TYPE=29, IF=30, ELSE=31, WHILE=32, FOR=33, 
		RETURN=34, VAR=35;
	public static final int
		RULE_expr = 0, RULE_type = 1, RULE_instr = 2, RULE_core_fct = 3, RULE_decl_fct = 4, 
		RULE_main = 5;
	private static String[] makeRuleNames() {
		return new String[] {
			"expr", "type", "instr", "core_fct", "decl_fct", "main"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'('", "')'", "'{'", "','", "'}'", "'['", "']'", "'int main()'", 
			"'print'", "';'", null, null, null, "'||'", "'&&'", "'*'", "'/'", "'%'", 
			"'+'", "'-'", "'>='", "'<='", "'>'", "'<'", "'=='", "'!='", "'!'", "'='", 
			null, "'if'", "'else'", "'while'", "'for'", "'return'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, "PRINT", "SEMICOL", 
			"NEWLINE", "INT", "BOOL", "OR", "AND", "MUL", "DIV", "MODULO", "ADD", 
			"SUB", "SUPEQ", "INFEQ", "SUP", "INF", "EQUALS", "DIFF", "NOT", "ASSIGN", 
			"BASE_TYPE", "IF", "ELSE", "WHILE", "FOR", "RETURN", "VAR"
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
	public String getGrammarFileName() { return "grammarTCL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public grammarTCLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NegationContext extends ExprContext {
		public TerminalNode NOT() { return getToken(grammarTCLParser.NOT, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public NegationContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterNegation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitNegation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitNegation(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ComparisonContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode SUP() { return getToken(grammarTCLParser.SUP, 0); }
		public TerminalNode INF() { return getToken(grammarTCLParser.INF, 0); }
		public TerminalNode SUPEQ() { return getToken(grammarTCLParser.SUPEQ, 0); }
		public TerminalNode INFEQ() { return getToken(grammarTCLParser.INFEQ, 0); }
		public ComparisonContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterComparison(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitComparison(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitComparison(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class OrContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode OR() { return getToken(grammarTCLParser.OR, 0); }
		public OrContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterOr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitOr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitOr(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class OppositeContext extends ExprContext {
		public TerminalNode SUB() { return getToken(grammarTCLParser.SUB, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public OppositeContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterOpposite(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitOpposite(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitOpposite(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IntegerContext extends ExprContext {
		public TerminalNode INT() { return getToken(grammarTCLParser.INT, 0); }
		public IntegerContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterInteger(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitInteger(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitInteger(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Tab_accessContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public Tab_accessContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterTab_access(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitTab_access(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitTab_access(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BracketsContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public BracketsContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterBrackets(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitBrackets(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitBrackets(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class CallContext extends ExprContext {
		public TerminalNode VAR() { return getToken(grammarTCLParser.VAR, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public CallContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitCall(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BooleanContext extends ExprContext {
		public TerminalNode BOOL() { return getToken(grammarTCLParser.BOOL, 0); }
		public BooleanContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterBoolean(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitBoolean(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitBoolean(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AndContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode AND() { return getToken(grammarTCLParser.AND, 0); }
		public AndContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterAnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitAnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitAnd(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class VariableContext extends ExprContext {
		public TerminalNode VAR() { return getToken(grammarTCLParser.VAR, 0); }
		public VariableContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MultiplicationContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode MUL() { return getToken(grammarTCLParser.MUL, 0); }
		public TerminalNode DIV() { return getToken(grammarTCLParser.DIV, 0); }
		public TerminalNode MODULO() { return getToken(grammarTCLParser.MODULO, 0); }
		public MultiplicationContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterMultiplication(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitMultiplication(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitMultiplication(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class EqualityContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode EQUALS() { return getToken(grammarTCLParser.EQUALS, 0); }
		public TerminalNode DIFF() { return getToken(grammarTCLParser.DIFF, 0); }
		public EqualityContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterEquality(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitEquality(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitEquality(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Tab_initializationContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public Tab_initializationContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterTab_initialization(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitTab_initialization(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitTab_initialization(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AdditionContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode ADD() { return getToken(grammarTCLParser.ADD, 0); }
		public TerminalNode SUB() { return getToken(grammarTCLParser.SUB, 0); }
		public AdditionContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterAddition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitAddition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitAddition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 0;
		enterRecursionRule(_localctx, 0, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(49);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				_localctx = new BracketsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(13);
				match(T__0);
				setState(14);
				expr(0);
				setState(15);
				match(T__1);
				}
				break;
			case 2:
				{
				_localctx = new Tab_initializationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(17);
				match(T__2);
				setState(26);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 34495016970L) != 0)) {
					{
					setState(18);
					expr(0);
					setState(23);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__3) {
						{
						{
						setState(19);
						match(T__3);
						setState(20);
						expr(0);
						}
						}
						setState(25);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(28);
				match(T__4);
				}
				break;
			case 3:
				{
				_localctx = new CallContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(29);
				match(VAR);
				setState(30);
				match(T__0);
				setState(39);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 34495016970L) != 0)) {
					{
					setState(31);
					expr(0);
					setState(36);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__3) {
						{
						{
						setState(32);
						match(T__3);
						setState(33);
						expr(0);
						}
						}
						setState(38);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(41);
				match(T__1);
				}
				break;
			case 4:
				{
				_localctx = new OppositeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(42);
				match(SUB);
				setState(43);
				expr(11);
				}
				break;
			case 5:
				{
				_localctx = new NegationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(44);
				match(NOT);
				setState(45);
				expr(10);
				}
				break;
			case 6:
				{
				_localctx = new VariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(46);
				match(VAR);
				}
				break;
			case 7:
				{
				_localctx = new IntegerContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(47);
				match(INT);
				}
				break;
			case 8:
				{
				_localctx = new BooleanContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(48);
				match(BOOL);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(76);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(74);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
					case 1:
						{
						_localctx = new MultiplicationContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(51);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(52);
						((MultiplicationContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 458752L) != 0)) ) {
							((MultiplicationContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(53);
						expr(10);
						}
						break;
					case 2:
						{
						_localctx = new AdditionContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(54);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(55);
						((AdditionContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==ADD || _la==SUB) ) {
							((AdditionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(56);
						expr(9);
						}
						break;
					case 3:
						{
						_localctx = new ComparisonContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(57);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(58);
						((ComparisonContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 31457280L) != 0)) ) {
							((ComparisonContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(59);
						expr(8);
						}
						break;
					case 4:
						{
						_localctx = new EqualityContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(60);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(61);
						((EqualityContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==EQUALS || _la==DIFF) ) {
							((EqualityContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(62);
						expr(7);
						}
						break;
					case 5:
						{
						_localctx = new AndContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(63);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(64);
						match(AND);
						setState(65);
						expr(6);
						}
						break;
					case 6:
						{
						_localctx = new OrContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(66);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(67);
						match(OR);
						setState(68);
						expr(5);
						}
						break;
					case 7:
						{
						_localctx = new Tab_accessContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(69);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(70);
						match(T__5);
						setState(71);
						expr(0);
						setState(72);
						match(T__6);
						}
						break;
					}
					} 
				}
				setState(78);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
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

	@SuppressWarnings("CheckReturnValue")
	public static class TypeContext extends ParserRuleContext {
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
	 
		public TypeContext() { }
		public void copyFrom(TypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Base_typeContext extends TypeContext {
		public TerminalNode BASE_TYPE() { return getToken(grammarTCLParser.BASE_TYPE, 0); }
		public Base_typeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterBase_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitBase_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitBase_type(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class Tab_typeContext extends TypeContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public Tab_typeContext(TypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterTab_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitTab_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitTab_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		return type(0);
	}

	private TypeContext type(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TypeContext _localctx = new TypeContext(_ctx, _parentState);
		TypeContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_type, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			_localctx = new Base_typeContext(_localctx);
			_ctx = _localctx;
			_prevctx = _localctx;

			setState(80);
			match(BASE_TYPE);
			}
			_ctx.stop = _input.LT(-1);
			setState(87);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Tab_typeContext(new TypeContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_type);
					setState(82);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(83);
					match(T__5);
					setState(84);
					match(T__6);
					}
					} 
				}
				setState(89);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,7,_ctx);
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

	@SuppressWarnings("CheckReturnValue")
	public static class InstrContext extends ParserRuleContext {
		public InstrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instr; }
	 
		public InstrContext() { }
		public void copyFrom(InstrContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class PrintContext extends InstrContext {
		public TerminalNode PRINT() { return getToken(grammarTCLParser.PRINT, 0); }
		public TerminalNode VAR() { return getToken(grammarTCLParser.VAR, 0); }
		public TerminalNode SEMICOL() { return getToken(grammarTCLParser.SEMICOL, 0); }
		public PrintContext(InstrContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterPrint(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitPrint(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitPrint(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AssignmentContext extends InstrContext {
		public TerminalNode VAR() { return getToken(grammarTCLParser.VAR, 0); }
		public TerminalNode ASSIGN() { return getToken(grammarTCLParser.ASSIGN, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode SEMICOL() { return getToken(grammarTCLParser.SEMICOL, 0); }
		public AssignmentContext(InstrContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitAssignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitAssignment(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ForContext extends InstrContext {
		public TerminalNode FOR() { return getToken(grammarTCLParser.FOR, 0); }
		public List<InstrContext> instr() {
			return getRuleContexts(InstrContext.class);
		}
		public InstrContext instr(int i) {
			return getRuleContext(InstrContext.class,i);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode SEMICOL() { return getToken(grammarTCLParser.SEMICOL, 0); }
		public ForContext(InstrContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterFor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitFor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitFor(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BlockContext extends InstrContext {
		public List<InstrContext> instr() {
			return getRuleContexts(InstrContext.class);
		}
		public InstrContext instr(int i) {
			return getRuleContext(InstrContext.class,i);
		}
		public BlockContext(InstrContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitBlock(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class WhileContext extends InstrContext {
		public TerminalNode WHILE() { return getToken(grammarTCLParser.WHILE, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public InstrContext instr() {
			return getRuleContext(InstrContext.class,0);
		}
		public WhileContext(InstrContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterWhile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitWhile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitWhile(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DeclarationContext extends InstrContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode VAR() { return getToken(grammarTCLParser.VAR, 0); }
		public TerminalNode SEMICOL() { return getToken(grammarTCLParser.SEMICOL, 0); }
		public TerminalNode ASSIGN() { return getToken(grammarTCLParser.ASSIGN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public DeclarationContext(InstrContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IfContext extends InstrContext {
		public TerminalNode IF() { return getToken(grammarTCLParser.IF, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<InstrContext> instr() {
			return getRuleContexts(InstrContext.class);
		}
		public InstrContext instr(int i) {
			return getRuleContext(InstrContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(grammarTCLParser.ELSE, 0); }
		public IfContext(InstrContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterIf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitIf(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitIf(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ReturnContext extends InstrContext {
		public TerminalNode RETURN() { return getToken(grammarTCLParser.RETURN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode SEMICOL() { return getToken(grammarTCLParser.SEMICOL, 0); }
		public ReturnContext(InstrContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterReturn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitReturn(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitReturn(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InstrContext instr() throws RecognitionException {
		InstrContext _localctx = new InstrContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_instr);
		int _la;
		try {
			setState(153);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case BASE_TYPE:
				_localctx = new DeclarationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(90);
				type(0);
				setState(91);
				match(VAR);
				setState(94);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ASSIGN) {
					{
					setState(92);
					match(ASSIGN);
					setState(93);
					expr(0);
					}
				}

				setState(96);
				match(SEMICOL);
				}
				break;
			case PRINT:
				_localctx = new PrintContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(98);
				match(PRINT);
				setState(99);
				match(T__0);
				setState(100);
				match(VAR);
				setState(101);
				match(T__1);
				setState(102);
				match(SEMICOL);
				}
				break;
			case VAR:
				_localctx = new AssignmentContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(103);
				match(VAR);
				setState(110);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__5) {
					{
					{
					setState(104);
					match(T__5);
					setState(105);
					expr(0);
					setState(106);
					match(T__6);
					}
					}
					setState(112);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(113);
				match(ASSIGN);
				setState(114);
				expr(0);
				setState(115);
				match(SEMICOL);
				}
				break;
			case T__2:
				_localctx = new BlockContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(117);
				match(T__2);
				setState(119); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(118);
					instr();
					}
					}
					setState(121); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 66035122696L) != 0) );
				setState(123);
				match(T__4);
				}
				break;
			case IF:
				_localctx = new IfContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(125);
				match(IF);
				setState(126);
				match(T__0);
				setState(127);
				expr(0);
				setState(128);
				match(T__1);
				setState(129);
				instr();
				setState(132);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
				case 1:
					{
					setState(130);
					match(ELSE);
					setState(131);
					instr();
					}
					break;
				}
				}
				break;
			case WHILE:
				_localctx = new WhileContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(134);
				match(WHILE);
				setState(135);
				match(T__0);
				setState(136);
				expr(0);
				setState(137);
				match(T__1);
				setState(138);
				instr();
				}
				break;
			case FOR:
				_localctx = new ForContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(140);
				match(FOR);
				setState(141);
				match(T__0);
				setState(142);
				instr();
				setState(143);
				expr(0);
				setState(144);
				match(SEMICOL);
				setState(145);
				instr();
				setState(146);
				match(T__1);
				setState(147);
				instr();
				}
				break;
			case RETURN:
				_localctx = new ReturnContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(149);
				match(RETURN);
				setState(150);
				expr(0);
				setState(151);
				match(SEMICOL);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Core_fctContext extends ParserRuleContext {
		public TerminalNode RETURN() { return getToken(grammarTCLParser.RETURN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode SEMICOL() { return getToken(grammarTCLParser.SEMICOL, 0); }
		public List<InstrContext> instr() {
			return getRuleContexts(InstrContext.class);
		}
		public InstrContext instr(int i) {
			return getRuleContext(InstrContext.class,i);
		}
		public Core_fctContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_core_fct; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterCore_fct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitCore_fct(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitCore_fct(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Core_fctContext core_fct() throws RecognitionException {
		Core_fctContext _localctx = new Core_fctContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_core_fct);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(155);
			match(T__2);
			setState(159);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(156);
					instr();
					}
					} 
				}
				setState(161);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			}
			setState(162);
			match(RETURN);
			setState(163);
			expr(0);
			setState(164);
			match(SEMICOL);
			setState(165);
			match(T__4);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Decl_fctContext extends ParserRuleContext {
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public List<TerminalNode> VAR() { return getTokens(grammarTCLParser.VAR); }
		public TerminalNode VAR(int i) {
			return getToken(grammarTCLParser.VAR, i);
		}
		public Core_fctContext core_fct() {
			return getRuleContext(Core_fctContext.class,0);
		}
		public Decl_fctContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decl_fct; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterDecl_fct(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitDecl_fct(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitDecl_fct(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Decl_fctContext decl_fct() throws RecognitionException {
		Decl_fctContext _localctx = new Decl_fctContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_decl_fct);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(167);
			type(0);
			setState(168);
			match(VAR);
			setState(169);
			match(T__0);
			setState(181);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BASE_TYPE) {
				{
				setState(170);
				type(0);
				setState(171);
				match(VAR);
				setState(178);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__3) {
					{
					{
					setState(172);
					match(T__3);
					setState(173);
					type(0);
					setState(174);
					match(VAR);
					}
					}
					setState(180);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(183);
			match(T__1);
			setState(184);
			core_fct();
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

	@SuppressWarnings("CheckReturnValue")
	public static class MainContext extends ParserRuleContext {
		public Core_fctContext core_fct() {
			return getRuleContext(Core_fctContext.class,0);
		}
		public TerminalNode EOF() { return getToken(grammarTCLParser.EOF, 0); }
		public List<Decl_fctContext> decl_fct() {
			return getRuleContexts(Decl_fctContext.class);
		}
		public Decl_fctContext decl_fct(int i) {
			return getRuleContext(Decl_fctContext.class,i);
		}
		public MainContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_main; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).enterMain(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof grammarTCLListener ) ((grammarTCLListener)listener).exitMain(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof grammarTCLVisitor ) return ((grammarTCLVisitor<? extends T>)visitor).visitMain(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MainContext main() throws RecognitionException {
		MainContext _localctx = new MainContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_main);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(189);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==BASE_TYPE) {
				{
				{
				setState(186);
				decl_fct();
				}
				}
				setState(191);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(192);
			match(T__7);
			setState(193);
			core_fct();
			setState(194);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 0:
			return expr_sempred((ExprContext)_localctx, predIndex);
		case 1:
			return type_sempred((TypeContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 9);
		case 1:
			return precpred(_ctx, 8);
		case 2:
			return precpred(_ctx, 7);
		case 3:
			return precpred(_ctx, 6);
		case 4:
			return precpred(_ctx, 5);
		case 5:
			return precpred(_ctx, 4);
		case 6:
			return precpred(_ctx, 12);
		}
		return true;
	}
	private boolean type_sempred(TypeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 7:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001#\u00c5\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0005\u0000\u0016"+
		"\b\u0000\n\u0000\f\u0000\u0019\t\u0000\u0003\u0000\u001b\b\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0005"+
		"\u0000#\b\u0000\n\u0000\f\u0000&\t\u0000\u0003\u0000(\b\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0003\u00002\b\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0005\u0000K\b\u0000\n\u0000\f\u0000N\t\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0005\u0001V\b\u0001\n\u0001\f\u0001Y\t\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0003\u0002_\b\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002m\b\u0002"+
		"\n\u0002\f\u0002p\t\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0004\u0002x\b\u0002\u000b\u0002\f\u0002y\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0003\u0002\u0085\b\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003"+
		"\u0002\u009a\b\u0002\u0001\u0003\u0001\u0003\u0005\u0003\u009e\b\u0003"+
		"\n\u0003\f\u0003\u00a1\t\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0005\u0004\u00b1"+
		"\b\u0004\n\u0004\f\u0004\u00b4\t\u0004\u0003\u0004\u00b6\b\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0005\u0005\u00bc\b\u0005\n"+
		"\u0005\f\u0005\u00bf\t\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0000\u0002\u0000\u0002\u0006\u0000\u0002\u0004\u0006"+
		"\b\n\u0000\u0004\u0001\u0000\u0010\u0012\u0001\u0000\u0013\u0014\u0001"+
		"\u0000\u0015\u0018\u0001\u0000\u0019\u001a\u00e0\u00001\u0001\u0000\u0000"+
		"\u0000\u0002O\u0001\u0000\u0000\u0000\u0004\u0099\u0001\u0000\u0000\u0000"+
		"\u0006\u009b\u0001\u0000\u0000\u0000\b\u00a7\u0001\u0000\u0000\u0000\n"+
		"\u00bd\u0001\u0000\u0000\u0000\f\r\u0006\u0000\uffff\uffff\u0000\r\u000e"+
		"\u0005\u0001\u0000\u0000\u000e\u000f\u0003\u0000\u0000\u0000\u000f\u0010"+
		"\u0005\u0002\u0000\u0000\u00102\u0001\u0000\u0000\u0000\u0011\u001a\u0005"+
		"\u0003\u0000\u0000\u0012\u0017\u0003\u0000\u0000\u0000\u0013\u0014\u0005"+
		"\u0004\u0000\u0000\u0014\u0016\u0003\u0000\u0000\u0000\u0015\u0013\u0001"+
		"\u0000\u0000\u0000\u0016\u0019\u0001\u0000\u0000\u0000\u0017\u0015\u0001"+
		"\u0000\u0000\u0000\u0017\u0018\u0001\u0000\u0000\u0000\u0018\u001b\u0001"+
		"\u0000\u0000\u0000\u0019\u0017\u0001\u0000\u0000\u0000\u001a\u0012\u0001"+
		"\u0000\u0000\u0000\u001a\u001b\u0001\u0000\u0000\u0000\u001b\u001c\u0001"+
		"\u0000\u0000\u0000\u001c2\u0005\u0005\u0000\u0000\u001d\u001e\u0005#\u0000"+
		"\u0000\u001e\'\u0005\u0001\u0000\u0000\u001f$\u0003\u0000\u0000\u0000"+
		" !\u0005\u0004\u0000\u0000!#\u0003\u0000\u0000\u0000\" \u0001\u0000\u0000"+
		"\u0000#&\u0001\u0000\u0000\u0000$\"\u0001\u0000\u0000\u0000$%\u0001\u0000"+
		"\u0000\u0000%(\u0001\u0000\u0000\u0000&$\u0001\u0000\u0000\u0000\'\u001f"+
		"\u0001\u0000\u0000\u0000\'(\u0001\u0000\u0000\u0000()\u0001\u0000\u0000"+
		"\u0000)2\u0005\u0002\u0000\u0000*+\u0005\u0014\u0000\u0000+2\u0003\u0000"+
		"\u0000\u000b,-\u0005\u001b\u0000\u0000-2\u0003\u0000\u0000\n.2\u0005#"+
		"\u0000\u0000/2\u0005\f\u0000\u000002\u0005\r\u0000\u00001\f\u0001\u0000"+
		"\u0000\u00001\u0011\u0001\u0000\u0000\u00001\u001d\u0001\u0000\u0000\u0000"+
		"1*\u0001\u0000\u0000\u00001,\u0001\u0000\u0000\u00001.\u0001\u0000\u0000"+
		"\u00001/\u0001\u0000\u0000\u000010\u0001\u0000\u0000\u00002L\u0001\u0000"+
		"\u0000\u000034\n\t\u0000\u000045\u0007\u0000\u0000\u00005K\u0003\u0000"+
		"\u0000\n67\n\b\u0000\u000078\u0007\u0001\u0000\u00008K\u0003\u0000\u0000"+
		"\t9:\n\u0007\u0000\u0000:;\u0007\u0002\u0000\u0000;K\u0003\u0000\u0000"+
		"\b<=\n\u0006\u0000\u0000=>\u0007\u0003\u0000\u0000>K\u0003\u0000\u0000"+
		"\u0007?@\n\u0005\u0000\u0000@A\u0005\u000f\u0000\u0000AK\u0003\u0000\u0000"+
		"\u0006BC\n\u0004\u0000\u0000CD\u0005\u000e\u0000\u0000DK\u0003\u0000\u0000"+
		"\u0005EF\n\f\u0000\u0000FG\u0005\u0006\u0000\u0000GH\u0003\u0000\u0000"+
		"\u0000HI\u0005\u0007\u0000\u0000IK\u0001\u0000\u0000\u0000J3\u0001\u0000"+
		"\u0000\u0000J6\u0001\u0000\u0000\u0000J9\u0001\u0000\u0000\u0000J<\u0001"+
		"\u0000\u0000\u0000J?\u0001\u0000\u0000\u0000JB\u0001\u0000\u0000\u0000"+
		"JE\u0001\u0000\u0000\u0000KN\u0001\u0000\u0000\u0000LJ\u0001\u0000\u0000"+
		"\u0000LM\u0001\u0000\u0000\u0000M\u0001\u0001\u0000\u0000\u0000NL\u0001"+
		"\u0000\u0000\u0000OP\u0006\u0001\uffff\uffff\u0000PQ\u0005\u001d\u0000"+
		"\u0000QW\u0001\u0000\u0000\u0000RS\n\u0001\u0000\u0000ST\u0005\u0006\u0000"+
		"\u0000TV\u0005\u0007\u0000\u0000UR\u0001\u0000\u0000\u0000VY\u0001\u0000"+
		"\u0000\u0000WU\u0001\u0000\u0000\u0000WX\u0001\u0000\u0000\u0000X\u0003"+
		"\u0001\u0000\u0000\u0000YW\u0001\u0000\u0000\u0000Z[\u0003\u0002\u0001"+
		"\u0000[^\u0005#\u0000\u0000\\]\u0005\u001c\u0000\u0000]_\u0003\u0000\u0000"+
		"\u0000^\\\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000_`\u0001\u0000"+
		"\u0000\u0000`a\u0005\n\u0000\u0000a\u009a\u0001\u0000\u0000\u0000bc\u0005"+
		"\t\u0000\u0000cd\u0005\u0001\u0000\u0000de\u0005#\u0000\u0000ef\u0005"+
		"\u0002\u0000\u0000f\u009a\u0005\n\u0000\u0000gn\u0005#\u0000\u0000hi\u0005"+
		"\u0006\u0000\u0000ij\u0003\u0000\u0000\u0000jk\u0005\u0007\u0000\u0000"+
		"km\u0001\u0000\u0000\u0000lh\u0001\u0000\u0000\u0000mp\u0001\u0000\u0000"+
		"\u0000nl\u0001\u0000\u0000\u0000no\u0001\u0000\u0000\u0000oq\u0001\u0000"+
		"\u0000\u0000pn\u0001\u0000\u0000\u0000qr\u0005\u001c\u0000\u0000rs\u0003"+
		"\u0000\u0000\u0000st\u0005\n\u0000\u0000t\u009a\u0001\u0000\u0000\u0000"+
		"uw\u0005\u0003\u0000\u0000vx\u0003\u0004\u0002\u0000wv\u0001\u0000\u0000"+
		"\u0000xy\u0001\u0000\u0000\u0000yw\u0001\u0000\u0000\u0000yz\u0001\u0000"+
		"\u0000\u0000z{\u0001\u0000\u0000\u0000{|\u0005\u0005\u0000\u0000|\u009a"+
		"\u0001\u0000\u0000\u0000}~\u0005\u001e\u0000\u0000~\u007f\u0005\u0001"+
		"\u0000\u0000\u007f\u0080\u0003\u0000\u0000\u0000\u0080\u0081\u0005\u0002"+
		"\u0000\u0000\u0081\u0084\u0003\u0004\u0002\u0000\u0082\u0083\u0005\u001f"+
		"\u0000\u0000\u0083\u0085\u0003\u0004\u0002\u0000\u0084\u0082\u0001\u0000"+
		"\u0000\u0000\u0084\u0085\u0001\u0000\u0000\u0000\u0085\u009a\u0001\u0000"+
		"\u0000\u0000\u0086\u0087\u0005 \u0000\u0000\u0087\u0088\u0005\u0001\u0000"+
		"\u0000\u0088\u0089\u0003\u0000\u0000\u0000\u0089\u008a\u0005\u0002\u0000"+
		"\u0000\u008a\u008b\u0003\u0004\u0002\u0000\u008b\u009a\u0001\u0000\u0000"+
		"\u0000\u008c\u008d\u0005!\u0000\u0000\u008d\u008e\u0005\u0001\u0000\u0000"+
		"\u008e\u008f\u0003\u0004\u0002\u0000\u008f\u0090\u0003\u0000\u0000\u0000"+
		"\u0090\u0091\u0005\n\u0000\u0000\u0091\u0092\u0003\u0004\u0002\u0000\u0092"+
		"\u0093\u0005\u0002\u0000\u0000\u0093\u0094\u0003\u0004\u0002\u0000\u0094"+
		"\u009a\u0001\u0000\u0000\u0000\u0095\u0096\u0005\"\u0000\u0000\u0096\u0097"+
		"\u0003\u0000\u0000\u0000\u0097\u0098\u0005\n\u0000\u0000\u0098\u009a\u0001"+
		"\u0000\u0000\u0000\u0099Z\u0001\u0000\u0000\u0000\u0099b\u0001\u0000\u0000"+
		"\u0000\u0099g\u0001\u0000\u0000\u0000\u0099u\u0001\u0000\u0000\u0000\u0099"+
		"}\u0001\u0000\u0000\u0000\u0099\u0086\u0001\u0000\u0000\u0000\u0099\u008c"+
		"\u0001\u0000\u0000\u0000\u0099\u0095\u0001\u0000\u0000\u0000\u009a\u0005"+
		"\u0001\u0000\u0000\u0000\u009b\u009f\u0005\u0003\u0000\u0000\u009c\u009e"+
		"\u0003\u0004\u0002\u0000\u009d\u009c\u0001\u0000\u0000\u0000\u009e\u00a1"+
		"\u0001\u0000\u0000\u0000\u009f\u009d\u0001\u0000\u0000\u0000\u009f\u00a0"+
		"\u0001\u0000\u0000\u0000\u00a0\u00a2\u0001\u0000\u0000\u0000\u00a1\u009f"+
		"\u0001\u0000\u0000\u0000\u00a2\u00a3\u0005\"\u0000\u0000\u00a3\u00a4\u0003"+
		"\u0000\u0000\u0000\u00a4\u00a5\u0005\n\u0000\u0000\u00a5\u00a6\u0005\u0005"+
		"\u0000\u0000\u00a6\u0007\u0001\u0000\u0000\u0000\u00a7\u00a8\u0003\u0002"+
		"\u0001\u0000\u00a8\u00a9\u0005#\u0000\u0000\u00a9\u00b5\u0005\u0001\u0000"+
		"\u0000\u00aa\u00ab\u0003\u0002\u0001\u0000\u00ab\u00b2\u0005#\u0000\u0000"+
		"\u00ac\u00ad\u0005\u0004\u0000\u0000\u00ad\u00ae\u0003\u0002\u0001\u0000"+
		"\u00ae\u00af\u0005#\u0000\u0000\u00af\u00b1\u0001\u0000\u0000\u0000\u00b0"+
		"\u00ac\u0001\u0000\u0000\u0000\u00b1\u00b4\u0001\u0000\u0000\u0000\u00b2"+
		"\u00b0\u0001\u0000\u0000\u0000\u00b2\u00b3\u0001\u0000\u0000\u0000\u00b3"+
		"\u00b6\u0001\u0000\u0000\u0000\u00b4\u00b2\u0001\u0000\u0000\u0000\u00b5"+
		"\u00aa\u0001\u0000\u0000\u0000\u00b5\u00b6\u0001\u0000\u0000\u0000\u00b6"+
		"\u00b7\u0001\u0000\u0000\u0000\u00b7\u00b8\u0005\u0002\u0000\u0000\u00b8"+
		"\u00b9\u0003\u0006\u0003\u0000\u00b9\t\u0001\u0000\u0000\u0000\u00ba\u00bc"+
		"\u0003\b\u0004\u0000\u00bb\u00ba\u0001\u0000\u0000\u0000\u00bc\u00bf\u0001"+
		"\u0000\u0000\u0000\u00bd\u00bb\u0001\u0000\u0000\u0000\u00bd\u00be\u0001"+
		"\u0000\u0000\u0000\u00be\u00c0\u0001\u0000\u0000\u0000\u00bf\u00bd\u0001"+
		"\u0000\u0000\u0000\u00c0\u00c1\u0005\b\u0000\u0000\u00c1\u00c2\u0003\u0006"+
		"\u0003\u0000\u00c2\u00c3\u0005\u0000\u0000\u0001\u00c3\u000b\u0001\u0000"+
		"\u0000\u0000\u0011\u0017\u001a$\'1JLW^ny\u0084\u0099\u009f\u00b2\u00b5"+
		"\u00bd";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}