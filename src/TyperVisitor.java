package src;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import src.Type.*;

import java.util.*;

public class TyperVisitor extends AbstractParseTreeVisitor<Type> implements grammarTCLVisitor<Type> {

    private final Map<UnknownType,Type> types = new HashMap<>();
    private final VarStack<Type> stack = new VarStack<>();
    private final CallStack callStack = new CallStack();

    public Map<UnknownType, Type> getTypes() {
        return types;
    }

    private void substituteTypes(HashMap<UnknownType, Type> constraints) {
        constraints.forEach((variable, type) -> {
            if (this.types.containsKey(variable)) {
                Type newType = variable.substitute(variable, type);
                Type oldType = this.types.get(variable);
                if (oldType instanceof UnknownType) {
                    this.types.put(variable, newType);
                    System.out.println("Add : " + variable + " : " + newType);
                }
                if (type instanceof UnknownType) {
                    this.types.put((UnknownType) type, oldType);
                    System.out.println("Add : " + type + " : " + oldType);
                }
            } else {
                if (this.types.containsValue(variable)) {
                    this.types.forEach((key, value) -> {
                        if (value.contains(variable)) {
                            this.types.put(key, value.substitute(variable, type));
                            System.out.println("Add : " + variable + " : " + type);
                        }
                    });
                } else {
                    this.types.put(variable, type);
                    System.out.println("Add : " + variable + " : " + type);
                }
            }
        });
        System.out.println(this.types);
    }

