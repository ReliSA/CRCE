package cz.zcu.kiv.crce.webservices.indexer.structures.wsdl;

import java.util.List;

/**
 * This is a data class for inner representation of WSDL message element (defined only in WSDL 1.1).
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceTypeWsdlMessage {
    
    private String name;
    private List<WebserviceTypeWsdlPart> parts;
    
    /**
     * Constructor.
     *
     * @param name Name of this message.
     * @param parts List of parts from which this message consists of.
     */
    public WebserviceTypeWsdlMessage(String name, List<WebserviceTypeWsdlPart> parts) {
        this.name = name;
        this. parts = parts;
    }

    /**
     * @return Name of this message.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Name of this message.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return List of parts from which this message consists of.
     */
    public List<WebserviceTypeWsdlPart> getParts() {
        return parts;
    }

    /**
     * @param parts List of parts from which this message consists of.
     */
    public void setParts(List<WebserviceTypeWsdlPart> parts) {
        this.parts = parts;
    }
    
}
