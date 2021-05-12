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

    public String getOwner() {
        return this.methodStruct.getOwner();
    }

    public String getReturnType() {
        return this.returnType;
    }

    public VariablesContainer getVariables() {
        return this.vars;
    }

    public void setIsProcessed() {
        isProcessed = true;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public boolean isPrimitive() {
        return isPrimitive;
    }
}
