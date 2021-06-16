package cz.zcu.kiv.crce.rest.client.indexer.config_v2;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.tools.ClassTools;

public class RequestParamConfig {
    @JsonProperty("classes")
    private Set<String> classes = new HashSet<>();
    @JsonProperty("fields")
    private Map<RequestParamFieldType, Map<String, String>> fields;

    /**
     * @return the className
     */
    public Set<String> getClassNames() {
        return this.classes;
    }

    /**
     * @param classNames the classNames to setwsEndpointParams
     */
    public void setClasses(Set<String> classNames) {
        for (final String className : classNames) {
            final String processed = ClassTools.processClassName(className);
            this.classes.add(processed);
        }
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
