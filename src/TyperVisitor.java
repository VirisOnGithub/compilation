package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import src.Type.*;

public class TyperVisitor extends AbstractParseTreeVisitor<Type> implements grammarTCLVisitor<Type> {

    private static class State {
        private final Map<UnknownType, Type> types;
        private final Map<UnknownType, List<Type>> constraints;

        public State() {
            this.types = new HashMap<>();
            this.constraints = new HashMap<>();
        }

        public State(State other) {
            this.types = new HashMap<>();
            this.types.putAll(other.getTypes());
            this.constraints = new HashMap<>();
            // boring copying stuff
            Map<UnknownType, List<Type>> map = new HashMap<>();
            for (var entry : other.getConstraints().entrySet()) {
                List<Type> types = new ArrayList<>();
                types.addAll(entry.getValue());
                map.put(entry.getKey(), types);
            }
        }

        public Map<UnknownType, Type> getTypes() {
            return types;
        }

        public Map<UnknownType, List<Type>> getConstraints() {
            return constraints;
        }
    }

    // map name to visible variables
    private final VarStack<UnknownType> varStack = new VarStack<>();
    // variables that MUST have a valid type
    private final Stack<List<UnknownType>> printStack = new Stack<>();
    // map function names to functions
    private final Map<String, UnknownType> functionList = new HashMap<>();
    // these are the variables we will backup up after a call
    private final Set<UnknownType> tempVarTypes = new HashSet<>();

    // we need that for the return
    private String lastFunctionEntered = null;

    private final State normalState = new State();
    private State tempState;

    private State currentState = normalState;

    public Map<UnknownType, Type> getTypes() {
        return this.currentState.getTypes();
    }

    private void cleanTypes() {
        for (var it = getTypes().entrySet().iterator(); it.hasNext();) {
            var entry = it.next();
            if (entry.getKey().getVarName().equals("#"))
                it.remove();
        }
    }

    public Map<UnknownType, List<Type>> getConstraints() {
        return this.currentState.getConstraints();
    }

    private void beginTempChangesMode() {
        this.bigAssSubstitute();
        this.tempState = new State(this.normalState);
        this.currentState = tempState;
    }

    private void endTempChangesMode() {
        this.tempState = null;
        this.currentState = normalState;
    }

    private void enterBlock() {
        this.varStack.enterBlock();
        this.printStack.add(new ArrayList<>());
    }

    private void leaveBlock() {
        this.debugConstraints();
        this.varStack.leaveBlock();
        forcePrintTypes();
        this.printStack.pop();
    }

    private void enterFunction(ParseTree funcNameNode, String funcName) {
        this.varStack.enterFunction();
        this.printStack.add(new ArrayList<>());
        this.lastFunctionEntered = funcName;
        this.functionList.put(funcName, new UnknownType(funcNameNode));
    }

    private void enterFunction(ParseTree funcNameNode) {
        enterFunction(funcNameNode, funcNameNode.getText());
    }

    private void leaveFunction() {
        this.bigAssSubstitute();
        this.varStack.leaveFunction();
        this.forcePrintTypes();
        this.printStack.pop();
        this.lastFunctionEntered = null;
    }

    private void addUnifyConstraint(Type t1, Type t2) {
        addConstraint(t1.unify(t2));
    }

    private void addUnifyConstraint(ParseTree var, Type... types) {
        for (Type type : types) {
            addUnifyConstraint(new UnknownType(var), type);
        }
    }

    // check the validity of print variables
    private void forcePrintTypes() {
        for (UnknownType type : this.printStack.getLast()) {
            Type varType = getVarType(type);
            System.out.println(type + " " + getVarType(type));
            if (varType == null) {
                throwCustomError("the type of the variable " + type.getVarName() + "is ambigious !");
            } else if (!isPrintable(varType)) {
                throwCustomError("Type error: var " + type.getVarName() + " cannot be printed");
            }
        }
    }

    @SuppressWarnings("unlikely-arg-type")
    private void printConstraints() {
        for (var constraint : getConstraints().entrySet()) {
            System.out.println(constraint.getKey() + "{ " + this.getTypes().get(constraint.getKey()) + " }  ~ "
                    + constraint.getValue() + "{ " + this.getTypes().get(constraint.getValue()) + " }");
        }
    }

    private void debugConstraints() {

        System.out.println("Types");

        System.out.println(this.getTypes());

        System.out.println("\nContraintes :");

        printConstraints();

        System.out.println("\n\n\n");

        this.bigAssSubstitute();

        System.out.println("Contraintes (Nouveau) :");

        printConstraints();

        System.out.println("\n\n");

        System.out.println(this.getTypes());

        System.out.println("\n\n\n");
    }

    // can return null
    private Type getVarType(Type var) {
        return getTypes().get(var);
    }

    // we can only print primitive and arrays with known type
    private boolean isPrintable(Type type) {
        if (type instanceof PrimitiveType)
            return true;
        if (type instanceof ArrayType at)
            return isPrintable(at.getTabType());
        return false;
    }

