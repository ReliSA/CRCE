package cz.zcu.kiv.crce.rest.client.indexer.config_v2;

import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zcu.kiv.crce.rest.client.indexer.config.DefinitionValuesProcessor;

public class WSClientConfig {
    @JsonProperty("class")
    private String className;

    @JsonProperty("methods")
    private Map<MethodType, Set<WSClientMethodConfig>> methods;

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
    public Map<MethodType, Set<WSClientMethodConfig>> getMethods() {
        return methods;
    }

    /**
     * @param methods the methods to set
     */
    public void setMethods(Map<MethodType, Set<WSClientMethodConfig>> methods) {
        this.methods = methods;
    }

    
}
