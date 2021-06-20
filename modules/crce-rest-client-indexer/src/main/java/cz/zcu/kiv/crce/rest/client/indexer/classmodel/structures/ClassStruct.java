package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ghessova on 05.03.2018.
 */
public class ClassStruct {

    private String name;
    private String parent;
    private String signature;
    private Map<String, Method> methodsMap = new HashMap<>();
    private List<Method> methods = null;
    private Set<Field> fields = new HashSet<>();
    private String[] interfaces;
    private Method clnitMethod;

    public ClassStruct() {}



    /**
     * @return the cinitMethod
     */
    public Method getClnitMethod() {
        return clnitMethod;
    }

    /**
     * @param cinitMethod the cinitMethod to set
     */
    public void setClnitMethod(Method cinitMethod) {
        this.clnitMethod = cinitMethod;
    }

    public ClassStruct(String name, String parent, String signature, String[] interfaces) {
        this(name, parent);
        this.signature = signature;
        this.interfaces = interfaces;
    }

    public ClassStruct(String name, String parent) {
        this.name = name;
        this.parent = parent;
    }

    public void addMethod(Method method) {
        methodsMap.put(method.getName(), method);
    }

    public Method getMethod(String name) {
        if (!methodsMap.containsKey(name)) {
            return null;
        }
        return methodsMap.get(name);
    }

    public List<Method> getMethods() {
        if (methods == null) {
            methods = new ArrayList<Method>(methodsMap.values());
        }
        return methods;
    }

    public Set<Field> getFields() {
        return fields;
    }

    public void addField(Field field) {
        fields.add(field);
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String[] getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(String[] interfaces) {
        this.interfaces = interfaces;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ClassStruct{" + "name=" + name + ", methods=" + methodsMap.values() + ", fields="
                + fields + ", parent=" + parent + ", interfaces=" + interfaces + '}';
    }

    /**
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param signature the signature to set
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }
}
