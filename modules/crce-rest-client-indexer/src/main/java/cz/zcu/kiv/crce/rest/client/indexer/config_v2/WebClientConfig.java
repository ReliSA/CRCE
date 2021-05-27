package cz.zcu.kiv.crce.rest.client.indexer.config_v2;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zcu.kiv.crce.rest.client.indexer.config.DefinitionValuesProcessor;

public class WebClientConfig {
    @JsonProperty("class")
    private String className;

    @JsonProperty("methods")
    private Map<MethodType, MethodConfig> methods;

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
    public Map<MethodType, MethodConfig> getMethods() {
        return methods;
    }

    /**
     * @param constructors the constructors to set
     */
    public void setMethods(Map<MethodType, MethodConfig> methods) {
        this.methods = methods;
    }


}
