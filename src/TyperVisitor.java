package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import src.Type.*;

public class TyperVisitor extends AbstractParseTreeVisitor<Type> implements grammarTCLVisitor<Type> {

    private final Map<UnknownType, Type> types = new HashMap<>();
    private final VarStack<UnknownType> varStack = new VarStack<>();
    private final Map<String, UnknownType> functionList = new HashMap<>();

    private String lastFunctionCalled = null;

    private final Stack<Map<UnknownType, List<Type>>> constraints = new Stack<>();

    public Map<UnknownType, Type> getTypes() {
        return types;
    }

    private void enterBlock() {
        this.bigAssSubstitute();
        this.constraints.add(new HashMap<>());
        this.varStack.enterBlock();
    }

    private void leaveBlock() {
        this.bigAssSubstitute();
        this.constraints.pop();
        this.varStack.leaveBlock();
    }

    private void enterFunction(ParseTree funcNameNode, String funcName) {
        this.varStack.enterFunction();
        this.constraints.add(new HashMap<>());
        this.lastFunctionCalled = funcName;
        this.functionList.put(funcName, new UnknownType(funcNameNode));
    }

    private void enterFunction(ParseTree funcNameNode) {
        enterFunction(funcNameNode, funcNameNode.getText());
    }

    private void addUnifyConstraint(Type t1, Type t2) {
        if (!(t1 instanceof UnknownType)) {
            System.out.println("Warning");
        }
        addConstraint(t1.unify(t2));
    }

    private void addUnifyConstraint(ParseTree var, Type... types) {
        for (Type type : types) {
            addUnifyConstraint(new UnknownType(var), type);
        }
    }

    private void addUnifyConstraint(ParseTree var1, ParseTree var2) {
        addUnifyConstraint(var1, new UnknownType(var2));
    }

    private void addUnifyConstraint(ParseTree var1, ParseTree var2, Type t1) {
        addUnifyConstraint(var1, new UnknownType(var2), t1);
    }

    private void debugConstraints() {

        System.out.println("Types");

        System.out.println(this.types);

        System.out.println("\nContraintes :");

        for (var cons : this.constraints) {
            for (var constraint : cons.entrySet()) {
                System.out.println(constraint.getKey() + "{ " + this.types.get(constraint.getKey()) + " }  ~ "
                        + constraint.getValue() + "{ " + this.types.get(constraint.getValue()) + " }");
            }
        }

        System.out.println("\n\n\n");

        this.bigAssSubstitute();

        System.out.println("Contraintes (Nouveau) :");

        for (var cons : this.constraints) {
            for (var constraint : cons.entrySet()) {
                System.out.println(constraint.getKey() + "{ " + this.types.get(constraint.getKey()) + " }  ~ "
                        + constraint.getValue() + "{ " + this.types.get(constraint.getValue()) + " }");
            }
        }

        System.out.println("\n\n");

        System.out.println(this.types);

        System.out.println("\n\n\n");
    }

    private void leaveFunction() {
        this.debugConstraints();
        this.varStack.leaveFunction();
        this.constraints.pop();
    }

    private Type getVarType(String varName) {
        return this.types.get(this.varStack.getVar(varName));
    }

    // we can only print primitive and arrays with known type
    private boolean isPrintable(Type type) {
        if (type instanceof PrimitiveType)
            return true;
        if (type instanceof ArrayType at)
            return isPrintable(at.getTabType());
        return false;
    }

    private void addConstraint(Map<UnknownType, Type> constraint) {
        for (var entry : constraint.entrySet()) {
            if (!constraints.getLast().containsKey(entry.getKey())) {
                constraints.getLast().put(entry.getKey(), new ArrayList<>());
            }

            constraints.getLast().get(entry.getKey()).add(entry.getValue());
        }
    }

