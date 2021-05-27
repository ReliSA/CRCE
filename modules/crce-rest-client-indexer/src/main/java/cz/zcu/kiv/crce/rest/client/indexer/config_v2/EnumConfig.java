package cz.zcu.kiv.crce.rest.client.indexer.config_v2;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EnumConfig {
    @JsonProperty("class")
    private String className;
    @JsonProperty("fields")
    private Set<EnumItemConfig> fields;

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
    public Set<EnumItemConfig> getFields() {
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(Set<EnumItemConfig> fields) {
        this.fields = fields;
    }


}
