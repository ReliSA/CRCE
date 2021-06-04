package cz.zcu.kiv.crce.rest.client.indexer.processor;

import java.util.Stack;

import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Method;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Operation.OperationType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.VarArray;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.Variable;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.Variable.VariableType;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ClassTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.NumTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.SafeStack;
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
        Variable var = SafeStack.peek(values);
        if (var != null && var.getType() == VariableType.OTHER
                && var.getOwner().equals(operation.getOwner())) {
            values.pop();
        }
    }

    /**
     * 
     * @param classes All classes available
     */
    public BasicProcessor(ClassMap classes) {
        this.classes = classes;
    }

    /**
     * Process get field from instance
     * @param values Stack
     * @param operation GETFIELD operation
     */
    protected void processGETFIELD(Stack<Variable> values, Operation operation) {
        handleAccessingObject(values, operation);
    }

    /**
     * Process (static) get field from class
     * @param values Stack
     * @param operation GETSTATICFIELD operation
     */
    protected void processGETSTATICFIELD(Stack<Variable> values, Operation operation) {
        if (!classes.containsKey(operation.getOwner())) {
            return;
        }
        ConstPool classPool = this.classes.get(operation.getOwner()).getClassPool();

        if (!classPool.containsKey(operation.getFieldName())) {
            classPool.put(operation.getFieldName(), new Variable("").setType(VariableType.SIMPLE));
        }
        Variable field = classPool.get(operation.getFieldName());
        if (field == null) {
            return;
        }
        field.setDescription(ClassTools.descriptionToClassPath(operation.getDesc()));
        values.add(field);
    }

    /**
     * Process static and classic putfield 
     * @param values Stack
     * @param operation PUTFIELD operation
     */
    protected void processPUTFIELD(Stack<Variable> values, Operation operation) {
        if (!classes.containsKey(operation.getOwner())) {
            return;
        }
        ConstPool classPool = this.classes.get(operation.getOwner()).getClassPool();
        Variable var = SafeStack.pop(values);
        if (VariableTools.isEmpty(var)) {
            return;
        }
        classPool.put(operation.getFieldName(), var);
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
            case Opcodes.PUTFIELD:
                processPUTFIELD(values, operation);
                break;
        }

    }

    /**
     * Processes constants like Integer, Float...
     * 
     * @param operation Operation create constant
     * @param values String values
     */
    protected void processNUMCONSTANT(Operation operation, Stack<Variable> values) {
        String newValue = operation.getValue() != null ? operation.getValue().toString() : null;
        Variable last = SafeStack.peek(values);
        if (newValue != null && NumTools.isNumeric(newValue) && last != null
                && last.getType() == VariableType.ARRAY && last.getValue() instanceof VarArray) {
            VarArray array = (VarArray) last.getValue();
            array.setPosition(Integer.valueOf(newValue));
            return;
        }
        values.add(new Variable(newValue).setType(VariableType.SIMPLE));
    }

    /**
     * Processes String constant
     * 
     * @param operation Operation create (String) constant
     * @param values String values
     */
    protected void processSTRINGCONST(Operation operation, Stack<Variable> values) {
        String newValue = operation.getValue() != null ? operation.getValue().toString() : null;
        values.add(new Variable(newValue).setType(VariableType.SIMPLE));
    }

    /**
     * Stores values into local variables aka const pool
     * 
     * @param method Method which includes set of variables to which data will be stored
     * @param operation STORE operation
     * @param values String values
     */
    protected void processSTORE(MethodWrapper method, Operation operation, Stack<Variable> values) {
        // ConstPool constPool = method.getConstPool();

        if (operation.getOpcode() == Opcodes.AASTORE) {
            processAASTORE(method, operation, values);
            return;
        }

        VariablesContainer variables = method.getVariables();

        Variable var = SafeStack.pop(values);
        if (VariableTools.isEmpty(var)) {
            return;
        }
        values.removeAll(values);
        variables.set(operation.getIndex(), var);
    }

    /**
     * Process saving data into array
     * @param method Input method
     * @param operation AASTORE operation
     * @param values STACK
     */
    protected void processAASTORE(MethodWrapper method, Operation operation,
            Stack<Variable> values) {
        Variable arrayItem = SafeStack.pop(values);
        Variable array = SafeStack.pop(values);
        if (arrayItem == null || VariableTools.isEmpty(array)
                || array.getType() != VariableType.ARRAY || !(array.getValue() instanceof VarArray)
                || (arrayItem.getValue() instanceof VarArray)) {
            return;
        }
        VarArray varArray = (VarArray) array.getValue();
        if (arrayItem.getType() == VariableType.SIMPLE && arrayItem.getValue() != null) {
            //varArray.set((String) arrayItem.getValue());
            varArray.set(arrayItem);
        }
        values.push(array);
    }

    /**
     * Loads value from constant pool into values stack
     * 
     * @param method Method where is Load performed
     * @param operation LOAD operation
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

    /**
     * Processing new array initiation
     * @param operation ANEWARRAY operation
     * @param values Stack
     */
    protected void processANEWARRAY(Operation operation, Stack<Variable> values) {
        Variable possibleNum = SafeStack.pop(values);
        Variable arr = new Variable().setType(VariableType.ARRAY);
        if (!VariableTools.isEmpty(possibleNum)
                && NumTools.isNumeric(possibleNum.getValue().toString())) {
            arr.setValue(new VarArray(Integer.valueOf(possibleNum.getValue().toString())));
        }
        values.push(arr);
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
                processSTRINGCONST(operation, values);
                break;
            case INT_CONSTANT:
                processNUMCONSTANT(operation, values);
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
