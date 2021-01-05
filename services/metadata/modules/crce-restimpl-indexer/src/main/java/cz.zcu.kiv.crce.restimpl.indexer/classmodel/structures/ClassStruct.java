package cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ghessova on 05.03.2018.
 */
public class ClassStruct extends PathPart {

    private String parent;
    private List<Method> methods = new ArrayList<>();
    private Set<Field> fields = new HashSet<>();
    private String[] interfaces;

    public ClassStruct() {
    }

    public ClassStruct(String name, String parent) {
        this.name = name;
        this.parent = parent;
    }

    public void addMethod(Method method) {
        methods.add(method);
    }

    public List<Method> getMethods() {
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

    @Override
    public String toString() {
        return "ClassStruct{" +
                "name=" + name +
                ", methods=" + methods +
                ", fields=" + fields +
                ", parent=" + parent +
                ", interfaces=" + interfaces +
                '}';
    }
}