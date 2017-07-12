package cz.zcu.kiv.crce.webservices.indexer.structures;

/**
 * This is a data class for inner representation of functionality of a parameter passed to the webservice endpoint.
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceEndpointParameter {
    
    private String name;
    private String type;
    private Long order;
    private Boolean optional;
    private Boolean array;
    
    /**
     * Constructor.
     *
     * @param name Name of parameter.
     * @param type Datatype of parameter.
     * @param order Position of parameter (i.e. at which place it is put when passed to the endpoint).
     * @param optional Defines whether this parameter is optional.
     * @param array Defines whether this parameter is represented as a single instance of <code>type</code> or a collection of it.
     */
    public WebserviceEndpointParameter(String name, String type, Long order, Boolean optional, Boolean array) {
        this.name = name;
        this.type = type;
        this.order = order;
        this.optional = optional;
        this.array = array;
    }

    /**
     * @return Name of parameter.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Name of parameter.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Datatype of parameter.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type Datatype of parameter.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return Position of parameter (i.e. at which place it is put when passed to the endpoint).
     */
    public Long getOrder() {
        return order;
    }

    /**
     * @param order Position of parameter (i.e. at which place it is put when passed to the endpoint).
     */
    public void setOrder(Long order) {
        this.order = order;
    }

    /**
     * @return Defines whether this parameter is optional.
     */
    public Boolean isOptional() {
        return optional;
    }

    /**
     * @param optional Defines whether this parameter is optional.
     */
    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    /**
     * @return Defines whether this parameter is represented as a single instance of <code>type</code> or a collection of it.
     */
    public Boolean isArray() {
        return array;
    }

    /**
     * @param array Defines whether this parameter is represented as a single instance of <code>type</code> or a collection of it.
     */
    public void setArray(Boolean array) {
        this.array = array;
    }
    
}
