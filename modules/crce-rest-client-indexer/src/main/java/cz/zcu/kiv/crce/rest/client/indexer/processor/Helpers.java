package cz.zcu.kiv.crce.rest.client.indexer.processor;

import java.util.Map;
import java.util.Stack;

import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Endpoint;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.ClassStruct;
import cz.zcu.kiv.crce.rest.client.indexer.processor.Variable.VariableType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassWrapper;

// TODO move to folder tools and create seperated classes StringC, OpcodeC......

public class Helpers {


    static class StringC {
        static private final String APPEND_FC = "append";
        static private final String TO_STRING_FC = "toString";

        /**
         * Detects a toString method by its name
         * 
         * @param name name of the method
         * @return is or is not
         */
        public static boolean isToString(String name) {
            return name.equals(TO_STRING_FC);
        }

        /**
         * Merges two StringBuilders into newone
         * 
         * @param first  First StringBuilder
         * @param second Second StringBuilder
         * @return Merged StringBuilder
         */
        public static StringBuilder mergeStringBuilder(StringBuilder first, StringBuilder second) {
            StringBuilder merged = new StringBuilder();
            merged.append(first.toString());
            merged.append(second.toString());
            return merged;
        }

        /**
         * Detects an append method by its name
         * 
         * @param name name of the method
         * @return is or is not
         */
        public static boolean isAppend(String name) {
            return name.equals(APPEND_FC);
        }

        enum OperationType {
            APPEND, TOSTRING
        }
    }
    static class OpcodeC {
        /**
         * Detects a putstatic operation by its opcode
         * 
         * @param opcode opcode of an operation
         * @return is or is not
         */
        public static boolean isPutStatic(int opcode) {
            return opcode == Opcodes.PUTSTATIC;
        }

        /**
         * Detects a getstatic operation by its opcode
         * 
         * @param opcode opcode of an operation
         * @return is or is not
         */
        public static boolean isGetStatic(int opcode) {
            return opcode == Opcodes.GETSTATIC;
        }

        /**
         * Detects an invokestatic operation by its opcode
         * 
         * @param opcode opcode of an operation
         * @return is or is not
         */
        public static boolean isInvokeStatic(int opcode) {
            return opcode == Opcodes.INVOKESTATIC;
        }

        /**
         * Detects an invokevirtual operation by its opcode
         * 
         * @param opcode opcode of an operation
         * @return is or is not
         */
        public static boolean isInvokeVirtual(int opcode) {
            return opcode == Opcodes.INVOKEVIRTUAL;
        }

        /**
         * Detects a return operation by its opcode
         * 
         * @param opcode opcode of an operation
         * @return is or is not
         */
        public static boolean isReturnA(int opcode) {
            return opcode == Opcodes.ARETURN;
        }
    }

    /**
     * Wrappes class structure with additional fields and functions
     * 
     * @param classes Map of classes
     * @return Wrapped classes
     */
    public static ClassMap convertStructMap(Map<String, ClassStruct> classes) {
        ClassMap converted = new ClassMap();
        for (String key : classes.keySet()) {
            converted.put(key, new ClassWrapper(classes.get(key)));
        }
        return converted;
    }

    public static class StackF {
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

        public static boolean contains(Stack<Variable> stack, VariableType vType) {
            for (Variable var : stack) {
                if (var.getType() == vType) {
                    return true;
                }
            }
            return false;
        }

        public static Variable peekEndpoint(Stack<Variable> stack) {
            Variable var = peek(stack);
            if (var != null && var.getType() == VariableType.ENDPOINT) {
                return var;
            }
            return null;
        }

        public static Endpoint removeEndpoint(Stack<Variable> stack, int position) {
            Variable var = null;
            if (stack.size() < position) {
                var = stack.get(position);
            }
            if (var != null && var.getType() == VariableType.ENDPOINT) {
                return (Endpoint) stack.remove(position).getValue();
            }
            return null;
        }

        public static Stack<Variable> removeUntil(VariableType type, Stack<Variable> stack) {
            Stack<Variable> vars = new Stack<>();
            /*
             * if (!contains(stack, type)) { return vars; }
             */
            Variable it = peek(stack);
            for (; it != null && it.getType() != VariableType.ENDPOINT; it = peek(stack)) {
                vars.push(it);
                pop(stack);
            }
            return vars;
        }

        public static Stack<Variable> removeUntil(VariableType type, int num,
                Stack<Variable> stack) {
            Stack<Variable> vars = new Stack<>();
            Variable it = peek(stack);
            for (int counter = 0; it != null && it.getType() != VariableType.ENDPOINT
                    && counter < num; it = peek(stack), counter++) {
                vars.push(it);
                pop(stack);
            }
            return vars;
        }
    }
    static class EndpointF {
        /**
         * Merges new endpoints into existing map of endpoints
         * 
         * @param endpoints Map of endpoints
         * @param endpoint  New endpoint
         */
        public static void merge(Map<String, Endpoint> endpoints, Endpoint endpoint) {
            if (endpoint.getUrl() == null) {
                return;
            }

            Endpoint updatedEndpoint = null;

            if (endpoints.containsKey(endpoint.getUrl())) {
                final Endpoint oldEndpoint = endpoints.remove(endpoint.getUrl());
                updatedEndpoint = oldEndpoint;

            } else if (endpoints.containsKey(endpoint.getPath())) {
                final Endpoint oldEndpoint = endpoints.remove(endpoint.getPath());
                updatedEndpoint = oldEndpoint;

            } else if (endpoints.containsKey(endpoint.getBaseUrl())) {
                final Endpoint oldEndpoint = endpoints.remove(endpoint.getBaseUrl());
                updatedEndpoint = oldEndpoint;

            } else {
                endpoints.put(endpoint.getUrl(), endpoint);
                return;
            }

            if (updatedEndpoint.sEquals(endpoint)) {
                // same instance, data are already changed
            } else {
                updatedEndpoint.merge(endpoint);
            }

            endpoints.put(updatedEndpoint.getUrl(), updatedEndpoint);
        }

        /**
         * Merges new endpoints map into existing one
         * 
         * @param endpoints    Current endpoints
         * @param newEndpoints New endpoints
         */
        public static void merge(Map<String, Endpoint> endpoints,
                Map<String, Endpoint> newEndpoints) {
            for (final String key : newEndpoints.keySet()) {
                final Endpoint endpoint = newEndpoints.get(key);
                if (endpoints.containsKey(key)) {
                    merge(endpoints, endpoint);
                } else {
                    endpoints.put(key, endpoint);
                }
            }
        }
    }
}
