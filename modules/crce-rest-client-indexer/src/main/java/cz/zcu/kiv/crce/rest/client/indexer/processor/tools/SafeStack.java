package cz.zcu.kiv.crce.rest.client.indexer.processor.tools;

import java.util.Stack;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.Variable;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.Variable.VariableType;

public class SafeStack {
    /**
     * Handles poping of an empty stack
     * 
     * @param <E>   Type of values in stack
     * @param stack Stack
     * @return value or null if stack is empty
     */
    public static <E> E pop(Stack<E> stack) {
        if (stack.size() > 0) {
            return stack.pop();
        }
        return null;
    }

    public static Variable popEndpoint(Stack<Variable> stack) {
        Variable var = peek(stack);
        if (var != null && var.getType() == VariableType.ENDPOINT) {
            pop(stack);
            return var;
        }
        return null;
    }

    /**
     * Handles peek of an empty stack
     * 
     * @param <E>   Type of values in stack
     * @param stack Stack
     * @return value or null if stack is empty
     */
    public static <E> E peek(Stack<E> stack) {
        if (stack.size() > 0) {
            return stack.peek();
        }
        return null;
    }

    /**
     * Peeks Endpoint from stack
     * @param stack Stack
     * @return Endpoint
     */
    public static Variable peekEndpoint(Stack<Variable> stack) {
        Variable var = peek(stack);
        if (var != null && (var.getType() == VariableType.ENDPOINT
                || var.getType() == VariableType.ENDPOINT_DATA)) {
            return var;
        }
        return null;
    }
}
