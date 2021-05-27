package cz.zcu.kiv.crce.rest.client.indexer.config_v2;

import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Config {
    @JsonProperty("package")
    private String name;

    @JsonProperty("wsClient")
    private Set<WSClientConfig> wsClients;

    @JsonProperty("wsClientData")
    private Set<WSClientConfig> wsClientDataHolders;

    @JsonProperty("argDefinitions")
    private Map<String, ArgConfig> argDefinitions;

    @JsonProperty("enums")
    private Set<EnumConfig> enums;

    /**
     * @return the enums
     */
    public Set<EnumConfig> getEnums() {
        return enums;
    }

    /**
     * @param enums the enums to set
     */
    public void setEnums(Set<EnumConfig> enums) {
        this.enums = enums;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the wsClients
     */
    public Set<WSClientConfig> getWsClients() {
        return wsClients;
    }

    /**
     * @param wsClients the wsClients to set
     */
    public void setWsClients(Set<WSClientConfig> wsClients) {
        this.wsClients = wsClients;
    }

    /**
     * @return the wsClientDataHolders
     */
    public Set<WSClientConfig> getWsClientDataHolders() {
        return wsClientDataHolders;
    }

    /**
     * @param wsClientDataHolders the wsClientDataHolders to set
     */
    public void setWsClientDataHolders(Set<WSClientConfig> wsClientDataHolders) {
        this.wsClientDataHolders = wsClientDataHolders;
    }

    /**
     * @return the argDefinitions
     */
    public Map<String, ArgConfig> getArgDefinitions() {
        return argDefinitions;
    }

    /**
     * @param argDefinitions the argDefinitions to set
     */
    public void setArgDefinitions(Map<String, ArgConfig> argDefinitions) {
        this.argDefinitions = argDefinitions;
    }

}
