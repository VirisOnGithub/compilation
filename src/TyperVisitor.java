package src;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

import src.Type.Type;
import src.Type.UnknownType;

public class TyperVisitor extends AbstractParseTreeVisitor<Type> implements grammarTCLVisitor<Type> {

    private Map<UnknownType,Type> types = new HashMap<UnknownType,Type>();

    public Map<UnknownType, Type> getTypes() {
        return types;
    }

    @Override
    public Type visitNegation(grammarTCLParser.NegationContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitNegation'");
    }

    @Override
    public Type visitComparison(grammarTCLParser.ComparisonContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitComparison'");
    }

    @Override
    public Type visitOr(grammarTCLParser.OrContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitOr'");
    }

    @Override
    public Type visitOpposite(grammarTCLParser.OppositeContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitOpposite'");
    }

    @Override
    public Type visitInteger(grammarTCLParser.IntegerContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitInteger'");
    }

    @Override
    public Type visitTab_access(grammarTCLParser.Tab_accessContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitTab_access'");
    }

    @Override
    public Type visitBrackets(grammarTCLParser.BracketsContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitBrackets'");
    }

    @Override
    public Type visitCall(grammarTCLParser.CallContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitCall'");
    }

    @Override
    public Type visitBoolean(grammarTCLParser.BooleanContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitBoolean'");
    }

    @Override
    public Type visitAnd(grammarTCLParser.AndContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitAnd'");
    }

    @Override
    public Type visitVariable(grammarTCLParser.VariableContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitVariable'");
    }

    @Override
    public Type visitMultiplication(grammarTCLParser.MultiplicationContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitMultiplication'");
    }

    @Override
    public Type visitEquality(grammarTCLParser.EqualityContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitEquality'");
    }

    @Override
    public Type visitTab_initialization(grammarTCLParser.Tab_initializationContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitTab_initialization'");
    }

    @Override
    public Type visitAddition(grammarTCLParser.AdditionContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitAddition'");
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
    public Type visitDeclaration(grammarTCLParser.DeclarationContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitDeclaration'");
    }

    @Override
    public Type visitPrint(grammarTCLParser.PrintContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitPrint'");
    }

    @Override
    public Type visitAssignment(grammarTCLParser.AssignmentContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitAssignment'");
    }

    @Override
    public Type visitBlock(grammarTCLParser.BlockContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitBlock'");
    }

    @Override
    public Type visitIf(grammarTCLParser.IfContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitIf'");
    }

    @Override
    public Type visitWhile(grammarTCLParser.WhileContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitWhile'");
    }

    @Override
    public Type visitFor(grammarTCLParser.ForContext ctx) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitFor'");
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
