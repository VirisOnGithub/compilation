package src.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FunctionType extends Type {
    private Type returnType;
    private ArrayList<Type> argsTypes;
    
    /**
     * Constructeur
     * @param returnType type de retour
     * @param argsTypes liste des types des arguments
     */
    public FunctionType(Type returnType, ArrayList<Type> argsTypes) {
        this.returnType = returnType;
        this.argsTypes = argsTypes;
    }

    /**
     * Getter du type de retour
     * @return type de retour
     */
    public Type getReturnType() {
        return returnType;
    }

    /**
     * Getter du type du i-eme argument
     * @param i entier
     * @return type du i-eme argument
     */
    public Type getArgsType(int i) {
        return argsTypes.get(i);
    }

    /**
     * Getter du nombre d'arguments
     * @return nombre d'arguments
     */
    public int getNbArgs() {
        return argsTypes.size();
    }

    @Override
    public Map<UnknownType, Type> unify(Type t) {
        HashMap<UnknownType, Type> map = new HashMap <> ();
        if (t instanceof UnknownType){
            if (this.contains((UnknownType)t)){
                //cas FUNC(X,Y)->Z ~ X
                throw new Error("TypeError: cannot unify " + this + " to " + t);
            }
            //cas FUNC(X,Y)->Z ~ W
            UnknownType newreturn = new UnknownType();
            map.put(newreturn, this.returnType);
            if (this.argsTypes != null) {
                for (Type i : this.argsTypes) {
                    UnknownType temp = new UnknownType();
                    map.put(temp, i);
                }
            }
            return map;
        }

        else if (t instanceof FunctionType tempT) {
            if (this.equals(t)) {
                return map;
            }
            map.putAll(this.returnType.unify(tempT.getReturnType()));
            if (this.getNbArgs() != tempT.getNbArgs()) {
                throw new Error("TypeError: cannot unify " + this + " to " + t);
            }
            for (int i = 0; i<this.getNbArgs(); i++) {
                    map.putAll(this.getArgsType(i).unify(tempT.getArgsType(i)));
            }
            return map;
        }
        throw new Error("TypeError: cannot unify " + this + " to " + t);
    }

    @Override
    public Type substitute(UnknownType v, Type t) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'substitute'");
    }

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

    @Override
    public boolean equals(Object t) {
        return t instanceof FunctionType
                && returnType.equals(((FunctionType) t).returnType)
                && argsTypes.equals(((FunctionType) t).argsTypes);
    }

    @Override
    public String toString() {
        String s = "( ";
        for (Type i : argsTypes){
            s += i.toString() + " ";
        }
        s += ")->" + returnType.toString();
        return s;
    }
}
