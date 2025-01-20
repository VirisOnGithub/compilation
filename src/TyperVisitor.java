package src;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import src.Type.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TyperVisitor extends AbstractParseTreeVisitor<Type> implements grammarTCLVisitor<Type> {

    private final Map<UnknownType,Type> types = new HashMap<>();
    private Type lastReturnType = null;
    private final VarStack<UnknownType, Type> typesStack = new VarStack<>();

    public Map<UnknownType, Type> getTypes() {
        return types;
    }

    private void enterBlock() {
        System.out.println("enterBlock");
        this.typesStack.enterBlock();
        System.out.println(this.typesStack);
    }

    /**
     * Lors de la sortie d'un block, pop la stack et met ses valeurs dans types.
     */
    private void leaveBlock() {
        System.out.println("leaveBlock");
        Map<UnknownType, Type> typesBlock = this.typesStack.pop();

        types.putAll(typesBlock);
        System.out.println("types = "+this.types);
    }

    private boolean isKnown(Type type) {
        if (type instanceof PrimitiveType) {
            return true;
        }
        if (type instanceof ArrayType) {
            return isKnown(((ArrayType) type).getTabType());
        }
        return false;
    }

    private void substituteTypes(HashMap<UnknownType, Type> constraints) {
        AtomicBoolean isFinish = new AtomicBoolean(true);
        Map<UnknownType, Type> newConstraints = new HashMap<>();
        Map<UnknownType, Type> oldConstraints = new HashMap<>();
        do {
            isFinish.set(true);
            constraints.forEach((variable, type) -> {
                if (!this.typesStack.contains(variable)) {
                    this.typesStack.assignVar(variable, type);
                } else {
                    Stack<Map<UnknownType, Type>> oldTypesStack = this.typesStack.getStack();
                    Stack<Map<UnknownType, Type>> newTypesStack = (Stack<Map<UnknownType, Type>>) oldTypesStack.clone();

                    oldTypesStack.forEach((layer) -> {
                        oldConstraints.put(variable, type);
                        int indexOfTmp = newTypesStack.indexOf(layer);
                        HashMap<UnknownType, Type> newLayer = new HashMap<>(layer);
                        if (layer.containsKey(variable)) {
                            if (isKnown(type)) {
                                Type oldTypeToSubstitute = this.typesStack.getLastTypeOfVarName(variable.getVarName());
                                newConstraints.putAll(oldTypeToSubstitute.unify(type));
                                isFinish.set(false);
                            }
                            newLayer.put(variable, variable.substituteAll(constraints));
                        } else {
                            layer.forEach((key, value) -> {
                                if (value.contains(variable)) {
                                    newLayer.put(key, value.substituteAll(constraints));
                                }
                            });
                        }
                        newTypesStack.set(indexOfTmp, newLayer);
                    });

                    this.typesStack.setStack(newTypesStack);

                }
            });
            oldConstraints.forEach(constraints::remove);
            constraints.putAll(newConstraints);
        } while (!isFinish.get());
        System.out.println(this.typesStack);
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
        System.out.println("visit tabAccess : expr '[' index(:expr) ']'");
        ParseTree exprNode = ctx.getChild(0);
        Type exprType = visit(exprNode);
        ParseTree indexNode = ctx.getChild(2);
        Type indexType = visit(indexNode);

        UnknownType exprUT = new UnknownType(exprNode);
        String exprName = exprUT.getVarName();

        HashMap<UnknownType, Type> constraints = new HashMap<>();
        UnknownType contentType = new UnknownType();
        try {
            constraints.putAll(exprType.unify(new ArrayType(contentType)));

            if (this.typesStack.getLastTypeOfVarName(exprName) == null) {
                constraints.putAll(exprUT.unify(exprType));
            }

            this.substituteTypes(constraints);

            constraints.putAll(indexType.unify(new PrimitiveType(Type.Base.INT)));
        } catch (Error e) {
            throw new TyperError(e.getMessage(), ctx);
        }

        if (exprType instanceof UnknownType) {
            System.out.println(new UnknownType(exprNode).getVarName());
            exprType = this.typesStack.getLastTypeOfVarName(new UnknownType(exprNode).getVarName());
            constraints.putAll(exprType.unify(new ArrayType(new UnknownType())));
        }

        this.substituteTypes(constraints);

        return  contentType;
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
        FunctionType functionType;
        if(typesStack.containsVarName(key.getVarName())) {
                functionType =(FunctionType) typesStack.getLastTypeOfVarName(key.getVarName());
        } else {
            throw new TyperError("Call of undefined function \"" + calledFunctionNode.getText() + "\"", ctx);
        }
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
        System.out.println("Type de la fonction : " + functionType);
        return functionType.getReturnType();
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

        ParseTree varNode = ctx.getChild(0);
        String varName = varNode.getText();
        Type varType = this.typesStack.getLastTypeOfVarName(varName);

        if (varType == null) {
            throw new TyperError("Variable use but not defined yet", ctx);
        }

        return varType;
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
        System.out.println("visit tab initialization : '{' (expr (',' expr)*)? '}'");
        HashMap<UnknownType, Type> constraints = new HashMap<>();
        Type type = new UnknownType();
        int nbChild = ctx.getChildCount();

        if (nbChild > 2) {
            Type previousExprType = new UnknownType();
            for (int i = 1; i <= nbChild - 2; i += 2) {
                ParseTree exprAtPositionI = ctx.getChild(i);
                Type exprType = visit(exprAtPositionI);
                if (exprType instanceof FunctionType) {
                    throw new TyperError("expr : '"+ exprAtPositionI.getText() +"' in tab initialization : '"+ ctx.getText()
                                +"' cannot be a Function",
                            (ParserRuleContext) ctx.getChild(i));
                }
                try {
                    if (i > 1) {
                        constraints.putAll(exprType.unify(previousExprType));
                    }

                } catch (Error e) {
                    throw new TyperError(exprAtPositionI.getText() +" is not the same type as "+ previousExprType +"\n"
                                + exprAtPositionI.getText() +" : "+ exprType +"\n"
                                + previousExprType +" : "+ previousExprType,
                            (ParserRuleContext) ctx.getChild(i));
                }

                type = exprType;
                previousExprType = exprType;
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
        HashMap<UnknownType, Type> constraints = new HashMap<>(array.unify(new UnknownType()));
        substituteTypes(constraints);
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

        if (this.typesStack.isVarNameInLastStack(variable.getVarName())) {
            throw new TyperError("Variable already defined", ctx);
        }

        HashMap<UnknownType, Type> constraints = new HashMap<>(variable.unify(type));
        this.substituteTypes(constraints);

        // cas : "auto a = b;"
        if (ctx.getChildCount() == 5){
            ParseTree exprNode = ctx.getChild(3);
            Type exprType = visit(exprNode);
            try {
                constraints.putAll(type.unify(exprType));
                constraints.putAll(variable.unify(exprType));
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

        if (!(this.typesStack.containsVarName(parameter.getVarName()))) {
            throw new TyperError("Type error: variable "+parameter.getVarName()+" isn't defined", ctx, 6);
        }
        Type paramType = this.typesStack.getLastTypeOfVarName(parameter.getVarName());
        if (paramType instanceof FunctionType) {
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
        UnknownType firstVariable = this.typesStack.getLastUTOfVarName((new UnknownType(firstVariableNode)).getVarName());
        HashMap<UnknownType, Type> constraints = new HashMap<>();
        System.out.println("!!!!!!" + constraints);
        int nbChildren = ctx.getChildCount();
        if (nbChildren == 4) {
            // Si on n'a pas de tableau
            ParseTree expressionNode = ctx.getChild(2);
            Type expression = visit(expressionNode);
            try {
                constraints.putAll(firstVariable.unify(expression));
                System.out.println("!!!!!!" + constraints);
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
        this.enterBlock();

        int nbChildren = ctx.getChildCount();
        for (int i = 1; i < nbChildren - 1; i++) {
            ParseTree child = ctx.getChild(i);
            visit(child);
        }

        System.out.println("Types of block n°" + this.typesStack.size() + " : " + this.typesStack.getLastStack());

        this.leaveBlock();
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

        try {
            System.out.println("LAAAAAAAA"+ this.lastReturnType);
            HashMap<UnknownType, Type> constraints = new HashMap<>(exprType.unify(this.lastReturnType));
            System.out.println("Contraintes " + constraints);
            this.substituteTypes(constraints);
        } catch (Error e) {
            throw new TyperError("Tous les return doivent avoir le même type", ctx);
        }

        this.lastReturnType = exprType;


        return exprType;
    }

    @Override
    public Type visitCore_fct(grammarTCLParser.Core_fctContext ctx) throws TyperError {
        System.out.println("Visit core function : '{' instr* RETURN expr SEMICOL '}'");
        int nbChildrenWithoutInstr = 5;
        int nbChildren = ctx.getChildCount();
        for (int i = 1; i <= nbChildren - nbChildrenWithoutInstr; i++){
            ParseTree p = ctx.getChild(i);
            visit(p);
        }

        int returnExprIndex = nbChildren - 3;
        ParseTree returnExpr = ctx.getChild(returnExprIndex);
        Type returnType = visit(returnExpr);

        try {
            HashMap<UnknownType, Type> constraints = new HashMap<>(returnType.unify(this.lastReturnType));
            System.out.println("Contraintes " + constraints);
            this.substituteTypes(constraints);
        } catch (Error e) {
            throw new TyperError("Problème return", ctx);
        }

        return returnType;
    }

    @Override
    public Type visitDecl_fct(grammarTCLParser.Decl_fctContext ctx) throws TyperError {
        ParseTree functionReturnTypeNode = ctx.getChild(0);
        Type functionReturnType = visit(functionReturnTypeNode);

        ParseTree functionNameNode = ctx.getChild(1);
        UnknownType functionName = new UnknownType(functionNameNode);
        if (this.typesStack.containsVarName(functionName.getVarName())) {
            throw new TyperError("This name is already used", ctx);
        }

        HashMap<UnknownType, Type> paramConstraints = new HashMap<>();
        ArrayList<Type> paramTypeList = new ArrayList<>();
        int childCount = ctx.getChildCount();

        boolean hasParameters = childCount > 5;
        if (hasParameters) {

            int paramNumber = (childCount - 4)/3;
            for (int k = 0; k < paramNumber; k++) {
                int currentTypeIndex = (3*k)+3;
                ParseTree paramTypeNode = ctx.getChild(currentTypeIndex);
                ParseTree paramNameNode = ctx.getChild(currentTypeIndex+1);
                Type paramType = visit(paramTypeNode);
                UnknownType paramName = new UnknownType(paramNameNode);

                try {
                    paramConstraints.putAll(paramName.unify(paramType));
                } catch (Error e) {
                    throw new TyperError(e.getMessage(), ctx);
                }
                paramTypeList.add(paramType);
            }
        }

        FunctionType functionType = new FunctionType(functionReturnType, paramTypeList);

        HashMap<UnknownType, Type> functionConstraints = new HashMap<>(functionName.unify(functionType));
        //Mise des types de la fonction et son nom au layer 0 ("variable globale")
        //Pour que cela soit utilisable tout au long du programme
        this.substituteTypes(functionConstraints);

        //Passage dans le layer d'au-dessus
        //Parce que les noms de paramÃ¨tres et les variables du core_fct
        // ne sont pas des variables globales
        this.enterBlock(); // pas utile pour moi (Luka)

        this.substituteTypes(paramConstraints);
        HashMap<UnknownType, Type> returnConstraints = new HashMap<>(functionReturnType.unify(new UnknownType()));
        this.substituteTypes(returnConstraints);

        this.lastReturnType = functionReturnType;

        int core_fctIndex = childCount - 1;
        ParseTree core_fctNode = ctx.getChild(core_fctIndex);
        visit(core_fctNode);

        this.leaveBlock();

        UnknownType oldFunction = this.typesStack.getLastUTOfVarName(functionName.getVarName());
        FunctionType oldFunctionType = (FunctionType) this.typesStack.getLastTypeOfVarName(functionName.getVarName());
        HashMap<UnknownType, Type> lastConstraint = (HashMap<UnknownType, Type>) oldFunction.unify(new FunctionType(this.lastReturnType, oldFunctionType.getArgs()));
        substituteTypes(lastConstraint);

        return null;
    }

    @Override
    public Type visitMain(grammarTCLParser.MainContext ctx) throws TyperError {
        System.out.println("visit main : decl_fct* 'int main()' core_fct EOF;");

        PrimitiveType intType = new PrimitiveType(Type.Base.INT);

        UnknownType main = new UnknownType("main", 0);
        Type mainType    = new FunctionType(intType, new ArrayList<>());

        HashMap<UnknownType, Type> constraints = new HashMap<>(main.unify(mainType));
        substituteTypes(constraints);

        int childCount = ctx.getChildCount();
        boolean noDecl_fct = childCount == 3;
        if (!noDecl_fct) {
            for (int i = 0; i < childCount - 3; i++){
                ParseTree decl_fctNode = ctx.getChild(i);
                visit(decl_fctNode);
            }
        }

        this.lastReturnType = intType;

        ParseTree core_fctNode = ctx.getChild(childCount-2);
        visit(core_fctNode);

        this.leaveBlock();

        return null;
    }
}
