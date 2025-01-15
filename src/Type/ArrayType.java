package src.Type;

import java.util.HashMap;
import java.util.Map;

public class ArrayType extends Type {
    private Type tabType;
    
    /**
     * Constructeur
     * @param t type des éléments du tableau
     */
    public ArrayType(Type t) {
        this.tabType = t;
    }

    /**
     * Getter du type des éléments du tableau
     * @return type des éléments du tableau
     */
    public Type getTabType() {
       return tabType;
    }

    @Override
    public Map<UnknownType, Type> unify(Type t) {
        HashMap<UnknownType, Type> map = new HashMap<>();
        if (t instanceof UnknownType) {
            if (this.contains((UnknownType) t)) {
                //cas Tab[X] ~ X
                throw new Error("TypeError: cannot unify " + this + " to " + t);
            } else {
                //cas Tab[X] ~ Y
                map.put((UnknownType) t, this.tabType);
                return map;
            }
        }
        if (t instanceof ArrayType) {
            //cas Tab[X] ~ Tab[X]
            //cas Tab[X] ~ Tab[Tab[X]]
            return this.getTabType().unify(((ArrayType) t).getTabType());
        } else {
            //cas Tab[X] ~ INT, BOOL, Function...
            throw new Error("TypeError: cannot unify " + this + " to " + t);
        }
    }

    @Override
    public Type substitute(UnknownType v, Type t) {
        return new ArrayType(this.tabType.substitute(v, t));
    }

    @Override
    public boolean contains(UnknownType v) {
        return tabType.contains(v);
    }

    @Override
    public boolean equals(Object t) {
        return t instanceof ArrayType
                && tabType.equals(((ArrayType)t).tabType);
    }

    @Override
    public String toString() {
        return "tab["+tabType.toString()+"]";
    }
}
