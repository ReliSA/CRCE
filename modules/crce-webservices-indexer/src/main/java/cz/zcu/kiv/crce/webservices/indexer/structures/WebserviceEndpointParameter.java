package cz.zcu.kiv.crce.webservices.indexer.structures;

/**
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceEndpointParameter {
    
    private String name;
    private String type;
    private long order;
    private boolean optional;
    private boolean array;
    
    public WebserviceEndpointParameter(String name, String type, long order, boolean optional, boolean array) {
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
    public long getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(long order) {
        this.order = order;
    }

    /**
     * @return the optional
     */
    public boolean isOptional() {
        return optional;
    }

    /**
     * @param optional the optional to set
     */
    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    /**
     * @return the array
     */
    public boolean isArray() {
        return array;
    }

    /**
     * @param array the array to set
     */
    public void setArray(boolean array) {
        this.array = array;
    }
    
}
