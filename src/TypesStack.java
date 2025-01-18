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

	public void leaveBlock() {
		stack.pop();
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