    private void substituteVar(UnknownType bigVar, Type bigType) {
        assert (!(bigType instanceof UnknownType));

        for (var cons : this.constraints) {
            // for (var it = cons.entrySet().iterator(); it.hasNext();) {
            // var entry = it.next();
            // var var1 = entry.getKey();
            // var var2 = entry.getValue();
            // if (var2 == bigVar) {
            // var var1Type = this.types.get(var1);
            // var1Type.unify(bigType);
            // this.types.replace(var1, bigType);
            // cons.replace(var1, bigType);
            // } else {
            // // if (var1 == bigVar) {
            // // cons.put(new UnknownType(), new UnknownType());
            // // System.out.println("YOOOOOOOOOOOO");
            // // }
            // }
            // }
        }
    }

    private void bigAssSubstitute() {
        // List<UnknownType> substitutes = new ArrayList<>();

        // boolean hasSubstitued = true;

        // while (hasSubstitued) {
        // hasSubstitued = false;

        // Map<Map<UnknownType, Type>, Map<UnknownType, Type>> newConstraints = new
        // HashMap<>();

        // for (var cons : this.constraints) {
        // for (var it = cons.entrySet().iterator(); it.hasNext();) {
        // var entry = it.next();

        // UnknownType leftVar = entry.getKey();
        // Type rightVar = entry.getValue();

        // assert(!this.types.containsKey(leftVar));

        // // if (rightVar instanceof UnknownType) {
        // // Type leftVarType = this.types.get(leftVar);
        // // Type rightVarType = this.types.get(rightVar);
        // // if (rightVarType != null && !(rightVarType instanceof UnknownType)) {
        // // var newConstraint = leftVarType.unify(rightVarType);
        // // if (newConstraints.containsKey(cons)) {
        // // newConstraints.get(cons).putAll(newConstraint);
        // // } else {
        // // newConstraints.put(cons, newConstraint);
        // // }
        // // }
        // // }

        // // la variable de gauche est typée
        // if (!(rightVar instanceof UnknownType)) {
        // if (!substitutes.contains(leftVar)) {
        // // substituteVar(leftVar, rightVar);
        // // if (this.types.get(leftVar) instanceof UnknownType ut)
        // // substituteVar(ut, rightVar);
        // // this.types.put(leftVar, rightVar);
        // substitutes.add(leftVar);
        // hasSubstitued = true;
        // break;
        // }
        // }

        // // variable à droite est "typée"
        // if (!(this.types.get(leftVar) instanceof UnknownType) && (rightVar instanceof
        // UnknownType rightVarUT)) {
        // if (this.types.containsKey(rightVarUT) && !substitutes.contains(rightVarUT))
        // {
        // // Type leftVarType = this.types.get(leftVar);
        // // this.types.put(rightVarUT, leftVarType);
        // // this.substituteVar(rightVarUT, leftVarType);
        // substitutes.add(rightVarUT);
        // hasSubstitued = true;
        // break;
        // }

        // }
        // }
        // if (hasSubstitued)
        // break;
        // }

        // for (var cons : newConstraints.entrySet()) {
        // var currentDepthConstraints = cons.getKey();
        // var newConstraintsToAdd = cons.getValue();

        // if (this.constraints.contains(currentDepthConstraints)) {
        // this.constraints.get(this.constraints.indexOf(currentDepthConstraints)).putAll(newConstraintsToAdd);
        // }
        // }

        // }
    }

    private void throwCustomError(String message) {
        System.err.println(message);
        System.exit(1);
    }

    private int getLine(ParserRuleContext ctx) {
        if (ctx instanceof TerminalNode) {
            return ((TerminalNode) ctx).getSymbol().getLine();
        } else {
            if (ctx != null) {
                return ctx.getStart().getLine();
            } else {
                throw new Error("Illegal UnknownType construction");
            }
        }
    }

