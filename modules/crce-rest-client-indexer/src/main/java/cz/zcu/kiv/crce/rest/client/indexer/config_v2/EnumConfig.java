package cz.zcu.kiv.crce.rest.client.indexer.config_v2;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EnumConfig {
    @JsonProperty("class")
    private String className;
    @JsonProperty("fields")
    private Map<MethodArgType, Map<String, String>> fields;

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return the fields
     */
    public Map<MethodArgType, Map<String, String>> getFields() {
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(Map<MethodArgType, Map<String, String>> fields) {
        this.fields = fields;
    }
}
