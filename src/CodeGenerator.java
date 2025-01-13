package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import src.Asm.*;
import src.Type.ArrayType;
import src.Type.Type;
import src.Type.UnknownType;

public class CodeGenerator  extends AbstractParseTreeVisitor<Program> implements grammarTCLVisitor<Program> {
    private Map<UnknownType,Type> types;
    private Integer nextRegister;
    private Integer nextLabel;
    private final Integer SP = 0;

    private ArrayList<HashMap<UnknownType, Integer>> variables;

  /**
   * Constructeur
   *
   * @param types types de chaque variable du code source
   */
  public CodeGenerator(Map<UnknownType, Type> types) {
    this.types = types;
    this.nextRegister = types.size() + 1;
    this.nextLabel = 1;
    this.variables = new ArrayList<>();
  }

  private static int getArrayDepth(Type type) {
    if (type instanceof ArrayType array)
      return 1 + getArrayDepth(array.getTabType());
    return 0;
  }

    private Program stackRegister(int register) {
        return null;
    }

    private Program unstackRegister(int register) {
        return null;
    }

    private int getVarRegister(String varName) {
        return 0;
    }

    private String getLabel() {
        this.nextLabel++;
        return "Label" + (this.nextLabel-1);
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
        // expr op=(SUP | INF | SUPEQ | INFEQ) expr	    # comparison
        Program p = new Program();
        p.addInstructions(visit(ctx.getChild(0)));
        int valueRegister = nextRegister - 1;
        p.addInstructions(visit(ctx.getChild(2)));
        // Get value of Child(0) in nextRegister - 2
        p.addInstruction(new UAL(UAL.Op.XOR, nextRegister, nextRegister, nextRegister));
        p.addInstruction(new UALi(UALi.Op.ADD, nextRegister, nextRegister, valueRegister));
        nextRegister++;
        // Get value of Child(2) in nextRegister - 1
        p.addInstruction(new UAL(UAL.Op.XOR, nextRegister, nextRegister, nextRegister));
        p.addInstruction(new UALi(UALi.Op.ADD, nextRegister, nextRegister, nextRegister - 2));
        nextRegister++;
        switch (ctx.getChild(1).getText()) {
            case ">" ->
                p.addInstruction(new CondJump(CondJump.Op.JSUP, nextRegister - 2, nextRegister - 1, "Label" + nextLabel));
            case "<" ->
                p.addInstruction(new CondJump(CondJump.Op.JINF, nextRegister - 2, nextRegister - 1, "Label" + nextLabel));
            case ">=" ->
                p.addInstruction(new CondJump(CondJump.Op.JSEQ, nextRegister - 2, nextRegister - 1, "Label" + nextLabel));
            case "<=" ->
                p.addInstruction(new CondJump(CondJump.Op.JIEQ, nextRegister - 2, nextRegister - 1, "Label" + nextLabel));
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
        // It explains why we still use the same register and why we increment its value only after
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
        p.addInstruction(new UAL(UAL.Op.XOR, nextRegister, nextRegister, nextRegister));
        p.addInstruction(
                new UALi(UALi.Op.ADD, nextRegister, nextRegister, Integer.parseInt(ctx.getChild(0).getText())));
        nextRegister++;
        return p;
    }

    @Override
    public Program visitTab_access(grammarTCLParser.Tab_accessContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitTab_access'");
    }

    @Override
    public Program visitBrackets(grammarTCLParser.BracketsContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitBrackets'");
    }

    @Override
    public Program visitCall(grammarTCLParser.CallContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitCall'");
    }

    @Override
    public Program visitBoolean(grammarTCLParser.BooleanContext ctx) {
        // BOOL # boolean
        // BOOL : 'true' | 'false'
        Program p = new Program();
        switch (ctx.getChild(0).getText()) {
            case "true" -> {
                p.addInstruction(new UAL(UAL.Op.XOR, nextRegister, nextRegister, nextRegister));
                p.addInstruction(new UALi(UALi.Op.ADD, nextRegister, nextRegister, 1));
            }
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

    @Override
    public Program visitVariable(grammarTCLParser.VariableContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitVariable'");
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
        int valueRegister = nextRegister - 1;
        p.addInstructions(visit(ctx.getChild(2)));
        // Get value of Child(0) in nextRegister - 2
        p.addInstruction(new UAL(UAL.Op.XOR, nextRegister, nextRegister, nextRegister));
        p.addInstruction(new UALi(UALi.Op.ADD, nextRegister, nextRegister, valueRegister));
        nextRegister++;
        // Get value of Child(2) in nextRegister - 1
        p.addInstruction(new UAL(UAL.Op.XOR, nextRegister, nextRegister, nextRegister));
        p.addInstruction(new UALi(UALi.Op.ADD, nextRegister, nextRegister, nextRegister - 2));
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitTab_initialization'");
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

    @Override
    public Program visitBase_type(grammarTCLParser.Base_typeContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitBase_type'");
    }

    @Override
    public Program visitTab_type(grammarTCLParser.Tab_typeContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitTab_type'");
    }

    @Override
    public Program visitDeclaration(grammarTCLParser.DeclarationContext ctx) {
        Program p = new Program();
        int nbChilds = ctx.getChildCount();
        variables.getLast().put(ctx.getChild(1).getText(), nextRegister);
        if (nbChilds == 5) {
            visit(ctx.getChild(3));
            p.addInstruction(new UAL(UAL.Op.ADD, nextRegister, nextRegister, nextRegister));
            p.addInstruction(new UAL(UAL.Op.ADD, nextRegister, nextRegister, nextRegister - 1));
        }
        nextRegister++;
        return p;
    }

    private static int getArrayDepth(Type type) {
        if (type instanceof ArrayType array)
            return 1 + getArrayDepth(array.getTabType());
        return 0;
    }

    @Override
    public Program visitPrint(grammarTCLParser.PrintContext ctx) {
        UnknownType variable = new UnknownType(ctx.VAR());
        Type varType = types.get(variable);
        int arrayDepth = getArrayDepth(varType);
        Program p = new Program();
        int varRegister = variable.getVarIndex();
        if (arrayDepth == 0) {
            // on affiche la variable de type primitif
            p.addInstruction(new IO(IO.Op.PRINT, varRegister));
        } else {
            int depthRegister = this.nextRegister;
            // on empile le tableau puis sa profondeur
            p.addInstruction(new Mem(src.Asm.Mem.Op.ST, varRegister, SP));
            p.addInstruction(new UALi(src.Asm.UALi.Op.ADD, SP, SP, 1));
            p.addInstruction(new UAL(src.Asm.UAL.Op.XOR, depthRegister, depthRegister, depthRegister));
            p.addInstruction(new UALi(src.Asm.UALi.Op.ADD, depthRegister, depthRegister, arrayDepth));
            p.addInstruction(new Mem(src.Asm.Mem.Op.ST, depthRegister, SP));
            p.addInstruction(new UALi(src.Asm.UALi.Op.ADD, SP, SP, 1));
            // on appelle la fonction print_tab
            p.addInstruction(new JumpCall(src.Asm.JumpCall.Op.CALL, "print_tab"));
            this.nextRegister++;
        }
        return p;
    }

    @Override
    public Program visitAssignment(grammarTCLParser.AssignmentContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitAssignment'");
    }

    @Override
    public Program visitBlock(grammarTCLParser.BlockContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitBlock'");
    }

    @Override
    public Program visitIf(grammarTCLParser.IfContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitIf'");
    }

    @Override
    public Program visitWhile(grammarTCLParser.WhileContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitWhile'");
    }

    @Override
    public Program visitFor(grammarTCLParser.ForContext ctx) {
        //FOR '(' instr ',' expr ',' instr ')' instr
        /* pseudo-assembler for(int i = 0; i < 10; i++)
            ST R0 1
        loop: visit(expr)
            XOR R1 R1 R1
            JEQU nextRegister-2 nextRegister-1 end_loop //-2 = cond return, -1 = XOR'd register
            instr
            ADDi R0 R0 1
            JMP loop
        end_loop: suite...
        */
        Program program = new Program();
        String labelStartLoop = this.getLabel();
        String labelEndLoop = this.getLabel();
        program.addInstructions(visit(ctx.getChild(2))); //initialization
        Program condLoopProgram = visit(ctx.getChild(4)); //loop condition
        condLoopProgram.getInstructions().getFirst().setLabel(labelStartLoop); //set looping label
        program.addInstructions(condLoopProgram);
        program.addInstruction(new UAL(UAL.Op.XOR, nextRegister, nextRegister, nextRegister)); //set new register to 0 for later test
        nextRegister++;
        program.addInstruction(new CondJump(CondJump.Op.JEQU, nextRegister-2, nextRegister-1, labelEndLoop)); //test if condition is false => stop looping
        program.addInstructions(visit(ctx.getChild(8))); //instructions inside the loop
        program.addInstructions(visit(ctx.getChild(6))); //iteration
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, labelStartLoop)); //go back to the start of the loop
        Program endLoopProgram = new Program();
        endLoopProgram.addInstruction(new UALi(UALi.Op.ADD, nextRegister, nextRegister, 0)); //dummy instruction to set end loop label
        endLoopProgram.getInstructions().getFirst().setLabel(labelEndLoop);
        program.addInstructions(endLoopProgram);
        return program;
    }

    @Override
    public Program visitReturn(grammarTCLParser.ReturnContext ctx) {
        //RETURN expr SEMICOL
        Program program = new Program();
        program.addInstructions(visit(ctx.getChild(1))); //expr, return value will be in R(nextRegister-1)
        return program;
    }

    @Override
    public Program visitCore_fct(grammarTCLParser.Core_fctContext ctx) {
        //core_fct: '{' instr* RETURN expr SEMICOL '}';
        Program program = new Program();
        int nbInstructions = ctx.getChildCount() - 5;
        for (int i = 0; i < nbInstructions; i++) { //instr*
            program.addInstructions(visit(ctx.getChild(i + 1)));
        }
        program.addInstructions(visit(ctx.getChild(ctx.getChildCount() - 3))); //expr
        return program;
    }

    @Override
    public Program visitDecl_fct(grammarTCLParser.Decl_fctContext ctx) {
        //decl_fct: type VAR '(' (type VAR (',' type VAR)*)? ')' core_fct;
        Program program = new Program();
        int nbChilds = ctx.getChildCount();
        int nbArguments = ((nbChilds - 5) == 0 ? 0 : (nbChilds - 4)/2);
        for(int i = 0; i < nbArguments; i++) { //the arguments are stacked before the call so we unstack them
            program.addInstruction(new Mem(Mem.Op.LD, nextRegister, SP));
            nextRegister++;
            program.addInstruction(new UALi(UALi.Op.SUB, SP, SP, 1));
        }
        program.getInstructions().getFirst().setLabel(ctx.getChild(2).toString()); //function label
        program.addInstructions(visit(ctx.getChild(nbChilds - 1))); //core_fct
        return program;
    }

    @Override
    public Program visitMain(grammarTCLParser.MainContext ctx) {
        //main: decl_fct* 'int main()' core_fct EOF
        Program program = new Program();
        program.addInstruction(new UAL(UAL.Op.XOR, 0, 0, 0)); //initialize SP
        program.addInstruction(new JumpCall(JumpCall.Op.JMP, "main")); //call main
        int nbChilds = ctx.getChildCount();
        for(int i = 0; i < nbChilds - 3; i++) { //decl_fct*
            program.addInstructions(visit(ctx.getChild(i)));
        }
        Program mainCoreProgram = visit(ctx.getChild(nbChilds - 2)); //core_fct
        mainCoreProgram.getInstructions().getFirst().setLabel("main"); //main label
        program.addInstructions(mainCoreProgram);
        program.addInstruction(new Stop()); //STOP
        return program;
    }
}
