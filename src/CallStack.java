package src;

import java.util.Stack;

public class CallStack {

	private final Stack<String> stack;

	public CallStack() {
		this.stack = new Stack<>();
	}

	public void enterFunction(String name) {
		stack.add(name);
	}

	public void leaveFunction() {
		stack.pop();
	}

	public Stack<String> getStack() {
		return this.stack;
	}

}