    @Override
    public Type visitNegation(grammarTCLParser.NegationContext ctx) {
        // System.out.println("visit negation : NOT expr");
        ParseTree p1 = ctx.getChild(1);
        addUnifyConstraint(p1, visit(p1), new PrimitiveType(Type.Base.BOOL));
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitComparison(grammarTCLParser.ComparisonContext ctx) {
        // System.out.println("visit comparison : expr op expr");
        ParseTree p1 = ctx.getChild(0);
        ParseTree p3 = ctx.getChild(2);

        addUnifyConstraint(p1, visit(p1), new PrimitiveType(Type.Base.INT));
        addUnifyConstraint(p3, visit(p3), new PrimitiveType(Type.Base.INT));

        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitOr(grammarTCLParser.OrContext ctx) {
        // System.out.println("visit or");
        ParseTree p1 = ctx.getChild(0);
        ParseTree p3 = ctx.getChild(2);

        addUnifyConstraint(p1, visit(p1), new PrimitiveType(Type.Base.BOOL));
        addUnifyConstraint(p3, visit(p3), new PrimitiveType(Type.Base.BOOL));

        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitOpposite(grammarTCLParser.OppositeContext ctx) {
        // System.out.println("visit opposite");
        ParseTree p1 = ctx.getChild(1);

        addUnifyConstraint(p1, visit(p1), new PrimitiveType(Type.Base.INT));

        return new PrimitiveType(Type.Base.INT);
    }

    @Override
    public Type visitInteger(grammarTCLParser.IntegerContext ctx) {
        // System.out.println("visit int");
        return new PrimitiveType(Type.Base.INT);
    }

    @Override
    public Type visitTab_access(grammarTCLParser.Tab_accessContext ctx) {
        // System.out.println("visit tabAccess");
        ParseTree p0 = ctx.getChild(0);
        ParseTree p2 = ctx.getChild(2);

        Type arrayType = visit(p0);

        addUnifyConstraint(p0, arrayType, new ArrayType(new UnknownType()));
        addUnifyConstraint(p2, visit(p2), new PrimitiveType(Type.Base.INT));

        return ((ArrayType) arrayType).getTabType();
    }

    @Override
    public Type visitBrackets(grammarTCLParser.BracketsContext ctx) {
        // System.out.println("visit brackets");
        ParseTree contentNode = ctx.getChild(1);
        return visit(contentNode);
    }

    @Override
    public Type visitCall(grammarTCLParser.CallContext ctx) {
        // System.out.println("visit call");
        String funcName = ctx.VAR().getText();
        if (!functionList.containsKey(funcName)) {
            throwCustomError("Call does not exist at line " + getLine(ctx));
        }
        FunctionType funcDeclType = (FunctionType) types.get(functionList.get(funcName));

        int NbChildren = ctx.getChildCount();
        if (NbChildren != 3) {
            int argCount = (NbChildren - 2) / 2;

            if (argCount != funcDeclType.getNbArgs()) {
                throwCustomError("pas le bon nombre d'arguments lors de l'appel de la fonction " + funcName);
            }

            for (int i = 0; i < argCount; i++) {
                ParseTree p2 = ctx.getChild(2 + i * 2);

                addUnifyConstraint(p2, visit(p2), funcDeclType.getArgsType(i));
            }
        }
        return funcDeclType.getReturnType();
    }

    @Override
    public Type visitBoolean(grammarTCLParser.BooleanContext ctx) {
        // System.out.println("visit bool");
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitAnd(grammarTCLParser.AndContext ctx) {
        // System.out.println("visit and");
        ParseTree p1 = ctx.getChild(0);
        ParseTree p3 = ctx.getChild(2);

        addUnifyConstraint(p1, visit(p1), new PrimitiveType(Type.Base.BOOL));
        addUnifyConstraint(p3, visit(p3), new PrimitiveType(Type.Base.BOOL));

        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitVariable(grammarTCLParser.VariableContext ctx) {
        // System.out.println("visit variable");
        if (!this.varStack.varExists(ctx.VAR().getText())) {
            throwCustomError("Type error: variable " + ctx.VAR().getText() + " isn't defined at line " + getLine(ctx));
        }

        UnknownType declVarNode = this.varStack.getVar(ctx.VAR().getText());
        UnknownType variable = new UnknownType(ctx.VAR());
        addUnifyConstraint(variable, declVarNode);
        return variable;
    }

    @Override
    public Type visitMultiplication(grammarTCLParser.MultiplicationContext ctx) {
        // System.out.println("visit Multiplication");

        ParseTree p1 = ctx.getChild(0);
        ParseTree p3 = ctx.getChild(2);

        addUnifyConstraint(p1, visit(p1), new PrimitiveType(Type.Base.INT));
        addUnifyConstraint(p3, visit(p3), new PrimitiveType(Type.Base.INT));

        return new PrimitiveType(Type.Base.INT);
    }

    @Override
    public Type visitEquality(grammarTCLParser.EqualityContext ctx) {
        // System.out.println("visit equality");
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);

        addUnifyConstraint(p1, t1, t3);
        addUnifyConstraint(p3, t1, t3);

        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitTab_initialization(grammarTCLParser.Tab_initializationContext ctx) {
        // System.out.println("visit tab initialization");

        int elementsCount = (ctx.getChildCount() - 1) / 2;

        if (elementsCount == 0)
            return new ArrayType(new UnknownType());

        ParseTree firstElement = ctx.getChild(1);
        Type elementType = visit(firstElement);

        if (elementsCount == 1)
            return new ArrayType(elementType);

        for (int i = 0; i < elementsCount - 1; i++) {
            int childIndex = 1 + 2 * i;
            int nextChildIndex = 1 + 2 * (i + 1);

            var element = ctx.getChild(childIndex);
            var nextElement = ctx.getChild(nextChildIndex);

            Type t1 = visit(element);
            Type t2 = visit(nextElement);

            addUnifyConstraint(element, new UnknownType(nextElement), t1, t2);
        }

        return new ArrayType(elementType);
    }

    @Override
    public Type visitAddition(grammarTCLParser.AdditionContext ctx) {
        // System.out.println("visit addition");
        ParseTree p1 = ctx.getChild(0);
        ParseTree p3 = ctx.getChild(2);

        addUnifyConstraint(p1, visit(p1), new PrimitiveType(Type.Base.INT));
        addUnifyConstraint(p3, visit(p3), new PrimitiveType(Type.Base.INT));

        return new PrimitiveType(Type.Base.INT);
    }

    @Override
    public Type visitBase_type(grammarTCLParser.Base_typeContext ctx) {
        // System.out.println("visit base type : BASE_TYPE");
        ParseTree p0 = ctx.getChild(0);
        if (!Objects.equals(p0.getText(), "int") && !Objects.equals(p0.getText(), "bool")
                && !Objects.equals(p0.getText(), "auto")) {
            throwCustomError("The supplied type is not a base type\nType provided : " + p0.getText() + "\nat line "
                    + getLine(ctx));
        }
        return switch (p0.getText()) {
            case "int" -> new PrimitiveType(Type.Base.INT);
            case "bool" -> new PrimitiveType(Type.Base.BOOL);
            case "auto" -> new UnknownType();
            default -> null;
        };
    }

    @Override
    public Type visitTab_type(grammarTCLParser.Tab_typeContext ctx) {
        // System.out.println("visit tab type");
        ParseTree p0 = ctx.getChild(0);
        Type t = visit(p0);
        ArrayType array = new ArrayType(t);
        return array;
    }

    @Override
    public Type visitDeclaration(grammarTCLParser.DeclarationContext ctx) {
        // System.out.println("visit declaration : type VAR (ASSIGN expr)? SEMICOL");
        ParseTree typeNode = ctx.getChild(0);
        Type type = visit(typeNode);

        ParseTree varNode = ctx.VAR();
        UnknownType varUT = new UnknownType(ctx.VAR());

        if (!this.varStack.assignVar(ctx.VAR().getText(), varUT)) {
            throwCustomError("redefinition of " + ctx.VAR().getText());
        }
        // the var name is now linked to the decl node

        if (type instanceof FunctionType) {
            throwCustomError("Type error: function type cannot be declared at line " + getLine(ctx));
        }

        // cas : "auto a = b;"
        if (ctx.getChildCount() == 5) {
            ParseTree exprNode = ctx.getChild(3);
            Type exprType = visit(exprNode);
            addUnifyConstraint(varNode, type, exprType);
        } else {
            addUnifyConstraint(varNode, type);
        }
        return null;
    }

    @Override
    public Type visitPrint(grammarTCLParser.PrintContext ctx) {
        // System.out.println("visit print : PRINT '(' VAR ')' SEMICOL ");
        UnknownType parameter = new UnknownType(ctx.getChild(2));
        String varName = ctx.VAR().getText();

        if (!this.varStack.varExists(varName)) {
            throwCustomError("Type error: variable " + parameter + " isn't defined at line " + getLine(ctx));
        }

        // we must know the type at this point
        this.debugConstraints();

        Type varType = getVarType(varName);

        if (!isPrintable(varType)) {
            throwCustomError("Type error: function type cannot be printed at line " + getLine(ctx));
        }

        return null;
    }

    @Override
    public Type visitAssignment(grammarTCLParser.AssignmentContext ctx) {
        // System.out.println("visit assignment : VAR ('[' expr ']')* ASSIGN expr
        // SEMICOL");
        ParseTree variableNode = ctx.VAR();

        if (!this.varStack.varExists(variableNode.getText())) {
            throwCustomError(
                    "Type error: variable " + variableNode.getText() + " isn't defined at line " + getLine(ctx));
        }

        UnknownType varRef = this.varStack.getVar(variableNode.getText());

        int nbChildren = ctx.getChildCount();
        // no tab access
        if (nbChildren == 4) {
            ParseTree expressionNode = ctx.getChild(2);
            addUnifyConstraint(expressionNode, visit(expressionNode), varRef);
        } else {
            // tab access
            int nbBrackets = (nbChildren - 4) / 3;
            ParseTree expressionNode = ctx.getChild(nbChildren - 2);
            Type expression = visit(expressionNode);
            for (int i = 0; i < nbBrackets; i++) {
                int currentBracketIndex = 2 + (3 * i);

                ParseTree tabIndexNode = ctx.getChild(currentBracketIndex);
                Type tabIndexType = visit(tabIndexNode);
                addUnifyConstraint(tabIndexNode, tabIndexType, new PrimitiveType(Type.Base.INT));

                expression = new ArrayType(expression);
            }
            addUnifyConstraint(varRef, expression);
        }

        return null;
    }

    @Override
    public Type visitBlock(grammarTCLParser.BlockContext ctx) {
        // System.out.println("visit block");
        this.enterBlock();
        for (int i = 1; i < ctx.getChildCount() - 1; i++) {
            ParseTree instruction = ctx.getChild(i);
            visit(instruction);
        }
        this.leaveBlock();
        return null;
    }

    @Override
    public Type visitIf(grammarTCLParser.IfContext ctx) {
        // System.out.println("visit if : IF '(' expr ')' instr (ELSE instr)?");

        ParseTree conditionNode = ctx.getChild(2);
        addUnifyConstraint(conditionNode, visit(conditionNode), new PrimitiveType(Type.Base.BOOL));

        ParseTree ifInstrNode = ctx.getChild(4);
        this.enterBlock();
        visit(ifInstrNode);
        this.leaveBlock();
        if (ctx.getChildCount() == 7) { // if expression contains an else
            ParseTree elseInstrNode = ctx.getChild(6);
            this.enterBlock();
            visit(elseInstrNode);
            this.leaveBlock();
        }
        return null;
    }

    @Override
    public Type visitWhile(grammarTCLParser.WhileContext ctx) {
        // System.out.println("visit while : WHILE '(' expr ')' instr");
        ParseTree testNode = ctx.getChild(2);
        addUnifyConstraint(testNode, visit(testNode), new PrimitiveType(Type.Base.BOOL));

        ParseTree instructionNode = ctx.getChild(4);
        this.enterBlock();
        visit(instructionNode);
        this.leaveBlock();
        return null;
    }

    @Override
    public Type visitFor(grammarTCLParser.ForContext ctx) { // nik ta mère
        // System.out.println("visit for : FOR '(' instr expr ';' instr ')' instr");
        this.enterBlock();
        ParseTree initializationNode = ctx.getChild(2);
        visit(initializationNode);

        ParseTree expressionNode = ctx.getChild(3);
        addUnifyConstraint(expressionNode, visit(expressionNode), new PrimitiveType(Type.Base.BOOL));

        ParseTree postLoopInstructionNode = ctx.getChild(5); // exemple : i++
        visit(postLoopInstructionNode);

        ParseTree contentNode = ctx.getChild(7);
        this.enterBlock();
        visit(contentNode);
        this.leaveBlock();
        this.leaveBlock();
        return null;
    }

    @Override
    public Type visitReturn(grammarTCLParser.ReturnContext ctx) {
        throw new UnsupportedOperationException("Unimplemented method 'visitReturn'");
    }

    @Override
    public Type visitCore_fct(grammarTCLParser.Core_fctContext ctx) {
        // System.out.println("visit core function");
        int nbChildrenWithoutInstr = 5; // '{' instr* RETURN expr SEMICOL '}';
        int nbChildren = ctx.getChildCount();
        for (int i = 1; i <= nbChildren - nbChildrenWithoutInstr; i++) {
            ParseTree p = ctx.getChild(i);
            visit(p);
        }
        int returnExprIndex = nbChildren - 3;
        ParseTree p = ctx.getChild(returnExprIndex);
        Type returnType = visit(p);

        FunctionType declFunction = (FunctionType) this.types.get(this.functionList.get(this.lastFunctionCalled));
        Type declReturnType = declFunction.getReturnType();
        addUnifyConstraint(p, returnType, declReturnType);
        return null;
    }

    @Override
    public Type visitDecl_fct(grammarTCLParser.Decl_fctContext ctx) {
        // System.out.println("visit declare function : type VAR '(' (type VAR (',' type
        // VAR)*)? ')' core_fct");
        ParseTree functionReturnTypeNode = ctx.getChild(0);
        ParseTree functionNameNode = ctx.getChild(1);

        UnknownType functionName = new UnknownType(functionNameNode);
        Type functionReturnType = visit(functionReturnTypeNode);

        int childCount = ctx.getChildCount();
        boolean noParameters = childCount == 5;

        enterFunction(functionNameNode);

        if (noParameters) {
            this.types.put(functionName, new FunctionType(functionReturnType, new ArrayList<>()));
        } else {
            ArrayList<Type> paramList = new ArrayList<>();

            int paramNumber = (childCount - 4) / 3;
            for (int k = 0; k < paramNumber; k++) {
                int currentTypeIndex = (3 * k) + 3;
                ParseTree paramTypeNode = ctx.getChild(currentTypeIndex);
                ParseTree paramNameNode = ctx.getChild(currentTypeIndex + 1);
                Type paramType = visit(paramTypeNode);
                UnknownType paramName = new UnknownType(paramNameNode);

                this.varStack.assignVar(paramNameNode.getText(), paramName);

                addUnifyConstraint(paramNameNode, paramType);

                paramList.add(paramType);
            }

            FunctionType functionType = new FunctionType(functionReturnType, paramList);
            this.types.put(functionName, functionType);
        }

        int core_fctIndex = childCount - 1; // it's always the last one
        ParseTree core_fctNode = ctx.getChild(core_fctIndex);

        visit(core_fctNode);
        leaveFunction();

        return null;
    }

    @Override
    public Type visitMain(grammarTCLParser.MainContext ctx) {
        // System.out.println("visit main : decl_fct* 'int main()' core_fct EOF;");

        final Type mainType = new FunctionType(new PrimitiveType(Type.Base.INT), new ArrayList<>());

        int childCount = ctx.getChildCount();

        // visit functions
        for (int i = 0; i < childCount - 3; i++) {
            ParseTree decl_fctNode = ctx.getChild(i);
            visit(decl_fctNode);
        }

        // visit main function
        ParseTree core_fctNode = ctx.getChild(childCount - 2);

        ParseTree funcMain = ctx.getChild(childCount - 3);
        UnknownType funcNameUT = new UnknownType(funcMain);

        this.types.put(funcNameUT, mainType);
        enterFunction(funcMain);

        visit(core_fctNode);

        leaveFunction();

        return null;
    }

}
