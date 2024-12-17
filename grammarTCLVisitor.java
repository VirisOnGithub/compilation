// Generated from grammarTCL.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link grammarTCLParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface grammarTCLVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code negation}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegation(grammarTCLParser.NegationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code comparison}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison(grammarTCLParser.ComparisonContext ctx);
	/**
	 * Visit a parse tree produced by the {@code or}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOr(grammarTCLParser.OrContext ctx);
	/**
	 * Visit a parse tree produced by the {@code opposite}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOpposite(grammarTCLParser.OppositeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code integer}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInteger(grammarTCLParser.IntegerContext ctx);
	/**
	 * Visit a parse tree produced by the {@code tab_access}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTab_access(grammarTCLParser.Tab_accessContext ctx);
	/**
	 * Visit a parse tree produced by the {@code brackets}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBrackets(grammarTCLParser.BracketsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code call}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCall(grammarTCLParser.CallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code boolean}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolean(grammarTCLParser.BooleanContext ctx);
	/**
	 * Visit a parse tree produced by the {@code and}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnd(grammarTCLParser.AndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code variable}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(grammarTCLParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code multiplication}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplication(grammarTCLParser.MultiplicationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code equality}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEquality(grammarTCLParser.EqualityContext ctx);
	/**
	 * Visit a parse tree produced by the {@code tab_initialization}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTab_initialization(grammarTCLParser.Tab_initializationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code addition}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddition(grammarTCLParser.AdditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code base_type}
	 * labeled alternative in {@link grammarTCLParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBase_type(grammarTCLParser.Base_typeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code tab_type}
	 * labeled alternative in {@link grammarTCLParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTab_type(grammarTCLParser.Tab_typeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code declaration}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(grammarTCLParser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code print}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrint(grammarTCLParser.PrintContext ctx);
	/**
	 * Visit a parse tree produced by the {@code assignment}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(grammarTCLParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code block}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(grammarTCLParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code if}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf(grammarTCLParser.IfContext ctx);
	/**
	 * Visit a parse tree produced by the {@code while}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile(grammarTCLParser.WhileContext ctx);
	/**
	 * Visit a parse tree produced by the {@code for}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor(grammarTCLParser.ForContext ctx);
	/**
	 * Visit a parse tree produced by the {@code return}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturn(grammarTCLParser.ReturnContext ctx);
	/**
	 * Visit a parse tree produced by {@link grammarTCLParser#core_fct}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCore_fct(grammarTCLParser.Core_fctContext ctx);
	/**
	 * Visit a parse tree produced by {@link grammarTCLParser#decl_fct}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecl_fct(grammarTCLParser.Decl_fctContext ctx);
	/**
	 * Visit a parse tree produced by {@link grammarTCLParser#main}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMain(grammarTCLParser.MainContext ctx);
}