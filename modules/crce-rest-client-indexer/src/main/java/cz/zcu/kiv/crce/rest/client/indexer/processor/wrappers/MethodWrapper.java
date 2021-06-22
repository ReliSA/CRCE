package cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers;

import java.util.Stack;
import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.DataType;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Method;
import cz.zcu.kiv.crce.rest.client.indexer.processor.VariablesContainer;
import cz.zcu.kiv.crce.rest.client.indexer.processor.structures.Variable;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ClassTools;

public class MethodWrapper {
    private Method methodStruct;
    private String returnType;
    private VariablesContainer vars;
    private boolean isStatic = false;
    private boolean isProcessed = false;
    private boolean isPrimitive = false;
    private Variable returnValue;

    /**
     * Init
     * @param methodStruct Method structure from visitor
     * @param owner Owner of method aka class which contains this method
     */
    public MethodWrapper(Method methodStruct, String owner) {
        this.methodStruct = methodStruct;
        if (methodStruct.getReturnType() == null) {//generic
            this.returnType = "java/lang/Object";
        }
        this.returnType = methodStruct.getReturnType().getBasicType();
        this.isStatic = (methodStruct.getAccess() & Opcodes.ACC_STATIC) != 0;
        if (isStatic) {
            this.vars = new VariablesContainer(methodStruct.getParameters());
        } else {
            this.vars = new VariablesContainer(methodStruct.getParameters(), owner);
        }
        isPrimitive = ClassTools.isPrimitive(returnType);
    }

    /**
     * @param returnValue the returnValue to set
     */
    public void setReturnValue(Variable returnValue) {
        this.returnValue = returnValue;
    }

    /**
     * @return the returnValue
     */
    public Variable getReturnValue() {
        if (returnValue == null) {
            return new Variable().setDescription(methodStruct.getReturnType().getBasicType());
        }
        return returnValue;
    }

    /**
     * @return the isStatic
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * @return the method
     */
    public Method getMethodStruct() {
        return methodStruct;
    }

    /**
     * 
     * @return Owner of this method
     */
    public String getOwner() {
        return this.methodStruct.getOwner();
    }

    /**
     * 
     * @return Return type of this method
     */
    public String getReturnType() {
        return this.returnType;
    }

    /**
     * 
     * @return Variables of this method
     */
    public VariablesContainer getVariables() {
        return this.vars;
    }

    /**
     * @param vars the vars to set
     */
    public void recreateVars(Stack<Variable> args) {
        if (isStatic) {
            this.vars = new VariablesContainer(methodStruct.getParameters());
        } else {
            this.vars = new VariablesContainer(methodStruct.getParameters(),
                    this.methodStruct.getOwner());
        }
        int i = args.size() - 1;
        if (!this.isStatic()) {
            i++;
        }
        for (final Variable arg : args) {
            this.getVariables().set(i--, arg);
        }
    }

    /**
     * Method is now marked as processed
     */
    public void setIsProcessed() {
        isProcessed = true;
    }

    /**
     * Method is now marked as processed
     */
    public void setIsProcessed(boolean processed) {
        isProcessed = processed;
    }

    /**
     * 
     * @return Is this method processed?
     */
    public boolean isProcessed() {
        return isProcessed;
    }

    /**
     * 
     * @return Is the return type of this method considered as primitive one (String, Number etc)
     */
    public boolean hasPrimitiveReturnType() {
        return isPrimitive;
    }
}
