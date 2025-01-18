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
	private String lastAdded;

	/**
	 * Constructor
	 */
	public VarStack() {
		this.stack = new Stack<>();
		this.lastAccessibleDepth = new Stack<>();
		this.lastAdded = null;
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
				System.out.println("------");
				System.out.println(varName + " " + depth);
				for (var entry : varMap.entrySet()) {
					System.out.println(entry.getKey() + " " + entry.getValue());
				}
				System.out.println(varMap.get(varName));
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
	 * @return true if the var did not exist
	 */
	public boolean assignVar(String varName, T value) {
		this.lastAdded = varName;
		return this.stack.getLast().put(varName, value) == null;
	}

	public boolean varExists(String varName) {
		for (int depth = stack.size() - 1; depth >= lastAccessibleDepth.getLast(); depth--) { // we unstack all the accessible maps
			var varMap = stack.get(depth);
			if (varMap.containsKey(varName)) { // we stop at the first corresponding variable name
				return true;
			}
		}
		return false;
	}

	public String getLastAdded() {
		return this.lastAdded;
	}
}