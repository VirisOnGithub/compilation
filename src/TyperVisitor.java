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
                if (this.types.containsValue(variable)) {
                    this.types.forEach((key, value) -> {
                        if (value.contains(variable)) {
                            this.types.put(key, value.substitute(variable, type));
                        }
                    });
                } else {
                    this.types.put(variable, type);
                }
            }
        });
    }


    @Override
    public Type visitNegation(grammarTCLParser.NegationContext ctx) {
        System.out.println("visit negation : NOT expr");
        ParseTree p1 = ctx.getChild(1);
        Type t = visit(p1);
        HashMap<UnknownType, Type> constraints = new HashMap<>(t.unify(new PrimitiveType(Type.Base.BOOL)));
        this.bigAssSubstitute(constraints);
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitComparison(grammarTCLParser.ComparisonContext ctx) {
        System.out.println("visit comparison : expr op expr");
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

        Type result;
        boolean temp = this.types.containsKey(ut);
        if (temp) {
            System.out.println("1");
            result = this.types.get(ut);
        }
        else {
            System.out.println("2");
            result = ut;
            type = new UnknownType();
            this.types.put(ut, type);
        }
        return result;
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
        System.out.println("Visit base type : BASE_TYPE");
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

    // type VAR (ASSIGN expr)? SEMICOL
    @Override
    public Type visitDeclaration(grammarTCLParser.DeclarationContext ctx) {
        System.out.println("visit declaration : type VAR (ASSIGN expr)? SEMICOL");
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
            System.out.println("!!!!!!" + a.unify(t3));
        }
        System.out.println("constraints :"+ constraints);
        this.bigAssSubstitute(constraints);

        System.out.println(this.types);
        return null;
    }

    @Override
    public Type visitPrint(grammarTCLParser.PrintContext ctx) {
        System.out.println("visit print : PRINT '(' VAR ')' SEMICOL ");
        UnknownType parameter = new UnknownType(ctx.getChild(2));

        if (!(this.types.containsKey(parameter))) {
            throw new Error("Type error: variable "+parameter+" isn't defined");
        }
        if (this.types.get(parameter) instanceof UnknownType) {
            throw new Error("Type error: variable's type isn't defined");
        }
        return null;
    }

    @Override
    public Type visitAssignment(grammarTCLParser.AssignmentContext ctx) {
        System.out.println("visit assignment : VAR ('[' expr ']')* ASSIGN expr SEMICOL");
        ParseTree firstVariableNode = ctx.getChild(0);
        UnknownType firstVariable = new UnknownType(firstVariableNode);
        HashMap<UnknownType, Type> constraints = new HashMap<>();
        if (ctx.getChildCount()==4){
            ParseTree expressionNode = ctx.getChild(2);
            Type expression = visit(expressionNode);
            constraints.putAll(firstVariable.unify(expression));
        }
        else {
            ParseTree tabIndexNode = ctx.getChild(2);
            Type tabIndexType = visit(tabIndexNode);
            constraints.putAll(tabIndexType.unify(new PrimitiveType(Type.Base.INT)));
            ParseTree expressionNode = ctx.getChild(5);
            Type expression = visit(expressionNode);
            constraints.putAll(firstVariable.unify(new ArrayType(expression)));
        }

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
        System.out.println("visit if : IF '(' expr ')' instr (ELSE instr)?");
        ParseTree conditionNode = ctx.getChild(2);
        Type conditionType = visit(conditionNode);
        HashMap<UnknownType, Type> constraints = new HashMap<>(conditionType.unify(new PrimitiveType(Type.Base.BOOL)));
        ParseTree ifInstrNode = ctx.getChild(4);
        visit(ifInstrNode);
        if (ctx.getChildCount() == 7) {
            ParseTree elseInstrNode = ctx.getChild(6);
            visit(elseInstrNode);
        }
        this.bigAssSubstitute(constraints);
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
        System.out.println("Visit declare function : type VAR '(' (type VAR (',' type VAR)*)? ')' core_fct;");
        ParseTree functionReturnTypeNode = ctx.getChild(0);
        ParseTree functionNameNode       = ctx.getChild(1);

        UnknownType functionName = new UnknownType(functionNameNode);
        Type functionReturnType = visit(functionReturnTypeNode);

        int childCount = ctx.getChildCount();
        boolean noParameters = childCount == 5;
        if (noParameters) {
            this.types.put(functionName, new FunctionType(functionReturnType, new ArrayList<>()));

            int core_fctIndex = 4;
            ParseTree core_fctNode = ctx.getChild(core_fctIndex);
            visit(core_fctNode);

        } else {
            ArrayList<Type> paramList = new ArrayList<>();

            int paramNumber = (childCount - 4)/3;
            for (int k = 0; k < paramNumber; k++) {
                int currentTypeIndex = (3*k)+3;
                ParseTree paramTypeNode = ctx.getChild(currentTypeIndex);
                ParseTree paramNameNode = ctx.getChild(currentTypeIndex+1);
                Type paramType = visit(paramTypeNode);
                UnknownType paramName = new UnknownType(paramNameNode);

                paramName.unify(paramType);
                paramList.add(paramType);
            }

            FunctionType functionType = new FunctionType(functionReturnType, paramList);
            this.types.put(functionName, functionType);
        }
        return null;
    }

    @Override
    public Type visitMain(grammarTCLParser.MainContext ctx) {
        System.out.println("visit main : decl_fct* 'int main()' core_fct EOF;");

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