    @Override
    public Type visitNegation(grammarTCLParser.NegationContext ctx) throws TyperError {
        System.out.println("visit negation : NOT expr");
        ParseTree negExpr = ctx.getChild(1);
        Type t = visit(negExpr);
        HashMap<UnknownType, Type> constraints;
        try {
            constraints = new HashMap<>(t.unify(new PrimitiveType(Type.Base.BOOL)));
        } catch (Error e) {
            throw new TyperError(negExpr.getText() + " is not a boolean", ctx);
        }
        this.substituteTypes(constraints);
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitComparison(grammarTCLParser.ComparisonContext ctx) throws TyperError {
        System.out.println("visit comparison : expr op expr");
        ParseTree leftArgNode = ctx.getChild(0);
        Type leftArgType = visit(leftArgNode);
        ParseTree rightArgNode = ctx.getChild(2);
        Type rightArgType = visit(rightArgNode);
        HashMap<UnknownType, Type> constraints;
        try{
            constraints = new HashMap<>(leftArgType.unify(new PrimitiveType(Type.Base.INT)));
        } catch (Error e) {
            throw new TyperError(leftArgNode.getText() + " is not an int", ctx);
        }
        try {
            constraints.putAll(rightArgType.unify(new PrimitiveType(Type.Base.INT)));
        } catch (Error e) {
            throw new TyperError(rightArgNode.getText() + " is not an int", (ParserRuleContext) ctx.getChild(2));
        }
        this.substituteTypes(constraints);
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitOr(grammarTCLParser.OrContext ctx) throws TyperError {
        System.out.println("visit or");
        ParseTree leftArgNode = ctx.getChild(0);
        Type leftArgType = visit(leftArgNode);
        ParseTree rightArgNode = ctx.getChild(2);
        Type rightArgType = visit(rightArgNode);
        HashMap<UnknownType, Type> constraints;
        try {
            constraints = new HashMap<>(leftArgType.unify(new PrimitiveType(Type.Base.BOOL)));
        } catch (Error e) {
            throw new TyperError(leftArgNode.getText() + "is not a boolean", ctx);
        }
        try {
            constraints.putAll(rightArgType.unify(new PrimitiveType(Type.Base.BOOL)));
        } catch (Error e) {
            throw new TyperError(rightArgNode.getText() + "is not a boolean", (ParserRuleContext) ctx.getChild(2));
        }
        this.substituteTypes(constraints);
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitOpposite(grammarTCLParser.OppositeContext ctx) throws TyperError {
        System.out.println("visit opposite");
        ParseTree argNode = ctx.getChild(1);
        Type argType = visit(argNode);
        HashMap<UnknownType, Type> constraints;
        try {
            constraints = new HashMap<>(argType.unify(new PrimitiveType(Type.Base.INT)));
        } catch (Error e) {
            throw new TyperError(argNode.getText() + " is not an int", (ParserRuleContext) ctx.getChild(1));
        }
        this.substituteTypes(constraints);
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
        try {
            constraints.putAll(t2.unify(new PrimitiveType(Type.Base.INT)));
        } catch (Error e) {
            throw new TyperError(e.getMessage(), ctx);
        }
        this.substituteTypes(constraints);
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
        ParseTree calledFunctionNode = ctx.getChild(0);
        UnknownType key = new UnknownType(calledFunctionNode);
        if (!types.containsKey(key)){
            throw new TyperError("Call does not exist", ctx);
        }
        Type functionType = types.get(key);
        ArrayList<Type> arguments = new ArrayList<>();
        int NbChildren = ctx.getChildCount();
        if(NbChildren != 3){
            for(int i = 0; i <= (NbChildren - 3 - 1)/2; i++){
                ParseTree attributeNode = ctx.getChild(2 + 2 * i);
                Type attributeType = visit(attributeNode);
                arguments.add(attributeType);
            }
        }
        FunctionType f = new FunctionType(new UnknownType(), arguments);
        HashMap<UnknownType, Type> constraints;
        try {
            constraints = new HashMap<>(functionType.unify(f));
        } catch (Error e) {
            throw new TyperError(e.getMessage(), ctx);
        }
        this.substituteTypes(constraints);
        System.out.println("TEST : SubstituteTypes du call ok");
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
        ParseTree leftArgNode = ctx.getChild(0);
        Type leftArgType = visit(leftArgNode);
        ParseTree rightArgNode = ctx.getChild(2);
        Type rightArgType = visit(rightArgNode);
        HashMap<UnknownType, Type> constraints;
        try {
            constraints = new HashMap<>(leftArgType.unify(new PrimitiveType(Type.Base.BOOL)));
        } catch (Error e) {
            throw new TyperError(leftArgNode.getText() + "is not a boolean", ctx);
        }
        try {
            constraints.putAll(rightArgType.unify(new PrimitiveType(Type.Base.BOOL)));
        } catch (Error e) {
            throw new TyperError(rightArgNode.getText() + "is not a boolean", (ParserRuleContext) ctx.getChild(2));
        }

        this.substituteTypes(constraints);
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitVariable(grammarTCLParser.VariableContext ctx) throws TyperError {
        System.out.println("visit variable");

        System.out.println(this.types);

        ParseTree varNode = ctx.getChild(0);
        UnknownType ut = new UnknownType(varNode);
        Type result = this.types.getOrDefault(ut, ut);
        if (!this.types.containsKey(ut)) {
            this.types.put(ut, new UnknownType());
        }
        return result;
    }

    @Override
    public Type visitMultiplication(grammarTCLParser.MultiplicationContext ctx) throws TyperError {
        System.out.println("Visit Multiplication");

        ParseTree leftArgNode = ctx.getChild(0);
        Type leftArgType = visit(leftArgNode);
        ParseTree rightArgNode = ctx.getChild(2);
        Type rightArgType = visit(rightArgNode);
        HashMap<UnknownType, Type> constraints;
        try {
            constraints = new HashMap<>(leftArgType.unify(new PrimitiveType(Type.Base.INT)));
        } catch (Error e) {
            throw new TyperError(leftArgNode.getText() + " is not an int", ctx);
        }
        try {
            constraints.putAll(rightArgType.unify(new PrimitiveType(Type.Base.INT)));
        } catch (Error e) {
            throw new TyperError(rightArgNode.getText() + " is not an int", (ParserRuleContext) ctx.getChild(2));
        }
        this.substituteTypes(constraints);
        return new PrimitiveType(Type.Base.INT);
    }

    @Override
    public Type visitEquality(grammarTCLParser.EqualityContext ctx) throws TyperError {
        System.out.println("visit equality");
        ParseTree leftExpressionNode = ctx.getChild(0);
        Type leftExpressionType = visit(leftExpressionNode);
        ParseTree rightExpressionNode = ctx.getChild(2);
        Type rightExpressionType = visit(rightExpressionNode);
        HashMap<UnknownType, Type> constraints;
        try {
            constraints = new HashMap<>(leftExpressionType.unify(rightExpressionType));
        } catch(Error e){
            throw new TyperError(leftExpressionNode.getText() + " and " + rightExpressionNode.getText() + " have not the same type", (ParserRuleContext) ctx.getChild(1));
        }
        this.substituteTypes(constraints);
        return new PrimitiveType(Type.Base.BOOL);
    }

    @Override
    public Type visitTab_initialization(grammarTCLParser.Tab_initializationContext ctx) throws TyperError {
        System.out.println("visit tab initialization");
        HashMap<UnknownType, Type> constraints = new HashMap<>();
        Type type = new UnknownType();

        // On teste si il y a un contenu dans les accolades
        if (ctx.getChildCount() > 2) {
            for (int i = 1; i < ctx.getChildCount() - 3; i += 2) {
                ParseTree exprAtPositionI = ctx.getChild(i);
                Type exprType = visit(exprAtPositionI);
                ParseTree exprAtNextPosition = ctx.getChild(i + 2);
                Type nextExprType = visit(exprAtNextPosition);
                try {
                    constraints.putAll(exprType.unify(nextExprType));
                } catch (Error e) {
                    throw new TyperError(exprAtPositionI.getText() + "is not the same type as " + exprAtNextPosition.getText() + "\n" + exprAtPositionI.getText() + " : " + exprType + "\n" + exprAtNextPosition.getText() + " : " + nextExprType, (ParserRuleContext) ctx.getChild(i));
                }
                if (!(exprType instanceof UnknownType)){
                    type = exprType;
                }

            }
        }
        this.substituteTypes(constraints);
        return new ArrayType(type);
    }

    @Override
    public Type visitAddition(grammarTCLParser.AdditionContext ctx) throws TyperError {
        System.out.println("visit addition");
        ParseTree leftOperandNode = ctx.getChild(0);
        Type leftOperandType = visit(leftOperandNode);
        ParseTree rightOperandNode = ctx.getChild(2);
        Type rightOperandType = visit(rightOperandNode);
        HashMap<UnknownType, Type> constraints;
        try {
            constraints = new HashMap<>(leftOperandType.unify(new PrimitiveType(Type.Base.INT)));
        } catch (Error e) {
            throw new TyperError(leftOperandNode.getText() + " is not an int", (ParserRuleContext) leftOperandNode);
        }
        try {
            constraints.putAll(rightOperandType.unify(new PrimitiveType(Type.Base.INT)));
        } catch (Error e) {
            throw new TyperError(rightOperandNode.getText() + " is not an int", (ParserRuleContext) rightOperandNode);
        }
        this.substituteTypes(constraints);
        return new PrimitiveType(Type.Base.INT);
    }

    @Override
    public Type visitBase_type(grammarTCLParser.Base_typeContext ctx) throws TyperError {
        System.out.println("Visit base type : BASE_TYPE");

        ParseTree baseTypeNode = ctx.getChild(0);
        String baseType = baseTypeNode.getText();

        if (!isValidBaseType(baseType)) {
            throw new TyperError(
                    "The supplied type is not a base type\nType provided : " + baseType, ctx
            );
        }

        return switch (baseType) {
            case "int"  -> new PrimitiveType(Type.Base.INT);
            case "bool" -> new PrimitiveType(Type.Base.BOOL);
            case "auto" -> new UnknownType();
            default     -> null;
        };
    }
    private static final String[] VALID_BASE_TYPES = {"int", "bool", "auto"};
    private boolean isValidBaseType(String text) {
        return Arrays.asList(VALID_BASE_TYPES).contains(text);
    }

    @Override
    public Type visitTab_type(grammarTCLParser.Tab_typeContext ctx) throws TyperError {
        System.out.println("Visit tab type");
        ParseTree baseTypeNode = ctx.getChild(0);
        Type baseType = visit(baseTypeNode);
        ArrayType array = new ArrayType(baseType);
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
            try {
                constraints.putAll(type.unify(exprType));
            } catch (Error e) {
                throw new TyperError(e.getMessage(), ctx);
            }
        }
        this.substituteTypes(constraints);
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
        ArrayList<String> reservedKeywords = new ArrayList<>(List.of("int", "bool", "auto", "void", "if", "else", "while", "for", "return", "main"));
        if(reservedKeywords.contains(firstVariableNode.getText())){
            throw new TyperError("Keyword is not allowed for variable name", ctx, 1);
        }
        UnknownType firstVariable = new UnknownType(firstVariableNode);
        HashMap<UnknownType, Type> constraints = new HashMap<>();
        int nbChildren = ctx.getChildCount();
        if (nbChildren == 4) {
            // Si on n'a pas de tableau
            ParseTree expressionNode = ctx.getChild(2);
            Type expression = visit(expressionNode);
            try {
                constraints.putAll(firstVariable.unify(expression));
            } catch (Error e) {
                throw new TyperError(e.getMessage(), ctx);
            }
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
            try {
                constraints.putAll(firstVariable.unify(expression));
            } catch (Error e) {
                throw new TyperError(e.getMessage(), ctx);
            }
        }

        this.substituteTypes(constraints);
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
        HashMap<UnknownType, Type> constraints;
        try {
            constraints = new HashMap<>(conditionType.unify(new PrimitiveType(Type.Base.BOOL)));
        } catch (Error e) {
            throw new TyperError(e.getMessage(), ctx);
        }
        ParseTree ifInstrNode = ctx.getChild(4);
        visit(ifInstrNode);
        if (ctx.getChildCount() == 7) {
            // Si on a un "else"
            ParseTree elseInstrNode = ctx.getChild(6);
            visit(elseInstrNode);
        }
        this.substituteTypes(constraints);
        return null;
    }

    @Override
    public Type visitWhile(grammarTCLParser.WhileContext ctx) throws TyperError {
        System.out.println("visit while : WHILE '(' expr ')' instr");
        ParseTree testNode = ctx.getChild(2);
        Type testType = visit(testNode);
        HashMap<UnknownType, Type> constraints;
        try {
            constraints = new HashMap<>(testType.unify(new PrimitiveType(Type.Base.BOOL)));
        } catch (Error e) {
            throw new TyperError(e.getMessage(), ctx);
        }
        ParseTree instructionNode = ctx.getChild(4);
        visit(instructionNode);
        this.substituteTypes(constraints);
        return null;
    }

    @Override
    public Type visitFor(grammarTCLParser.ForContext ctx) throws TyperError {
        System.out.println("visit for : FOR '(' instr  expr ';' instr ')' instr");
        // Ne pas oublier : avec la syntaxe actuelle, on écrit :
        // for(int i = 0; i < 10; i = i + 1;){ ... }
        // Le dernier point-virgule est nécessaire !
        ParseTree initializationNode = ctx.getChild(2); // int i = 0;
        visit(initializationNode);
        ParseTree expressionNode = ctx.getChild(3); // i < 10;
        Type expressionType = visit(expressionNode);
        HashMap<UnknownType, Type> constraints;
        try {
            constraints = new HashMap<>(expressionType.unify(new PrimitiveType(Type.Base.BOOL)));
        } catch (Error e) {
            throw new TyperError(e.getMessage(), ctx);
        }
        this.substituteTypes(constraints);
        ParseTree postLoopInstructionNode = ctx.getChild(5); // i = i + 1
        visit(postLoopInstructionNode);
        ParseTree contentNode = ctx.getChild(7); // bloc d'instructions
        visit(contentNode);
        return null;
    }

    @Override
    public Type visitReturn(grammarTCLParser.ReturnContext ctx) throws TyperError {
        System.out.println("visit return : RETURN expr SEMICOL ");
        ParseTree expr = ctx.getChild(1);
        Type exprType = visit(expr);
        return exprType;
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
        ParseTree returnExpr = ctx.getChild(returnExprIndex);
        return visit(returnExpr);
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
            Type core_fctType = visit(core_fctNode);

            HashMap<UnknownType, Type> constraints;
            try {
                constraints = new HashMap<>(functionReturnType.unify(core_fctType));
            } catch (Error e) {
                throw new TyperError(e.getMessage(), ctx);
            }
            this.substituteTypes(constraints);

        } else {
            ArrayList<Type> paramList = new ArrayList<>();

            int paramNumber = (childCount - 4)/3;
            for (int k = 0; k < paramNumber; k++) {
                int currentTypeIndex = (3*k)+3;
                ParseTree paramTypeNode = ctx.getChild(currentTypeIndex);
                ParseTree paramNameNode = ctx.getChild(currentTypeIndex+1);
                Type paramType = visit(paramTypeNode);
                UnknownType paramName = new UnknownType(paramNameNode);

                try {
                    paramName.unify(paramType);
                } catch (Error e) {
                    throw new TyperError(e.getMessage(), ctx);
                }
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
