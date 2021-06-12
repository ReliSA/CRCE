package cz.zcu.kiv.crce.rest.client.indexer.config_v2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.tools.ClassTools;
import cz.zcu.kiv.crce.rest.client.indexer.shared.HttpMethodExt;

public class WSClientConfig {

    @JsonProperty("classes")
    private Set<String> classNames = new HashSet<>();

    @JsonProperty("interfaces")
    private Set<String> interfaces = new HashSet<>();

    @JsonProperty("inherits")
    private Set<String> inherits = new HashSet<>();

    @JsonProperty("settings")
    private Map<MethodArgType, Set<WSClientMethodConfig>> settings = new HashMap<>();

    @JsonProperty("request")
    private Map<HttpMethodExt, Set<WSClientMethodConfig>> request = new HashMap<>();


    /**
     * @param inherits the inherits to set
     */
    public void setInherits(Set<String> inherits) {
        for (final String className : inherits) {
            final String processed = ClassTools.processClassName(className);
            this.inherits.add(processed);
        }
    }

    /**
     * @return the inherits
     */
    public Set<String> getInherits() {
        return inherits;
    }

    /**
     * @return the settings
     */
    public Map<MethodArgType, Set<WSClientMethodConfig>> getSettings() {
        return settings;
    }

    /**
     * @param settings the settings to set
     */
    public void setSettings(final Map<MethodArgType, Set<WSClientMethodConfig>> settings) {
        this.settings = settings;
    }

    /**
     * @return the request
     */
    public Map<HttpMethodExt, Set<WSClientMethodConfig>> getRequest() {
        return request;
    }

    /**
     * @param request the request to set
     */
    public void setRequest(final Map<HttpMethodExt, Set<WSClientMethodConfig>> request) {
        this.request = request;
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
    public void setClassNames(final Set<String> classNames) {
        for (final String className : classNames) {
            final String processed = ClassTools.processClassName(className);
            this.classNames.add(processed);
        }
    }

    /**
     * @return the interfaces
     */
    public Set<String> getInterfaces() {
        return interfaces;
    }

    /**
     * @param interfaces the interfaces to set
     */
    public void setInterfaces(final Set<String> interfaces) {
        for (final String interf : interfaces) {
            final String processed = ClassTools.processClassName(interf);
            this.interfaces.add(processed);
        }
    }

}
