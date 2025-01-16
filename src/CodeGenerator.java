package src;

import java.util.Map;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import src.Asm.*;
import src.Type.ArrayType;
import src.Type.Type;
import src.Type.UnknownType;

/**
 * A class that generates linear code from a valid TCL program tree, called like this: program = codeGenerator.visit(tree).
 * The type analysis must have already been done.
 */
public class CodeGenerator extends AbstractParseTreeVisitor<Program> implements grammarTCLVisitor<Program> {
    private final Map<UnknownType,Type> types; // links each variable with its type

    private final int SP = 0; // stackPointer should always be 1 over the last stacked variable, and shouldn't go over 4095
    private final int TP = 1; // stackPointer for arrays (tabPointer), contains the address of the next free space in memory for arrays

    private Integer nextRegister; // nextRegister should always be a non utilised register number
    private Integer nextLabel; // nextLabel should always be a non utilised label number

    private final VarStack<Integer> varRegisters; // links each variable with its register number, for each depth

    /**
     * Constructor
     * @param types types of each variable of the source code
     */
    public CodeGenerator(Map<UnknownType, Type> types) {
        this.types = types;
        this.nextRegister = 2;
        this.nextLabel = 0;
        this.varRegisters = new VarStack<>();
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
     * Macro to assign a value to a register
     * @param register the register we want to stock the value in
     * @param value the value we want to stock in the register
     * @return a program that assign the value to the register
     */
    private Program assignRegister(int register, int value) {
        Program program= new Program();
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
     * @return 0 if it is not an array, else the depth/dimension of the array
     */
    private static int getArrayDepth(Type type) {
        if (type instanceof ArrayType array)
            return 1 + getArrayDepth(array.getTabType());
        return 0;
    }

    @Override
    public Program visitNegation(grammarTCLParser.NegationContext ctx) {
        Program program= new Program();
        program.addInstructions(visit(ctx.getChild(1)));
        program.addInstruction(new UALi(UALi.Op.XOR, this.nextRegister + 1, this.nextRegister, 1));
        this.nextRegister += 2;
        return program;
    }

    @Override
    public Program visitComparison(grammarTCLParser.ComparisonContext ctx) {
        // expr op=(SUP | INF | SUPEQ | INFEQ) expr # comparison
        Program program= new Program();
        program.addInstructions(visit(ctx.getChild(0)));
        program.addInstructions(visit(ctx.getChild(2)));
        // Get value of Child(0) in this.nextRegister - 2
        program.addInstructions(this.assignRegister(nextRegister, this.nextRegister - 1));
        this.nextRegister++;
        // Get value of Child(2) in this.nextRegister - 1
        program.addInstructions(this.assignRegister(nextRegister, this.nextRegister - 2));
        this.nextRegister++;
        switch (ctx.getChild(1).getText()) {
            case ">" ->
                program.addInstruction(
                        new CondJump(CondJump.Op.JSUP, this.nextRegister - 2, this.nextRegister - 1, "Label" + this.nextLabel));
            case "<" ->
                program.addInstruction(
                        new CondJump(CondJump.Op.JINF, this.nextRegister - 2, this.nextRegister - 1, "Label" + this.nextLabel));
            case ">=" ->
                program.addInstruction(
                        new CondJump(CondJump.Op.JSEQ, this.nextRegister - 2, this.nextRegister - 1, "Label" + this.nextLabel));
            case "<=" ->
                program.addInstruction(
                        new CondJump(CondJump.Op.JIEQ, this.nextRegister - 2, this.nextRegister - 1, "Label" + this.nextLabel));
        }
        // Return 0 if Child(0) !comparison Child(2), then JMP to end of comparison
        program.addInstruction(new UAL(UAL.Op.XOR, this.nextRegister, this.nextRegister, this.nextRegister));
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, "Label" + this.nextLabel + 1));
        // Return 1 if Child(0) comparison Child(2), then JMP to end of comparison
        UAL asm = new UAL(UAL.Op.XOR, this.nextRegister, this.nextRegister, this.nextRegister);
        asm.setLabel("Label" + this.nextLabel);
        program.addInstruction(asm);
        program.addInstruction(new UALi(UALi.Op.ADD, this.nextRegister, this.nextRegister, 1));
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, "Label" + this.nextLabel + 1));
        this.nextLabel++;
        // Line to JMP to when comparison is finished
        // This line doesn't do anything but is needed to JMP to the end of comparison
        // It explains why we still use the same register and why we increment its value
        // only after
        UALi asmi = new UALi(UALi.Op.ADD, this.nextRegister, this.nextRegister, 0);
        asmi.setLabel("Label" + this.nextLabel);
        program.addInstruction(asmi);
        this.nextRegister++;
        this.nextLabel++;
        return program;
    }

    @Override
    public Program visitOr(grammarTCLParser.OrContext ctx) {
        // expr OR expr # or
        // OR : '||';
        Program program= new Program();
        program.addInstructions(visit(ctx.getChild(0)));
        int valueRegister = this.nextRegister - 1;
        program.addInstructions(visit(ctx.getChild(2)));
        program.addInstruction(new UAL(UAL.Op.OR, this.nextRegister, valueRegister, this.nextRegister - 1));
        this.nextRegister++;
        return program;
    }

    @Override
    public Program visitOpposite(grammarTCLParser.OppositeContext ctx) {
        Program program= new Program();
        program.addInstructions(visit(ctx.getChild(1)));
        program.addInstruction(new UALi(UALi.Op.XOR, this.nextRegister, this.nextRegister - 1, 0xFFFFFFFF));
        program.addInstruction(new UALi(UALi.Op.SUB, this.nextRegister, this.nextRegister, 1));
        this.nextRegister++;
        return program;
    }

    @Override
    public Program visitInteger(grammarTCLParser.IntegerContext ctx) {
        // INT # integer
        // INT : '-'?[0-9]+
        Program program= new Program();
        int value = Integer.parseInt(ctx.INT().getText());
        program.addInstructions(this.assignRegister(nextRegister, value));
        this.nextRegister++;
        return program;
    }

    @Override
    public Program visitTab_access(grammarTCLParser.Tab_accessContext ctx) {
        Program program= new Program();
        program.addInstructions(visit(ctx.getChild(0)));
        int tabRegister = this.nextRegister - 1;
        program.addInstructions(visit(ctx.getChild(2)));
        int indexRegister = this.nextRegister - 1;
        int profondeur = getArrayDepth(this.types.get(new UnknownType(ctx.getChild(0)))) - 1;
        program.addInstructions(this.stackRegister(tabRegister));
        program.addInstructions(this.stackRegister(indexRegister));
        program.addInstructions(this.assignRegister(nextRegister, profondeur));
        program.addInstructions(this.stackRegister(nextRegister));
        this.nextRegister++;
        program.addInstruction(new JumpCall(JumpCall.Op.CALL, "*tab_access"));
        program.addInstructions(this.unstackRegister(nextRegister));
        // on récupère la valeur pointée
        program.addInstruction(new Mem(Mem.Op.LD, this.nextRegister, this.nextRegister));
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
        int nbChilds = ctx.getChildCount();
        int nbArguments = nbChilds == 3 ? 0 : (nbChilds - 2)/2;

        for (int i = 0; i < nbArguments; i++) { // expr*
            program.addInstructions(visit(ctx.getChild((2*i)+3))); // value of expression will be stocked in R(nextRegister-1)
            program.addInstructions(this.stackRegister(nextRegister-1)); // stack argument
            this.nextRegister++;
        }
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, ctx.getChild(0).getText())); // call the function

        return program;
    }

    @Override
    public Program visitBoolean(grammarTCLParser.BooleanContext ctx) {
        // BOOL # boolean
        // BOOL : 'true' | 'false'
        Program program= new Program();
        switch (ctx.getChild(0).getText()) {
            case "true" -> program.addInstructions(this.assignRegister(nextRegister, 1));
            case "false" -> program.addInstruction(new UAL(UAL.Op.XOR, this.nextRegister, this.nextRegister, this.nextRegister));
        }
        this.nextRegister++;
        return program;
    }

    @Override
    public Program visitAnd(grammarTCLParser.AndContext ctx) {
        // expr AND expr # and
        // AND : '&&'
        Program program= new Program();
        program.addInstructions(visit(ctx.getChild(0)));
        int valueRegister = this.nextRegister - 1;
        program.addInstructions(visit(ctx.getChild(2)));
        program.addInstruction(new UAL(UAL.Op.AND, this.nextRegister, valueRegister, this.nextRegister - 1));
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
        int varRegister = this.varRegisters.getVar(ctx.getChild(0).getText()); // the register in which VAR is

        program.addInstruction(new UALi(UALi.Op.ADD, this.nextRegister, varRegister, 0)); // return the variable in R(nextRegister-1)
        this.nextRegister++;

        return program;
    }

    @Override
    public Program visitMultiplication(grammarTCLParser.MultiplicationContext ctx) {
        // expr op=(MUL | DIV | MODULO) expr # multiplication
        Program program= new Program();
        program.addInstructions(visit(ctx.getChild(0)));
        int valueRegister = this.nextRegister - 1;
        program.addInstructions(visit(ctx.getChild(2)));
        switch (ctx.getChild(1).getText()) {
            case "*" -> program.addInstruction(new UAL(UAL.Op.MUL, this.nextRegister, valueRegister, this.nextRegister - 1));
            case "/" -> program.addInstruction(new UAL(UAL.Op.DIV, this.nextRegister, this.nextRegister - 1, valueRegister));
            case "%" -> program.addInstruction(new UAL(UAL.Op.MOD, this.nextRegister, this.nextRegister - 1, valueRegister));
        }
        this.nextRegister++;
        return program;
    }

    @Override
    public Program visitEquality(grammarTCLParser.EqualityContext ctx) {
        // expr op=(EQUALS | DIFF) expr # equality
        // EQUALS : '==';
        // DIFF : '!=';
        Program program= new Program();
        program.addInstructions(visit(ctx.getChild(0)));
        program.addInstructions(visit(ctx.getChild(2)));
        // Get value of Child(0) in this.nextRegister - 2
        program.addInstructions(this.assignRegister(nextRegister, this.nextRegister - 1));
        this.nextRegister++;
        // Get value of Child(2) in this.nextRegister - 1
        program.addInstructions(this.assignRegister(nextRegister, this.nextRegister - 2));
        this.nextRegister++;
        switch (ctx.getChild(1).getText()) {
            case "==" ->
                program.addInstruction(
                        new CondJump(CondJump.Op.JEQU, this.nextRegister - 2, this.nextRegister - 1, "Label" + this.nextLabel));
            case "!=" ->
                program.addInstruction(
                        new CondJump(CondJump.Op.JNEQ, this.nextRegister - 2, this.nextRegister - 1, "Label" + this.nextLabel));
        }
        // Return 0 if Child(0) !equality Child(2), then JMP to end of equality
        program.addInstruction(new UAL(UAL.Op.XOR, this.nextRegister, this.nextRegister, this.nextRegister));
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, "Label" + this.nextLabel + 1));
        // Return 1 if Child(0) equality Child(2), then JMP to end of equality
        UAL asm = new UAL(UAL.Op.XOR, this.nextRegister, this.nextRegister, this.nextRegister);
        asm.setLabel("Label" + this.nextLabel);
        program.addInstruction(asm);
        program.addInstruction(new UALi(UALi.Op.ADD, this.nextRegister, this.nextRegister, 1));
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, "Label" + this.nextLabel + 1));
        this.nextLabel++;
        // Line to JMP to when equality is finished
        // This line doesn't do anything but is needed to JMP to the end of equality
        // It explains why we still use the same register and why we increment its value
        // only after
        UALi asmi = new UALi(UALi.Op.ADD, this.nextRegister, this.nextRegister, 0);
        asmi.setLabel("Label" + this.nextLabel);
        program.addInstruction(asmi);
        this.nextRegister++;
        this.nextLabel++;
        return program;
    }

    @Override
    public Program visitTab_initialization(grammarTCLParser.Tab_initializationContext ctx) {
        int varCount = (ctx.getChildCount() - 2) / 2 + 1;
        Program program= new Program();
        // registre contenant la longueur du tableau
        int lengthRegister = this.nextRegister;
        this.nextRegister++;
        // registre contenant le pointeur sur le tableau (mutable)
        int pointerRegister = this.nextRegister;
        this.nextRegister++;
        // registre contenant le pointeur sur le tableau (valeur retournée)
        int fixedPointerRegister = this.nextRegister;
        this.nextRegister++;
        // lengthRegister := varCount
        program.addInstructions(this.assignRegister(lengthRegister, varCount));
        
        // pointerRegister := TP
        program.addInstruction(new UALi(src.Asm.UALi.Op.ADD, pointerRegister, this.TP, 0));
        // fixedPointerRegister := pointerRegister
        program.addInstruction(new UALi(src.Asm.UALi.Op.ADD, fixedPointerRegister, pointerRegister, 0));

        // on empile la longueur du tableau
        program.addInstruction(new Mem(Mem.Op.ST, lengthRegister, pointerRegister));
        program.addInstruction(new UALi(src.Asm.UALi.Op.ADD, pointerRegister, pointerRegister, 1));
        
        // le TP pointe maintenant à la fin du tableau
        program.addInstruction(new UALi(UALi.Op.ADD, this.TP, this.TP, 12));
        for (int i = 0; i < varCount; i++) {
            if (i % 10 == 0 && i > 0) {
                // on ajoute le pointeur sur la suite à la fin du tableau
                program.addInstruction(new Mem(src.Asm.Mem.Op.ST, this.TP, pointerRegister));
                // on bouge le pointeur au début du prochain tableau
                program.addInstruction(new UALi(UALi.Op.ADD, pointerRegister, this.TP, 0));
                // on met à jour le prochain espace vide pour les tableaux
                program.addInstruction(new UALi(UALi.Op.ADD, this.TP, this.TP, 11));
            }
            // on a la valeur dans nextRegister - 1
            program.addInstructions(visit(ctx.getChild(2 * i + 1)));
            // on la rajoute dans le tableau
            program.addInstruction(new Mem(Mem.Op.ST, this.nextRegister - 1, pointerRegister));
            // on pointe sur la case suivante
            program.addInstruction(new UALi(UALi.Op.ADD, pointerRegister, pointerRegister, 1));
        }
        // nextRegister := fixedPointerRegister
        program.addInstruction(new UALi(src.Asm.UALi.Op.ADD, this.nextRegister, fixedPointerRegister, 0));
        this.nextRegister++;
        return program;
    }

    @Override
    public Program visitAddition(grammarTCLParser.AdditionContext ctx) {
        // expr op=(ADD | SUB) expr # addition
        Program program= new Program();
        program.addInstructions(visit(ctx.getChild(0)));
        int valueRegister = this.nextRegister - 1;
        program.addInstructions(visit(ctx.getChild(2)));
        switch (ctx.getChild(1).getText()) {
            case "+" -> program.addInstruction(new UAL(UAL.Op.ADD, this.nextRegister, valueRegister, this.nextRegister - 1));
            case "-" -> program.addInstruction(new UAL(UAL.Op.SUB, this.nextRegister, this.nextRegister - 1, valueRegister));
        }
        this.nextRegister++;
        return program;
    }

    /**
     * Visit a node that contains the type of a variable, which shouldn't be called in the context of linear code creation
     * @param ctx the context within the parse tree
     * @return a RuntimeException
     */
    @Override
    public Program visitBase_type(grammarTCLParser.Base_typeContext ctx) {
        // BASE_TYPE

        throw new RuntimeException("Method 'visitBase_type' should not be called");
    }

    /**
     * Visit a node that contains the type of an array, which shouldn't be called in the context of linear code creation
     * @param ctx the context within the parse tree
     * @return a RuntimeException
     */
    @Override
    public Program visitTab_type(grammarTCLParser.Tab_typeContext ctx) {
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

        Program program= new Program();
        int nbChilds = ctx.getChildCount();
        int varRegister = this.nextRegister;
        this.nextRegister++;

        this.varRegisters.assignVar(ctx.VAR().getText(), varRegister);
        if (nbChilds == 5) {
            program.addInstructions(visit(ctx.getChild(3)));
            program.addInstruction(new UALi(src.Asm.UALi.Op.ADD, varRegister, this.nextRegister - 1, 0)); // varRegister := nextRegister - 1
        }

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

        Program program= new Program();
        UnknownType variable = new UnknownType(ctx.VAR());
        Type varType = this.types.get(variable);
        int arrayDepth = getArrayDepth(varType);
        int varRegister = this.varRegisters.getVar(ctx.VAR().getText());
        int depthRegister = this.nextRegister;
        this.nextRegister++;

        if (arrayDepth == 0) { // primitive type
            program.addInstruction(new IO(IO.Op.PRINT, varRegister));
        } else { // array type
            program.addInstructions(this.stackRegister(varRegister)); // we stack the array
            program.addInstructions(this.assignRegister(depthRegister, arrayDepth));
            program.addInstructions(this.stackRegister(depthRegister)); // we stack its depth
            program.addInstruction(new JumpCall(src.Asm.JumpCall.Op.CALL, "*print_tab")); // we call the print_tab function
        }

        final int NEW_LINE = 10;
        program.addInstructions(this.assignRegister(nextRegister, NEW_LINE));
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

        Program program= new Program();
        int varRegister = this.varRegisters.getVar(ctx.VAR().getText());
        int bracketsCount = (ctx.getChildCount() - 4) / 3;
        int arrayDepth = getArrayDepth(this.types.get(new UnknownType(ctx.VAR())));
        int rightRegister = this.nextRegister - 1;
        int leftRegister = this.nextRegister;
        this.nextRegister++;
        int depthRegister = this.nextRegister;
        this.nextRegister++;

        program.addInstructions(visit(ctx.getChild(ctx.getChildCount() - 2)));
        program.addInstruction(new UALi(UALi.Op.ADD, leftRegister, varRegister, 0));
        for (int i = 0; i < bracketsCount; i++) {
            int child = 2 + (i * 3);
            program.addInstructions(visit(ctx.getChild(child)));
            if (i > 0)
                program.addInstruction(new Mem(Mem.Op.LD, leftRegister, leftRegister));
            program.addInstructions(this.stackRegister(leftRegister));
            program.addInstructions(this.stackRegister(nextRegister - 1));
            program.addInstructions(this.assignRegister(depthRegister, arrayDepth - 1 - i));
            program.addInstructions(this.stackRegister(depthRegister));
            program.addInstruction(new JumpCall(JumpCall.Op.CALL, "*tab_access"));
            program.addInstructions(this.unstackRegister(leftRegister));
        }

        if (bracketsCount == 0) {
            program.addInstruction(new UALi(UALi.Op.ADD, varRegister, rightRegister, 0));
        } else {
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
        /* pseudo-assembler: if (cond) then instr1 else instr2
         *   cond
         *   XOR R1 R1 R1
         *   JEQU nextRegister-2 nextRegister-1 if //-2 = cond return, -1 = XOR'd register
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
            program.addInstructions(visit(ctx.getChild(7))); // else instructions
            this.varRegisters.leaveBlock(); // }
        }
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, labelEnd)); // JMP end

        Program ifInstrProgram = new Program();
        this.varRegisters.enterBlock(); // {
        ifInstrProgram.addInstructions(visit(ctx.getChild(5))); // if instructions
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
        /* pseudo-assembler: while(i<10) instr
         * loop: visit(cond)
         *   XOR R1 R1 R1
         *   JEQU nextRegister-2 nextRegister-1 end_loop //-2 = cond return, -1 = XOR'd register
         *   instr
         *   JMP loop
         * end_loop: following code...
        */

        Program program = new Program();
        String labelStartLoop = this.getLabel();
        String labelEndLoop = this.getLabel();

        Program condLoopProgram = visit(ctx.getChild(2)); // loop condition
        condLoopProgram.getInstructions().getFirst().setLabel(labelStartLoop); // set looping label
        program.addInstructions(condLoopProgram);
        program.addInstruction(new UAL(UAL.Op.XOR, this.nextRegister, this.nextRegister, this.nextRegister)); // set new register to 0 for later test
        this.nextRegister++;
        program.addInstruction(new CondJump(CondJump.Op.JEQU, this.nextRegister-2, this.nextRegister-1, labelEndLoop)); // test if condition is false => stop looping
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
        // FOR '(' instr ',' expr ',' instr ')' instr
        /* pseudo-assembler: for(int i = 0; i < 10; i++) instr
         *   ST R0 1
         * loop: visit(cond)
         *   XOR R1 R1 R1
         *   JEQU nextRegister-2 nextRegister-1 end_loop //-2 = cond return, -1 = XOR'd register
         *   instr
         *   ADDi R0 R0 1
         *   JMP loop
         * end_loop: following code...
        */

        Program program = new Program();
        String labelStartLoop = this.getLabel();
        String labelEndLoop = this.getLabel();

        this.varRegisters.enterBlock(); // needed because the for can declare variables locally (for (int i = 0;...)
        program.addInstructions(visit(ctx.getChild(2))); // initialization
        Program condLoopProgram = visit(ctx.getChild(4)); // loop condition
        condLoopProgram.getInstructions().getFirst().setLabel(labelStartLoop); // set looping label
        program.addInstructions(condLoopProgram);
        program.addInstruction(new UAL(UAL.Op.XOR, this.nextRegister, this.nextRegister, this.nextRegister)); // set new register to 0 for later test
        this.nextRegister++;
        program.addInstruction(new CondJump(CondJump.Op.JEQU, this.nextRegister-2, this.nextRegister-1, labelEndLoop)); // test if condition is false => stop looping
        this.varRegisters.enterBlock(); // start of the loop {
        program.addInstructions(visit(ctx.getChild(8))); // instructions inside the loop
        this.varRegisters.leaveBlock(); // } end of the loop
        program.addInstructions(visit(ctx.getChild(6))); // iteration
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

        program.addInstructions(visit(ctx.expr())); // expr, return value will be in R(nextRegister-1)
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

        for (int i = 0; i < nbInstructions; i++) { // instr*
            program.addInstructions(visit(ctx.getChild(i + 1)));
        }
        program.addInstructions(visit(ctx.expr())); // expr, return value will be in R(nextRegister-1)
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
        int nbChilds = ctx.getChildCount();
        int nbArguments = (nbChilds == 5 ? 0 : (nbChilds - 4)/3);

        this.varRegisters.enterFunction();
        for (int i = 0; i < nbArguments; i++) { // the arguments are stacked before the call so we unstack them
            program.addInstructions(this.unstackRegister(nextRegister));
            this.nextRegister++;
            this.varRegisters.assignVar(ctx.getChild((3 * i) + 5).getText(), this.nextRegister-1); // arguments counts as new definitions of variables
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
        int nbChilds = ctx.getChildCount();

        this.varRegisters.enterBlock(); // a first enterBlock is needed for the whole program to work
        program.addInstructions(this.assignRegister(SP, 0)); // initialize SP
        program.addInstructions(this.assignRegister(TP, 4096)); // initialize TP, arbitrarily chose 4096 as stack height
        program.addInstruction(new JumpCall(JumpCall.Op.CALL, "*main")); // call main
        program.addInstruction(new Stop()); // STOP
        program.addInstructions(this.getPrintProgram()); // a callable assembler function for printing arrays (used in visitPrint)
        program.addInstructions(this.getTabAccessProgram());
        // program.addInstructions(this.getDumpMemory()); // in case of debugging

        for (int i = 0; i < nbChilds - 3; i++) { // decl_fct*
            program.addInstructions(visit(ctx.getChild(i)));
        }

        this.varRegisters.enterFunction(); // 'main' function declaration
        Program mainCoreProgram = visit(ctx.core_fct()); // core_fct
        mainCoreProgram.getInstructions().getFirst().setLabel("*main"); // main label
        program.addInstructions(mainCoreProgram);
        this.varRegisters.leaveFunction();
        this.varRegisters.leaveBlock();

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

        // inserting code from compilation/src/CodeGen/print_tab.asm
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
        Program program= new Program();

        int[] r = new int[9];
        for (int i = 0; i < 9; i++) {
            r[i] = this.nextRegister;
            this.nextRegister++;
        }

        // inserting code from compilation/src/CodeGen/tab_access.asm
        program.addInstructions(this.unstackRegister(r[0]));
        program.getInstructions().getFirst().setLabel("*tab_access");
        program.addInstructions(this.unstackRegister(r[1]));
        program.addInstructions(this.unstackRegister(r[2]));
        program.addInstruction(new Mem(Mem.Op.LD, r[3], r[2]));
        program.addInstruction(new UALi(UALi.Op.ADD, r[4], r[3], 0));
        program.addInstruction(new CondJump(CondJump.Op.JIEQ, r[1], r[3], "*skip_resize"));
        program.addInstruction(new UALi(UALi.Op.ADD, r[4], r[1], 1));

        program.addInstruction(new Mem("*skip_resize", Mem.Op.ST, r[4], r[2]));
        program.addInstruction(new UALi(UALi.Op.ADD, r[2], r[2], 1));
        program.addInstruction(new UAL(UAL.Op.XOR, r[5], r[5], r[5]));
        program.addInstruction(new UAL(UAL.Op.XOR, r[6], r[6], r[6]));
        
        program.addInstruction(new UALi("*begin_loop", UALi.Op.MOD, r[7], r[5], 10));
        program.addInstruction(new CondJump(CondJump.Op.JNEQ, r[7], r[6], "*skip_tab_access_end"));
        program.addInstruction(new CondJump(CondJump.Op.JEQU, r[5], r[6], "*skip_tab_access_end"));
        program.addInstruction(new CondJump(CondJump.Op.JIEQ, r[3], r[0], "*skip_alloc"));
        program.addInstruction(new Mem(Mem.Op.ST, this.TP, r[2]));
        program.addInstruction(new UALi(UALi.Op.ADD, this.TP, this.TP, 11));

        program.addInstruction(new Mem("*skip_alloc", Mem.Op.LD, r[2], r[2]));

        program.addInstruction(new CondJump("*skip_tab_access_end", CondJump.Op.JINF, r[5], r[3], "*skip_fill"));
        program.addInstruction(new CondJump(CondJump.Op.JNEQ, r[0], r[6], "*skip_simple_init"));
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
     * Creates the linear code of an assembler function that dumps the memory (excluding the stack)
     * @return the corresponding linear code
     */
    private Program getDumpMemory() {
        Program program= new Program();

        final int SPACE = 32;
        final int NEW_LINE = 10;

        int[] r = new int[7];
        for (int i = 0; i < 7; i++) {
            r[i] = this.nextRegister;
            this.nextRegister++;
        }

        // inserting code from compilation/src/CodeGen/dump_memory.asm
        program.addInstructions(this.assignRegister(r[0], 4096));
        program.getInstructions().getFirst().setLabel("*dump_memory");
        program.addInstructions(this.assignRegister(r[1], SPACE));
        program.addInstructions(this.assignRegister(r[2], NEW_LINE));
        program.addInstructions(this.assignRegister(r[3], 4500));

        program.addInstruction(new CondJump("*debut_jump", CondJump.Op.JEQU, r[0], r[3], "*fin_dump"));
        program.addInstruction(new Mem(Mem.Op.LD, r[4], r[0]));
        program.addInstruction(new IO(IO.Op.PRINT, r[4]));
        program.addInstruction(new IO(IO.Op.OUT, r[1]));
        program.addInstruction(new UALi(UALi.Op.ADD, r[0], r[0], 1));
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, "*debut_jump"));

        program.addInstruction(new Ret("*fin_dump"));

        return program;
    }
}
