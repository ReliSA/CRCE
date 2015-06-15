package cz.zcu.kiv.crce.webservices.indexer.structures;

/**
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceEndpointResponse {
    
    private String type;
    
    public WebserviceEndpointResponse(String type) {
        this.type = type;
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
    
}
