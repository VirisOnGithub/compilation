package src;

import java.util.ArrayList;
import java.util.Map;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import src.Asm.*;
import src.Type.ArrayType;
import src.Type.Type;
import src.Type.UnknownType;

/**
 * A class that generates linear code from a valid TCL program tree after the type analysis has been done, used like this: program = codeGenerator.visit(tree)
 */
public class CodeGenerator extends AbstractParseTreeVisitor<Program> implements grammarTCLVisitor<Program> {
    /**
     * A map that links each variable with its type
     */
    private final Map<UnknownType,Type> types;
    /**
     * A stack of maps that links each variable with the number of the register that stocks this variable, at its corresponding depth
     */
    private final VarStack<Integer> varRegisters;
    /**
     * A stack of maps that links each function with the number of the register that stocks its return value
     */
    private final VarStack<Integer> returnRegisters;

    /**
     * Stack pointer, contains the address of the next free space in the stack, and shouldn't go over 4095
     */
    private final int SP = 0;
    /**
     * Stack pointer for arrays (Tab Pointer), contains the address of the next free space in memory for arrays (starting at 4096)
     */
    private final int TP = 1;

    /**
     * Contains the number of the next non utilised register number, should always be incremented immediately after each use
     */
    private Integer nextRegister;
    /**
     * Contains the number of the next non utilised label number, should always be incremented immediately after each use
     */
    private Integer nextLabel;
    /**
     * Contains the name of the function that is currently being declared
     */
    private String currentFunction;


    /**
     * Constructor
     * @param types types of each variable of the source code
     */
    public CodeGenerator(Map<UnknownType, Type> types) {
        this.types = types;
        this.varRegisters = new VarStack<>();
        this.returnRegisters = new VarStack<>();
        this.nextRegister = 2;
        this.nextLabel = 0;
        this.currentFunction = "";
    }

    /**
     * Macro to add instructions to stack a register
     * @param register the number of the register that needs to be stacked
     * @return a program that stack the register
     */
    private Program stackRegister(int register) {
        Program program = new Program();
        program.addInstruction(new Mem(Mem.Op.ST, register, this.SP));
        program.addInstruction(new UALi(UALi.Op.ADD, this.SP, this.SP, 1));
        return program;
    }

    /**
     * Macro to add instructions to unstack into a register
     * @param register the number of the register that needs to be unstacked
     * @return a program that unstack into the register
     */
    private Program unstackRegister(int register) {
        Program program = new Program();
        program.addInstruction(new UALi(UALi.Op.SUB, this.SP, this.SP, 1));
        program.addInstruction(new Mem(Mem.Op.LD, register, this.SP));
        return program;
    }

    /**
     * Macro to set a given value into a given register
     * @param register the register we want to stock the value in
     * @param value the value we want to stock in the register
     * @return a program that set the value into the register
     */
    private Program setRegisterTo(int register, int value) {
        Program program = new Program();
        program.addInstruction(new UAL(UAL.Op.XOR, register, register, register));
        program.addInstruction(new UALi(UALi.Op.ADD, register, register, value));
        return program;
    }

    /**
     * Macro to get a new valid non utilised label name
     * @return the label name
     */
    private String getLabel() {
        this.nextLabel++;
        return "*label" + (nextLabel-1);
    }
    
    /**
     * Returns the depth (or dimension) of a given variable
     * @param type the type we want to know the depth of
     * @return 0 if it is not an array, else the depth (or dimension) of the array
     */
    private static int getArrayDepth(Type type) {
        assert type != null : "Le type ne devrait pas être nul !";
        if (type instanceof ArrayType array)
            return 1 + getArrayDepth(array.getTabType());
        return 0;
    }

    /**
     * Visit a node that contains a boolean inversion and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitNegation(grammarTCLParser.NegationContext ctx) {
        // NOT expr

        Program program = new Program();

        program.addInstructions(visit(ctx.expr())); // in R(nextRegister - 1), we have either 0x00000000 or 0x00000001
        program.addInstruction(new UALi(UALi.Op.XOR, this.nextRegister, this.nextRegister - 1, 1)); // XOR with 0 keeps the value and XOR with 1 flips the value
        this.nextRegister++;

        return program;
    }

    /**
     * Visit a node that contains a comparison test and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitComparison(grammarTCLParser.ComparisonContext ctx) {
        // expr op=(SUP | INF | SUPEQ | INFEQ) expr
        /* pseudo-assembler: expr1 > expr2
         *   R1 := visit(expr1)
         *   R2 := visit(expr2)
         *   JSUP R1 R2 true_label
         *   ST R3 0
         *   JMP end_label
         * true_label: ST R3 1
         * end_label: following code...
         */

        Program program = new Program();
        String true_label = this.getLabel();
        String end_label = this.getLabel();
        int returnRegister = this.nextRegister;
        this.nextRegister++;

