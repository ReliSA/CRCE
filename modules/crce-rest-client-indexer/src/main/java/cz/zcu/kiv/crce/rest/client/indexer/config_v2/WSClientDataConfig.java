package cz.zcu.kiv.crce.rest.client.indexer.config_v2;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zcu.kiv.crce.rest.client.indexer.config.DefinitionValuesProcessor;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.tools.ClassTools;
import cz.zcu.kiv.crce.rest.client.indexer.shared.HeaderType;

public class WSClientDataConfig {
    @JsonProperty("classes")
    private Set<String> classNames;

    @JsonProperty("methods")
    private Set<WSClientMethodConfig> methods;


    /**
     * @return the methods
     */
    public Set<WSClientMethodConfig> getMethods() {
        return methods;
    }

    /**
     * @param methods the methods to set
     */
    public void setMethods(Set<WSClientMethodConfig> methods) {
        this.methods = methods;
    }

    /**
     * @return the classNames
     */
    public Set<String> getClassNames() {
        return classNames;
    }

    /**
     * @param classNames the classNames to set
     */
    public void setClassNames(Set<String> classNames) {
        this.classNames = new HashSet<>();
        for (String className : classNames) {
            //TODO: replace
            final String processed = ClassTools.processClassName(className);
            this.classNames.add(processed);
        }
    }

    @JsonProperty("headers")
    private Map<HeaderType, Set<WSClientMethodConfig>> headers;

    /**
     * @return the headers
     */
    public Map<HeaderType, Set<WSClientMethodConfig>> getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(Map<HeaderType, Set<WSClientMethodConfig>> headers) {
        this.headers = headers;
    }

}
