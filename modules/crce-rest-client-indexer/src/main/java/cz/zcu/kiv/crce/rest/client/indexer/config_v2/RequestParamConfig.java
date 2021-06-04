package cz.zcu.kiv.crce.rest.client.indexer.config_v2;

import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestParamConfig {
    @JsonProperty("classes")
    private Set<String> classNames;
    @JsonProperty("fields")
    private Map<RequestParamFieldType, Map<String, String>> fields;

    /**
     * @return the className
     */
    public Set<String> getClassNames() {
        return this.classNames;
    }

    /**
     * @param className the className to setwsEndpointParams
     */
    public void setClassName(Set<String> classNames) {
        this.classNames = classNames;
    }

    /**
     * @return the fields
     */
    public Map<RequestParamFieldType, Map<String, String>> getFields() {
        return fields;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(Map<RequestParamFieldType, Map<String, String>> fields) {
        this.fields = fields;
    }
}
