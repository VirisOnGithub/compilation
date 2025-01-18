package src;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * A Class that associate a value with variables and remember the depth at which the variable was declared
 * @param <T> the type of value we associate the variables with
 */
public class VarStack<T> {
	/**
	 * A stack of maps that associates each variable with a value, at its corresponding depth
	 */
	private final Stack<Map<String, T>> stack;
	/**
	 * A stack whose top is the furthest variables are still in reach within the execution context, mainly used for function calls
	 */
	private final Stack<Integer> lastAccessibleDepth;
	/**
	 * The name of the last added variable
	 */
	private String lastAdded;

	/**
	 * Constructor
	 */
	public VarStack() {
		this.stack = new Stack<>();
		this.lastAccessibleDepth = new Stack<>();
		this.lastAdded = null;
		this.enterFunction(); // getVar() will return an error if you never entered a block or function
	}

	/**
	 * Go one depth more
	 * @apiNote needs to be called just before entering a {} block
	 */
	public void enterBlock() {
		stack.add(new HashMap<>());
	}

	/**
	 * Go one depth less
	 * @apiNote needs to be called just after leaving a {} block
	 */
	public void leaveBlock() {
		stack.pop();
	}

	/**
	 * Go one depth more, and make previous variables inaccessible
	 * @apiNote needs to be called just before entering a function
	 */
	public void enterFunction() {
		lastAccessibleDepth.add(stack.size());
		this.enterBlock();
	}

	/**
	 * Go one depth more, and make previous variables accessible again
	 * @apiNote needs to be called just after leaving a function
	 */
	public void leaveFunction() {
		this.leaveBlock();
		lastAccessibleDepth.pop();
	}

	/**
	 * Get the value associated with a given variable
	 * @param varName the name of the variable we try to find the value for
	 * @return the value associated with varName is it if, RuntimeException else
	 */
	public T getVar(String varName) throws RuntimeException {
		for (int depth = stack.size() - 1; depth >= lastAccessibleDepth.getLast(); depth--) { // we un-stack all the accessible maps
			var varMap = stack.get(depth);
			if (varMap.containsKey(varName)) { // we stop at the first corresponding variable name
				return varMap.get(varName);
			}
		}
		throw new RuntimeException("The variable " + varName + " has not been found"); // if none was found
	}

	/**
	 * Set the value of a given variable
	 * @apiNote should be called when a variable is declared
	 * @param varName the name of the variable
	 * @param value the value we want to map the variable to
	 * @return true if the var did not exist
	 */
	public boolean assignVar(String varName, T value) {
		this.lastAdded = varName;
		return this.stack.getLast().put(varName, value) == null;
	}

	/**
	 * Tells is a given variable has been declared and is accessible
	 * @param varName the variable we want to know if it exists
	 * @return true if the variable exists, false else
	 */
	public boolean varExists(String varName) {
		for (int depth = stack.size() - 1; depth >= lastAccessibleDepth.getLast(); depth--) { // we un-stack all the accessible maps
			var varMap = stack.get(depth);
			if (varMap.containsKey(varName)) { // we stop at the first corresponding variable name
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the name of the last variable added
	 * @return the name of the last variable added
	 */
	public String getLastAdded() {
		return this.lastAdded;
	}
}