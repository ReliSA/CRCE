package cz.zcu.kiv.crce.webservices.indexer.structures.wsdl;

import java.util.List;

/**
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceTypeWsdlMessage {
    
    private String name;
    private List<WebserviceTypeWsdlPart> parts;
    
    public WebserviceTypeWsdlMessage(String name, List<WebserviceTypeWsdlPart> parts) {
        this.name = name;
        this. parts = parts;
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
     * @return the parts
     */
    public List<WebserviceTypeWsdlPart> getParts() {
        return parts;
    }

    /**
     * @param parts the parts to set
     */
    public void setParts(List<WebserviceTypeWsdlPart> parts) {
        this.parts = parts;
    }
    
}
