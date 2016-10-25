package cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Cihlář
 */
public class DbAttribute<T> {

    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String VALUE = "value";

    private String name;
    private String type;
    private Object value;

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty(TYPE)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty(VALUE)
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
