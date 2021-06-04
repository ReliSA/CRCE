package cz.zcu.kiv.crce.rest.client.indexer.processor;

import java.util.Stack;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Method;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation.OperationType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.Variable;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.Variable.VariableType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ClassTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.MethodTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.SafeStack;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.StringTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.VariableTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.MethodTools.MethodType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.MethodWrapper;

public class MethodProcessor extends BasicProcessor {

    private StringTools.OperationType stringOP = null;
    private static final Logger logger = LoggerFactory.getLogger(MethodProcessor.class);

    /**
     * 
     * @param classes All classes available
     */
    public MethodProcessor(ClassMap classes) {
        super(classes);
    }

    /**
     * Safe retrieving of method (if not processed than process it)
     * @param operation Operation of method execution
     * @return Method wrapper
     */
    protected MethodWrapper getMethodWrapper(Operation operation) {
        final String methodName = operation.getMethodName();
        final String operationOwner = operation.getOwner();
        final ClassWrapper classWrapper = this.classes.getOrDefault(operationOwner, null);
        if (classWrapper == null) {
            logger.error("Missing class=" + operationOwner);
            return null;
        }

        MethodWrapper methodWrapper = classWrapper.getMethod(methodName);

        if (methodWrapper == null) {
            logger.error("Missing method=" + methodName + " class=" + operationOwner);
            return null;
        }

        return methodWrapper;
    }

    /**
     * Cleans leftovers from Stack
     * @param values Stack
     * @param operation Operation of method call
     */
    protected void cleanupAfterMCall(Stack<Variable> values, Operation operation) {
        removeMethodArgsFromStack(values, operation);
        handleAccessingObject(values, operation);
    }

    /**
     * Removes values based on description of method
     * e.g.:
     * invokevirtual java/lang/StringBuilder.append(Ljava/lang/String;)Ljava/lang/StringBuilder -> one param
     * pops out one parameter from stacck
     * @param values
     * @param operation
     */
    protected void removeMethodArgsFromStack(Stack<Variable> values, Operation operation) {
        String[] args = MethodTools.getArgsFromSignature(operation.getDescription());
        if (args != null) {
            for (String arg : args) {
                SafeStack.pop(values);
            }
        }
    }

    /**
     * Process method its Strings and numbers
     * 
     * @param method Method to process
     */
    protected void process(MethodWrapper method) {
        Stack<Variable> values = new Stack<>();
        this.processInner(method, values);
    }

    /**
     * Process appending Strings and pushes merged string back to the Stack
     * @param values Stack
     */
    private void processAppendString(Stack<Variable> values) {
        if (this.stringOP == StringTools.OperationType.APPEND) {
            Variable merged = new Variable().setType(VariableType.SIMPLE);
            String mergedS = "";
            while (SafeStack.peek(values) != null) {
                Variable last = SafeStack.peek(values);
                if (!VariableTools.isStringVar(last) && !VariableTools.isNumberVar(last)) {
                    break;
                }
                values.pop();
                mergedS = last.getValue() + mergedS;
            }
            merged.setValue(mergedS);
            values.push(merged);

        }

    }

    /**
     * Process invoked virtual methods
     * @param values Stack
     * @param operation INVOKEVIRTUAL operation
     */
    protected void processINVOKEVIRTUAL(Stack<Variable> values, Operation operation) {
        final String methodName = operation.getMethodName();
        handleAccessingObject(values, operation);

        if (StringTools.isToString(methodName)) {
            this.stringOP = StringTools.OperationType.TOSTRING;
        } else if (StringTools.isAppend(methodName)) {
            processAppendString(values);
            this.stringOP = StringTools.OperationType.APPEND;
        } else {
            removeMethodArgsFromStack(values, operation);
            if (!this.classes.containsKey(operation.getOwner())) {
                return;
            }
            final MethodWrapper mw = getMethodWrapper(operation);
            if (mw == null) {
                return;
            }
            Variable variable = new Variable(mw.getMethodStruct().getReturnValue())
                    .setType(VariableType.OTHER).setDescription(mw.getReturnType());

            if (mw.hasPrimitiveReturnType()) {
                variable.setType(VariableType.SIMPLE);
            }
            values.add(variable);
        }
    }

