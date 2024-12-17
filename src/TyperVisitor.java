package src;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

import org.antlr.v4.runtime.tree.ParseTree;
import src.Type.PrimitiveType;
import src.Type.Type;
import src.Type.UnknownType;

public class TyperVisitor extends AbstractParseTreeVisitor<Type> implements grammarTCLVisitor<Type> {

    private final Map<UnknownType,Type> types = new HashMap<UnknownType,Type>();

    public Map<UnknownType, Type> getTypes() {
        return types;
    }

    @Override
    public Type visitNegation(grammarTCLParser.NegationContext ctx) {
        // TODO Auto-generated method stub
        ParseTree p1 = ctx.getChild(1);
        Type t = visit(p1);
        if (t instanceof PrimitiveType && ((PrimitiveType) t).getType() != Type.Base.BOOL) {
            throw new Error("Type error: negation of non-boolean");
        }
        return null;
    }

    @Override
    public Type visitComparison(grammarTCLParser.ComparisonContext ctx) {
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        if (!t1.equals(t3)) {
            throw new Error("Type error: comparison between different types");
        }
        UnknownType ut = new UnknownType();
        types.putAll(t1.unify(t3));
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitOr(grammarTCLParser.OrContext ctx) {
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
        ParseTree p1 = ctx.getChild(1);
        Type t = visit(p1);
        if (t instanceof PrimitiveType && ((PrimitiveType) t).getType() != Type.Base.INT) {
            throw new Error("Type error: opposite of non-integer");
        }
        return null;
    }

    @Override
    public Type visitInteger(grammarTCLParser.IntegerContext ctx) {
        return new PrimitiveType(Type.Base.INT);
    }

    @Override
    public Type visitTab_access(grammarTCLParser.Tab_accessContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitTab_access'");
    }

    @Override
    public Type visitBrackets(grammarTCLParser.BracketsContext ctx) {
        ParseTree p1 = ctx.getChild(1);
        return visit(p1);
    }

    @Override
    public Type visitCall(grammarTCLParser.CallContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitCall'");
    }

    @Override
    public Type visitBoolean(grammarTCLParser.BooleanContext ctx) {
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitAnd(grammarTCLParser.AndContext ctx) {
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
        return new UnknownType();
    }

    @Override
    public Type visitMultiplication(grammarTCLParser.MultiplicationContext ctx) {
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
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        if (!t1.equals(t3)) {
            throw new Error("Type error: equality between different types");
        }
        UnknownType ut = new UnknownType();
        types.putAll(t1.unify(t3));
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitTab_initialization(grammarTCLParser.Tab_initializationContext ctx) {
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitBase_type'");
    }

    @Override
    public Type visitTab_type(grammarTCLParser.Tab_typeContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitTab_type'");
    }

    @Override
    public Type visitDeclaration(grammarTCLParser.DeclarationContext ctx) { // pas sur
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        if (!(t1 instanceof PrimitiveType)) {
            throw new Error("Type error: declaration of variable with non-variable type");
        }
        if (ctx.getChildCount() == 5){
            ParseTree p3 = ctx.getChild(3);
            Type t3 = visit(p3);
            if (t1.equals(t3)) {
                throw new Error("Type error: declaration of variable with different type");
            }
        }
        return null;
    }

    @Override
    public Type visitPrint(grammarTCLParser.PrintContext ctx) {
        ParseTree p1 = ctx.getChild(2);
        Type t = visit(p1);
        if (!(t instanceof UnknownType)) {
            throw new Error("Type error: print of non-variable");
        }
        return null;
    }

    @Override
    public Type visitAssignment(grammarTCLParser.AssignmentContext ctx) {
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
        for (int i = 1; i < ctx.getChildCount() - 2; i++) {
            ParseTree p = ctx.getChild(i);
            visit(p);
        }
        return null;
    }

    @Override
    public Type visitIf(grammarTCLParser.IfContext ctx) {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitReturn'");
    }

    @Override
    public Type visitCore_fct(grammarTCLParser.Core_fctContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitCore_fct'");
    }

    @Override
    public Type visitDecl_fct(grammarTCLParser.Decl_fctContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitDecl_fct'");
    }

    @Override
    public Type visitMain(grammarTCLParser.MainContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitMain'");
    }
}
