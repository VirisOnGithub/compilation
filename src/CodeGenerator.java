package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import src.Asm.*;
import src.Type.ArrayType;
import src.Type.Type;
import src.Type.UnknownType;

public class CodeGenerator extends AbstractParseTreeVisitor<Program> implements grammarTCLVisitor<Program> {
    private final Map<UnknownType,Type> types; // links each variable with its type

    private final int SP = 0; // stackPointer should always be 1 over the last stacked variable, and shouldn't go over 4095
    private final int TP = 1; // stackPointer for tabs, contains the address of the next free space in memory for tabs
    private Integer nextRegister; // nextRegister should always be a non utilised register number
    private Integer nextLabel; // nextLabel should always be a non utilised label number

    private final ArrayList<Map<String, Integer>> varRegisters; // links each variable with its register number, for each depth
    private final Stack<Integer> lastAccessibleDepth; // stack top is the furthest that we can search in varRegisters

    private final Stack<String> functionsEndLabels; // stack top is the label of the end of the currently called function

    /**
     * Constructor
     * @param types types de chaque variable du code source
     */
    public CodeGenerator(Map<UnknownType, Type> types) {
        this.types = types;
        this.nextRegister = 2;
        this.nextLabel = 0;
        this.varRegisters = new ArrayList<>();
        this.lastAccessibleDepth = new Stack<>();
        this.functionsEndLabels = new Stack<>();
    }

    /**
     * Macro to add instructions to stack a register
     * @param register the number of the register that needs to be stacked
     * @return a program that stack the register
     */
    private Program stackRegister(int register) {
        Program program = new Program();
        program.addInstruction(new Mem(Mem.Op.ST, register, SP));
        program.addInstruction(new UALi(UALi.Op.ADD, SP, SP, 1));
        return program;
    }

    /**
     * Macro to add instructions to unstack into a register
     * @param register the number of the register that needs to be unstacked
     * @return a program that unstack into the register
     */
    private Program unstackRegister(int register) {
        Program program = new Program();
        program.addInstruction(new UALi(UALi.Op.SUB, SP, SP, 1));
        program.addInstruction(new Mem(Mem.Op.LD, register, SP));
        return program;
    }

    /**
     * Macro to assign a value to a register
     * @param register the register we want to stock the value in
     * @param value the value we want to stock in the register
     * @return a program that assign the value to the register
     */
    private Program assignRegister(int register, int value) {
        Program p = new Program();
        p.addInstruction(new UAL(UAL.Op.XOR, register, register, register));
        p.addInstruction(new UALi(UALi.Op.ADD, register, register, value));
        return p;
    }

    /**
     * Macro to get a new valid non utilised label name
     * @return the label name
     */
    private String getLabel() {
        nextLabel++;
        return "label" + (nextLabel-1);
    }

    /**
     * Get the number of the register were a given variable is stocked in
     * @param varName the name of the variable we try to find the register for
     * @return the number of the register where varName is, RuntimeException else
     */
    private int getVarRegister(String varName) {
        for (int depth = varRegisters.size() - 1; depth >= lastAccessibleDepth.getLast(); depth--) { // we unstack all the accessible maps in varRegisters
            var varMap = varRegisters.get(depth);
            if (varMap.containsKey(varName)) { // we stop at the first corresponding variable name
                return varMap.get(varName);
            }
        }
        throw new RuntimeException("The variable " + varName + " has not been assigned"); // if none was found
    }

    /**
     * Set the register number of a given variable
     * @param varName the name of the variable
     * @param register the register we want to put the variable in
     */
    private void assignVarRegister(String varName, int register) {
        varRegisters.getLast().put(varName, register);
        System.out.println("R" + register + " : " + varName);
    }

    /**
     * Needs to be called just before entering a function
     */
    private void enterFunction() {
        lastAccessibleDepth.add(varRegisters.size());
        this.enterBlock();
    }

    /**
     * Needs to be called just after leaving a function
     */
    private void exitFunction() {
        this.exitBlock();
        lastAccessibleDepth.pop();
    }

