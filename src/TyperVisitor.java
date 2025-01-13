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

    private void bigAssSubstitute(HashMap<UnknownType, Type> contraintes){
        this.types.forEach( (variable, value) -> {
            value.substituteAll(contraintes);
        } );
    }

    @Override
    public Type visitNegation(grammarTCLParser.NegationContext ctx) {
        // TODO Auto-generated method stub
        System.out.println("visit negation");
        ParseTree p1 = ctx.getChild(1);
        Type t = visit(p1);
        HashMap<UnknownType, Type> constraints = new HashMap<>(t.unify(new PrimitiveType(Type.Base.BOOL)));
        this.bigAssSubstitute(constraints);
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitComparison(grammarTCLParser.ComparisonContext ctx) {
        System.out.println("visit comparison");
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
        System.out.println("visit or");
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
        System.out.println("visit opposite");
        ParseTree p1 = ctx.getChild(1);
        Type t = visit(p1);
        HashMap<UnknownType, Type> constraints = new HashMap<>(t.unify(new PrimitiveType(Type.Base.INT)));
        this.bigAssSubstitute(constraints);
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
        System.out.println("visit brackets");
        ParseTree p1 = ctx.getChild(1);
        return visit(p1);
    }

    @Override
    public Type visitCall(grammarTCLParser.CallContext ctx) {
        System.out.println("visit call");
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
        System.out.println("visit bool");

        ParseTree p1 = ctx.getChild(0);
        System.out.println(p1);
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitAnd(grammarTCLParser.AndContext ctx) {
        System.out.println("visit and");
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
        System.out.println("visit variable");
        Type type;

        System.out.println(this.types);

        ParseTree p0 = ctx.getChild(0);
        UnknownType ut = new UnknownType(p0);
        System.out.println("ut : "+ut);

        //TODO refaire contains
        boolean temp = this.types.containsKey(ut);
        if (temp) {
            System.out.println("1");
            type = this.types.get(ut);
        } else {
            System.out.println("2");
            type = new UnknownType();
            this.types.put(ut, type);
        }
        return type;
    }

    @Override
    public Type visitMultiplication(grammarTCLParser.MultiplicationContext ctx) {
        System.out.println("Visit Multiplication");

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
        System.out.println("visit equality");
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
        System.out.println("visit tab initialization");
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
        System.out.println("visit addition");
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
        System.out.println("Visit base type");
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
        System.out.println("Visit tab type");
        ParseTree p0 = ctx.getChild(0);
        Type t = visit(p0);
        ArrayType array = new ArrayType(t);
        this.types.put(new UnknownType(), array);
        return array;
    }

    @Override
    public Type visitDeclaration(grammarTCLParser.DeclarationContext ctx) { // pas sur
        System.out.println("visit declaration");
        ParseTree p0 = ctx.getChild(0);
        Type t0 = visit(p0);
        HashMap<UnknownType, Type> constraints = new HashMap<>();
        if (!(t0 instanceof PrimitiveType) && !(t0 instanceof UnknownType)) {
            throw new Error("Type error: declaration of variable with non-variable type");
        }

        ParseTree p1 = ctx.getChild(1);
        UnknownType ut = new UnknownType(p1);

        this.types.put(ut, t0);

        if (ctx.getChildCount() == 5){
            ParseTree p3 = ctx.getChild(3);
            Type t3 = visit(p3);
            constraints.putAll(t0.unify(t3));
        }
        this.bigAssSubstitute(constraints);
        System.out.println(this.types);
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
        HashMap<UnknownType, Type> constraints = new HashMap<>(t1.unify(t2));
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        constraints.putAll(t3.unify(new PrimitiveType(Type.Base.INT)));
        this.bigAssSubstitute(constraints);
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
        HashMap<UnknownType, Type> constraints = new HashMap<>(t3.unify(new PrimitiveType(Type.Base.BOOL)));
        this.bigAssSubstitute(constraints);
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
        HashMap<UnknownType, Type> constraints = new HashMap<>(t1.unify(new PrimitiveType(Type.Base.BOOL)));
        this.bigAssSubstitute(constraints);
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
