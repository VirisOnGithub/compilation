package src.Type;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents an unknown type used in type variable unification and substitution processes.
 */
public class UnknownType extends Type {
    private String varName;
    private int varIndex;
    private static int newVariableCounter = 0;

    /**
     * Constructor without a name.
     * Initializes a variable with a default name "#" and assigns a unique index.
     */
    public UnknownType() {
        this.varIndex = newVariableCounter++;
        this.varName = "#";
    }

    /**
     * Constructor with a variable name and a number.
     *
     * @param s Variable name.
     * @param n Variable number.
     */
    public UnknownType(String s, int n) {
        this.varName = s;
        this.varIndex = n;
    }

    /**
     * Constructor from a ParseTree.
     * Standardizes the variable name based on the parse tree's text.
     *
     * @param ctx ParseTree used to initialize the variable.
     * @throws Error if the ParseTree context is not a recognized type.
     */
    public UnknownType(ParseTree ctx) {
        this.varName = ctx.getText();
        if (ctx instanceof TerminalNode) {
            this.varIndex = ((TerminalNode) ctx).getSymbol().getStartIndex();
        } else {
            if (ctx instanceof ParserRuleContext) {
                this.varIndex = ((ParserRuleContext) ctx).getStart().getStartIndex();
            } else {
                throw new Error("Illegal UnknownType construction");
            }
        }
    }

    /**
     * Gets the name of the variable.
     *
     * @return The variable name as a string.
     */
    public String getVarName() {
        return varName;
    }

    /**
     * Gets the index of the variable.
     *
     * @return The variable index as an integer.
     */
    public int getVarIndex() {
        return varIndex;
    }

    /**
     * Sets the index of the variable.
     *
     * @param n The variable index to set.
     */
    public void setVarIndex(int n) {
        this.varIndex = n;
    }

    /**
     * Unifies the current type with another type.
     *
     * @param t The type to unify with.
     * @return A map of type substitutions, or throws an error if unification is not possible.
     */
    @Override
    public Map<UnknownType, Type> unify(Type t) {
        HashMap<UnknownType, Type> map = new HashMap<>();
        if (t instanceof UnknownType && this.equals(t)) {
            return map;
        }
        if (t.contains(this)) {
            throw new Error("TypeError: cannot unify " + this + " to " + t);
        }
        map.put(this, t);

        return map;
    }

    /**
     * Substitutes a type variable with another type.
     *
     * @param v The type variable to substitute.
     * @param t The type that replaces the variable.
     * @return A new type with the substitution applied.
     */
    @Override
    public Type substitute(UnknownType v, Type t) {
        if (this.equals(v)) {
            return t;
        } else return this;
    }

    /**
     * Checks if the type contains a given type variable.
     *
     * @param v The type variable to check for.
     * @return True if the type contains the variable, false otherwise.
     */
    @Override
    public boolean contains(UnknownType v) {
        return this.equals(v);
    }

    /*
     * HashCode based on the name only.
     * Uncomment if needed: Generates a hash code using the variable name or index.
     *
     * @Override
     * public int hashCode() {
     *     if (this.getVarName().equals("#")) {
     *         return Objects.hash(varIndex);
     *     } else {
     *         return Objects.hash(varName);
     *     }
     * }
     */

    /**
     * Checks if the current object is equal to another object.
     *
     * @param t The object to compare.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object t) {
        if (t instanceof UnknownType tempT) {
            return this.getVarName().equals(tempT.getVarName()) && this.getVarIndex() == tempT.getVarIndex();
        } else {
            return false;
        }
    }

    /**
     * Converts the unknown type to a string representation.
     *
     * @return A string in the format "UnknownType(varName, varIndex)".
     */
    @Override
    public String toString() {
        return "UnknownType(" + varName + ", " + varIndex + ")";
    }
}