    /**
     * Needs to be called just before entering a {} block
     */
    private void enterBlock() {
        varRegisters.add(new HashMap<>());
    }

    /**
     * Needs to be called just after leaving a {} block
     */
    private void exitBlock() {
        varRegisters.removeLast();
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
        Program p = new Program();
        p.addInstructions(visit(ctx.getChild(1)));
        p.addInstruction(new UALi(UALi.Op.XOR, nextRegister + 1, nextRegister, 1));
        nextRegister += 2;
        return p;
    }

    @Override
    public Program visitComparison(grammarTCLParser.ComparisonContext ctx) {
        // expr op=(SUP | INF | SUPEQ | INFEQ) expr # comparison
        Program p = new Program();
        p.addInstructions(visit(ctx.getChild(0)));
        p.addInstructions(visit(ctx.getChild(2)));
        // Get value of Child(0) in nextRegister - 2
        p.addInstructions(assignRegister(nextRegister, nextRegister - 1));
        nextRegister++;
        // Get value of Child(2) in nextRegister - 1
        p.addInstructions(assignRegister(nextRegister, nextRegister - 2));
        nextRegister++;
        switch (ctx.getChild(1).getText()) {
            case ">" ->
                p.addInstruction(
                        new CondJump(CondJump.Op.JSUP, nextRegister - 2, nextRegister - 1, "Label" + nextLabel));
            case "<" ->
                p.addInstruction(
                        new CondJump(CondJump.Op.JINF, nextRegister - 2, nextRegister - 1, "Label" + nextLabel));
            case ">=" ->
                p.addInstruction(
                        new CondJump(CondJump.Op.JSEQ, nextRegister - 2, nextRegister - 1, "Label" + nextLabel));
            case "<=" ->
                p.addInstruction(
                        new CondJump(CondJump.Op.JIEQ, nextRegister - 2, nextRegister - 1, "Label" + nextLabel));
        }
        // Return 0 if Child(0) !comparison Child(2), then JMP to end of comparison
        p.addInstruction(new UAL(UAL.Op.XOR, nextRegister, nextRegister, nextRegister));
        p.addInstruction(new JumpCall(JumpCall.Op.JMP, "Label" + nextLabel + 1));
        // Return 1 if Child(0) comparison Child(2), then JMP to end of comparison
        UAL asm = new UAL(UAL.Op.XOR, nextRegister, nextRegister, nextRegister);
        asm.setLabel("Label" + nextLabel);
        p.addInstruction(asm);
        p.addInstruction(new UALi(UALi.Op.ADD, nextRegister, nextRegister, 1));
        p.addInstruction(new JumpCall(JumpCall.Op.JMP, "Label" + nextLabel + 1));
        nextLabel++;
        // Line to JMP to when comparison is finished
        // This line doesn't do anything but is needed to JMP to the end of comparison
        // It explains why we still use the same register and why we increment its value
        // only after
        UALi asmi = new UALi(UALi.Op.ADD, nextRegister, nextRegister, 0);
        asmi.setLabel("Label" + nextLabel);
        p.addInstruction(asmi);
        nextRegister++;
        nextLabel++;
        return p;
    }

    @Override
    public Program visitOr(grammarTCLParser.OrContext ctx) {
        // expr OR expr # or
        // OR : '||';
        Program p = new Program();
        p.addInstructions(visit(ctx.getChild(0)));
        int valueRegister = nextRegister - 1;
        p.addInstructions(visit(ctx.getChild(2)));
        p.addInstruction(new UAL(UAL.Op.OR, nextRegister, valueRegister, nextRegister - 1));
        nextRegister++;
        return p;
    }

    @Override
    public Program visitOpposite(grammarTCLParser.OppositeContext ctx) {
        Program p = new Program();
        p.addInstructions(visit(ctx.getChild(1)));
        p.addInstruction(new UALi(UALi.Op.XOR, nextRegister, nextRegister - 1, 0xFFFFFFFF));
        p.addInstruction(new UALi(UALi.Op.SUB, nextRegister, nextRegister, 1));
        nextRegister++;
        return p;
    }

