package cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ghessova on 10.03.2018. Field or method parameter
 */
public class Variable {

    private String name;
    private DataType dataType;
    // private Map<String, Annotation> annotations = new HashMap<>();

    public Variable(DataType dataType) {
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    /*
     * public Map<String, Annotation> getAnnotations() { return annotations; }
     * 
     * public void addAnnotation(Annotation annotation) { annotations.put(annotation.getName(),
     * annotation); }
     */

    @Override
    public String toString() {
        return "Variable{" + "name='" + name + '\'' + ", dataType='" + dataType + '}';
    }
}
