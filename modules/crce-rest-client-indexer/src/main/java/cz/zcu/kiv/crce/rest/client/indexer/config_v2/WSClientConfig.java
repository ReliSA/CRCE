package cz.zcu.kiv.crce.rest.client.indexer.config_v2;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.structures.SettingsType;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.tools.ClassTools;
import cz.zcu.kiv.crce.rest.client.indexer.shared.HttpMethodExt;

public class WSClientConfig {

    @JsonProperty("classes")
    private Set<String> classNames;

    @JsonProperty("interfaces")
    private Set<String> interfaces;

    @JsonProperty("settings")
    private Map<SettingsType, Set<WSClientMethodConfig>> settings;

    @JsonProperty("request")
    private Map<HttpMethodExt, Set<WSClientMethodConfig>> request;

    

    /**
     * @return the settings
     */
    public Map<SettingsType, Set<WSClientMethodConfig>> getSettings() {
        return settings;
    }

    /**
     * @param settings the settings to set
     */
    public void setSettings(final Map<SettingsType, Set<WSClientMethodConfig>> settings) {
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
        this.classNames = new HashSet<>();
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
        this.interfaces = new HashSet<>();
        for (final String interf : interfaces) {
            final String processed = ClassTools.processClassName(interf);
            this.interfaces.add(processed);
        }
    }

}
