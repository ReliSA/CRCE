package cz.zcu.kiv.crce.webservices.indexer.structures;

/**
 * This is a data class for inner representation of functionality of a response returned from the webservice endpoint.
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceEndpointResponse {
    
    private String type;
    private Boolean array;
    
    /**
     * Constructor.
     *
     * @param type Datatype of response.
     * @param array Defines whether this response is represented as a single instance of <code>type</code> or a collection of it.
     */
    public WebserviceEndpointResponse(String type, Boolean array) {
        this.type = type;
        this.array = array;
    }

    /**
     * @return Datatype of response.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type Datatype of response.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return Defines whether this response is represented as a single instance of <code>type</code> or a collection of it.
     */
    public Boolean isArray() {
        return array;
    }

    /**
     * @param array Defines whether this response is represented as a single instance of <code>type</code> or a collection of it.
     */
    public void setArray(Boolean array) {
        this.array = array;
    }
    
}
