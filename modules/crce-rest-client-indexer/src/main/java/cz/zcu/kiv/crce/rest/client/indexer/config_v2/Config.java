package cz.zcu.kiv.crce.rest.client.indexer.config_v2;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Config {
    @JsonProperty("package")
    private String name;

    @JsonProperty("wsClient")
    private LinkedHashSet<WSClientConfig> wsClients;

    @JsonProperty("wsClientData")
    private LinkedHashSet<WSClientDataConfig> wsClientDataHolders;

    @JsonProperty("argDefinitions")
    private Map<String, ArgConfig> argDefinitions;

    @JsonProperty("requestParams")
    private Set<RequestParamConfig> requestParams;


    /**
     * @return the wsClients
     */
    public Set<WSClientConfig> getWsClients() {
        return wsClients;
    }

    /**
     * @param wsClients the wsClients to set
     */
    public void setWsClients(LinkedHashSet<WSClientConfig> wsClients) {
        this.wsClients = wsClients;
    }

    /**
     * @return the requestParams
     */
    public Set<RequestParamConfig> getRequestParams() {
        return requestParams;
    }

    /**
     * @param requestParams the requestParams to set
     */
    public void setRequestParams(Set<RequestParamConfig> requestParams) {
        this.requestParams = requestParams;
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
     * @return the wsClientDataHolders
     */
    public Set<WSClientDataConfig> getWsClientDataHolders() {
        return wsClientDataHolders;
    }

    /**
     * @param wsClientDataHolders the wsClientDataHolders to set
     */
    public void setWsClientDataHolders(LinkedHashSet<WSClientDataConfig> wsClientDataHolders) {
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
