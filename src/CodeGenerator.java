package src;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import src.Asm.*;
import src.Type.Type;
import src.Type.UnknownType;

public class CodeGenerator  extends AbstractParseTreeVisitor<Program> implements grammarTCLVisitor<Program> {
    private Map<UnknownType,Type> types;
    private Integer nextRegister;
    private final Integer SP = 0;

    /**
     * Constructeur
     * @param types types de chaque variable du code source
     */
    public CodeGenerator(Map<UnknownType, Type> types) {
        this.types = types;
        this.nextRegister = 1;
    }

    @Override
    public Program visitNegation(grammarTCLParser.NegationContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitNegation'");
    }

    @Override
    public Program visitComparison(grammarTCLParser.ComparisonContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitComparison'");
    }

    @Override
    public Program visitOr(grammarTCLParser.OrContext ctx) {
        Program p = new Program();
        p.addInstructions(visit(ctx.getChild(0)));
        p.addInstructions(visit(ctx.getChild(2)));
        p.addInstruction(new UAL(UAL.Op.OR, nextRegister, nextRegister-2, nextRegister-1));
        nextRegister++;
        return p;
    }

    @Override
    public Program visitOpposite(grammarTCLParser.OppositeContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitOpposite'");
    }

    @Override
    public Program visitInteger(grammarTCLParser.IntegerContext ctx) {
        Program p = new Program();
        p.addInstruction(new UAL(UAL.Op.XOR, nextRegister, nextRegister, nextRegister));
        p.addInstruction(new UALi(UALi.Op.ADD, nextRegister, nextRegister, Integer.parseInt(ctx.getChild(0).getText())));
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
        Program p = new Program();
        p.addInstructions(visit(ctx.getChild(0)));
        p.addInstructions(visit(ctx.getChild(2)));
        p.addInstruction(new UAL(UAL.Op.AND, nextRegister, nextRegister-2, nextRegister-1));
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
        Program p = new Program();
        p.addInstructions(visit(ctx.getChild(0)));
        p.addInstructions(visit(ctx.getChild(2)));
        p.addInstruction(new Mem(Mem.Op.LD, nextRegister, SP));
        nextRegister++;
        p.addInstruction(new UALi(UALi.Op.SUB, SP, SP, 1));
        p.addInstruction(new Mem(Mem.Op.LD, nextRegister, SP));
        nextRegister++;
        switch (ctx.getChild(1).getText()) {
            case "*" -> p.addInstruction(new UAL(UAL.Op.MUL, nextRegister, nextRegister -2, nextRegister -1));
            case "/" -> p.addInstruction(new UAL(UAL.Op.DIV, nextRegister, nextRegister -1, nextRegister -2));
            case "%" -> p.addInstruction(new UAL(UAL.Op.MOD, nextRegister, nextRegister -1, nextRegister -2));
        }
        p.addInstruction(new Mem(Mem.Op.ST, nextRegister, SP));
        nextRegister++;
        return p;
    }

    @Override
    public Program visitEquality(grammarTCLParser.EqualityContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitEquality'");
    }

    @Override
    public Program visitTab_initialization(grammarTCLParser.Tab_initializationContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitTab_initialization'");
    }

    @Override
    public Program visitAddition(grammarTCLParser.AdditionContext ctx) {
        Program p = new Program();
        p.addInstructions(visit(ctx.getChild(0)));
        p.addInstructions(visit(ctx.getChild(2)));
        p.addInstruction(new Mem(Mem.Op.LD, nextRegister, SP));
        nextRegister++;
        p.addInstruction(new UALi(UALi.Op.SUB, SP, SP, 1));
        p.addInstruction(new Mem(Mem.Op.LD, nextRegister, SP));
        nextRegister++;
        switch (ctx.getChild(1).getText()) {
            case "+" -> p.addInstruction(new UAL(UAL.Op.ADD, nextRegister, nextRegister -2, nextRegister -1));
            case "-" -> p.addInstruction(new UAL(UAL.Op.SUB, nextRegister, nextRegister -1, nextRegister -2));
        }
        p.addInstruction(new Mem(Mem.Op.ST, nextRegister, SP));
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitDeclaration'");
    }

    @Override
    public Program visitPrint(grammarTCLParser.PrintContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitPrint'");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitFor'");
    }

    @Override
    public Program visitReturn(grammarTCLParser.ReturnContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitReturn'");
    }

    @Override
    public Program visitCore_fct(grammarTCLParser.Core_fctContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitCore_fct'");
    }

    @Override
    public Program visitDecl_fct(grammarTCLParser.Decl_fctContext ctx) {
        //decl_fct: type VAR '(' (type VAR (',' type VAR)*)? ')' core_fct;
        Program fonction = new Program();
        int nbChilds = ctx.getChildCount();
        int nbArguments = ((nbChilds - 5) == 0 ? 0 : (nbChilds - 4)/2);
        for(int i = 0; i < nbArguments; i++) { //the arguments are stacked before the call so we unstack them
            Instruction depile = new Mem(Mem.Op.LD, nextRegister, SP);
            nextRegister++;
            if(i == 0) {
                depile.setLabel(ctx.getChild(2).toString()); //function label for later CALL
            }
            fonction.addInstruction(depile);
            fonction.addInstruction(new UALi(UALi.Op.SUB, SP, SP, 1));
        }
        fonction.addInstructions(visit(ctx.getChild(nbChilds - 1))); //core_fct
        return fonction;
    }

    @Override
    public Program visitMain(grammarTCLParser.MainContext ctx) {
        //main: decl_fct* 'int main()' core_fct EOF
        Program main = new Program();
        main.addInstruction(new UAL(UAL.Op.XOR, 0, 0, 0)); //initialize SP
        main.addInstruction(new JumpCall(JumpCall.Op.JMP, "main")); //call main
        int nbChilds = ctx.getChildCount();
        for(int i = 0; i < nbChilds - 3; i++) { //decl_fct*
            main.addInstructions(visit(ctx.getChild(i)));
        }
        Program inMain = visit(ctx.getChild(nbChilds - 2)); //core_fct
        inMain.getInstructions().getFirst().setLabel("main"); //main label
        main.addInstructions(inMain);
        main.addInstruction(new Stop()); //STOP
        return main;
    }
}
