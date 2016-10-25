package cz.zcu.kiv.crce.metadata.dao.mongodb.internal.db;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author cihlator
 */
public class DbDirective {

    private String name;
    private String value;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