        program.addInstructions(visit(ctx.getChild(0)));
        int leftValue = this.nextRegister - 1; // R1 := visit(expr1)
        program.addInstructions(visit(ctx.getChild(2)));
        int rightValue = this.nextRegister - 1; // R2 := visit(expr2)
        switch (ctx.op.getText()) { // we do the correct test
            case ">" -> program.addInstruction(new CondJump(CondJump.Op.JSUP, leftValue, rightValue, true_label));
            case "<" -> program.addInstruction(new CondJump(CondJump.Op.JINF, leftValue, rightValue, true_label));
            case ">=" -> program.addInstruction(new CondJump(CondJump.Op.JSEQ, leftValue, rightValue, true_label));
            case "<=" -> program.addInstruction(new CondJump(CondJump.Op.JIEQ, leftValue, rightValue, true_label));
        }
        program.addInstruction(new UAL(UAL.Op.XOR, returnRegister, returnRegister, returnRegister)); // if not true, set return to 0
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, end_label)); // JMP end_label

        Program trueLabelProgram = new Program();
        trueLabelProgram.addInstruction(new UAL(UAL.Op.XOR, returnRegister, returnRegister, returnRegister)); // if true, set return to 1
        trueLabelProgram.addInstruction(new UALi(UALi.Op.ADD, returnRegister, returnRegister, 1));
        trueLabelProgram.getInstructions().getFirst().setLabel(true_label); // set true_label label
        program.addInstructions(trueLabelProgram);

        Program endLabelProgram = new Program();
        endLabelProgram.addInstruction(new UALi(UALi.Op.ADD, this.nextRegister, returnRegister, 0)); // stock the return value in R(nextRegister-1)
        this.nextRegister++;
        endLabelProgram.getInstructions().getFirst().setLabel(end_label); // set end_label label
        program.addInstructions(endLabelProgram);

        return program;
    }

    /**
     * Visit a node that contains an OR operation and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitOr(grammarTCLParser.OrContext ctx) {
        // expr OR expr

        Program program = new Program();

        program.addInstructions(visit(ctx.getChild(0)));
        int leftRegister = this.nextRegister - 1; // stock the value of the left expr
        program.addInstructions(visit(ctx.getChild(2)));
        int rightRegister = this.nextRegister - 1; // stock the value of the right expr
        program.addInstruction(new UAL(UAL.Op.OR, this.nextRegister, leftRegister, rightRegister)); // do the operation
        this.nextRegister++;

        return program;
    }

    /**
     * Visit a node that contains an integer inversion and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitOpposite(grammarTCLParser.OppositeContext ctx) {
        // '-' expr

        Program program = new Program();

        program.addInstructions(visit(ctx.expr())); // number will be stocked in R(nextRegister -1)
        program.addInstruction(new UALi(UALi.Op.XOR, this.nextRegister, this.nextRegister - 1, 0xFFFFFFFF)); // black magic
        program.addInstruction(new UALi(UALi.Op.ADD, this.nextRegister, this.nextRegister, 1));
        this.nextRegister++;

        return program;
    }

    /**
     * Visit a node that contains an integer and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitInteger(grammarTCLParser.IntegerContext ctx) {
        // INT

        Program program = new Program();
        int value = Integer.parseInt(ctx.INT().getText());

        program.addInstructions(this.setRegisterTo(nextRegister, value)); // we return the value of the boolean in R(nextRegister-1)
        this.nextRegister++;

        return program;
    }

    /**
     * Visit a node that contains access to a cell of an array and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitTab_access(grammarTCLParser.Tab_accessContext ctx) {
        // expr '[' expr ']'

        Program program = new Program();

        program.addInstructions(visit(ctx.getChild(0)));
        int tabRegister = this.nextRegister - 1; // stock the name of the array
        program.addInstructions(visit(ctx.getChild(2)));
        int indexRegister = this.nextRegister - 1; // stock the index we access in the array
        int depth = getArrayDepth(this.types.get(new UnknownType(ctx.getChild(0))));

        program.addInstructions(this.stackRegister(tabRegister)); // stack the arguments
        program.addInstructions(this.stackRegister(indexRegister));
        program.addInstructions(this.setRegisterTo(this.nextRegister, depth));
        program.addInstructions(this.stackRegister(this.nextRegister));
        this.nextRegister++;
        program.addInstruction(new JumpCall(JumpCall.Op.CALL, "*tab_access")); // call the assembler function
        program.addInstructions(this.unstackRegister(this.nextRegister)); // returns a pointer
        program.addInstruction(new Mem(Mem.Op.LD, this.nextRegister, this.nextRegister)); // get the pointed value
        this.nextRegister++;

        return program;
    }

    /**
     * Visit a node that contains an expression between brackets and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitBrackets(grammarTCLParser.BracketsContext ctx) {
        // '(' expr ')'

        Program program = new Program();

        program.addInstructions(visit(ctx.getChild(1))); // expr

        return program;
    }

    /**
     * Visit a node that contains a function call and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitCall(grammarTCLParser.CallContext ctx) {
        // VAR '(' (expr (',' expr)*)? ')'

        Program program = new Program();
        int childCount = ctx.getChildCount();
        int nbArguments = (childCount == 3) ? 0 : (childCount - 2)/2;
        String calledFunction = ctx.VAR().getText();
        int returnRegister = this.returnRegisters.getVar(calledFunction);

        ArrayList<Integer> arguments = new ArrayList<>();
        for (int i = 0; i < nbArguments; i++) { // expr*
            program.addInstructions(visit(ctx.getChild((2 * i) + 2))); // value of expression will be stocked in R(nextRegister-1)
            arguments.addLast(this.nextRegister-1); // we save the registers because we want to calculate the arguments first but stack the local variables first
        }

        int lastUsedRegister = this.nextRegister - 1;
        if (calledFunction.equals(this.currentFunction)) { // if it's a recursive call
            for (int i = returnRegister + 1; i <= lastUsedRegister; i++) { // returnRegister is the first assigned register in visitDecl_fct
                program.addInstructions(this.stackRegister(i)); // we save the current state of used registers in the function
            }
        }

        for (int i = 0; i < arguments.size(); i++) { // don't use foreach to be sure of the order of the elements
            program.addInstructions(this.stackRegister(arguments.get(i))); // we stack the arguments
        }
        program.addInstruction(new JumpCall(JumpCall.Op.CALL, ctx.getChild(0).getText())); // we call the function
        program.addInstruction(new UALi(UALi.Op.ADD, this.nextRegister, returnRegister, 0)); // we stock the return value in R(nextRegister - 1)
        nextRegister++;

        if (calledFunction.equals(this.currentFunction)) { // if it's a recursive call
            for (int i = lastUsedRegister; i > returnRegister; i--) { // (un-stack in reverse order!)
                program.addInstructions(this.unstackRegister(i)); // we load the state of used registers before the call
            }
        }

        return program;
    }

    /**
     * Visit a node that contains a boolean and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitBoolean(grammarTCLParser.BooleanContext ctx) {
        // BOOL

        Program program = new Program();

        switch (ctx.BOOL().getText()) { // we return the value of the boolean in R(nextRegister-1)
            case "true" -> program.addInstructions(this.setRegisterTo(this.nextRegister, 1));
            case "false" -> program.addInstructions(this.setRegisterTo(this.nextRegister, 0));
        }
        this.nextRegister++;

        return program;
    }

    /**
     * Visit a node that contains an AND operation and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitAnd(grammarTCLParser.AndContext ctx) {
        // expr AND expr

        Program program = new Program();

        program.addInstructions(visit(ctx.getChild(0)));
        int leftRegister = this.nextRegister - 1; // stock the value of the left expr
        program.addInstructions(visit(ctx.getChild(2)));
        int rightRegister = this.nextRegister - 1; // stock the value of the right expr
        program.addInstruction(new UAL(UAL.Op.AND, this.nextRegister, leftRegister, rightRegister)); // do the operation
        this.nextRegister++;

        return program;
    }

    /**
     * Visit a node that contains a variable call and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitVariable(grammarTCLParser.VariableContext ctx) {
        // VAR

        Program program = new Program();
        int varRegister = this.varRegisters.getVar(ctx.VAR().getText()); // the register in which VAR is

        program.addInstruction(new UALi(UALi.Op.ADD, this.nextRegister, varRegister, 0)); // return the variable in R(nextRegister-1)
        this.nextRegister++;

        return program;
    }

    /**
     * Visit a node that contains a multiplication (or division, or modulo) and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitMultiplication(grammarTCLParser.MultiplicationContext ctx) {
        // expr op=(MUL | DIV | MODULO) expr

        Program program = new Program();

        program.addInstructions(visit(ctx.getChild(0)));
        int leftRegister = this.nextRegister - 1; // stock the value of the left expr
        program.addInstructions(visit(ctx.getChild(2)));
        int rightRegister = this.nextRegister - 1; // stock the value of the right expr
        switch (ctx.op.getText()) { // do the operation
            case "*" -> program.addInstruction(new UAL(UAL.Op.MUL, this.nextRegister, leftRegister, rightRegister));
            case "/" -> program.addInstruction(new UAL(UAL.Op.DIV, this.nextRegister, leftRegister, rightRegister));
            case "%" -> program.addInstruction(new UAL(UAL.Op.MOD, this.nextRegister, leftRegister, rightRegister));
        }
        this.nextRegister++;

        return program;
    }

    /**
     * Visit a node that contains an equality test and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitEquality(grammarTCLParser.EqualityContext ctx) {
        // expr op=(EQUALS | DIFF) expr
        /* pseudo-assembler: expr1 == expr2
         *   R1 := visit(expr1)
         *   R2 := visit(expr2)
         *   JEQU R1 R2 true_label
         *   ST R3 0
         *   JMP end_label
         * true_label: ST R3 1
         * end_label: following code...
         */

        Program program = new Program();
        String true_label = this.getLabel();
        String end_label = this.getLabel();
        int returnRegister = this.nextRegister;
        this.nextRegister++;

        program.addInstructions(visit(ctx.getChild(0)));
        int leftValue = this.nextRegister - 1; // R1 := visit(expr1)
        program.addInstructions(visit(ctx.getChild(2)));
        int rightValue = this.nextRegister - 1; // R2 := visit(expr2)
        switch (ctx.op.getText()) { // we do the correct test
            case "==" -> program.addInstruction(new CondJump(CondJump.Op.JEQU, leftValue, rightValue, true_label));
            case "!=" -> program.addInstruction(new CondJump(CondJump.Op.JNEQ, leftValue, rightValue, true_label));
        }
        program.addInstruction(new UAL(UAL.Op.XOR, returnRegister, returnRegister, returnRegister)); // if not true, set return to 0
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, end_label)); // JMP end_label

        Program trueLabelProgram = new Program();
        trueLabelProgram.addInstruction(new UAL(UAL.Op.XOR, returnRegister, returnRegister, returnRegister)); // if true, set return to 1
        trueLabelProgram.addInstruction(new UALi(UALi.Op.ADD, returnRegister, returnRegister, 1));
        trueLabelProgram.getInstructions().getFirst().setLabel(true_label); // set true_label label
        program.addInstructions(trueLabelProgram);

        Program endLabelProgram = new Program();
        endLabelProgram.addInstruction(new UALi(UALi.Op.ADD, this.nextRegister, returnRegister, 0)); // stock the return value in R(nextRegister -1)
        this.nextRegister++;
        endLabelProgram.getInstructions().getFirst().setLabel(end_label); // set end_label label
        program.addInstructions(endLabelProgram);

        return program;
    }

    /**
     * Visit a node that contains the initialization of an array and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitTab_initialization(grammarTCLParser.Tab_initializationContext ctx) {
        // '{' (expr (',' expr)*)? '}'

        Program program = new Program();

        // 2 child -> 0 expr
        // 3 child -> 1 expr
        // 5 child -> 2 expr
        // 7 child -> 3 expr...

        int childCount = ctx.getChildCount();
        int varCount = (childCount == 2) ? 0 : (childCount - 1) / 2;
        int lengthRegister = this.nextRegister; // register containing the length of the array
        this.nextRegister++;
        int pointerRegister = this.nextRegister; // register containing a pointer to the array (mutable)
        this.nextRegister++;
        int fixedPointerRegister = this.nextRegister; // register containing a pointer to the array (return value)
        this.nextRegister++;

        program.addInstructions(this.setRegisterTo(lengthRegister, varCount)); // lengthRegister := varCount
        program.addInstruction(new UALi(UALi.Op.ADD, pointerRegister, this.TP, 0)); // pointerRegister := TP
        program.addInstruction(new UALi(UALi.Op.ADD, fixedPointerRegister, pointerRegister, 0)); // fixedPointerRegister := pointerRegister

        program.addInstruction(new Mem(Mem.Op.ST, lengthRegister, pointerRegister)); // we stack the length of the array
        program.addInstruction(new UALi(UALi.Op.ADD, pointerRegister, pointerRegister, 1));

        program.addInstruction(new UALi(UALi.Op.ADD, this.TP, this.TP, 12)); // TP now points to the end of the array
        for (int i = 0; i < varCount; i++) { // for each element in the initialization {}
            if ((i % 10 == 0) && (i > 0)) { // if we go over the 10th element in the array part
                program.addInstruction(new Mem(Mem.Op.ST, this.TP, pointerRegister)); // we add the pointer to the rest of the array at the end of the array part
                program.addInstruction(new UALi(UALi.Op.ADD, pointerRegister, this.TP, 0)); // we move the pointer to the start of the next array part
                program.addInstruction(new UALi(UALi.Op.ADD, this.TP, this.TP, 11)); // we update the next free space in memory for arrays
            }
            Program bracketsContent = visit(ctx.getChild((2 * i) + 1));
            if (bracketsContent != null) // the content of the brackets can be empty
                program.addInstructions(bracketsContent); // we get the value in R(nextRegister - 1)
            program.addInstruction(new Mem(Mem.Op.ST, this.nextRegister - 1, pointerRegister)); // we append it in the array
            program.addInstruction(new UALi(UALi.Op.ADD, pointerRegister, pointerRegister, 1)); // we move the pointer to the next cell
        }
        program.addInstruction(new UALi(UALi.Op.ADD, this.nextRegister, fixedPointerRegister, 0)); // nextRegister := fixedPointerRegister
        this.nextRegister++;

        return program;
    }

    /**
     * Visit a node that contains an addition (or subtraction) and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitAddition(grammarTCLParser.AdditionContext ctx) {
        // expr op=(ADD | SUB) expr

        Program program = new Program();

        program.addInstructions(visit(ctx.getChild(0)));
        int leftValue = this.nextRegister - 1; // stock the value of the left expr
        program.addInstructions(visit(ctx.getChild(2)));
        int rightValue = this.nextRegister - 1; // stock the value of the right expr
        switch (ctx.op.getText()) { // do the operation
            case "+" -> program.addInstruction(new UAL(UAL.Op.ADD, this.nextRegister, leftValue, rightValue));
            case "-" -> program.addInstruction(new UAL(UAL.Op.SUB, this.nextRegister, leftValue, rightValue));
        }
        this.nextRegister++;

        return program;
    }

    /**
     * Visit a node that contains a variable type, which shouldn't be called in the context of linear code creation
     * @param ctx the context within the parse tree
     * @return a RuntimeException
     */
    @Override
    public Program visitBase_type(grammarTCLParser.Base_typeContext ctx) throws RuntimeException {
        // BASE_TYPE

        throw new RuntimeException("Method 'visitBase_type' should not be called");
    }

    /**
     * Visit a node that contains an array type, which shouldn't be called in the context of linear code creation
     * @param ctx the context within the parse tree
     * @return a RuntimeException
     */
    @Override
    public Program visitTab_type(grammarTCLParser.Tab_typeContext ctx) throws RuntimeException {
        // type '[' ']'

        throw new RuntimeException("Method 'visitTab_type' should not be called");
    }

    /**
     * Visit a node that contains a variable declaration and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitDeclaration(grammarTCLParser.DeclarationContext ctx) {
        // type VAR (ASSIGN expr)? SEMICOL

        Program program = new Program();
        int childCount = ctx.getChildCount();
        int varRegister = this.nextRegister;
        this.nextRegister++;

        if (childCount == 5) { // if the variable is assigned a value
            program.addInstructions(visit(ctx.expr()));
            program.addInstruction(new UALi(UALi.Op.ADD, varRegister, this.nextRegister - 1, 0)); // varRegister := nextRegister - 1
        }
        this.varRegisters.assignVar(ctx.VAR().getText(), varRegister); // declare the variable

        return program;
    }

    /**
     * Visit a node that contains a print call and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitPrint(grammarTCLParser.PrintContext ctx) {
        // PRINT '(' VAR ')' SEMICOL

        Program program = new Program();
        UnknownType variable = new UnknownType(ctx.VAR());
        Type varType = this.types.get(variable);
        int arrayDepth = getArrayDepth(varType);
        int varRegister = this.varRegisters.getVar(ctx.VAR().getText());

        if (arrayDepth == 0) { // primitive type
            program.addInstruction(new IO(IO.Op.PRINT, varRegister));
        } else { // array type
            int depthRegister = this.nextRegister;
            this.nextRegister++;
            program.addInstructions(this.stackRegister(varRegister)); // we stack the array
            program.addInstructions(this.setRegisterTo(depthRegister, arrayDepth));
            program.addInstructions(this.stackRegister(depthRegister)); // we stack its depth
            program.addInstruction(new JumpCall(JumpCall.Op.CALL, "*print_tab")); // we call the print_tab function
        }

        final int NEW_LINE = 10;
        program.addInstructions(this.setRegisterTo(this.nextRegister, NEW_LINE));
        program.addInstruction(new IO(IO.Op.OUT, this.nextRegister)); // once finished printing, we jump to the next line
        this.nextRegister++;

        return program;
    }

    /**
     * Visit a node that contains a variable assignment and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitAssignment(grammarTCLParser.AssignmentContext ctx) {
        // VAR ('[' expr ']')* ASSIGN expr SEMICOL

        Program program = new Program();
        int varRegister = this.varRegisters.getVar(ctx.VAR().getText());
        int bracketsCount = (ctx.getChildCount() - 4) / 3;
        int arrayDepth = getArrayDepth(this.types.get(new UnknownType(ctx.VAR())));
        int leftRegister = this.nextRegister;
        this.nextRegister++;
        int depthRegister = this.nextRegister;
        this.nextRegister++;

        program.addInstructions(visit(ctx.getChild(ctx.getChildCount() - 2))); // expr result returned in R(nextRegister - 1)
        int rightRegister = this.nextRegister - 1;
        program.addInstruction(new UALi(UALi.Op.ADD, leftRegister, varRegister, 0)); // leftRegister := varRegister
        
        for (int i = 0; i < bracketsCount; i++) {
            int child = 2 + (i * 3);
            if (i > 0) // if we were accessing an array, get the pointed value
                program.addInstruction(new Mem(Mem.Op.LD, leftRegister, leftRegister));
            program.addInstructions(visit(ctx.getChild(child))); // element pointer returned in R(nextRegister - 1)
            int indexRegister = this.nextRegister - 1;
            program.addInstructions(this.stackRegister(leftRegister)); // we stack the arguments
            program.addInstructions(this.stackRegister(indexRegister));
            program.addInstructions(this.setRegisterTo(depthRegister, arrayDepth));
            program.addInstructions(this.stackRegister(depthRegister));
            program.addInstruction(new JumpCall(JumpCall.Op.CALL, "*tab_access")); // we call the tab_access function
            program.addInstructions(this.unstackRegister(leftRegister)); // we get the result
            arrayDepth--; // the content of the array has less depth
        }

        if (bracketsCount == 0) { // if it's not a pointer, we store the value in the register
            program.addInstruction(new UALi(UALi.Op.ADD, varRegister, rightRegister, 0));
        } else { // if it's a pointer, we store the value in memory
            program.addInstruction(new Mem(Mem.Op.ST, rightRegister, leftRegister));
        }

        return program;
    }

    /**
     * Visit a node that contains code inside brackets and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitBlock(grammarTCLParser.BlockContext ctx) {
        // '{' instr+ '}'

        Program program = new Program();
        int nbInstr = ctx.getChildCount() - 2;

        this.varRegisters.enterBlock(); // '{'
        for (int i = 0; i < nbInstr; i++) { // instr+
            program.addInstructions(visit(ctx.getChild(i + 1)));
        }
        this.varRegisters.leaveBlock(); // '}'

        return program;
    }

    /**
     * Visit a node that contains an if structure and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitIf(grammarTCLParser.IfContext ctx) {
        // IF '(' expr ')' instr (ELSE instr)?
        /* pseudo-assembler: if (cond) { instr1 } else { instr2 }
         *   R0 := visit(cond)
         *   XOR R1 R1 R1
         *   JEQU R0 R1 if
         *   instr2 // only if there is an else
         *   JMP end
         * if: instr1
         * end: following code...
         */

        Program program = new Program();
        String labelIf = this.getLabel();
        String labelEnd = this.getLabel();

        program.addInstructions(visit(ctx.getChild(2))); // if condition
        program.addInstruction(new UAL(UAL.Op.XOR, this.nextRegister, this.nextRegister, this.nextRegister)); // set new register to 0 for later test
        this.nextRegister++;
        program.addInstruction(new CondJump(CondJump.Op.JNEQ, this.nextRegister-2, this.nextRegister-1, labelIf)); // test if condition is true (!=0) => jump if
        if (ctx.getChildCount() == 7) { // if there is an else
            this.varRegisters.enterBlock(); // {
            program.addInstructions(visit(ctx.getChild(6))); // else instructions
            this.varRegisters.leaveBlock(); // }
        }
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, labelEnd)); // JMP end

        Program ifInstrProgram = new Program();
        this.varRegisters.enterBlock(); // {
        ifInstrProgram.addInstructions(visit(ctx.getChild(4))); // if instructions
        this.varRegisters.leaveBlock(); // }
        ifInstrProgram.getInstructions().getFirst().setLabel(labelIf);
        program.addInstructions(ifInstrProgram);

        Program endIfProgram = new Program();
        endIfProgram.addInstruction(new UALi(UALi.Op.ADD, this.nextRegister-1, this.nextRegister-1, 0)); // dummy instruction to set end if label
        endIfProgram.getInstructions().getFirst().setLabel(labelEnd);
        program.addInstructions(endIfProgram);
        return program;
    }

    /**
     * Visit a node that contains a while structure and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitWhile(grammarTCLParser.WhileContext ctx) {
        // WHILE '(' expr ')' instr
        /* pseudo-assembler: while (i<10) { instr }
         * loop: R1 := visit(cond)
         *   XOR R2 R2 R2
         *   JEQU R1 R2 end_loop
         *   instr
         *   JMP loop
         * end_loop: following code...
        */

        Program program = new Program();
        String labelStartLoop = this.getLabel();
        String labelEndLoop = this.getLabel();

        Program condLoopProgram = visit(ctx.getChild(2)); // loop condition
        int condReturnRegister = this.nextRegister - 1; // return value of the condition
        condLoopProgram.getInstructions().getFirst().setLabel(labelStartLoop); // set looping label
        program.addInstructions(condLoopProgram);

        program.addInstruction(new UAL(UAL.Op.XOR, this.nextRegister, this.nextRegister, this.nextRegister)); // set new register to 0 for later test
        this.nextRegister++;
        program.addInstruction(new CondJump(CondJump.Op.JEQU, condReturnRegister, this.nextRegister-1, labelEndLoop)); // test if condition is false => stop looping
        this.varRegisters.enterBlock(); // start of the loop {
        program.addInstructions(visit(ctx.getChild(4))); // instructions inside the loop
        this.varRegisters.leaveBlock(); // } end of the loop
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, labelStartLoop)); // go back to the start of the loop

        Program endLoopProgram = new Program();
        endLoopProgram.addInstruction(new UALi(UALi.Op.ADD, this.nextRegister-1, this.nextRegister-1, 0)); // dummy instruction to set end loop label
        endLoopProgram.getInstructions().getFirst().setLabel(labelEndLoop);
        program.addInstructions(endLoopProgram);

        return program;
    }

    /**
     * Visit a node that contains a for structure and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitFor(grammarTCLParser.ForContext ctx) {
        // FOR '(' instr  expr ';' instr ')' instr
        /* pseudo-assembler: for (int i = 0; i < 10; i++;) { instr }
         *   ST R0 1
         * loop: R1 := visit(cond)
         *   XOR R2 R2 R2
         *   JEQU R1 R2 end_loop
         *   instr
         *   ADDi R0 R0 1
         *   JMP loop
         * end_loop: following code...
        */

        Program program = new Program();
        String labelStartLoop = this.getLabel();
        String labelEndLoop = this.getLabel();

        this.varRegisters.enterBlock(); // needed because a FOR structure can declare variables locally (for (int i = 0;...)
        program.addInstructions(visit(ctx.getChild(2))); // initialization

        Program condLoopProgram = visit(ctx.getChild(3)); // loop condition
        int condReturnRegister = this.nextRegister - 1; // return value of the condition
        condLoopProgram.getInstructions().getFirst().setLabel(labelStartLoop); // set looping label
        program.addInstructions(condLoopProgram);

        program.addInstruction(new UAL(UAL.Op.XOR, this.nextRegister, this.nextRegister, this.nextRegister)); // set new register to 0 for later test
        this.nextRegister++;
        program.addInstruction(new CondJump(CondJump.Op.JEQU, condReturnRegister, this.nextRegister-1, labelEndLoop)); // test if condition is false => stop looping
        this.varRegisters.enterBlock(); // start of the loop {
        program.addInstructions(visit(ctx.getChild(7))); // instructions inside the loop
        this.varRegisters.leaveBlock(); // } end of the loop
        program.addInstructions(visit(ctx.getChild(5))); // iteration
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, labelStartLoop)); // go back to the start of the loop

        Program endLoopProgram = new Program();
        endLoopProgram.addInstruction(new UALi(UALi.Op.ADD, this.nextRegister-1, this.nextRegister-1, 0)); // dummy instruction to set end loop label
        endLoopProgram.getInstructions().getFirst().setLabel(labelEndLoop);
        program.addInstructions(endLoopProgram);
        this.varRegisters.leaveBlock();

        return program;
    }

    /**
     * Visit a node that contains the return of a function and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitReturn(grammarTCLParser.ReturnContext ctx) {
        // RETURN expr SEMICOL

        Program program = new Program();
        String functionName = this.currentFunction;
        int returnRegister = this.returnRegisters.getVar(functionName);

        program.addInstructions(visit(ctx.expr())); // expr, return value will be in R(nextRegister-1)
        program.addInstruction(new UALi(UALi.Op.ADD, returnRegister, this.nextRegister-1, 0)); // we return the value in the correct register
        program.addInstruction(new Ret()); // return;

        return program;
    }

    /**
     * Visit a node that contains the code of a function and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitCore_fct(grammarTCLParser.Core_fctContext ctx) {
        // '{' instr* RETURN expr SEMICOL '}'

        Program program = new Program();
        int nbInstructions = ctx.getChildCount() - 5;
        String functionName = this.currentFunction;
        int returnRegister = this.returnRegisters.getVar(functionName);

        for (int i = 0; i < nbInstructions; i++) { // instr*
            program.addInstructions(visit(ctx.getChild(i + 1)));
        }
        program.addInstructions(visit(ctx.expr())); // expr, return value will be in R(nextRegister-1)
        program.addInstruction(new UALi(UALi.Op.ADD, returnRegister, this.nextRegister-1, 0)); // we return the value in the correct register
        program.addInstruction(new Ret()); // return;

        return program;
    }

    /**
     * Visit a node that declare a function and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitDecl_fct(grammarTCLParser.Decl_fctContext ctx) {
        // type VAR '(' (type VAR (',' type VAR)*)? ')' core_fct

        Program program = new Program();
        int childCount = ctx.getChildCount();
        int nbArguments = (childCount == 5) ? 0 : (childCount - 4) / 3;
        String functionName = ctx.getChild(1).getText();

        this.varRegisters.enterFunction();
        this.currentFunction = functionName;
        this.returnRegisters.assignVar(functionName, this.nextRegister); // set the register were the return value will be stocked
        nextRegister++;
        for (int i = nbArguments - 1; i >= 0; i--) { // the arguments are stacked before the call so we un-stack them, in reverse order
            program.addInstructions(this.unstackRegister(this.nextRegister));
            this.nextRegister++;
            this.varRegisters.assignVar(ctx.getChild((3 * i) + 4).getText(), this.nextRegister-1); // arguments counts as new definitions of variables
        }
        program.addInstructions(visit(ctx.core_fct())); // core_fct
        program.getInstructions().getFirst().setLabel(ctx.getChild(1).toString()); // function label
        this.varRegisters.leaveFunction();

        return program;
    }

    /**
     * Visit the 'main' node and create the corresponding linear code
     * @param ctx the context within the parse tree
     * @return a program containing the linear code
     */
    @Override
    public Program visitMain(grammarTCLParser.MainContext ctx) {
        // decl_fct* 'int main()' core_fct EOF

        Program program = new Program();
        int childCount = ctx.getChildCount();

        program.addInstructions(this.setRegisterTo(SP, 0)); // initialize SP
        program.addInstructions(this.setRegisterTo(TP, 4096)); // initialize TP, arbitrarily chose 4096 as stack height
        program.addInstruction(new JumpCall(JumpCall.Op.CALL, "*main")); // call main
        program.addInstruction(new Stop()); // STOP
        program.addInstructions(this.getPrintProgram()); // a callable assembler function for printing arrays (used in visitPrint)
        program.addInstructions(this.getTabAccessProgram());

        for (int i = 0; i < childCount - 3; i++) { // decl_fct*
            program.addInstructions(visit(ctx.getChild(i)));
        }

        this.varRegisters.enterFunction(); // 'main' function declaration
        this.currentFunction = "*main";
        this.returnRegisters.assignVar(this.currentFunction, this.nextRegister); // set the register were the return value will be stocked
        nextRegister++;
        Program mainCoreProgram = visit(ctx.core_fct()); // core_fct
        mainCoreProgram.getInstructions().getFirst().setLabel("*main"); // main label
        program.addInstructions(mainCoreProgram);
        this.varRegisters.leaveFunction();

        return program;
    }

    /**
     * Creates the linear code of an assembler function that prints an array
     * @return the corresponding linear code
     */
    private Program getPrintProgram() {
        Program program = new Program();

        final int SQUARE_BRACKET_OPEN = 91;
        final int SQUARE_BRACKET_CLOSE = 93;
        final int SPACE = 32;

        int[] r = new int[12];
        for (int i = 0; i < 12; i++) {
            r[i] = this.nextRegister;
            this.nextRegister++;
        }

        // inserting code from compilation/CodeGen/print_tab.asm
        program.addInstruction(new UALi("*print_tab", UALi.Op.SUB, this.SP, this.SP, 1));
        program.addInstruction(new Mem(Mem.Op.LD, r[1], this.SP));
        program.addInstruction(new UALi(UALi.Op.SUB, this.SP, this.SP, 1));
        program.addInstruction(new Mem(Mem.Op.LD, r[2], this.SP));
        program.addInstruction(new Mem(Mem.Op.LD, r[3], r[2]));
        program.addInstruction(new UAL(UAL.Op.XOR, r[4], r[4], r[4]));
        program.addInstruction(new UALi(UALi.Op.ADD, r[4], r[4], SQUARE_BRACKET_OPEN));
        program.addInstruction(new IO(IO.Op.OUT, r[4]));
        program.addInstruction(new UAL(UAL.Op.XOR, r[4], r[4], r[4]));
        program.addInstruction(new UALi(UALi.Op.ADD, r[4], r[4], SPACE));
        program.addInstruction(new IO(IO.Op.OUT, r[4]));
        program.addInstruction(new UAL(UAL.Op.XOR, r[5], r[5], r[5]));
        program.addInstruction(new UALi(UALi.Op.ADD, r[2], r[2], 1));
        program.addInstruction(new UALi(UALi.Op.ADD, this.SP, this.SP, 2));
        
        program.addInstruction(new CondJump("*loop_start", CondJump.Op.JEQU, r[5], r[3], "*loop_end"));
        program.addInstruction(new UAL(UAL.Op.XOR, r[6], r[6], r[6]));
        program.addInstruction(new UALi(UALi.Op.MOD, r[7], r[5], 10));
        program.addInstruction(new CondJump(CondJump.Op.JNEQ, r[7], r[6], "*skip_tab_end"));
        program.addInstruction(new CondJump(CondJump.Op.JEQU, r[5], r[6], "*skip_tab_end"));
        program.addInstruction(new Mem(Mem.Op.LD, r[2], r[2]));

        program.addInstruction(new UAL("*skip_tab_end", UAL.Op.XOR, r[9], r[9], r[9]));
        program.addInstruction(new UALi(UALi.Op.ADD, r[9], r[9], 1));
        program.addInstruction(new CondJump(CondJump.Op.JEQU, r[1], r[9], "*print_elem"));
        program.addInstructions(this.stackRegister(r[5]));
        program.addInstructions(this.stackRegister(r[3]));
        program.addInstructions(this.stackRegister(r[2]));
        program.addInstruction(new Mem(Mem.Op.LD, r[2], r[2]));
        program.addInstructions(this.stackRegister(r[2]));
        program.addInstruction(new UALi(UALi.Op.SUB, r[11], r[1], 1));
        program.addInstructions(this.stackRegister(r[11]));
        program.addInstruction(new JumpCall(JumpCall.Op.CALL, "*print_tab"));
        program.addInstruction(new UALi(UALi.Op.ADD, this.SP, this.SP, 1));
        program.addInstruction(new Mem(Mem.Op.LD, r[1], this.SP));
        program.addInstruction(new UALi(UALi.Op.ADD, r[1], r[1], 1));
        program.addInstruction(new UALi(UALi.Op.SUB, this.SP, this.SP, 1));
        program.addInstructions(this.unstackRegister(r[2]));
        program.addInstructions(this.unstackRegister(r[3]));
        program.addInstructions(this.unstackRegister(r[5]));
        program.addInstruction(new UAL(UAL.Op.XOR, r[4], r[4], r[4]));
        program.addInstruction(new UALi(UALi.Op.ADD, r[4], r[4], SPACE));
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, "*print_elem_end"));
        
        program.addInstruction(new Mem("*print_elem", Mem.Op.LD, r[0], r[2]));
        program.addInstruction(new IO(IO.Op.PRINT, r[0]));
        
        program.addInstruction(new UAL("*print_elem_end", UAL.Op.XOR, r[8], r[8], r[8]));
        program.addInstruction(new UALi(UALi.Op.ADD, r[8], r[8], SPACE));
        program.addInstruction(new IO(IO.Op.OUT, r[8]));
        program.addInstruction(new UALi(UALi.Op.ADD, r[2], r[2], 1));
        program.addInstruction(new UALi(UALi.Op.ADD, r[5], r[5], 1));
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, "*loop_start"));
        
        program.addInstruction(new UAL("*loop_end", UAL.Op.XOR, r[4], r[4], r[4]));
        program.addInstruction(new UALi(UALi.Op.ADD, r[4], r[4], SQUARE_BRACKET_CLOSE));
        program.addInstruction(new IO(IO.Op.OUT, r[4]));
        program.addInstruction(new UALi(UALi.Op.SUB, this.SP, this.SP, 2));
        program.addInstruction(new Ret());

        return program;
    }

    /**
     * Creates the linear code of an assembler function that access an element of an array
     * @return the corresponding linear code
     */
    private Program getTabAccessProgram() {
        Program program = new Program();

        int[] r = new int[10];
        for (int i = 0; i < 10; i++) {
            r[i] = this.nextRegister;
            this.nextRegister++;
        }

        // inserting code from compilation/CodeGen/tab_access.asm
        program.addInstructions(this.unstackRegister(r[0]));
        program.getInstructions().getFirst().setLabel("*tab_access");
        program.addInstructions(this.unstackRegister(r[1]));
        program.addInstructions(this.unstackRegister(r[2]));
        program.addInstruction(new Mem(Mem.Op.LD, r[3], r[2]));
        program.addInstruction(new UALi(UALi.Op.ADD, r[4], r[3], 0));
        program.addInstruction(new CondJump(CondJump.Op.JINF, r[1], r[3], "*skip_resize"));
        program.addInstruction(new UALi(UALi.Op.ADD, r[4], r[1], 1));

        program.addInstruction(new Mem("*skip_resize", Mem.Op.ST, r[4], r[2]));
        program.addInstruction(new UALi(UALi.Op.ADD, r[2], r[2], 1));
        program.addInstruction(new UAL(UAL.Op.XOR, r[5], r[5], r[5]));
        program.addInstruction(new UAL(UAL.Op.XOR, r[6], r[6], r[6]));
        program.addInstruction(new UAL(UAL.Op.XOR, r[9], r[9], r[9]));
        program.addInstruction(new UALi(UALi.Op.ADD, r[9], r[9], 1));
        
        program.addInstruction(new UALi("*begin_loop", UALi.Op.MOD, r[7], r[5], 10));
        program.addInstruction(new CondJump(CondJump.Op.JNEQ, r[7], r[6], "*skip_tab_access_end"));
        program.addInstruction(new CondJump(CondJump.Op.JEQU, r[5], r[6], "*skip_tab_access_end"));
        program.addInstruction(new CondJump(CondJump.Op.JIEQ, r[3], r[0], "*skip_alloc"));
        program.addInstruction(new Mem(Mem.Op.ST, this.TP, r[2]));
        program.addInstruction(new UALi(UALi.Op.ADD, this.TP, this.TP, 11));

        program.addInstruction(new Mem("*skip_alloc", Mem.Op.LD, r[2], r[2]));

        program.addInstruction(new CondJump("*skip_tab_access_end", CondJump.Op.JINF, r[5], r[3], "*skip_fill"));
        program.addInstruction(new CondJump(CondJump.Op.JNEQ, r[0], r[9], "*skip_simple_init"));
        program.addInstruction(new Mem(Mem.Op.ST, r[6], r[2]));
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, "*skip_fill"));

        program.addInstruction(new UALi("*skip_simple_init", UALi.Op.ADD, r[8], r[2], 0));
        program.addInstruction(new Mem(Mem.Op.ST, this.TP, r[2]));
        program.addInstruction(new UALi(UALi.Op.ADD, this.TP, this.TP, 12));
        program.addInstruction(new Mem(Mem.Op.LD, r[2], r[2]));
        program.addInstruction(new Mem(Mem.Op.ST, r[6], r[2]));
        program.addInstruction(new UALi(UALi.Op.ADD, r[2], r[8], 0));

        program.addInstruction(new CondJump("*skip_fill", CondJump.Op.JNEQ, r[5], r[1], "*skip_return"));
        program.addInstructions(this.stackRegister(r[2]));
        program.addInstruction(new Ret());

        program.addInstruction(new UALi("*skip_return", UALi.Op.ADD, r[2], r[2], 1));
        program.addInstruction(new UALi(UALi.Op.ADD, r[5], r[5], 1));
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, "*begin_loop"));
        
        return program;
    }

    /**
     * Creates the linear code of an assembler function that dumps the memory (excluding the stack), for debugging purposes
     * @return the corresponding linear code
     */
    private Program getDumpMemory() {
        Program program = new Program();

        final int SPACE = 32;
        final int NEW_LINE = 10;

        int[] r = new int[7];
        for (int i = 0; i < 7; i++) {
            r[i] = this.nextRegister;
            this.nextRegister++;
        }

        // inserting code from compilation/CodeGen/dump_memory.asm
        program.addInstructions(this.setRegisterTo(r[0], 4096));
        program.getInstructions().getFirst().setLabel("*dump_memory");
        program.addInstructions(this.setRegisterTo(r[1], SPACE));
        program.addInstructions(this.setRegisterTo(r[2], NEW_LINE));
        program.addInstructions(this.setRegisterTo(r[3], 4500));

        program.addInstruction(new CondJump("*debut_jump", CondJump.Op.JEQU, r[0], r[3], "*fin_dump"));
        program.addInstruction(new Mem(Mem.Op.LD, r[4], r[0]));
        program.addInstruction(new IO(IO.Op.PRINT, r[4]));
        program.addInstruction(new IO(IO.Op.OUT, r[1]));
        program.addInstruction(new UALi(UALi.Op.ADD, r[0], r[0], 1));
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, "*debut_jump"));

        program.addInstruction(new IO("*fin_dump", IO.Op.OUT, r[2]));
        program.addInstruction(new Ret());

        return program;
    }
}
