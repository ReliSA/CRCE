package cz.zcu.kiv.crce.webservices.indexer.structures;

import java.util.List;

/**
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class Webservice {
    
    private String name;
    private String url;
    private String idlVersion;
    private String type; // rpc / messaging / rest
    private List<WebserviceEndpoint> endpoints;
    
    public Webservice(String name, String url, String idlVersion, String type, List<WebserviceEndpoint> endpoints) {
        this.name = name;
        this.url = url;
        this.idlVersion = idlVersion;
        this.type = type;
        this.endpoints = endpoints;
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
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the idlVersion
     */
    public String getIdlVersion() {
        return idlVersion;
    }

    /**
     * @param idlVersion the idlVersion to set
     */
    public void setIdlVersion(String idlVersion) {
        this.idlVersion = idlVersion;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the endpoints
     */
    public List<WebserviceEndpoint> getEndpoints() {
        return endpoints;
    }

    /**
     * @param endpoints the endpoints to set
     */
    public void setEndpoints(List<WebserviceEndpoint> endpoints) {
        this.endpoints = endpoints;
    }
    
}
