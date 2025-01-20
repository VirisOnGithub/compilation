package src.Type;

import java.util.HashMap;
import java.util.Map;

/**
 * A class representing array types.
 * Holds the type of elements within an array and provides functionality for
 * unification, substitution, and comparison of array types.
 */
public class ArrayType extends Type {
    private Type tabType;

    /**
     * Constructor for ArrayType.
     *
     * @param t the type of elements within the array
     */
    public ArrayType(Type t) {
        this.tabType = t;
    }

    /**
     * Gets the type of elements in the array.
     *
     * @return the type of array elements
     */
    public Type getTabType() {
        return tabType;
    }

    /**
     * Unifies the current ArrayType with another type given.
     *
     * @param t the type to unify with
     * @return a map containing substitutions if the types can be unified
     * @throws Error if unification is not possible
     */
    @Override
    public Map<UnknownType, Type> unify(Type t) {
        HashMap<UnknownType, Type> map = new HashMap<>();
        if (t instanceof UnknownType) {
            if (this.contains((UnknownType) t)) {
                // Case: Tab[X] ~ X
                throw new Error("TypeError: cannot unify " + this + " to " + t);
            } else {
                // Case: Tab[X] ~ Y
                map.put((UnknownType) t, this);
                return map;
            }
        }
        if (t instanceof ArrayType) {
            // Case: Tab[X] ~ Tab[X] or Tab[X] ~ Tab[Tab[X]]
            return this.getTabType().unify(((ArrayType) t).getTabType());
        } else {
            // Case: Tab[X] ~ INT, BOOL, Function...
            throw new Error("TypeError: cannot unify " + this + " to " + t);
        }
    }

    /**
     * Substitutes a given type variable with another type in the current ArrayType.
     *
     * @param v the type variable to replace
     * @param t the type to replace the variable with
     * @return a new ArrayType with the substitution applied
     */
    @Override
    public Type substitute(UnknownType v, Type t) {
        return new ArrayType(this.tabType.substitute(v, t));
    }

    /**
     * Checks if the current ArrayType contains a specific type variable.
     *
     * @param v the type variable to check
     * @return true if the type variable is contained, false otherwise
     */
    @Override
    public boolean contains(UnknownType v) {
        return tabType.contains(v);
    }

    /**
     * Compares the current ArrayType for equality with another object.
     *
     * @param t the object to compare with
     * @return true if the object is an ArrayType with equal element type, false otherwise
     */
    @Override
    public boolean equals(Object t) {
        return t instanceof ArrayType
                && tabType.equals(((ArrayType) t).tabType);
    }

    /**
     * Provides a string representation of the ArrayType.
     *
     * @return a string representing the ArrayType
     */
    @Override
    public String toString() {
        return "tab[" + tabType.toString() + "]";
    }
}
