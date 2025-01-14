package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

import org.antlr.v4.runtime.tree.ParseTree;
import src.Type.*;

public class TyperVisitor extends AbstractParseTreeVisitor<Type> implements grammarTCLVisitor<Type> {

    private final Map<UnknownType,Type> types = new HashMap<>();

    public Map<UnknownType, Type> getTypes() {
        return types;
    }

    private void bigAssSubstitute(HashMap<UnknownType, Type> constraints) {
        constraints.forEach((variable, type) -> {
            if (this.types.containsKey(variable)) {
                Type newType = variable.substitute(variable, type);
                Type oldType = this.types.get(variable);
                if (oldType instanceof UnknownType) {
                    this.types.put(variable, newType);
                }
                if (type instanceof UnknownType) {
                    this.types.put((UnknownType) type, oldType);
                }
            } else {
                this.types.put(variable, type);
            }
        });
    }


    @Override
    public Type visitNegation(grammarTCLParser.NegationContext ctx) {
        ParseTree p1 = ctx.getChild(1);
        Type t = visit(p1);
        HashMap<UnknownType, Type> constraints = new HashMap<>(t.unify(new PrimitiveType(Type.Base.BOOL)));
        this.bigAssSubstitute(constraints);
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitComparison(grammarTCLParser.ComparisonContext ctx) {
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        HashMap<UnknownType, Type> constraints = new HashMap<>(t1.unify(new PrimitiveType(Type.Base.INT)));
        constraints.putAll(t3.unify(new PrimitiveType(Type.Base.INT)));
        this.bigAssSubstitute(constraints);
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitOr(grammarTCLParser.OrContext ctx) {
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        HashMap<UnknownType, Type> constraints = new HashMap<>(t1.unify(new PrimitiveType(Type.Base.BOOL)));
        constraints.putAll(t3.unify(new PrimitiveType(Type.Base.BOOL)));
        this.bigAssSubstitute(constraints);
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitOpposite(grammarTCLParser.OppositeContext ctx) {
        ParseTree p1 = ctx.getChild(1);
        Type t = visit(p1);
        HashMap<UnknownType, Type> constraints = new HashMap<>(t.unify(new PrimitiveType(Type.Base.INT)));
        this.bigAssSubstitute(constraints);
        return null;
    }

    @Override
    public Type visitInteger(grammarTCLParser.IntegerContext ctx) {
        return new PrimitiveType(Type.Base.INT);
    }

    @Override
    public Type visitTab_access(grammarTCLParser.Tab_accessContext ctx) {
        ParseTree p0 = ctx.getChild(0);
        Type t0 = visit(p0);
        ParseTree p2 = ctx.getChild(2);
        Type t2 = visit(p2);
        HashMap<UnknownType, Type> constraints = new HashMap<>(t0.unify(new ArrayType(new UnknownType())));
        constraints.putAll(t2.unify(new PrimitiveType(Type.Base.INT)));
        this.bigAssSubstitute(constraints);
        return ((ArrayType)t0).getTabType();
    }

    @Override
    public Type visitBrackets(grammarTCLParser.BracketsContext ctx) {
        ParseTree p1 = ctx.getChild(1);
        return visit(p1);
    }

    @Override
    public Type visitCall(grammarTCLParser.CallContext ctx) {
        ParseTree p0 = ctx.getChild(0);
        UnknownType key = new UnknownType(p0);
        if (!types.containsKey(key)){
            throw new RuntimeException("call does not exist");
        }
        Type t = types.get(key);
        ArrayList<Type> arguments = new ArrayList<>();
        int NbChildren = ctx.getChildCount();
        if(NbChildren != 3){
            ParseTree p1 = ctx.getChild(2);
            visit(p1);
            for(int i = 0; i < (NbChildren - 3 - 1)/2; i++){
                ParseTree p2 = ctx.getChild(4+2*i);
                Type type = visit(p2);
                arguments.add(type);
            }
        }
        FunctionType f = new FunctionType(new UnknownType(), arguments);
        HashMap<UnknownType, Type> constraints = new HashMap<>(t.unify(f));
        this.bigAssSubstitute(constraints);
        return null;
    }

    @Override
    public Type visitBoolean(grammarTCLParser.BooleanContext ctx) {
        ParseTree p1 = ctx.getChild(0);
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitAnd(grammarTCLParser.AndContext ctx) {
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        HashMap<UnknownType, Type> constraints = new HashMap<>(t1.unify(new PrimitiveType(Type.Base.BOOL)));
        constraints.putAll(t3.unify(new PrimitiveType(Type.Base.BOOL)));
        this.bigAssSubstitute(constraints);
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitVariable(grammarTCLParser.VariableContext ctx) {
        Type type;
        ParseTree p0 = ctx.getChild(0);
        UnknownType ut = new UnknownType(p0);
        Type result;
        boolean temp = this.types.containsKey(ut);
        if (temp) {
            result = this.types.get(ut);
        }
        else {
            result = ut;
            type = new UnknownType();
            this.types.put(ut, type);
        }
        return result;
    }

    @Override
    public Type visitMultiplication(grammarTCLParser.MultiplicationContext ctx) {
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        HashMap<UnknownType, Type> constraints = new HashMap<>(t1.unify(new PrimitiveType(Type.Base.INT)));
        constraints.putAll(t3.unify(new PrimitiveType(Type.Base.INT)));
        this.bigAssSubstitute(constraints);
        return new PrimitiveType(Type.Base.INT);
    }

    @Override
    public Type visitEquality(grammarTCLParser.EqualityContext ctx) {
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        HashMap<UnknownType, Type> constraints = new HashMap<>(t1.unify(t3));
        this.bigAssSubstitute(constraints);
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitTab_initialization(grammarTCLParser.Tab_initializationContext ctx) {
        HashMap<UnknownType, Type> constraints = new HashMap<>();
        Type type = new UnknownType();

        if (ctx.getChildCount() > 2) {
            for (int i = 1; i < ctx.getChildCount() - 3; i += 2) {
                ParseTree p = ctx.getChild(i);
                Type t = visit(p);
                ParseTree p2 = ctx.getChild(i + 2);
                Type t2 = visit(p2);
                constraints.putAll(t.unify(t2));
                if (!(t instanceof UnknownType)){
                    type = t;
                }

            }
        }
        this.bigAssSubstitute(constraints);
        return new ArrayType(type);
    }

    @Override
    public Type visitAddition(grammarTCLParser.AdditionContext ctx) {
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        HashMap<UnknownType, Type> constraints = new HashMap<>(t1.unify(new PrimitiveType(Type.Base.INT)));
        constraints.putAll(t3.unify(new PrimitiveType(Type.Base.INT)));
        this.bigAssSubstitute(constraints);
        return new PrimitiveType(Type.Base.INT);
    }

    @Override
    public Type visitBase_type(grammarTCLParser.Base_typeContext ctx) {
        ParseTree p0 = ctx.getChild(0);
        if (!Objects.equals(p0.getText(), "int") && !Objects.equals(p0.getText(), "bool") && !Objects.equals(p0.getText(), "auto")) {
            throw new Error("Not a base type.");
        }
        return switch (p0.getText()) {
            case "int"  -> new PrimitiveType(Type.Base.INT);
            case "bool" -> new PrimitiveType(Type.Base.BOOL);
            case "auto" -> new UnknownType();
            default -> null;
        };
    }

    @Override
    public Type visitTab_type(grammarTCLParser.Tab_typeContext ctx) {
        ParseTree p0 = ctx.getChild(0);
        Type t = visit(p0);
        ArrayType array = new ArrayType(t);
        this.types.put(new UnknownType(), array);
        return array;
    }

    // type VAR (ASSIGN expr)? SEMICOL
    @Override
    public Type visitDeclaration(grammarTCLParser.DeclarationContext ctx) {
        ParseTree p0 = ctx.getChild(0);
        Type t0 = visit(p0);
        if (t0 instanceof FunctionType) {
            throw new Error("Type error: declaration of variable with non-variable type");
        }

        UnknownType a = new UnknownType(ctx.getChild(1));
        HashMap<UnknownType, Type> constraints = new HashMap<>();
        constraints.put(a, t0);

        // cas : "auto a = b;"
        if (ctx.getChildCount() == 5){
            ParseTree p3 = ctx.getChild(3);
            Type t3 = visit(p3);
            constraints.putAll(a.unify(t3));
        }
        this.bigAssSubstitute(constraints);

        return null;
    }

    @Override
    public Type visitPrint(grammarTCLParser.PrintContext ctx) {
        UnknownType parameter = new UnknownType(ctx.getChild(2));

        if (!(this.types.containsKey(parameter))) {
            throw new Error("Type error: variable "+parameter+" isn't defined");
        }
        if (this.types.get(parameter) instanceof UnknownType) {
            // throw new Error("Type error: variable's type isn't defined");
        }
        return null;
    }

    @Override
    public Type visitAssignment(grammarTCLParser.AssignmentContext ctx) {
        // ParseTree p1 = ctx.getChild(0);
        // Type t1 = visit(p1);
        // ParseTree p2 = ctx.getChild(5);
        // Type t2 = visit(p2);
        // HashMap<UnknownType, Type> constraints = new HashMap<>(t1.unify(t2));
        // ParseTree p3 = ctx.getChild(2);
        // Type t3 = visit(p3);
        // constraints.putAll(t3.unify(new PrimitiveType(Type.Base.INT)));
        // this.bigAssSubstitute(constraints);
        return null;
    }

    @Override
    public Type visitBlock(grammarTCLParser.BlockContext ctx) {
        for (int i = 1; i < ctx.getChildCount() - 1; i++) {
            ParseTree p = ctx.getChild(i);
            visit(p);
        }
        return null;
    }

    @Override
    public Type visitIf(grammarTCLParser.IfContext ctx) {
        ParseTree conditionNode = ctx.getChild(2);
        Type conditionType = visit(conditionNode);
        HashMap<UnknownType, Type> constraints = new HashMap<>(conditionType.unify(new PrimitiveType(Type.Base.BOOL)));
        this.bigAssSubstitute(constraints);
        ParseTree ifIsntrNode = ctx.getChild(4);
        visit(ifIsntrNode);
        if (ctx.getChildCount() == 7) {
            ParseTree elseInstrNode = ctx.getChild(6);
            visit(elseInstrNode);
        }
        return null;
    }

    @Override
    public Type visitWhile(grammarTCLParser.WhileContext ctx) {
        ParseTree p1 = ctx.getChild(1);
        Type t1 = visit(p1);
        HashMap<UnknownType, Type> constraints = new HashMap<>(t1.unify(new PrimitiveType(Type.Base.BOOL)));
        this.bigAssSubstitute(constraints);
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
        HashMap<UnknownType, Type> constraints = new HashMap<>(t2.unify(new PrimitiveType(Type.Base.BOOL)));
        this.bigAssSubstitute(constraints);
        ParseTree p3 = ctx.getChild(5);
        visit(p3);
        ParseTree p4 = ctx.getChild(7);
        visit(p4);
        return null;
    }

    @Override
    public Type visitReturn(grammarTCLParser.ReturnContext ctx) {
        ParseTree p1 = ctx.getChild(1);
        visit(p1);
        return null;
    }

    @Override
    public Type visitCore_fct(grammarTCLParser.Core_fctContext ctx) {
        int nbChildrenWithoutInstr = 5; // '{' instr* RETURN expr SEMICOL '}';
        int nbChildren = ctx.getChildCount();
        for (int i = 1; i <= nbChildren - nbChildrenWithoutInstr; i++){
            ParseTree p = ctx.getChild(i);
            visit(p);
        }
        int returnExprIndex = nbChildren - 3;
        ParseTree p = ctx.getChild(returnExprIndex);
        visit(p);
        return null;
    }

    @Override
    public Type visitDecl_fct(grammarTCLParser.Decl_fctContext ctx) {
        ParseTree functionReturnTypeNode = ctx.getChild(0);
        ParseTree functionNameNode       = ctx.getChild(1);

        UnknownType functionName = new UnknownType(functionNameNode);
        Type functionReturnType = visit(functionReturnTypeNode);

        int i = 4;
        int childCount = ctx.getChildCount()-1;
        boolean noParameters = childCount == 6;
        if (noParameters) {
            this.types.put(functionName, new FunctionType(functionReturnType, new ArrayList<>()));
            int core_fctIndex = 4;
            ParseTree core_fctNode = ctx.getChild(core_fctIndex);
            visit(core_fctNode);
        } else {
            ParseTree p3 = ctx.getChild(3);
            ParseTree p4 = ctx.getChild(4);
            visit(p3);
            visit(p4);
            // number of parameters is 0 mod 3
            int nbVars = (childCount - i - 3)/3; // first 3 represent first arg + closing parenthesis
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
        UnknownType main = new UnknownType("main", 12);
        Type mainType = new FunctionType(new PrimitiveType(Type.Base.INT), new ArrayList<>());
        this.types.put(main, mainType);

        int childCount = ctx.getChildCount();
        boolean noDecl_fct = childCount == 3;
        if (noDecl_fct) {
            ParseTree core_fctNode = ctx.getChild(1);
            visit(core_fctNode);
        } else {
            for (int i = 0; i < childCount - 3; i++){
                ParseTree decl_fctNode = ctx.getChild(i);
                visit(decl_fctNode);
            }
        }
        return null;
    }
}