    private void addConstraintsTo(Map<UnknownType, List<Type>> dest, Map<UnknownType, Type> constraints) {
        for (var entry : constraints.entrySet()) {
            if (!dest.containsKey(entry.getKey())) {
                dest.put(entry.getKey(), new ArrayList<>());
            }

            dest.get(entry.getKey()).add(entry.getValue());
        }
    }

    private void addAllConstraintsTo(Map<UnknownType, List<Type>> dest, Map<UnknownType, List<Type>> constraints) {
        for (var entry : constraints.entrySet()) {
            if (!dest.containsKey(entry.getKey())) {
                dest.put(entry.getKey(), new ArrayList<>());
            }

            dest.get(entry.getKey()).addAll(entry.getValue());
        }
    }

    private void addConstraint(Map<UnknownType, Type> constraint) {
        // si on rajoute une contrainte avec un type qu'on connaît déjà
        // on rajoute plutôt la contrainte avec le type réel
        for (var entry : constraint.entrySet()) {
            var var = entry.getValue();
            Type varType = getVarType(var);
            if (varType != null) {
                entry.setValue(varType);
            }
        }
        addConstraintsTo(getConstraints(), constraint);
    }

    private boolean canBeSubstituted(Type type) {
        while (type instanceof ArrayType at) {
            type = at.getTabType();
        }
        if (type instanceof PrimitiveType)
            return true;
        return false;
    }

    private void substituteContraints(UnknownType var, Type varType) {
        for (var constraint : getConstraints().entrySet()) {
            // UnknownType weakVar = constraint.getKey();
            List<Type> types = constraint.getValue();
            List<Type> newTypes = new ArrayList<>();

            boolean removed = false;
            for (var it = types.iterator(); it.hasNext() && !removed;) {
                Type type = it.next();
                Type newType = varType;
                while (type instanceof ArrayType at) {
                    type = at.getTabType();
                    newType = new ArrayType(newType);
                }

                if (type.equals(var)) {
                    it.remove();
                    newTypes.add(newType);
                    break;
                }
            }

            for (Type type : newTypes) {
                types.add(type);
            }
        }
    }

    private void substituteTypes(UnknownType var, Type varType) {
        for (var entry : getTypes().entrySet()) {
            // UnknownType varEntry = entry.getKey();
            Type type = entry.getValue();
            if (type instanceof FunctionType ft) {
                ArrayList<Type> params = new ArrayList<>();
                // substitute params
                for (int i = 0; i < ft.getNbArgs(); i++) {
                    Type argType = ft.getArgsType(i);
                    if (argType.equals(var)) {
                        params.add(varType);
                    } else {
                        params.add(argType);
                    }
                }
                // substiture return type
                Type returnType = ft.getReturnType();
                if (returnType.equals(var)) {
                    returnType = varType;
                }
                entry.setValue(new FunctionType(returnType, params));
            }
        }
    }

    private void littleAssSubstitute(UnknownType var, Type varType) {
        // substitute on constraints
        substituteContraints(var, varType);

        // substitute on types (only used for functions)
        substituteTypes(var, varType);
    }

    // returns the var to substitute
    private UnknownType findVarToSubstitute(Map<UnknownType, List<Type>> newConstraints) {
        for (var constraint : getConstraints().entrySet()) {
            UnknownType var = constraint.getKey();
            List<Type> types = constraint.getValue();

            boolean sus = false;

            Type varType = getVarType(var);
            if (varType != null) {
                for (var it = types.iterator(); it.hasNext();) {
                    Type type = it.next();
                    if (type.equals(varType)) {
                        // si X := INT ~ INT
                        // => X := INT et on substitue
                        it.remove();
                        sus = true;
                    } else {
                        var newConstraint = type.unify(varType);
                        addConstraintsTo(newConstraints, newConstraint);
                    }
                }
            }

            if (sus) {
                return var;
            }
        }
        return null;
    }

