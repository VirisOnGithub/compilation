package src.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a function type with a return type and a list of argument types.
 */
public class FunctionType extends Type {
    private Type returnType;
    private ArrayList<Type> argsTypes;

    /**
     * Constructor to create a FunctionType object.
     *
     * @param returnType return type of the function
     * @param argsTypes  list of argument types for the function
     */
    public FunctionType(Type returnType, ArrayList<Type> argsTypes) {
        this.returnType = returnType;
        this.argsTypes = argsTypes;
    }

    /**
     * Gets the return type of the function.
     *
     * @return the return type
     */
    public Type getReturnType() {
        return returnType;
    }

    /**
     * Gets the list of argument types of the function.
     *
     * @return a list of argument types
     */
    public ArrayList<Type> getArgs() {
        return this.argsTypes;
    }

    /**
     * Gets the type of the argument at a given index.
     *
     * @param i index of the argument
     * @return the type of the specified argument
     */
    public Type getArgType(int i) {
        return argsTypes.get(i);
    }

    /**
     * Gets the number of arguments.
     *
     * @return the number of arguments
     */
    public int getNbArgs() {
        return argsTypes.size();
    }

    /**
     * Unifies the current function type with another type.
     *
     * @param t the type to unify with
     * @return a map of substitutions, or throws an error if not unifiable
     */
    @Override
    public Map<UnknownType, Type> unify(Type t) {
        HashMap<UnknownType, Type> map = new HashMap<>();
        if (t instanceof UnknownType) {
            if (this.contains((UnknownType) t)) {
                // Case FUNC(X,Y)->Z ~ X
                throw new Error("TypeError: cannot unify " + this + " to " + t);
            }
            // Case FUNC(X,Y)->Z ~ W
            UnknownType newreturn = new UnknownType();
            map.put(newreturn, this.returnType);
            if (this.argsTypes != null) {
                for (Type i : this.argsTypes) {
                    UnknownType temp = new UnknownType();
                    map.put(temp, i);
                }
            }
            return map;
        } else if (t instanceof FunctionType tempT) {
            if (this.equals(t)) {
                return map;
            }
            map.putAll(this.returnType.unify(tempT.getReturnType()));
            if (this.getNbArgs() != tempT.getNbArgs()) {
                throw new Error("TypeError: cannot unify " + this + " to " + t);
            }
            for (int i = 0; i < this.getNbArgs(); i++) {
                map.putAll(this.getArgType(i).unify(tempT.getArgType(i)));
            }
            return map;
        }
        throw new Error("TypeError: cannot unify " + this + " to " + t);
    }

    /**
     * Substitutes occurrences of a type variable with another type in the function type.
     *
     * @param v the type variable to substitute
     * @param t the type to replace the variable with
     * @return the resulting FunctionType after substitution
     */
    @Override
    public Type substitute(UnknownType v, Type t) {
        ArrayList<Type> newArgsTypes = new ArrayList<>();
        if (this.getNbArgs() != 0) {
            for (Type i : this.argsTypes) {
                newArgsTypes.add(i.substitute(v, t));
            }
        }
        return new FunctionType(returnType.substitute(v, t), newArgsTypes);
    }

    /**
     * Checks if the function type contains a specific type variable.
     *
     * @param v the type variable to check
     * @return true if the function type depends on the variable, false otherwise
     */
    @Override
    public boolean contains(UnknownType v) {
        boolean result = false;
        if (this.argsTypes != null) {
            for (Type i : this.argsTypes) {
                result = result || i.contains(v);
            }
        }
        result = result || this.returnType.contains(v);
        return result;
    }

    /**
     * Checks if the current function type is equal to another object.
     *
     * @param t the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object t) {
        return t instanceof FunctionType
                && returnType.equals(((FunctionType) t).returnType)
                && argsTypes.equals(((FunctionType) t).argsTypes);
    }

    /**
     * Converts the function type to a string representation.
     *
     * @return a string representing the function type
     */
    @Override
    public String toString() {
        String s = "( ";
        for (Type i : argsTypes) {
            s += i.toString() + " ";
        }
        s += ")->" + returnType.toString();
        return s;
    }
}
