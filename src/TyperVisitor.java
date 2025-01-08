package src;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

import org.antlr.v4.runtime.tree.ParseTree;
import src.Type.PrimitiveType;
import src.Type.Type;
import src.Type.UnknownType;

public class TyperVisitor extends AbstractParseTreeVisitor<Type> implements grammarTCLVisitor<Type> {

    private final Map<UnknownType,Type> types = new HashMap<>();

    public Map<UnknownType, Type> getTypes() {
        return types;
    }

    @Override
    public Type visitNegation(grammarTCLParser.NegationContext ctx) {
        // TODO Auto-generated method stub
        System.out.println("visit negation");
        ParseTree p1 = ctx.getChild(1);
        Type t = visit(p1);
        if (t instanceof PrimitiveType && ((PrimitiveType) t).getType() != Type.Base.BOOL) {
            throw new Error("Type error: negation of non-boolean");
        }
        return null;
    }

    @Override
    public Type visitComparison(grammarTCLParser.ComparisonContext ctx) {
        System.out.println("visit comparison");
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        if (t1 instanceof PrimitiveType && ((PrimitiveType) t1).getType() != Type.Base.INT) {
            throw new Error("Type error: multiplication with non-integer on the first operand");
        }
        if (t3 instanceof PrimitiveType && ((PrimitiveType) t3).getType() != Type.Base.INT) {
            throw new Error("Type error: multiplication with non-integer on the second operand");
        }
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitOr(grammarTCLParser.OrContext ctx) {
        System.out.println("visit or");
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        if (t1 instanceof PrimitiveType && ((PrimitiveType) t1).getType() != Type.Base.BOOL) {
            throw new Error("Type error: or with non-boolean on the first operand");
        }
        if (t3 instanceof PrimitiveType && ((PrimitiveType) t3).getType() != Type.Base.BOOL) {
            throw new Error("Type error: or with non-boolean on the second operand");
        }
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitOpposite(grammarTCLParser.OppositeContext ctx) {
        System.out.println("visit opposite");
        ParseTree p1 = ctx.getChild(1);
        Type t = visit(p1);
        if (t instanceof PrimitiveType && ((PrimitiveType) t).getType() != Type.Base.INT) {
            throw new Error("Type error: opposite of non-integer");
        }
        return null;
    }

    @Override
    public Type visitInteger(grammarTCLParser.IntegerContext ctx) {
        System.out.println("visit int");
        return new PrimitiveType(Type.Base.INT);
    }

    @Override
    public Type visitTab_access(grammarTCLParser.Tab_accessContext ctx) {
        System.out.println("visit tabAccess");
        ParseTree p0 = ctx.getChild(0);
        visit(p0);
        ParseTree p1 = ctx.getChild(2);
        visit(p1);
        return null;
    }

    @Override
    public Type visitBrackets(grammarTCLParser.BracketsContext ctx) {
        System.out.println("visit brackets");
        ParseTree p1 = ctx.getChild(1);
        return visit(p1);
    }

    @Override
    public Type visitCall(grammarTCLParser.CallContext ctx) {
        System.out.println("visit call");
        ParseTree p0 = ctx.getChild(0);
        visit(p0);
        int NbChildren = ctx.getChildCount();
        if(NbChildren != 3){
            ParseTree p1 = ctx.getChild(2);
            visit(p1);
            for(int i = 0; i < (NbChildren - 3 - 1)/2; i++){
                ParseTree p2 = ctx.getChild(4+2*i);
                visit(p2);
            }
        }
        return null;
    }

    @Override
    public Type visitBoolean(grammarTCLParser.BooleanContext ctx) {
        System.out.println("visit bool");
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitAnd(grammarTCLParser.AndContext ctx) {
        System.out.println("visit and");
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        if (t1 instanceof PrimitiveType && ((PrimitiveType) t1).getType() != Type.Base.BOOL) {
            throw new Error("Type error: and with non-boolean on the first operand");
        }
        if (t3 instanceof PrimitiveType && ((PrimitiveType) t3).getType() != Type.Base.BOOL) {
            throw new Error("Type error: and with non-boolean on the second operand");
        }
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitVariable(grammarTCLParser.VariableContext ctx) {
        System.out.println("visit unknown type");
        return new UnknownType();
    }

    @Override
    public Type visitMultiplication(grammarTCLParser.MultiplicationContext ctx) {
        System.out.println("Visit Multiplication");

        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        if (t1 instanceof PrimitiveType && ((PrimitiveType) t1).getType() != Type.Base.INT) {
            throw new Error("Type error: multiplication with non-integer on the first operand");
        }
        if (t3 instanceof PrimitiveType && ((PrimitiveType) t3).getType() != Type.Base.INT) {
            throw new Error("Type error: multiplication with non-integer on the second operand");
        }
        return new PrimitiveType(Type.Base.INT);
    }

    @Override
    public Type visitEquality(grammarTCLParser.EqualityContext ctx) {
        System.out.println("visit equality");
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        if (!t1.equals(t3)) {
            throw new Error("Type error: equality between different types");
        }
        UnknownType ut = new UnknownType();
        types.putAll(t1.unify(t3));
        //substituteAll()
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitTab_initialization(grammarTCLParser.Tab_initializationContext ctx) {
        System.out.println("visit tab initialization");
        if (ctx.getChildCount() > 2) {
            for (int i = 1; i < ctx.getChildCount() - 3; i += 2) {
                ParseTree p = ctx.getChild(i);
                Type t = visit(p);
                ParseTree p2 = ctx.getChild(i + 2);
                Type t2 = visit(p2);
                if (!t.equals(t2)) {
                    throw new Error("Type error: initialization of array with different types");
                }
            }
        }
        return null;
    }

    @Override
    public Type visitAddition(grammarTCLParser.AdditionContext ctx) {
        System.out.println("visit addition");
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        if (t1 instanceof PrimitiveType && ((PrimitiveType) t1).getType() != Type.Base.INT) {
            throw new Error("Type error: addition with non-integer on the first operand");
        }
        if (t3 instanceof PrimitiveType && ((PrimitiveType) t3).getType() != Type.Base.INT) {
            throw new Error("Type error: addition with non-integer on the second operand");
        }
        return new PrimitiveType(Type.Base.INT);
    }

    @Override
    public Type visitBase_type(grammarTCLParser.Base_typeContext ctx) {
        System.out.println("Visit base type");
        ParseTree p0 = ctx.getChild(0);
        if (!Objects.equals(p0.getText(), "int") && !Objects.equals(p0.getText(), "bool") && !Objects.equals(p0.getText(), "auto")) {
            throw new Error("sale merde");
        }
        switch (p0.getText()){
            case "int":
                return new PrimitiveType(Type.Base.INT);
            case "bool":
                return new PrimitiveType(Type.Base.BOOL);
            case "auto":
                return new UnknownType();
        }
        return null;
    }

    @Override
    public Type visitTab_type(grammarTCLParser.Tab_typeContext ctx) {
        System.out.println("Visit tab type");
        ParseTree p0 = ctx.getChild(0);
        visit(p0);
        return null;
    }

    @Override
    public Type visitDeclaration(grammarTCLParser.DeclarationContext ctx) { // pas sur
        System.out.println("visit declaration");
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        if (!(t1 instanceof PrimitiveType) && !(t1 instanceof UnknownType)) {
            throw new Error("Type error: declaration of variable with non-variable type");
        }
        ParseTree p2 = ctx.getChild(1);
        Type t2 = visit(p2);
        t1.unify(t2);
        if (ctx.getChildCount() == 5){
            ParseTree p3 = ctx.getChild(3);
            Type t3 = visit(p3);
            t2.unify(t3);
        }
        return null;
    }

    @Override
    public Type visitPrint(grammarTCLParser.PrintContext ctx) {
        System.out.println("visit print");
        ParseTree p1 = ctx.getChild(2);
        Type t = visit(p1);
        if (!(t instanceof UnknownType)) {
            throw new Error("Type error: print of non-variable");
        }
        return null;
    }

    @Override
    public Type visitAssignment(grammarTCLParser.AssignmentContext ctx) {
        System.out.println("visit assignment");
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p2 = ctx.getChild(5);
        Type t2 = visit(p2);
        if (!t1.equals(t2)) {
            throw new Error("Type error: assignment of different types");
        }
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        if (t3 instanceof PrimitiveType && ((PrimitiveType) t3).getType() != Type.Base.INT) {
            throw new Error("Type error: the address is not a integer");
        }
        return null;
    }

    @Override
    public Type visitBlock(grammarTCLParser.BlockContext ctx) {
        System.out.println("visit block");
        for (int i = 1; i < ctx.getChildCount() - 1; i++) {
            ParseTree p = ctx.getChild(i);
            visit(p);
        }
        return null;
    }

    @Override
    public Type visitIf(grammarTCLParser.IfContext ctx) {
        System.out.println("visit if");
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        if (t3 instanceof PrimitiveType && ((PrimitiveType) t3).getType() != Type.Base.BOOL) {
            throw new Error("Type error: if condition is not a boolean");
        }
        ParseTree p5 = ctx.getChild(4);
        visit(p5);
        if (ctx.getChildCount() == 7) {
            ParseTree p7 = ctx.getChild(6);
            visit(p7);
        }
        return null;
    }

    @Override
    public Type visitWhile(grammarTCLParser.WhileContext ctx) {
        System.out.println("visit while");
        ParseTree p1 = ctx.getChild(1);
        Type t1 = visit(p1);
        if (t1 instanceof PrimitiveType && ((PrimitiveType) t1).getType() != Type.Base.BOOL) {
            throw new Error("Type error: while condition is not a boolean");
        }
        ParseTree p3 = ctx.getChild(3);
        visit(p3);
        return null;
    }

    @Override
    public Type visitFor(grammarTCLParser.ForContext ctx) {
        System.out.println("visit for");
        ParseTree p1 = ctx.getChild(2);
        visit(p1);
        ParseTree p2 = ctx.getChild(3);
        Type t2 = visit(p2);
        if (t2 instanceof PrimitiveType && ((PrimitiveType) t2).getType() != Type.Base.BOOL) {
            throw new Error("Type error: for condition is not a boolean");
        }
        ParseTree p3 = ctx.getChild(5);
        visit(p3);
        ParseTree p4 = ctx.getChild(7);
        visit(p4);
        return null;
    }

    @Override
    public Type visitReturn(grammarTCLParser.ReturnContext ctx) {
        System.out.println("visit return");
        ParseTree p1 = ctx.getChild(1);
        visit(p1);
        return null;
    }

    @Override
    public Type visitCore_fct(grammarTCLParser.Core_fctContext ctx) {
        System.out.println("Visit core function");
        int nbChildrenWithoutInstr = 5; // '{' instr* RETURN expr SEMICOL '}';
        int nbChildren = ctx.getChildCount();
        for (int i = 0; i < nbChildren - nbChildrenWithoutInstr; i++){
            ParseTree p0 = ctx.getChild(1 + i);
            visit(p0);
        }
        ParseTree p1 = ctx.getChild(nbChildren - 3); // return is the 2nd last
        visit(p1);
        return null;
    }

    @Override
    public Type visitDecl_fct(grammarTCLParser.Decl_fctContext ctx) {
        System.out.println("Visit declare function");
        ParseTree p0 = ctx.getChild(0);
        visit(p0);
        ParseTree p1 = ctx.getChild(1);
        visit(p1);
        int i = 4;
        int j = ctx.getChildCount()-1;
        if(i == j){
            ParseTree p2 = ctx.getChild(i);
            visit(p2);
        } else {
            ParseTree p3 = ctx.getChild(3);
            ParseTree p4 = ctx.getChild(4);
            visit(p3);
            visit(p4);
            // number of parameters is 0 mod 3
            int nbVars = (j - i - 3)/3; // first 3 represent first arg + closing parenthesis
            for (int k = 0; k < nbVars; k++){
                ParseTree p5 = ctx.getChild(7+3*k);
                ParseTree p6 = ctx.getChild(8+3*k);
                visit(p5);
                visit(p6);
            }
        }
        return null;
    }

    @Override
    public Type visitMain(grammarTCLParser.MainContext ctx) {
        System.out.println("visit main");
        int childCount = ctx.getChildCount();
        // if only 3 children, it means no fun decl
        if(childCount == 3){
            ParseTree p0 = ctx.getChild(1);
            visit(p0);
        } else {
            // for each fun decl, it should be visited
            for (int i = 0; i < childCount - 3; i++){
                ParseTree p1 = ctx.getChild(i);
                visit(p1);
            }
        }
        return null;
    }
}
