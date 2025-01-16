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
    private final List<UnknownType> functionList = new ArrayList<>();

    private final Stack<Map<UnknownType, Type>> constraints = new Stack<>();

    public Map<UnknownType, Type> getTypes() {
        return types;
    }

    private void enterBlock() {
        this.constraints.add(new HashMap<>());
        this.varStack.enterBlock();
    }

    private void leaveBlock() {
        // unify variables
        // substitue
        this.bigAssSubstitute();
        this.constraints.pop();
        this.varStack.leaveBlock();
    }

    private void enterFunction() {
        this.varStack.enterFunction();
        this.constraints.add(new HashMap<>());
    }

    private void leaveFunction() {
        System.out.println("Contraintes :");
		
        for (var constraint : this.constraints.getLast().entrySet()) {
            System.out.println(constraint.getKey() + " ~ " + constraint.getValue());
        }

        System.out.println("\n\n\n");

        this.bigAssSubstitute();
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
        constraints.getLast().putAll(constraint);
    }

    private void bigAssSubstitute() {
        for (var cons : this.constraints) {
            cons.forEach((variable, type) -> {
                assert(this.types.containsKey(variable));

                variable.substitute(variable, type);
            });
        // cons.forEach((variable, type) -> {
        //     if (this.types.containsKey(variable)) {
        //         Type newType = variable.substitute(variable, type);
        //         Type oldType = this.types.get(variable);
        //         if (oldType instanceof UnknownType) {
        //             this.types.put(variable, newType);
        //             // System.out.println("New type !");
        //             // this.typeStack.updateVar(variable.getVarName(), type);
        //         }
        //         if (type instanceof UnknownType ut) {
        //             this.types.put(ut, oldType);
        //         }
        //     } else {
        //         if (this.types.containsValue(variable)) {
        //             this.types.forEach((key, value) -> {
        //                 if (value.contains(variable)) {
        //                     this.types.put(key, value.substitute(variable, type));
        //                 }
        //             });
        //         } else {
        //             this.types.put(variable, type);
        //         }
        //     }
        // });
    }
        // System.out.println(this.types);
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
        Type t = visit(p1);
        addConstraint(t.unify(new PrimitiveType(Type.Base.BOOL)));
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitComparison(grammarTCLParser.ComparisonContext ctx) {
        // System.out.println("visit comparison : expr op expr");
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        addConstraint(t1.unify(new PrimitiveType(Type.Base.INT)));
        addConstraint(t3.unify(new PrimitiveType(Type.Base.INT)));
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitOr(grammarTCLParser.OrContext ctx) {
        // System.out.println("visit or");
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        addConstraint(t1.unify(new PrimitiveType(Type.Base.BOOL)));
        addConstraint(t3.unify(new PrimitiveType(Type.Base.BOOL)));
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitOpposite(grammarTCLParser.OppositeContext ctx) {
        // System.out.println("visit opposite");
        ParseTree p1 = ctx.getChild(1);
        Type t = visit(p1);
        addConstraint(t.unify(new PrimitiveType(Type.Base.INT)));
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
        Type t0 = visit(p0);
        ParseTree p2 = ctx.getChild(2);
        Type t2 = visit(p2);
        addConstraint(t0.unify(new ArrayType(new UnknownType())));
        addConstraint(t2.unify(new PrimitiveType(Type.Base.INT)));
        return ((ArrayType) t0).getTabType();
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
        ParseTree p0 = ctx.getChild(0);
        UnknownType key = new UnknownType(p0);
        if (!types.containsKey(key)) {
            throwCustomError("Call does not exist at line " + getLine(ctx));
        }
        Type t = types.get(key);
        ArrayList<Type> arguments = new ArrayList<>();
        int NbChildren = ctx.getChildCount();
        if (NbChildren != 3) {
            ParseTree p1 = ctx.getChild(2);
            visit(p1);
            for (int i = 0; i < (NbChildren - 3 - 1) / 2; i++) {
                ParseTree p2 = ctx.getChild(4 + 2 * i);
                Type type = visit(p2);
                arguments.add(type);
            }
        }
        FunctionType f = new FunctionType(new UnknownType(), arguments);
        addConstraint(t.unify(f));
        return null;
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
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        addConstraint(t1.unify(new PrimitiveType(Type.Base.BOOL)));
        addConstraint(t3.unify(new PrimitiveType(Type.Base.BOOL)));
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitVariable(grammarTCLParser.VariableContext ctx) {
        // System.out.println("visit variable");

        UnknownType declVarNode = this.varStack.getVar(ctx.VAR().getText());
        UnknownType variable = new UnknownType(ctx.VAR());
        addConstraint(declVarNode.unify(variable));
        return variable;
    }

    @Override
    public Type visitMultiplication(grammarTCLParser.MultiplicationContext ctx) {
        // System.out.println("visit Multiplication");

        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        addConstraint(t1.unify(new PrimitiveType(Type.Base.INT)));
        addConstraint(t3.unify(new PrimitiveType(Type.Base.INT)));
        return new PrimitiveType(Type.Base.INT);
    }

    @Override
    public Type visitEquality(grammarTCLParser.EqualityContext ctx) {
        // System.out.println("visit equality");
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        addConstraint(t1.unify(t3));
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitTab_initialization(grammarTCLParser.Tab_initializationContext ctx) {
        // System.out.println("visit tab initialization");

        int elementsCount = (ctx.getChildCount() - 1) / 2;

        if (elementsCount == 0)
            return new ArrayType(new UnknownType());

        Type elementType = visit(ctx.getChild(1));

        if (elementsCount == 1)
            return new ArrayType(elementType);

        for (int i = 1; i < elementsCount - 1; i++) {
            int childIndex = 1 + 2 * i;
            int nextChildIndex = 1 + 2 * (i + 1);

            var element = ctx.getChild(childIndex);
            var nextElement = ctx.getChild(nextChildIndex);

            Type t1 = visit(element);
            Type t2 = visit(nextElement);

            addConstraint(t1.unify(t2));
        }

        return new ArrayType(elementType);
    }

    @Override
    public Type visitAddition(grammarTCLParser.AdditionContext ctx) {
        // System.out.println("visit addition");
        ParseTree p1 = ctx.getChild(0);
        Type t1 = visit(p1);
        ParseTree p3 = ctx.getChild(2);
        Type t3 = visit(p3);
        addConstraint(t1.unify(new PrimitiveType(Type.Base.INT)));
        addConstraint(t3.unify(new PrimitiveType(Type.Base.INT)));
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
        
        UnknownType contextVar = new UnknownType(ctx.VAR());
        this.types.put(contextVar, type);
        // the var name is now linked to the decl node
        this.varStack.assignVar(ctx.VAR().getText(), contextVar);
        
        if (type instanceof FunctionType) {
            throwCustomError("Type error: function type cannot be declared at line " + getLine(ctx));
        }

        ParseTree variableNode = ctx.getChild(1);
        UnknownType variable = new UnknownType(variableNode);
        addConstraint(variable.unify(type));

        // cas : "auto a = b;"
        if (ctx.getChildCount() == 5) {
            ParseTree exprNode = ctx.getChild(3);
            Type exprType = visit(exprNode);
            addConstraint(type.unify(exprType));
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

        this.bigAssSubstitute();

        Type varType = getVarType(varName);
        
        if (!isPrintable(varType)) {
            throwCustomError("Type error: function type cannot be printed at line " + getLine(ctx));
        }
        return null;
    }

    @Override
    public Type visitAssignment(grammarTCLParser.AssignmentContext ctx) {
        // System.out.println("visit assignment : VAR ('[' expr ']')* ASSIGN expr SEMICOL");
        ParseTree firstVariableNode = ctx.getChild(0);
        UnknownType firstVariable = new UnknownType(firstVariableNode);
        int nbChildren = ctx.getChildCount();
        // no tab access
        if (nbChildren == 4) {
            ParseTree expressionNode = ctx.getChild(2);
            Type expression = visit(expressionNode);
            addConstraint(firstVariable.unify(expression));
        } else {
            // tab access
            int nbBrackets = (nbChildren - 4) / 3;
            ParseTree expressionNode = ctx.getChild(nbChildren - 2);
            Type expression = visit(expressionNode);
            for (int i = 0; i < nbBrackets; i++) {
                int currentBracketIndex = 2 + (3 * i);
                ParseTree tabIndexNode = ctx.getChild(currentBracketIndex);
                Type tabIndexType = visit(tabIndexNode);
                addConstraint(tabIndexType.unify(new PrimitiveType(Type.Base.INT)));
                expression = new ArrayType(expression);
            }
            addConstraint(firstVariable.unify(expression));
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
        Type conditionType = visit(conditionNode);
        addConstraint(conditionType.unify(new PrimitiveType(Type.Base.BOOL)));
       
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
        Type testType = visit(testNode);
        addConstraint(testType.unify(new PrimitiveType(Type.Base.BOOL)));
        
        ParseTree instructionNode = ctx.getChild(4);
        this.enterBlock();
        visit(instructionNode);
        this.leaveBlock();
        return null;
    }

    @Override
    public Type visitFor(grammarTCLParser.ForContext ctx) { // nik ta mère
        // System.out.println("visit for : FOR '(' instr  expr ';' instr ')' instr");
        this.enterBlock();
        ParseTree initializationNode = ctx.getChild(2);
        visit(initializationNode);
        
        ParseTree expressionNode = ctx.getChild(3);
        Type expressionType = visit(expressionNode);
        addConstraint(expressionType.unify(new PrimitiveType(Type.Base.BOOL)));
        
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
        FunctionType declFunction = (FunctionType) this.types.get(this.functionList.getLast());
        Type declReturnType = declFunction.getReturnType();
        addConstraint(returnType.unify(declReturnType));
        return null;
    }

    @Override
    public Type visitDecl_fct(grammarTCLParser.Decl_fctContext ctx) {
        // System.out.println("visit declare function : type VAR '(' (type VAR (',' type VAR)*)? ')' core_fct");
        ParseTree functionReturnTypeNode = ctx.getChild(0);
        ParseTree functionNameNode = ctx.getChild(1);

        UnknownType functionName = new UnknownType(functionNameNode);
        Type functionReturnType = visit(functionReturnTypeNode);

        // we stack the type of the function for later
        this.functionList.add(functionName);

        int childCount = ctx.getChildCount();
        boolean noParameters = childCount == 5;

        enterFunction();

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
                
                // TODO: remove
                addConstraint(paramName.unify(paramType));
                
                this.types.put(paramName, paramType);
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

        UnknownType main = new UnknownType("main", 12);
        Type mainType = new FunctionType(new PrimitiveType(Type.Base.INT), new ArrayList<>());
        this.types.put(main, mainType);

        int childCount = ctx.getChildCount();

        // visit functions
        for (int i = 0; i < childCount - 3; i++) {
            ParseTree decl_fctNode = ctx.getChild(i);
            visit(decl_fctNode);
        }

        // visit main function
        ParseTree core_fctNode = ctx.getChild(childCount - 2);
        enterFunction();
        
        // we stack the type of the function for later
        ParseTree funcMain = ctx.getChild(childCount - 3);
        UnknownType funcNameNode = new UnknownType(funcMain);
        this.functionList.add(funcNameNode);
        this.types.put(funcNameNode, new FunctionType(new PrimitiveType(Type.Base.INT), new ArrayList<>()));
        
        visit(core_fctNode);
        
        leaveFunction();


        

        return null;
    }

}
