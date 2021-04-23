package cz.zcu.kiv.crce.rest.client.indexer.config;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EDataContainerConfig {
    @JsonProperty("class")
    private String className;

    @JsonProperty("methods")
    private Set<EDataContainerMethodConfig> methods;

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
     * @return the methods
     */
    public Set<EDataContainerMethodConfig> getMethods() {
        return methods;
    }

    /**
     * @param methods the methods to set
     */
    public void setMethods(Set<EDataContainerMethodConfig> methods) {
        this.methods = methods;
    }



}
