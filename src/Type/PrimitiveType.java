package src.Type;
import java.util.HashMap;
import java.util.Map;

public  class PrimitiveType extends Type {
    private Type.Base type; 
    
    /**
     * Constructeur
     * @param type type de base
     */
    public PrimitiveType(Type.Base type) {
        this.type = type;
    }

    /**
     * Getter du type
     * @return type
     */
    public Type.Base getType() {
        return type;
    }

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
        //cas INT ~ INT
        return map;
    }

    @Override
    public Type substitute(UnknownType v, Type t) {
        return this;
    }

    @Override
    public boolean contains(UnknownType v) {
        return false;
    }

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
