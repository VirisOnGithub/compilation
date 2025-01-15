package src;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class VarStack<T> {

	private final Stack<Map<String, T>> stack;
	private final Stack<Integer> lastAccessibleDepth;

	public VarStack() {
		this.stack = new Stack<>();
		this.lastAccessibleDepth = new Stack<>();
	}

	public void enterBlock() {
		stack.add(new HashMap<>());
	}

	public void leaveBlock() {
		stack.pop();
	}

	public void enterFunction() {
        lastAccessibleDepth.add(stack.size());
        this.enterBlock();
    }

	public void leaveFunction() {
        this.leaveBlock();
        lastAccessibleDepth.pop();
    }

	public T getVarRegister(T varName) {
        for (int depth = stack.size() - 1; depth >= lastAccessibleDepth.getLast(); depth--) { // we unstack all the accessible maps in varRegisters
            var varMap = stack.get(depth);
            if (varMap.containsKey(varName)) { // we stop at the first corresponding variable name
                return varMap.get(varName);
            }
        }
        throw new RuntimeException("The variable " + varName + " has not been assigned"); // if none was found
    }

	/**
     * Set the register number of a given variable
     * @param varName the name of the variable
     * @param value the value we want to map the variable to
     */
    public void assignVar(String varName, T value) {
        this.stack.getLast().put(varName, value);
    }

}
