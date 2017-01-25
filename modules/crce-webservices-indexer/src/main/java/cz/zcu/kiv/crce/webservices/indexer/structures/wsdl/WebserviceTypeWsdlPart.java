package cz.zcu.kiv.crce.webservices.indexer.structures.wsdl;

/**
 * This is a data class for inner representation of WSDL part element.
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceTypeWsdlPart {
    
    private String name;
    private String type;
    private String element;
    
    /**
     * Constructor.
     *
     * @param name Name of this part.
     * @param type Datatype of a parameter (in case of RPC communication pattern).
     * @param element Reference to a grammar definition (in case of messaging communication pattern).
     */
    public WebserviceTypeWsdlPart(String name, String type, String element) {
        this.name = name;
        this.type = type;
        this.element = element;
    }

    /**
     * @return Name of this part.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Name of this part.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Datatype of a parameter (in case of RPC communication pattern).
     */
    public String getType() {
        return type;
    }

    /**
     * @param type Datatype of a parameter (in case of RPC communication pattern).
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return Reference to a grammar definition (in case of messaging communication pattern).
     */
    public String getElement() {
        return element;
    }

    /**
     * @param element Reference to a grammar definition (in case of messaging communication pattern).
     */
    public void setElement(String element) {
        this.element = element;
    }
}
