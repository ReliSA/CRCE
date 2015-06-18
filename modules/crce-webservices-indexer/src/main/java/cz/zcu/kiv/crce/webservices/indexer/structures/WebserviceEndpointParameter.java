package cz.zcu.kiv.crce.webservices.indexer.structures;

/**
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceEndpointParameter {
    
    private String name;
    private String type;
    private Long order;
    private Boolean optional;
    private Boolean array;
    
    public WebserviceEndpointParameter(String name, String type, Long order, Boolean optional, Boolean array) {
        this.name = name;
        this.type = type;
        this.order = order;
        this.optional = optional;
        this.array = array;
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
     * @return the order
     */
    public Long getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(Long order) {
        this.order = order;
    }

    /**
     * @return the optional
     */
    public Boolean isOptional() {
        return optional;
    }

    /**
     * @param optional the optional to set
     */
    public void setOptional(Boolean optional) {
        this.optional = optional;
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