    @Override
    public Program visitInteger(grammarTCLParser.IntegerContext ctx) {
        // INT # integer
        // INT : '-'?[0-9]+
        Program p = new Program();
        int value = Integer.parseInt(ctx.INT().getText());
        p.addInstructions(assignRegister(nextRegister, value));
        nextRegister++;
        return p;
    }

    @Override
    public Program visitTab_access(grammarTCLParser.Tab_accessContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitTab_access'");
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
            program.addInstructions(stackRegister(nextRegister-1)); // stack argument
            nextRegister++;
        }
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, ctx.getChild(0).getText())); // call the function

        return program;
    }

    @Override
    public Program visitBoolean(grammarTCLParser.BooleanContext ctx) {
        // BOOL # boolean
        // BOOL : 'true' | 'false'
        Program p = new Program();
        switch (ctx.getChild(0).getText()) {
            case "true" -> p.addInstructions(assignRegister(nextRegister, 1));
            case "false" -> p.addInstruction(new UAL(UAL.Op.XOR, nextRegister, nextRegister, nextRegister));
        }
        nextRegister++;
        return p;
    }

    @Override
    public Program visitAnd(grammarTCLParser.AndContext ctx) {
        // expr AND expr # and
        // AND : '&&'
        Program p = new Program();
        p.addInstructions(visit(ctx.getChild(0)));
        int valueRegister = nextRegister - 1;
        p.addInstructions(visit(ctx.getChild(2)));
        p.addInstruction(new UAL(UAL.Op.AND, nextRegister, valueRegister, nextRegister - 1));
        nextRegister++;
        return p;
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
        int varRegister = this.getVarRegister(ctx.getChild(0).getText()); // the register in which VAR is

        program.addInstruction(new UALi(UALi.Op.ADD, nextRegister, varRegister, 0)); // return the variable in R(nextRegister-1)
        nextRegister++;

        return program;
    }

    @Override
    public Program visitMultiplication(grammarTCLParser.MultiplicationContext ctx) {
        // expr op=(MUL | DIV | MODULO) expr # multiplication
        Program p = new Program();
        p.addInstructions(visit(ctx.getChild(0)));
        int valueRegister = nextRegister - 1;
        p.addInstructions(visit(ctx.getChild(2)));
        switch (ctx.getChild(1).getText()) {
            case "*" -> p.addInstruction(new UAL(UAL.Op.MUL, nextRegister, valueRegister, nextRegister - 1));
            case "/" -> p.addInstruction(new UAL(UAL.Op.DIV, nextRegister, nextRegister - 1, valueRegister));
            case "%" -> p.addInstruction(new UAL(UAL.Op.MOD, nextRegister, nextRegister - 1, valueRegister));
        }
        nextRegister++;
        return p;
    }

    @Override
    public Program visitEquality(grammarTCLParser.EqualityContext ctx) {
        // expr op=(EQUALS | DIFF) expr # equality
        // EQUALS : '==';
        // DIFF : '!=';
        Program p = new Program();
        p.addInstructions(visit(ctx.getChild(0)));
        p.addInstructions(visit(ctx.getChild(2)));
        // Get value of Child(0) in nextRegister - 2
        p.addInstructions(assignRegister(nextRegister, nextRegister - 1));
        nextRegister++;
        // Get value of Child(2) in nextRegister - 1
        p.addInstructions(assignRegister(nextRegister, nextRegister - 2));
        nextRegister++;
        switch (ctx.getChild(1).getText()) {
            case "==" ->
                p.addInstruction(
                        new CondJump(CondJump.Op.JEQU, nextRegister - 2, nextRegister - 1, "Label" + nextLabel));
            case "!=" ->
                p.addInstruction(
                        new CondJump(CondJump.Op.JNEQ, nextRegister - 2, nextRegister - 1, "Label" + nextLabel));
        }
        // Return 0 if Child(0) !equality Child(2), then JMP to end of equality
        p.addInstruction(new UAL(UAL.Op.XOR, nextRegister, nextRegister, nextRegister));
        p.addInstruction(new JumpCall(JumpCall.Op.JMP, "Label" + nextLabel + 1));
        // Return 1 if Child(0) equality Child(2), then JMP to end of equality
        UAL asm = new UAL(UAL.Op.XOR, nextRegister, nextRegister, nextRegister);
        asm.setLabel("Label" + nextLabel);
        p.addInstruction(asm);
        p.addInstruction(new UALi(UALi.Op.ADD, nextRegister, nextRegister, 1));
        p.addInstruction(new JumpCall(JumpCall.Op.JMP, "Label" + nextLabel + 1));
        nextLabel++;
        // Line to JMP to when equality is finished
        // This line doesn't do anything but is needed to JMP to the end of equality
        // It explains why we still use the same register and why we increment its value
        // only after
        UALi asmi = new UALi(UALi.Op.ADD, nextRegister, nextRegister, 0);
        asmi.setLabel("Label" + nextLabel);
        p.addInstruction(asmi);
        nextRegister++;
        nextLabel++;
        return p;
    }

    @Override
    public Program visitTab_initialization(grammarTCLParser.Tab_initializationContext ctx) {
        int varCount = (ctx.getChildCount() - 2) / 2 + 1;
        Program p = new Program();
        // registre contenant la longueur du tableau
        int lengthRegister = nextRegister;
        nextRegister++;
        // registre contenant le pointeur sur le tableau (mutable)
        int pointerRegister = nextRegister;
        nextRegister++;
        // registre contenant le pointeur sur le tableau (valeur retournée)
        int fixedPointerRegister = nextRegister;
        nextRegister++;
        // lengthRegister := varCount
        p.addInstructions(assignRegister(lengthRegister, varCount));
        
        // pointerRegister := TP
        p.addInstruction(new Mem(src.Asm.Mem.Op.LD, pointerRegister, TP));
        // fixedPointerRegister := pointerRegister
        p.addInstruction(new UALi(src.Asm.UALi.Op.ADD, fixedPointerRegister, pointerRegister, 0));

        // on empile la longueur du tableau
        p.addInstruction(new Mem(Mem.Op.ST, lengthRegister, pointerRegister));
        p.addInstruction(new UALi(src.Asm.UALi.Op.ADD, pointerRegister, pointerRegister, 1));
        
        // le TP pointe maintenant à la fin du tableau
        p.addInstruction(new UALi(UALi.Op.ADD, TP, TP, 12));
        for (int i = 0; i < varCount; i++) {
            if (i % 10 == 0 && i > 0) {
                // on ajoute le pointeur sur la suite à la fin du tableau
                p.addInstruction(new Mem(src.Asm.Mem.Op.ST, TP, pointerRegister));
                // on bouge le pointeur au début du prochain tableau
                p.addInstruction(new Mem(src.Asm.Mem.Op.LD, pointerRegister, TP));
                // on met à jour le prochain espace vide pour les tableaux
                p.addInstruction(new UALi(UALi.Op.ADD, TP, TP, 11));
            }
            // on a la valeur dans nextRegister - 1
            p.addInstructions(visit(ctx.getChild(2 * i + 1)));
            // on la rajoute dans le tableau
            p.addInstruction(new Mem(Mem.Op.ST, nextRegister - 1, pointerRegister));
            // on pointe sur la case suivante
            p.addInstruction(new UALi(UALi.Op.ADD, pointerRegister, pointerRegister, 1));
        }
        // nextRegister := fixedPointerRegister
        p.addInstruction(new UALi(src.Asm.UALi.Op.ADD, nextRegister, fixedPointerRegister, 0));
        nextRegister++;
        return p;
    }

    @Override
    public Program visitAddition(grammarTCLParser.AdditionContext ctx) {
        // expr op=(ADD | SUB) expr # addition
        Program p = new Program();
        p.addInstructions(visit(ctx.getChild(0)));
        int valueRegister = nextRegister - 1;
        p.addInstructions(visit(ctx.getChild(2)));
        switch (ctx.getChild(1).getText()) {
            case "+" -> p.addInstruction(new UAL(UAL.Op.ADD, nextRegister, valueRegister, nextRegister - 1));
            case "-" -> p.addInstruction(new UAL(UAL.Op.SUB, nextRegister, nextRegister - 1, valueRegister));
        }
        nextRegister++;
        return p;
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

    @Override
    public Program visitDeclaration(grammarTCLParser.DeclarationContext ctx) {
        Program p = new Program();
        int nbChilds = ctx.getChildCount();
        assignVarRegister(ctx.getChild(1).getText(), nextRegister);
        if (nbChilds == 5) {
            p.addInstructions(visit(ctx.getChild(3)));
            // nextRegister := nextRegister - 1
            p.addInstruction(new UALi(src.Asm.UALi.Op.ADD, nextRegister, nextRegister - 1, 0));
        }
        nextRegister++;
        return p;
    }

    @Override
    public Program visitPrint(grammarTCLParser.PrintContext ctx) {
        UnknownType variable = new UnknownType(ctx.VAR());
        Type varType = types.get(variable);
        int arrayDepth = getArrayDepth(varType);
        Program p = new Program();
        int varRegister = getVarRegister(ctx.VAR().getText());
        if (arrayDepth == 0) {
            // on affiche la variable de type primitif
            p.addInstruction(new IO(IO.Op.PRINT, varRegister));
        } else {
            int depthRegister = this.nextRegister;
            this.nextRegister++;
            // on empile le tableau puis sa profondeur
            p.addInstructions(stackRegister(varRegister));
            p.addInstructions(assignRegister(depthRegister, arrayDepth));
            p.addInstructions(stackRegister(depthRegister));
            // on appelle la fonction print_tab
            p.addInstruction(new JumpCall(src.Asm.JumpCall.Op.CALL, "print_tab"));
        }
        return p;
    }

    @Override
    public Program visitAssignment(grammarTCLParser.AssignmentContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitAssignment'");
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

        this.enterBlock(); // '{'
        for (int i = 0; i < nbInstr; i++) { // instr+
            program.addInstructions(visit(ctx.getChild(i + 1)));
        }
        this.exitBlock(); // '}'

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
        program.addInstruction(new UAL(UAL.Op.XOR, nextRegister, nextRegister, nextRegister)); // set new register to 0 for later test
        nextRegister++;
        program.addInstruction(new CondJump(CondJump.Op.JNEQ, nextRegister-2, nextRegister-1, labelIf)); // test if condition is true (!=0) => jump if
        if (ctx.getChildCount() == 7) { // if there is an else
            this.enterBlock(); // {
            program.addInstructions(visit(ctx.getChild(7))); // else instructions
            this.exitBlock(); // }
        }
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, labelEnd)); // JMP end

        Program ifInstrProgram = new Program();
        this.enterBlock(); // {
        ifInstrProgram.addInstructions(visit(ctx.getChild(5))); // if instructions
        this.exitBlock(); // }
        ifInstrProgram.getInstructions().getFirst().setLabel(labelIf);
        program.addInstructions(ifInstrProgram);

        Program endIfProgram = new Program();
        endIfProgram.addInstruction(new UALi(UALi.Op.ADD, nextRegister-1, nextRegister-1, 0)); // dummy instruction to set end if label
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
        program.addInstruction(new UAL(UAL.Op.XOR, nextRegister, nextRegister, nextRegister)); // set new register to 0 for later test
        nextRegister++;
        program.addInstruction(new CondJump(CondJump.Op.JEQU, nextRegister-2, nextRegister-1, labelEndLoop)); // test if condition is false => stop looping
        this.enterBlock(); // start of the loop {
        program.addInstructions(visit(ctx.getChild(4))); // instructions inside the loop
        this.exitBlock(); // } end of the loop
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, labelStartLoop)); // go back to the start of the loop

        Program endLoopProgram = new Program();
        endLoopProgram.addInstruction(new UALi(UALi.Op.ADD, nextRegister-1, nextRegister-1, 0)); // dummy instruction to set end loop label
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

        this.enterBlock(); // needed because the for can declare variables locally (for (int i = 0;...)
        program.addInstructions(visit(ctx.getChild(2))); // initialization
        Program condLoopProgram = visit(ctx.getChild(4)); // loop condition
        condLoopProgram.getInstructions().getFirst().setLabel(labelStartLoop); // set looping label
        program.addInstructions(condLoopProgram);
        program.addInstruction(new UAL(UAL.Op.XOR, nextRegister, nextRegister, nextRegister)); // set new register to 0 for later test
        nextRegister++;
        program.addInstruction(new CondJump(CondJump.Op.JEQU, nextRegister-2, nextRegister-1, labelEndLoop)); // test if condition is false => stop looping
        this.enterBlock(); // start of the loop {
        program.addInstructions(visit(ctx.getChild(8))); // instructions inside the loop
        this.exitBlock(); // } end of the loop
        program.addInstructions(visit(ctx.getChild(6))); // iteration
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, labelStartLoop)); // go back to the start of the loop

        Program endLoopProgram = new Program();
        endLoopProgram.addInstruction(new UALi(UALi.Op.ADD, nextRegister-1, nextRegister-1, 0)); // dummy instruction to set end loop label
        endLoopProgram.getInstructions().getFirst().setLabel(labelEndLoop);
        program.addInstructions(endLoopProgram);
        this.exitBlock();

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

        program.addInstructions(visit(ctx.getChild(1))); // expr, return value will be in R(nextRegister-1)
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, this.functionsEndLabels.getFirst())); // return;

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
        String endLabel = this.getLabel();
        int nbInstructions = ctx.getChildCount() - 5;

        this.functionsEndLabels.add(endLabel); // every subsequent return; instruction will jump to the end of the function
        for (int i = 0; i < nbInstructions; i++) { // instr*
            program.addInstructions(visit(ctx.getChild(i + 1)));
        }
        program.addInstructions(visit(ctx.getChild(ctx.getChildCount() - 3))); // expr, return value will be in R(nextRegister-1)
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, this.functionsEndLabels.getFirst())); // return;
        this.functionsEndLabels.pop(); // since there is no more instructions there is no more return; to be found

        Program endFunctionProgram = new Program();
        endFunctionProgram.addInstruction(new UALi(UALi.Op.ADD, nextRegister-1, nextRegister-1, 0)); // dummy instruction to set end function label
        endFunctionProgram.getInstructions().getFirst().setLabel(endLabel);
        program.addInstructions(endFunctionProgram);

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

        this.enterFunction();
        for (int i = 0; i < nbArguments; i++) { // the arguments are stacked before the call so we unstack them
            program.addInstructions(this.unstackRegister(nextRegister));
            nextRegister++;
            this.assignVarRegister(ctx.getChild((3*i)+5).getText(), nextRegister-1); // arguments counts as new definitions of variables
        }
        program.getInstructions().getFirst().setLabel(ctx.getChild(1).toString()); // function label
        program.addInstructions(visit(ctx.getChild(nbChilds - 1))); // core_fct
        this.exitFunction();

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

        program.addInstructions(getPrintProgram());

        this.enterBlock(); // a first enterBlock is needed for the whole program to work
        program.addInstructions(this.assignRegister(SP, 0)); // initialize SP
        program.addInstructions(this.assignRegister(TP, 4096)); // initialize TP
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, "main")); // call main
        for (int i = 0; i < nbChilds - 3; i++) { // decl_fct*
            program.addInstructions(visit(ctx.getChild(i)));
        }

        this.enterFunction(); // 'main' function declaration
        Program mainCoreProgram = visit(ctx.getChild(nbChilds - 2)); // core_fct
        mainCoreProgram.getInstructions().getFirst().setLabel("main"); // main label
        program.addInstructions(mainCoreProgram);
        program.addInstruction(new Stop()); // STOP
        this.exitFunction();
        this.exitBlock();

        return program;
    }

    private Program getPrintProgram() {
        final int SQUARE_BRACKET_OPEN = 91;
        final int SQUARE_BRACKET_CLOSE = 93;
        final int SPACE = 32;

        Program p = new Program();


        p.addInstruction(new UALi(src.Asm.UALi.Op.SUB, SP, SP, 1));       
        p.getInstructions().getFirst().setLabel("print_tab");

        return p;
    }
}
