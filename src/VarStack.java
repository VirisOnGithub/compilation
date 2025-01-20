package src;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import src.Type.Type;
import src.Type.UnknownType;

/**
 * A Class that associate a value with variables and remember the depth at which the variable was declared
 * @param <T> the type of value we associate the variables with
 */
public class VarStack<K, T> {
	/**
	 * A stack of maps that associates each variable with a value, at its corresponding depth
	 */
	private final Stack<Map<K, T>> stack;
	/**
	 * A stack whose top is the furthest variables are still in reach within the execution context, mainly used for function calls
	 */
	private final Stack<Integer> lastAccessibleDepth;

	/**
	 * Constructor
	 */
	public VarStack() {
		this.stack = new Stack<>();
		this.lastAccessibleDepth = new Stack<>();
		this.enterFunction(); // getVar() will return an error if you never entered a block or function
	}

	/**
	 * Go one depth more
	 * @apiNote needs to be called just before entering a {} block
	 */
	public void enterBlock() {
		this.stack.add(new HashMap<>());
	}

	/**
	 * Go one depth less
	 * @apiNote needs to be called just after leaving a {} block
	 */
	public void leaveBlock() {
		this.stack.pop();
	}

	/**
	 * Go one depth more, and make previous variables inaccessible
	 * @apiNote needs to be called just before entering a function
	 */
	public void enterFunction() {
		this.lastAccessibleDepth.add(this.stack.size());
		this.enterBlock();
	}

	/**
	 * Go one depth more, and make previous variables accessible again
	 * @apiNote needs to be called just after leaving a function
	 */
	public void leaveFunction() {
		this.leaveBlock();
		this.lastAccessibleDepth.pop();
	}

	/**
	 * Get the value associated with a given variable
	 * @param varName the name of the variable we try to find the value for
	 * @return the value associated with varName is it if, RuntimeException else
	 */
	public T getVar(K varKey) throws RuntimeException {
		for (int depth = this.stack.size() - 1; depth >= this.lastAccessibleDepth.getLast(); depth--) { // we un-stack all the accessible maps
			var varMap = this.stack.get(depth);
			if (varMap.containsKey(varKey)) { // we stop at the first corresponding variable name
				return varMap.get(varKey);
			}
		}
		throw new RuntimeException("The variable " + varKey + " has not been found"); // if none was found
	}

	/**
	 * Set the value of a given variable
	 * @apiNote should be called when a variable is declared
	 * @param varName the name of the variable
	 * @param value the value we want to map the variable to
	 * @return true if the var did not exist
	 */
	public boolean assignVar(K varKey, T value) {
		return this.stack.getLast().put(varKey, value) == null;
	}

	/**
	 * Tells is a given variable has been declared and is accessible
	 * @param varName the variable we want to know if it exists
	 * @return true if the variable exists, false else
	 */
	public boolean varExists(K varKey) {
		for (int depth = this.stack.size() - 1; depth >= this.lastAccessibleDepth.getLast(); depth--) { // we un-stack all the accessible maps
			var varMap = this.stack.get(depth);
			if (varMap.containsKey(varKey)) { // we stop at the first corresponding variable name
				return true;
			}
		}
		return false;
	}






	// le groupe 2 n'a pas besoin de ce qui suit



	public void set(int index, Map<K, T> map) {
		this.stack.set(index, map);
	}

	public int indexOf(Map<UnknownType, Type> layer) {
		return this.stack.indexOf(layer);
	}

	/**
	 * Récupère le dernier layer où ut est une key ou une value de celui ci, retourne null sinon
	 * @param ut UnknownType
	 * @return Map<UnknownType, Type> || null, si ut n'est pas présent dans les map de this.stack
	 */
	public Map<K, T> getLastStackOfUT(UnknownType ut) {
		Map<K, T> result = null;
		int size = this.stack.size();
		boolean notFound = true;

		for (int i = size-1; i >= 0 && notFound; i--) {
			Map<K, T> tmp = this.stack.get(i);
			if (tmp.containsKey(ut) || tmp.containsValue(ut)) {
				result = tmp;
				notFound = false;
			}
		}
		return result;
	}

	public Map<K, T> getLastStackOfVarName (String varName) {
		int size = this.stack.size();

		for (int i = size-1; i >= 0; i--) {
			Map<K, T> tmp = this.stack.get(i);
			for (K key : tmp.keySet()) {
				assert key instanceof UnknownType;
				UnknownType ut = (UnknownType) key;
                if (ut.getVarName().equals(varName)) {
                    return tmp;
                }
			}
		}
		return null;
	}

	/**
	 * Retourne le dernier type déclaré pour une variable de nom varName
	 * @param varName
	 * @return null
	 */
	public T getLastTypeOfVarName (String varName) {
		T result = null;
		int size = this.stack.size();
		boolean notFound = true;

		for (int i = size-1; i >= 0 && notFound; i--) {
			Map<K, T> tmp = this.stack.get(i);
			for (K key : tmp.keySet()) {
				assert key instanceof UnknownType;
				UnknownType ut = (UnknownType) key;
                if (ut.getVarName().equals(varName)) {
                    result   = tmp.get(key);
                    notFound = false;
                    break;
                }
			}
		}
		return result;
	}

	public boolean containsVarName(String varName) {
		return this.getLastStackOfVarName(varName) != null;
	}

	public int size() {
		return stack.size();
	}

	public Map<K, T> getLastStack () {
		return this.stack.getLast();
	}

	public Map<K, T> pop() {
		return this.stack.pop();
	}


}