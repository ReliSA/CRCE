package cz.zcu.kiv.crce.webservices.indexer.structures;

import java.util.List;

/**
 * This is a data class for inner representation of webservice functionality.
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class Webservice {
    
    private String name;
    private String url;
    private String idlVersion;
    private String type; // rpc / messaging / rest
    private List<WebserviceEndpoint> endpoints;
    
    /**
     * Constructor.
     *
     * @param name Name of webservice.
     * @param url URL from which webservice can be accessed.
     * @param idlVersion Version of IDL which describes this webservice.
     * @param type Type of webservice communication pattern. E.g. "rpc", "messaging", "rest", etc...
     * @param endpoints List of endpoints available at this webservice.
     */
    public Webservice(String name, String url, String idlVersion, String type, List<WebserviceEndpoint> endpoints) {
        this.name = name;
        this.url = url;
        this.idlVersion = idlVersion;
        this.type = type;
        this.endpoints = endpoints;
    }

    /**
     * @return Name of webservice.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Name of webservice.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return URL from which webservice can be accessed.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url URL from which webservice can be accessed.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return Version of IDL which describes this webservice.
     */
    public String getIdlVersion() {
        return idlVersion;
    }

    /**
     * @param idlVersion Version of IDL which describes this webservice.
     */
    public void setIdlVersion(String idlVersion) {
        this.idlVersion = idlVersion;
    }

    /**
     * @return Type of webservice communication pattern. E.g. "rpc", "messaging", "rest", etc...
     */
    public String getType() {
        return type;
    }

    /**
     * @param type Type of webservice communication pattern. E.g. "rpc", "messaging", "rest", etc...
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return List of endpoints available at this webservice.
     */
    public List<WebserviceEndpoint> getEndpoints() {
        return endpoints;
    }

    /**
     * @param endpoints List of endpoints available at this webservice.
     */
    public void setEndpoints(List<WebserviceEndpoint> endpoints) {
        this.endpoints = endpoints;
    }
    
}
