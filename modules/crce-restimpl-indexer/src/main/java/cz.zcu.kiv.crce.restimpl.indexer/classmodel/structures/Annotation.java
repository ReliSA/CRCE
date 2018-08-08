package cz.zcu.kiv.crce.restimpl.indexer.classmodel.structures;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ghessova on 05.03.2018.
 */
public class Annotation {

    private String name;
    private Map<String, Object> values;

    public Annotation() {
        values = new HashMap<>();
    }

    public Annotation(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValue(String key, Object value) {
        values.put(key, value);
    }

    @Override
    public String toString() {
        return "Annotation{" +
                "name='" + name + '\'' +
                ", values=" + values +
                '}';
    }
}
