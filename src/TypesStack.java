package src;

import src.Type.Type;
import src.Type.UnknownType;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class TypesStack {

	private final Stack<Map<UnknownType, Type>> stack;

	public TypesStack() {
		this.stack = new Stack<>();
	}

	public void enterBlock() {
		stack.add(new HashMap<>());
	}

	public Map<UnknownType, Type> pop() {
		return stack.pop();
	}

	public Map<UnknownType, Type> getLastStack () {
		int size = this.stack.size();
		return this.stack.get(size-1);
	}

	public void putLastStack (UnknownType key, Type value) {
		int size = this.stack.size();
		this.stack.get(size-1).put(key, value);
	}

	/**
	 * Cherche si la clé est contenue dans la stack.
	 * @param key UnknowType
	 * @return boolean, Renvoie true si la key est contenue dans au moins une des HashMap de la stack.
	 */
	public boolean containsKey(UnknownType key) {
		return this.getLastStackOfUT(key) != null;
	}

	public Map<UnknownType, Type> getLastStackOfUT(UnknownType key) {
		Map<UnknownType, Type> result = null;
		int size = this.stack.size();
		boolean notFound = true;

		for (int i = size-1; i >= 0 && notFound; i--) {
			Map<UnknownType, Type> tmp = this.stack.get(i);
			if (tmp.containsKey(key)) {
				result = tmp;
				notFound = false;
			}
		}
		return result;
	}

	public Map<UnknownType, Type> getLastStackOfVarName (String varName) {
		Map<UnknownType, Type> result = null;
		int size = this.stack.size();
		boolean notFound = true;

		for (int i = size-1; i >= 0 && notFound; i--) {
			Map<UnknownType, Type> tmp = this.stack.get(i);
			for (UnknownType key : tmp.keySet()) {
                if (key.getVarName().equals(varName)) {
                    result   = tmp;
                    notFound = false;
                    break;
                }
			};
		}
		return result;
	}

	/**
	 * Retourne le Type de la key
	 * @param key
	 * @return
	 */
	public Type getTypeOfUT(UnknownType key) {
		Type result = null;
		int size = this.stack.size();
		boolean notFound = true;

		for (int i = size-1; i >= 0 && notFound; i--) {
			Map<UnknownType, Type> tmp = this.stack.get(i);
			if (tmp.containsKey(key)) {
				result = tmp.get(key);
				notFound = false;
			}
		}
		return result;
	}

	/**
	 * Retourne le dernier type déclaré pour une variable de nom varName
	 * @param varName
	 * @return null
	 */
	public Type getLastTypeOfVarName (String varName) {
		Type result = null;
		int size = this.stack.size();
		boolean notFound = true;

		for (int i = size-1; i >= 0 && notFound; i--) {
			Map<UnknownType, Type> tmp = this.stack.get(i);
			for (UnknownType key : tmp.keySet()) {
                if (key.getVarName().equals(varName)) {
                    result   = tmp.get(key);
                    notFound = false;
                    break;
                }
			};
		}
		return result;
	}

	public Stack<Map<UnknownType, Type>> getStack() {
		return this.stack;
	}

	public int size() {
		return stack.size();
	}

	public boolean isEmpty() {
		return stack.isEmpty();
	}

}
