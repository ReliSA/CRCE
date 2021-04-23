package cz.zcu.kiv.crce.rest.client.indexer.processor;

import java.util.Stack;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Method;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation.OperationType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.Helpers.StringC;
import cz.zcu.kiv.crce.rest.client.indexer.processor.Variable.VariableType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ClassTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.MethodTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.VariableTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.MethodTools.MethodType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassWrapper;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.MethodWrapper;

public class MethodProcessor extends BasicProcessor {

    private Helpers.StringC.OperationType stringOP = null;

    public MethodProcessor(ClassMap classes) {
        super(classes);
    }

    protected void removeMethodArgsFromStack(Stack<Variable> values, Operation operation) {
        String[] args = MethodTools.getArgsFromSignature(operation.getDescription());
        if (args != null) {
            for (String arg : args) {
                Helpers.StackF.pop(values);
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

    private void processAppendString(Stack<Variable> values) {
        if (this.stringOP == Helpers.StringC.OperationType.APPEND) {
            Variable merged = new Variable().setType(VariableType.SIMPLE);
            String mergedS = "";
            while (Helpers.StackF.peek(values) != null) {
                Variable last = Helpers.StackF.peek(values);
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

    protected void processINVOKEVIRTUAL(Stack<Variable> values, Operation operation) {
        handleAccessingObject(values, operation);
        final String methodName = operation.getMethodName();

        if (Helpers.StringC.isToString(methodName)) {
            this.stringOP = StringC.OperationType.TOSTRING;
        } else if (Helpers.StringC.isAppend(methodName)) {
            processAppendString(values);
            this.stringOP = Helpers.StringC.OperationType.APPEND;
        } else {
            handleAccessingObject(values, operation);
            removeMethodArgsFromStack(values, operation);
            if (!this.classes.containsKey(operation.getOwner())) {
                return;
            }
            ClassWrapper cw = this.classes.get(operation.getOwner());
            MethodWrapper mw = cw.getMethod(operation.getMethodName());
            if (mw == null) {
                return;
            }
            Variable variable = new Variable(mw.getMethodStruct().getReturnValue())
                    .setType(VariableType.OTHER).setDescription(mw.getDescription());

            if (ClassTools.isPrimitive(mw.getDescription())) {
                variable.setType(VariableType.SIMPLE);
            }
            values.add(variable);
        }
    }

    protected void processINVOKESPECIAL(Stack<Variable> values, Operation operation) {
        Variable variable = Helpers.StackF.peek(values);
        if (MethodTools.getType(operation.getDescription()) == MethodType.INIT) {
            variable = new Variable().setType(VariableType.OTHER)
                    .setDescription(operation.getDescription()).setOwner(operation.getOwner());
            values.add(variable);
            return;
        }
    }

    protected void processINVOKEINTERFACE(Stack<Variable> values, Operation operation) {
        removeMethodArgsFromStack(values, operation);
        handleAccessingObject(values, operation);
    }


    protected void processINVOKESTATIC(Stack<Variable> values, Operation operation) {
        final String methodName = operation.getMethodName();
        final String operationOwner = operation.getOwner();
        final ClassWrapper classWrapper = this.classes.getOrDefault(operationOwner, null);

        if (classWrapper == null) {
            return;
        }
        MethodWrapper methodWrapper = classWrapper.getMethod(methodName);
        final Method method = methodWrapper.getMethodStruct();
        if (methodWrapper.getDescription().equals("")) {
            return;
        }
        if (method.getReturnValue() == null) {
            this.process(methodWrapper);
        }
        Variable newVar = new Variable();
        if (ClassTools.isPrimitive(methodWrapper.getDescription())) {
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
