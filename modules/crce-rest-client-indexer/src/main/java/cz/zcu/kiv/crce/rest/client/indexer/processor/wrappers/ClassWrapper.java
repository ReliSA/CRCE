package cz.zcu.kiv.crce.rest.client.indexer.processor.wrappers;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.ClassStruct;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Field;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Method;
import cz.zcu.kiv.crce.rest.client.indexer.processor.ConstPool;

public class ClassWrapper {
    private ConstPool classPool;
    private ClassStruct classStruct;
    private List<MethodWrapper> methodsList;
    private Map<String, MethodWrapper> methods;
    private Map<String, Field> fieldsContainer;

    public ClassWrapper(ClassStruct classStruct) {
        this.classStruct = classStruct;
        classPool = new ConstPool();
        methods = new HashMap<>();
        fieldsContainer = new HashMap<>();

        for (Method method : classStruct.getMethods()) {

            methods.put(method.getName(), new MethodWrapper(method, classStruct.getName()));
        }
        for (Field field : classStruct.getFields()) {
            fieldsContainer.put(field.getName(), field);
        }
    }

    /**
     * 
     * @return Fields
     */
    public Map<String, Field> getFieldsContainer() {
        return this.fieldsContainer;
    }

    /**
     * 
     * @return keys of constant pool this class
     */
    public Set<String> getFieldNames() {
        return classPool.keySet();
    }

    public void setFieldType(String fieldName, String type) {

    }

    /**
     * @return the classPool
     */
    public ConstPool getClassPool() {
        return classPool;
    }

    /**
     * @return the classStruct
     */
    public ClassStruct getClassStruct() {
        return classStruct;
    }

    /**
     * Gives method saved by its name
     * 
     * @param name Name of the method
     * @return Method
     */
    public MethodWrapper getMethod(String name) {
        return this.methods.get(name);
    }

    /**
     * Gives a whole list of methods stored in local map
     * 
     * @return List of methods
     */
    public List<MethodWrapper> getMethods() {
        if (methodsList == null) {
            methodsList = new LinkedList<MethodWrapper>(methods.values());
        }
        return methodsList;
    }

    /**
     * Removes method from map
     * 
     * @param name Name of the method
     */
    public void removeMethod(String name) {
        this.methods.remove(name);
        methodsList = new LinkedList<MethodWrapper>(methods.values());
    }
}
