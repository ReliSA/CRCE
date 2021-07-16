package cz.zcu.kiv.crce.rest.client.indexer.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zcu.kiv.crce.rest.client.indexer.config.tools.ClassTools;
import cz.zcu.kiv.crce.rest.client.indexer.shared.HeaderType;

public class WSClientDataConfig {
    @JsonProperty("classes")
    private Set<String> classes = new HashSet<>();

    @JsonProperty("interfaces")
    private Set<String> interfaces = new HashSet<>();

    @JsonProperty("inherits")
    private Set<String> inherits = new HashSet<>();

    @JsonProperty("settings")
    private Map<MethodArgType, Set<WSClientMethodConfig>> settings = new HashMap<>();

    @JsonProperty("core")
    private Set<String> core = new HashSet<>();


    /**
     * @param core the core to set
     */
    public void setCore(Set<String> core) {
        for (final String className : core) {
            final String processed = ClassTools.processClassName(className);
            this.core.add(processed);
        }
    }

    /**
     * @return the core
     */
    public Set<String> getCore() {
        return core;
    }

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
     * @return the classes
     */
    public Set<String> getClasses() {
        return classes;
    }



    /**
     * @param settings the settings to set
     */
    public void setSettings(Map<MethodArgType, Set<WSClientMethodConfig>> settings) {
        this.settings = settings;
    }



    /**
     * @param classes the classes to set
     */
    public void setClasses(Set<String> classes) {
        for (String className : classes) {
            final String processed = ClassTools.processClassName(className);
            this.classes.add(processed);
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
    public void setInterfaces(Set<String> interfaces) {
        for (String className : interfaces) {
            final String processed = ClassTools.processClassName(className);
            this.interfaces.add(processed);
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
