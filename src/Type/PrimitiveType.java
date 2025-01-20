package src.Type;
import java.util.HashMap;
import java.util.Map;

public  class PrimitiveType extends Type {
    private Type.Base type;

    /**
     * Constructor
     *
     * @param type the base type of this PrimitiveType
     */
    public PrimitiveType(Type.Base type) {
        this.type = type;
    }

    /**
     * Getter for the type
     *
     * @return the base type
     */
    public Type.Base getType() {
        return type;
    }

    
    /**
     * Unifies this type with another type.
     * If the other type is an instance of UnknownType, it associates this PrimitiveType 
     * as the value corresponding to the UnknownType in the resulting map.
     * Throws an error if the types cannot be unified.
     *
     * @param t the type to unify with
     * @return a map with UnknownType as keys and unified types as values
     */
    @Override
    public Map<UnknownType, Type> unify(Type t) {
        HashMap<UnknownType, Type> map = new HashMap <> ();
        if (t instanceof UnknownType) {
            //cas INT ~ X
            map.put((UnknownType) t, this);
            return map;
        }
        if (!this.equals(t)) {
            //cas INT ~ BOOL, Function, Array...
            throw new Error("TypeError: cannot unify " + this + " to " + t);
        }
        //else : cas INT ~ INT
        return map;
    }

    /**
     * Substitutes occurrences of a specified UnknownType with another type.
     * Since PrimitiveType does not contain UnknownType, this method simply returns itself.
     *
     * @param v the UnknownType to replace
     * @param t the type to substitute with
     * @return the substituted type
     */
    @Override
    public Type substitute(UnknownType v, Type t) {
        return this;
    }

    /**
     * Checks if this type contains the specified UnknownType.
     * PrimitiveType can never contain UnknownType, so this always returns false.
     *
     * @param v the UnknownType to check
     * @return false, as PrimitiveType does not contain UnknownType
     */
    @Override
    public boolean contains(UnknownType v) {
        return false;
    }

    /**
     * Checks equality between this PrimitiveType and another object.
     * Two PrimitiveTypes are considered equal if their base types are equal.
     *
     * @param t the object to compare with
     * @return true if the object is a PrimitiveType with the same base type, false otherwise
     */
    @Override
    public boolean equals(Object t) {
        return t instanceof PrimitiveType
                && type.equals(((PrimitiveType)t).type);
    }

    
    @Override
    public String toString() {
        return type.toString();
    }
}
