package cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers;

import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Method;
import cz.zcu.kiv.crce.rest.client.indexer.processor.VariablesContainer;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ClassTools;

public class MethodWrapper {
    private Method methodStruct;
    private String returnType;
    private VariablesContainer vars;
    private boolean isStatic = false;
    private boolean isProcessed = false;
    private boolean isPrimitive = false;

    /**
     * Init
     * @param methodStruct Method structure from visitor
     * @param owner Owner of method aka class which contains this method
     */
    public MethodWrapper(Method methodStruct, String owner) {
        this.methodStruct = methodStruct;
        this.returnType = ClassTools.descriptionToClassPath(methodStruct.getDesc());
        this.isStatic = (methodStruct.getAccess() & Opcodes.ACC_STATIC) != 0;
        if (isStatic) {
            this.vars = new VariablesContainer(methodStruct.getParameters());
        } else {
            this.vars = new VariablesContainer(methodStruct.getParameters(), owner);
        }
        isPrimitive = ClassTools.isPrimitive(returnType);
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
     * Method is now marked as processed
     */
    public void setIsProcessed() {
        isProcessed = true;
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
