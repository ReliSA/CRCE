package cz.zcu.kiv.crce.webservices.indexer.structures;

/**
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceEndpointResponse {
    
    private String type;
    private Boolean array;
    
    public WebserviceEndpointResponse(String type, Boolean array) {
        this.type = type;
        this.array = array;
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
     * @return the array
     */
    public Boolean isArray() {
        return array;
    }

    /**
     * @param array the array to set
     */
    public void setArray(Boolean array) {
        this.array = array;
    }
    
}
