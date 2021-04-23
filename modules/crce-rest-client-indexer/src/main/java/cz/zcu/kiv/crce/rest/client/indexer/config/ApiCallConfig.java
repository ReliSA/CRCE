package cz.zcu.kiv.crce.rest.client.indexer.config;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiCallConfig {
    @JsonProperty("class")
    private String className;

    @JsonProperty("methods")
    private Set<ApiCallMethodConfig> methods;

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
        final String processed = DefinitionValuesProcessor.processClassName(className);
        this.className = processed;
    }

    /**
     * @return the constructors
     */
    public Set<ApiCallMethodConfig> getMethods() {
        return methods;
    }

    /**
     * @param constructors the constructors to set
     */
    public void setMethods(Set<ApiCallMethodConfig> methods) {
        this.methods = methods;
    }


}
