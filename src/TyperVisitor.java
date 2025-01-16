package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import src.Type.*;

import javax.naming.Context;

public class TyperVisitor extends AbstractParseTreeVisitor<Type> implements grammarTCLVisitor<Type> {

    private final Map<UnknownType,Type> types = new HashMap<>();
    private final VarStack<Type> stack = new VarStack<>();
    private final CallStack callStack = new CallStack();

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
        System.out.println(this.types);
    }

    @Override
    public Type visitNegation(grammarTCLParser.NegationContext ctx) throws TyperError {
        System.out.println("visit negation : NOT expr");
        ParseTree p1 = ctx.getChild(1);
        Type t = visit(p1);
        HashMap<UnknownType, Type> constraints = new HashMap<>(t.unify(new PrimitiveType(Type.Base.BOOL)));
        this.bigAssSubstitute(constraints);
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitComparison(grammarTCLParser.ComparisonContext ctx) throws TyperError {
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
    public Type visitOr(grammarTCLParser.OrContext ctx) throws TyperError {
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
    public Type visitOpposite(grammarTCLParser.OppositeContext ctx) throws TyperError {
        System.out.println("visit opposite");
        ParseTree p1 = ctx.getChild(1);
        Type t = visit(p1);
        HashMap<UnknownType, Type> constraints = new HashMap<>(t.unify(new PrimitiveType(Type.Base.INT)));
        this.bigAssSubstitute(constraints);
        return new PrimitiveType(Type.Base.INT);
    }

    @Override
    public Type visitInteger(grammarTCLParser.IntegerContext ctx) throws TyperError {
        System.out.println("visit int");
        return new PrimitiveType(Type.Base.INT);
    }

    @Override
    public Type visitTab_access(grammarTCLParser.Tab_accessContext ctx) throws TyperError {
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
    public Type visitBrackets(grammarTCLParser.BracketsContext ctx) throws TyperError {
        System.out.println("visit brackets");
        ParseTree contentNode = ctx.getChild(1);
        return visit(contentNode);
    }

    @Override
    public Type visitCall(grammarTCLParser.CallContext ctx) throws TyperError {
        System.out.println("visit call");
        ParseTree p0 = ctx.getChild(0);
        UnknownType key = new UnknownType(p0);
        if (!types.containsKey(key)){
            throw new TyperError("Call does not exist", ctx);
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
    public Type visitBoolean(grammarTCLParser.BooleanContext ctx) throws TyperError {
        System.out.println("visit bool");
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitAnd(grammarTCLParser.AndContext ctx) throws TyperError {
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
    public Type visitVariable(grammarTCLParser.VariableContext ctx) throws TyperError {
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
    public Type visitMultiplication(grammarTCLParser.MultiplicationContext ctx) throws TyperError {
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
    public Type visitEquality(grammarTCLParser.EqualityContext ctx) throws TyperError {
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
    public Type visitTab_initialization(grammarTCLParser.Tab_initializationContext ctx) throws TyperError {
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
    public Type visitAddition(grammarTCLParser.AdditionContext ctx) throws TyperError {
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
    public Type visitBase_type(grammarTCLParser.Base_typeContext ctx) throws TyperError {
        System.out.println("Visit base type : BASE_TYPE");
        ParseTree p0 = ctx.getChild(0);
        if (!Objects.equals(p0.getText(), "int") && !Objects.equals(p0.getText(), "bool") && !Objects.equals(p0.getText(), "auto")) {
            throw new TyperError("The supplied type is not a base type\nType provided : " + p0.getText(), ctx);
        }
        return switch (p0.getText()) {
            case "int"  -> new PrimitiveType(Type.Base.INT);
            case "bool" -> new PrimitiveType(Type.Base.BOOL);
            case "auto" -> new UnknownType();
            default -> null;
        };
    }

    @Override
    public Type visitTab_type(grammarTCLParser.Tab_typeContext ctx) throws TyperError {
        System.out.println("Visit tab type");
        ParseTree p0 = ctx.getChild(0);
        Type t = visit(p0);
        ArrayType array = new ArrayType(t);
        this.types.put(new UnknownType(), array);
        return array;
    }

    @Override
    public Type visitDeclaration(grammarTCLParser.DeclarationContext ctx) throws TyperError {
        System.out.println("visit declaration : type VAR (ASSIGN expr)? SEMICOL");
        ParseTree typeNode = ctx.getChild(0);
        Type type = visit(typeNode);
        if (type instanceof FunctionType) {
            throw new TyperError("Type error: function type cannot be declared", ctx);
        }

        ParseTree variableNode = ctx.getChild(1);
        UnknownType variable = new UnknownType(variableNode);
        HashMap<UnknownType, Type> constraints = new HashMap<>(variable.unify(type));

        // cas : "auto a = b;"
        if (ctx.getChildCount() == 5){
            ParseTree exprNode = ctx.getChild(3);
            UnknownType expr = new UnknownType(exprNode);
            Type exprType = visit(exprNode);
            constraints.putAll(type.unify(exprType));
        }
        this.bigAssSubstitute(constraints);
        return null;
    }

    @Override
    public Type visitPrint(grammarTCLParser.PrintContext ctx) throws TyperError {
        System.out.println("visit print : PRINT '(' VAR ')' SEMICOL ");
        UnknownType parameter = new UnknownType(ctx.getChild(2));

        if (!(this.types.containsKey(parameter))) {
            throw new TyperError("Type error: variable "+parameter.getVarName()+" isn't defined", ctx, 6);
        }
        if (this.types.get(parameter) instanceof FunctionType) {
            throw new TyperError("Type error: function type cannot be printed", ctx);
        }
        return null;
    }

    @Override
    public Type visitAssignment(grammarTCLParser.AssignmentContext ctx) throws TyperError {
        System.out.println("visit assignment : VAR ('[' expr ']')* ASSIGN expr SEMICOL");
        ParseTree firstVariableNode = ctx.getChild(0);
        UnknownType firstVariable = new UnknownType(firstVariableNode);
        HashMap<UnknownType, Type> constraints = new HashMap<>();
        int nbChildren = ctx.getChildCount();
        if (nbChildren == 4) {
            ParseTree expressionNode = ctx.getChild(2);
            Type expression = visit(expressionNode);
            constraints.putAll(firstVariable.unify(expression));
        } else {
            int nbBrackets = (nbChildren-4)/3;
            ParseTree expressionNode = ctx.getChild(nbChildren-2);
            Type expression = visit(expressionNode);
            for (int i = 0; i < nbBrackets; i++) {
                int currentBracketIndex = 2+(3*i);
                ParseTree tabIndexNode = ctx.getChild(currentBracketIndex);
                Type tabIndexType = visit(tabIndexNode);
                constraints.putAll(tabIndexType.unify(new PrimitiveType(Type.Base.INT)));
                expression = new ArrayType(expression);
            }
            constraints.putAll(firstVariable.unify(expression));
        }

        this.bigAssSubstitute(constraints);
        return null;
    }

    @Override
    public Type visitBlock(grammarTCLParser.BlockContext ctx) throws TyperError {
        System.out.println("visit block");
        for (int i = 1; i < ctx.getChildCount() - 1; i++) {
            ParseTree instruction = ctx.getChild(i);
            visit(instruction);
        }
        return null;
    }

    @Override
    public Type visitIf(grammarTCLParser.IfContext ctx) throws TyperError {
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
    public Type visitWhile(grammarTCLParser.WhileContext ctx) throws TyperError {
        System.out.println("visit while : WHILE '(' expr ')' instr");
        ParseTree testNode = ctx.getChild(2);
        Type testType = visit(testNode);
        HashMap<UnknownType, Type> constraints = new HashMap<>(testType.unify(new PrimitiveType(Type.Base.BOOL)));
        ParseTree instructionNode = ctx.getChild(4);
        visit(instructionNode);
        this.bigAssSubstitute(constraints);
        return null;
    }

    @Override
    public Type visitFor(grammarTCLParser.ForContext ctx) throws TyperError { // nik ta mère
        System.out.println("visit for : FOR '(' instr  expr ';' instr ')' instr");
        ParseTree initializationNode = ctx.getChild(2);
        visit(initializationNode);
        ParseTree expressionNode = ctx.getChild(3);
        Type expressionType = visit(expressionNode);
        HashMap<UnknownType, Type> constraints = new HashMap<>(expressionType.unify(new PrimitiveType(Type.Base.BOOL)));
        this.bigAssSubstitute(constraints);
        ParseTree postLoopInstructionNode = ctx.getChild(5);
        visit(postLoopInstructionNode);
        ParseTree contentNode = ctx.getChild(7);
        visit(contentNode);
        return null;
    }

    @Override
    public Type visitReturn(grammarTCLParser.ReturnContext ctx) throws TyperError {
        System.out.println("visit return : RETURN expr SEMICOL ");
        ParseTree expr = ctx.getChild(1);
        Type exprType = visit(expr);
        return null;
    }

    @Override
    public Type visitCore_fct(grammarTCLParser.Core_fctContext ctx) throws TyperError {
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
    public Type visitDecl_fct(grammarTCLParser.Decl_fctContext ctx) throws TyperError {
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
    public Type visitMain(grammarTCLParser.MainContext ctx) throws TyperError {
        System.out.println("visit main : decl_fct* 'int main()' core_fct EOF;");

        UnknownType main = new UnknownType("main", 12);
        Type mainType = new FunctionType(new PrimitiveType(Type.Base.INT), new ArrayList<>());
        this.types.put(main, mainType);

        int childCount = ctx.getChildCount();
        boolean noDecl_fct = childCount == 3;
        if (!noDecl_fct) {
            for (int i = 0; i < childCount - 3; i++){
                ParseTree decl_fctNode = ctx.getChild(i);
                visit(decl_fctNode);
            }
        }
        ParseTree core_fctNode = ctx.getChild(childCount-2);
        visit(core_fctNode);
        return null;
    }
}
