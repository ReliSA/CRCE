package cz.zcu.kiv.crce.rest.client.indexer.processor;

import java.util.Stack;

import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Method;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation.OperationType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.Variable.VariableType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ClassTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.NumTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.VariableTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.ClassMap;
import cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers.MethodWrapper;

public class BasicProcessor {


    protected ClassMap classes;

    /**
     * Check if on top of the stack is not owner (instance of class) of the method e.q.
     * com/baeldung/reactive/constants/NonStaticUri (contains getTest) -> is currently on stack
     * invokevirtual com/baeldung/reactive/constants/NonStaticUri.getTest()Ljava/lang/String 
     * -> deletes NonStaticUri
     * 
     * @param values
     * @param operation
     */
    protected void handleAccessingObject(Stack<Variable> values, Operation operation) {
        // TODO: give me those params
        Variable var = Helpers.StackF.peek(values);
        if (var != null && var.getType() == VariableType.OTHER
                && var.getOwner().equals(operation.getOwner())) {
            values.pop();
        }
    }

    public BasicProcessor(ClassMap classes) {
        this.classes = classes;
    }

    protected void processGETFIELD(Stack<Variable> values, Operation operation) {
        handleAccessingObject(values, operation);
    }

    protected void processGETSTATICFIELD(Stack<Variable> values, Operation operation) {
        if (!classes.containsKey(operation.getOwner())) {
            return;
        }
        ConstPool classPool = this.classes.get(operation.getOwner()).getClassPool();

        if (!classPool.containsKey(operation.getFieldName())) {
            return;
        }
        Variable field = classPool.get(operation.getFieldName());
        if (field == null) {
            return;
        }
        field.setDescription(ClassTools.descriptionToClassPath(operation.getDesc()));
        values.add(field);
    }

    /**
     * Processes operations with fields of concrete class
     * 
     * @param operation Operation performed on field
     * @param values String values
     */
    protected void processFIELD(Operation operation, Stack<Variable> values) {
        switch (operation.getOpcode()) {
            case Opcodes.GETFIELD:
                processGETFIELD(values, operation);
            case Opcodes.GETSTATIC:
                processGETSTATICFIELD(values, operation);
                break;
            case Opcodes.PUTSTATIC:
            case Opcodes.PUTFIELD: {
                if (!classes.containsKey(operation.getOwner())) {
                    return;
                }
                ConstPool classPool = this.classes.get(operation.getOwner()).getClassPool();
                Variable var = Helpers.StackF.pop(values);
                if (VariableTools.isEmpty(var)) {
                    break;
                }
                classPool.put(operation.getFieldName(), var);
            }
                break;
        }

    }

    /**
     * Processes constants like String, Integer, Float...
     * 
     * @param operation Operation create constant
     * @param values String values
     */
    protected void processCONSTANT(Operation operation, Stack<Variable> values) {
        String newValue = operation.getValue() != null ? operation.getValue().toString() : null;
        Variable last = Helpers.StackF.peek(values);
        if (newValue != null && NumTools.isNumeric(newValue) && last != null
                && last.getType() == VariableType.ARRAY && last.getValue() instanceof VarArray) {
            VarArray array = (VarArray) last.getValue();
            array.setPosition(Integer.valueOf(newValue));
            return;
        }
        values.add(new Variable(newValue).setType(VariableType.SIMPLE));
    }

    /**
     * Stores values into local variables aka const pool
     * 
     * @param method Method which includes concrete const pool
     * @param operation Operation for storing into variable
     * @param values String values
     */
    protected void processSTORE(MethodWrapper method, Operation operation, Stack<Variable> values) {
        // ConstPool constPool = method.getConstPool();

        if (operation.getOpcode() == Opcodes.AASTORE) {
            processAASTORE(method, operation, values);
            return;
        }

        VariablesContainer variables = method.getVariables();

        Variable var = Helpers.StackF.pop(values);
        if (VariableTools.isEmpty(var)) {
            return;
        }
        values.removeAll(values);
        variables.set(operation.getIndex(), var);
    }

    protected void processAASTORE(MethodWrapper method, Operation operation,
            Stack<Variable> values) {
        Variable arrayItem = Helpers.StackF.pop(values);
        Variable array = Helpers.StackF.pop(values);
        if (arrayItem == null || VariableTools.isEmpty(array)
                || array.getType() != VariableType.ARRAY || !(array.getValue() instanceof VarArray)
                || (arrayItem.getValue() instanceof VarArray)) {
            return;
        }
        VarArray varArray = (VarArray) array.getValue();
        varArray.set((VariableTools.isEmpty(arrayItem) || arrayItem.getValue().toString().isEmpty())
                ? arrayItem.getDescription()
                : (String) arrayItem.getValue());
        values.push(array);
    }

    /**
     * Loads value from constant pool into values stack
     * 
     * @param method Method where is Load performed
     * @param operation Loading operation
     * @param values String values
     */
    protected void processLOAD(MethodWrapper method, Operation operation, Stack<Variable> values) {
        VariablesContainer variables = method.getVariables();
        Variable var = variables.get(operation.getIndex());

        if (var == null) {
            var = variables.init(operation.getIndex());
        }
        values.push(var);
        /*
         * if (var.getType() != VariableType.OTHER) { values.add(var); }
         */
    }

    /**
     * Wrapper for processing all operations of a function
     * 
     * @param method Method which will be processed
     * @param values String values
     */
    protected void processInner(MethodWrapper method, Stack<Variable> values) {

        Method methodStruct = method.getMethodStruct();
        for (Operation operation : methodStruct.getOperations()) {
            processOperation(method, operation, values);
        }
    }

    protected void processANEWARRAY(Operation operation, Stack<Variable> values) {
        Variable possibleNum = Helpers.StackF.pop(values);
        Variable arr =
                new Variable().setType(VariableType.ARRAY).setDescription(operation.getDesc());
        if (!VariableTools.isEmpty(possibleNum)
                && NumTools.isNumeric(possibleNum.getValue().toString())) {
            arr.setValue(new VarArray(Integer.valueOf(possibleNum.getValue().toString())));
        }
        values.push(arr);
    }

    protected void processDUP(Stack<Variable> values) {
        Variable last = Helpers.StackF.pop(values);
        if (!VariableTools.isEmpty(last) && last.getType() == VariableType.ARRAY) {

        }
    }

    /**
     * Processes operation of given method and stores/modifies values holder
     * 
     * @param method Method where is operation performed
     * @param operation Concrete operation
     * @param values String values
     */
    protected void processOperation(MethodWrapper method, Operation operation,
            Stack<Variable> values) {
        final OperationType type = operation.getType();
        switch (type) {
            case FIELD:
                processFIELD(operation, values);
                break;

            case STRING_CONSTANT:
            case INT_CONSTANT:
                processCONSTANT(operation, values);
                break;

            case LOAD:
                processLOAD(method, operation, values);
                break;

            case STORE:
                processSTORE(method, operation, values);
                break;
            case ANEWARRAY:
                processANEWARRAY(operation, values);
                break;
            case DUP:
                break;
            default:;

        }
    }
}