    /**
     * Processes invoked special methods e.g. <init>
     * @param values Stack
     * @param operation INVOKESPECIAL operation
     */
    protected void processINVOKESPECIAL(Stack<Variable> values, Operation operation) {
        if (MethodTools.getType(operation.getDescription()) == MethodType.INIT) {
            Variable variable = new Variable().setType(VariableType.OTHER)
                    .setDescription(operation.getDescription()).setOwner(operation.getOwner());
            values.add(variable);
            return;
        }
        cleanupAfterMCall(values, operation);
    }

    /**
     * Processes invoked interface method (only cleanup)
     * @param values
     * @param operation
     */
    protected void processINVOKEINTERFACE(Stack<Variable> values, Operation operation) {
        cleanupAfterMCall(values, operation);
    }

    /**
     * Processes static invocation of methods
     * @param values Stack
     * @param operation INVOKESTATIC operation
     */
    protected void processINVOKESTATIC(Stack<Variable> values, Operation operation) {
        final MethodWrapper mw = getMethodWrapper(operation);
        if (mw == null) {
            return;
        }
        final Method method = getMethodWrapper(operation).getMethodStruct();
        if (mw.getOwner().equals("")) {
            return;
        }
        Variable newVar = new Variable();
        if (ClassTools.isPrimitive(mw.getReturnType())) {
            newVar.setType(VariableType.SIMPLE);
        } else {
            newVar.setType(VariableType.OTHER);
        }
        removeMethodArgsFromStack(values, operation);
        newVar.setValue(method.getReturnValue());
        values.push(newVar);
    }

    /**
     * Process CALL operation (toString, append - operation with Strings)
     * 
     * @param operation Operation to be handled
     * @param values String values
     */
    protected void processCALL(Operation operation, Stack<Variable> values) {
        switch (operation.getOpcode()) {
            case Opcodes.INVOKESTATIC:
                processINVOKESTATIC(values, operation);
                break;
            case Opcodes.INVOKEVIRTUAL:
                processINVOKEVIRTUAL(values, operation);
                break;
            case Opcodes.INVOKESPECIAL:
                processINVOKESPECIAL(values, operation);
                break;
            case Opcodes.INVOKEINTERFACE:
                processINVOKEINTERFACE(values, operation);
                break;
        }
    }

    /**
     * Processes return values of each method and sets its return value
     * 
     * @param methodWrapper Method to be processed
     * @param operation Explicit operation
     * @param values String values
     */
    protected void processRETURN(MethodWrapper methodWrapper, Operation operation,
            Stack<Variable> values) {
        Method method = methodWrapper.getMethodStruct();

        if (method.getDesc().equals("()V")) {
            return;
        }
        if (values.size() == 0) {
            return;
        }
        Variable var = values.peek();
        switch (operation.getOpcode()) {
            case Opcodes.ARETURN:
            case Opcodes.LRETURN:
            case Opcodes.FRETURN:
            case Opcodes.DRETURN:
            case Opcodes.IRETURN:
                method.setReturnValue(var.getValue() == null ? "" : var.getValue().toString());
                break;
            case Opcodes.RETURN:
                method.setReturnValue("");
                break;
        }
        values.removeAll(values);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void processOperation(MethodWrapper method, Operation operation,
            Stack<Variable> values) {
        super.processOperation(method, operation, values);
        final OperationType type = operation.getType();
        switch (type) {
            case RETURN:
                processRETURN(method, operation, values);
                values.removeAll(values);
                break;
            case CALL:
                processCALL(operation, values);
                break;
            default:;
        }
    }

}
