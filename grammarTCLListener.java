// Generated from grammarTCL.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link grammarTCLParser}.
 */
public interface grammarTCLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by the {@code negation}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNegation(grammarTCLParser.NegationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code negation}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNegation(grammarTCLParser.NegationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code comparison}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterComparison(grammarTCLParser.ComparisonContext ctx);
	/**
	 * Exit a parse tree produced by the {@code comparison}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitComparison(grammarTCLParser.ComparisonContext ctx);
	/**
	 * Enter a parse tree produced by the {@code or}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterOr(grammarTCLParser.OrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code or}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitOr(grammarTCLParser.OrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code opposite}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterOpposite(grammarTCLParser.OppositeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code opposite}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitOpposite(grammarTCLParser.OppositeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code integer}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterInteger(grammarTCLParser.IntegerContext ctx);
	/**
	 * Exit a parse tree produced by the {@code integer}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitInteger(grammarTCLParser.IntegerContext ctx);
	/**
	 * Enter a parse tree produced by the {@code tab_access}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterTab_access(grammarTCLParser.Tab_accessContext ctx);
	/**
	 * Exit a parse tree produced by the {@code tab_access}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitTab_access(grammarTCLParser.Tab_accessContext ctx);
	/**
	 * Enter a parse tree produced by the {@code brackets}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBrackets(grammarTCLParser.BracketsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code brackets}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBrackets(grammarTCLParser.BracketsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code call}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterCall(grammarTCLParser.CallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code call}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitCall(grammarTCLParser.CallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code boolean}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBoolean(grammarTCLParser.BooleanContext ctx);
	/**
	 * Exit a parse tree produced by the {@code boolean}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBoolean(grammarTCLParser.BooleanContext ctx);
	/**
	 * Enter a parse tree produced by the {@code and}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAnd(grammarTCLParser.AndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code and}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAnd(grammarTCLParser.AndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code variable}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterVariable(grammarTCLParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code variable}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitVariable(grammarTCLParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code multiplication}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMultiplication(grammarTCLParser.MultiplicationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code multiplication}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMultiplication(grammarTCLParser.MultiplicationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code equality}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterEquality(grammarTCLParser.EqualityContext ctx);
	/**
	 * Exit a parse tree produced by the {@code equality}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitEquality(grammarTCLParser.EqualityContext ctx);
	/**
	 * Enter a parse tree produced by the {@code tab_initialization}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterTab_initialization(grammarTCLParser.Tab_initializationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code tab_initialization}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitTab_initialization(grammarTCLParser.Tab_initializationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code addition}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAddition(grammarTCLParser.AdditionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code addition}
	 * labeled alternative in {@link grammarTCLParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAddition(grammarTCLParser.AdditionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code base_type}
	 * labeled alternative in {@link grammarTCLParser#type}.
	 * @param ctx the parse tree
	 */
	void enterBase_type(grammarTCLParser.Base_typeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code base_type}
	 * labeled alternative in {@link grammarTCLParser#type}.
	 * @param ctx the parse tree
	 */
	void exitBase_type(grammarTCLParser.Base_typeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code tab_type}
	 * labeled alternative in {@link grammarTCLParser#type}.
	 * @param ctx the parse tree
	 */
	void enterTab_type(grammarTCLParser.Tab_typeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code tab_type}
	 * labeled alternative in {@link grammarTCLParser#type}.
	 * @param ctx the parse tree
	 */
	void exitTab_type(grammarTCLParser.Tab_typeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code declaration}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(grammarTCLParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code declaration}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(grammarTCLParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code print}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 */
	void enterPrint(grammarTCLParser.PrintContext ctx);
	/**
	 * Exit a parse tree produced by the {@code print}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 */
	void exitPrint(grammarTCLParser.PrintContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assignment}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(grammarTCLParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assignment}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(grammarTCLParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code block}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 */
	void enterBlock(grammarTCLParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code block}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 */
	void exitBlock(grammarTCLParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code if}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 */
	void enterIf(grammarTCLParser.IfContext ctx);
	/**
	 * Exit a parse tree produced by the {@code if}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 */
	void exitIf(grammarTCLParser.IfContext ctx);
	/**
	 * Enter a parse tree produced by the {@code while}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 */
	void enterWhile(grammarTCLParser.WhileContext ctx);
	/**
	 * Exit a parse tree produced by the {@code while}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 */
	void exitWhile(grammarTCLParser.WhileContext ctx);
	/**
	 * Enter a parse tree produced by the {@code for}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 */
	void enterFor(grammarTCLParser.ForContext ctx);
	/**
	 * Exit a parse tree produced by the {@code for}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 */
	void exitFor(grammarTCLParser.ForContext ctx);
	/**
	 * Enter a parse tree produced by the {@code return}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 */
	void enterReturn(grammarTCLParser.ReturnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code return}
	 * labeled alternative in {@link grammarTCLParser#instr}.
	 * @param ctx the parse tree
	 */
	void exitReturn(grammarTCLParser.ReturnContext ctx);
	/**
	 * Enter a parse tree produced by {@link grammarTCLParser#core_fct}.
	 * @param ctx the parse tree
	 */
	void enterCore_fct(grammarTCLParser.Core_fctContext ctx);
	/**
	 * Exit a parse tree produced by {@link grammarTCLParser#core_fct}.
	 * @param ctx the parse tree
	 */
	void exitCore_fct(grammarTCLParser.Core_fctContext ctx);
	/**
	 * Enter a parse tree produced by {@link grammarTCLParser#decl_fct}.
	 * @param ctx the parse tree
	 */
	void enterDecl_fct(grammarTCLParser.Decl_fctContext ctx);
	/**
	 * Exit a parse tree produced by {@link grammarTCLParser#decl_fct}.
	 * @param ctx the parse tree
	 */
	void exitDecl_fct(grammarTCLParser.Decl_fctContext ctx);
	/**
	 * Enter a parse tree produced by {@link grammarTCLParser#main}.
	 * @param ctx the parse tree
	 */
	void enterMain(grammarTCLParser.MainContext ctx);
	/**
	 * Exit a parse tree produced by {@link grammarTCLParser#main}.
	 * @param ctx the parse tree
	 */
	void exitMain(grammarTCLParser.MainContext ctx);
}