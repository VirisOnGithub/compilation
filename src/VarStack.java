package src;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * A Class that associate a value with variables and remember the depth at which the variable was declared
 * @param <T> The type of value wa associate the variables with
 */
public class VarStack<T> {
	private final Stack<Map<String, T>> stack;
	private final Stack<Integer> lastAccessibleDepth;

	/**
	 * Constructor
	 */
	public VarStack() {
		this.stack = new Stack<>();
		this.lastAccessibleDepth = new Stack<>();
	}

	/**
	 * @apiNote Needs to be called just before entering a {} block
	 */
	public void enterBlock() {
		stack.add(new HashMap<>());
	}

	/**
	 * @apiNote Needs to be called just after leaving a {} block
	 */
	public void leaveBlock() {
		stack.pop();
	}

	/**
	 * @apiNote Needs to be called just before entering a function
	 */
	public void enterFunction() {
		lastAccessibleDepth.add(stack.size());
		this.enterBlock();
	}

	/**
	 * @apiNote Needs to be called just after leaving a function
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
	public T getVar(String varName) {
		for (int depth = stack.size() - 1; depth >= lastAccessibleDepth.getLast(); depth--) { // we unstack all the accessible maps
			var varMap = stack.get(depth);
			if (varMap.containsKey(varName)) { // we stop at the first corresponding variable name
				return varMap.get(varName);
			}
		}
		throw new RuntimeException("The variable " + varName + " has not been found"); // if none was found
	}

	/**
	 * Set the value of a given variable
	 * @apiNote Should be called when a variable is declared
	 * @param varName the name of the variable
	 * @param value the value we want to map the variable to
	 */
	public void assignVar(String varName, T value) {
		this.stack.getLast().put(varName, value);
	}

	/**
	 * Update the value of a given variable
	 * @apiNote Must be called only if the variable has already been assigned
	 * @param varName the name of the variable
	 * @param value the value we want to map the variable to
	 */
	public void updateVar(String varName, T newValue) {
		for (int depth = stack.size() - 1; depth >= lastAccessibleDepth.getLast(); depth--) { // we unstack all the accessible maps
			var varMap = stack.get(depth);
			if (varMap.containsKey(varName)) { // we stop at the first corresponding variable name
				varMap.put(varName, newValue);	// and we replace it with the new value
			}
		}
		throw new RuntimeException("The variable " + varName + " has not been found"); // if none was found
	}
}