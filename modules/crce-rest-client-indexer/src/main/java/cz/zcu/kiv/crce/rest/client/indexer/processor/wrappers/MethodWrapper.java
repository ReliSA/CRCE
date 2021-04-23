package cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers;

import org.objectweb.asm.Opcodes;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Method;
import cz.zcu.kiv.crce.rest.client.indexer.processor.VariablesContainer;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.ClassTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.MethodTools;
import cz.zcu.kiv.crce.rest.client.indexer.processor.tools.MethodTools.MethodType;

public class MethodWrapper {
    private Method methodStruct;
    private String description;
    private VariablesContainer vars;
    private boolean isStatic = false;


    public MethodWrapper(Method methodStruct, String owner) {
        this.methodStruct = methodStruct;
        this.description = ClassTools.descriptionToOwner(methodStruct.getDesc());
        this.isStatic = (methodStruct.getAccess() & Opcodes.ACC_STATIC) != 0;
        if (isStatic) {
            this.vars = new VariablesContainer(methodStruct.getParameters());
        } else {
            this.vars = new VariablesContainer(methodStruct.getParameters(), owner);
        }
    }

    /**
     * @return the method
     */
    public Method getMethodStruct() {
        return methodStruct;
    }

    public String getDescription() {
        return this.description;
    }

    public VariablesContainer getVariables() {
        return this.vars;
    }
}