    // returns true if something happened
    private boolean tryReplaceWithRealType() {
        for (var constraint : getConstraints().entrySet()) {
            UnknownType var = constraint.getKey();
            List<Type> types = constraint.getValue();

            for (var it = types.iterator(); it.hasNext();) {
                Type type = it.next();
                // unifying primitives
                if (canBeSubstituted(type)) {
                    Type varType = getVarType(var);
                    if (varType != null) {
                        // la variable a déjà un type assigné
                        varType.unify(type);
                        return true;
                    } else {
                        // la variable n'avait pas de type assigné
                        this.getTypes().put(var, type);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void bigAssSubstitute() {
        UnknownType substituteVar = null;
        Map<UnknownType, List<Type>> newConstraints = new HashMap<>();
        while (true) {
            // add new constraints
            addAllConstraintsTo(getConstraints(), newConstraints);
            newConstraints.clear();

            // try substitute
            if (substituteVar != null) {
                littleAssSubstitute(substituteVar, getVarType(substituteVar));
                substituteVar = null;
                continue;
            }

            // pour chaque type, si type primitif on substitue
            // et on le rajoute à la liste des substitués
            // et on revient au début
            // on rajoute aussi les contraintes dans l'autre sens
            substituteVar = findVarToSubstitute(newConstraints);
            if (substituteVar != null) {
                continue;
            }

            // ensuite pour chaque type
            // unifier toutes les contraintes d'un type
            // si contraint à un type primitif, et qu'il n'a pas de type, on lui affecte le
            // type
            // si déjà un type, on unifie les deux et on supprime la contrainte
            // et on lui enlève la contrainte de ce type
            // et on revient au début
            if (!tryReplaceWithRealType())
                break;
        }
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

        UnknownType temp = new UnknownType();
        addUnifyConstraint(p0, arrayType, new ArrayType(temp));
        addUnifyConstraint(p2, visit(p2), new PrimitiveType(Type.Base.INT));

        // on peut être dans une déclaration
        this.tempVarTypes.add(new UnknownType(p0));

        return temp;
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
        FunctionType funcDeclType = (FunctionType) getVarType(functionList.get(funcName));

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
        // if (!this.varStack.varExists(ctx.VAR().getText())) {
        // throwCustomError("Type error: variable " + ctx.VAR().getText() + " isn't
        // defined at line " + getLine(ctx));
        // }

        // if (!this.getTypes().containsKey(this.varStack.getVar(ctx.VAR().getText())))
        // {
        // throwCustomError("Type error: variable " + ctx.VAR().getText() + " has not
        // been assigned yet at line " + getLine(ctx));
        // }

        UnknownType declVarNode = this.varStack.getVar(ctx.VAR().getText());
        UnknownType variable = new UnknownType(ctx.VAR());
        addUnifyConstraint(variable, declVarNode);
        if (this.currentState == tempState) {
            tempVarTypes.add(declVarNode);
            tempVarTypes.add(variable);
        }
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

        // link the var name to the declaration node to find it later
        if (!this.varStack.assignVar(ctx.VAR().getText(), varUT)) {
            throwCustomError("redefinition of " + ctx.VAR().getText());
        }

        if (type instanceof FunctionType) {
            throwCustomError("Type error: function type cannot be declared at line " + getLine(ctx));
        }

        // cas : "auto a = b;"
        if (ctx.getChildCount() == 5) {
            ParseTree exprNode = ctx.getChild(3);

            beginTempChangesMode();

            Type exprType = visit(exprNode);

            addUnifyConstraint(varNode, type, exprType);

            this.bigAssSubstitute();

            // on récupère tout

            exprType = getVarType(new UnknownType(varNode));

            Map<UnknownType, Type> typesBackup = new HashMap<>();
            for (UnknownType var : this.tempVarTypes) {
                Type varType = getVarType(var);
                if (!(varType instanceof FunctionType)) {
                    typesBackup.put(var, varType);
                }
            }

            // plus besoin de ça
            this.tempVarTypes.clear();

            endTempChangesMode();

            // et on le remet

            addConstraint(typesBackup);
            addUnifyConstraint(varNode, exprType);

        } else {
            addUnifyConstraint(varNode, type);
        }
        return null;
    }

    @Override
    public Type visitPrint(grammarTCLParser.PrintContext ctx) {
        // System.out.println("visit print : PRINT '(' VAR ')' SEMICOL ");
        String varName = ctx.VAR().getText();

        if (!this.varStack.varExists(varName)) {
            throwCustomError("Type error: variable " + varName + " isn't defined at line " + getLine(ctx));
        }

        UnknownType declaredVar = this.varStack.getVar(ctx.VAR().getText());

        this.printStack.getLast().add(new UnknownType(ctx.VAR()));
        addUnifyConstraint(ctx.VAR(), declaredVar);

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
        addUnifyConstraint(variableNode, varRef);

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
        // RETURN expr SEMICOL
        int returnExprIndex = 1;
        ParseTree returnExpr = ctx.getChild(returnExprIndex);
        Type returnType = visit(returnExpr);
        FunctionType declFunction = (FunctionType) getVarType(this.functionList.get(this.lastFunctionEntered));
        Type declReturnType = declFunction.getReturnType();
        addUnifyConstraint(returnExpr, returnType, declReturnType);
        return null;
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

        FunctionType declFunction = (FunctionType) getVarType(this.functionList.get(this.lastFunctionEntered));
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
            this.getTypes().put(functionName, new FunctionType(functionReturnType, new ArrayList<>()));
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
            this.getTypes().put(functionName, functionType);
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

        enterBlock();

        // visit functions
        for (int i = 0; i < childCount - 3; i++) {
            ParseTree decl_fctNode = ctx.getChild(i);
            visit(decl_fctNode);
        }

        // visit main function
        ParseTree core_fctNode = ctx.getChild(childCount - 2);

        ParseTree funcMain = ctx.getChild(childCount - 3);
        UnknownType funcNameUT = new UnknownType(funcMain);

        this.getTypes().put(funcNameUT, mainType);
        enterFunction(funcMain);

        visit(core_fctNode);

        leaveFunction();
        leaveBlock();

        cleanTypes();

        return null;
    }

}
