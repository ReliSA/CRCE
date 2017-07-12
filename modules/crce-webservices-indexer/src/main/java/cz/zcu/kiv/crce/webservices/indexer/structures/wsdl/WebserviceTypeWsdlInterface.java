package cz.zcu.kiv.crce.webservices.indexer.structures.wsdl;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a data class for inner representation of WSDL portType (WSDL 1.1) / interface (WSDL 2.0) element.
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceTypeWsdlInterface {
    
    private String name;
    private List<WebserviceTypeWsdlOperation> operations = new ArrayList<>();
    
    /**
     * Constructor.
     *
     * @param name Name of this portType (WSDL 1.1) / interface (WSDL 2.0).
     * @param operations List of defined abstract operations.
     */
    public WebserviceTypeWsdlInterface(String name, List<WebserviceTypeWsdlOperation> operations) {
        this.name = name;
        this.operations = operations;
    }

    /**
     * @return Name of this portType (WSDL 1.1) / interface (WSDL 2.0).
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Name of this portType (WSDL 1.1) / interface (WSDL 2.0).
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return List of defined abstract operations.
     */
    public List<WebserviceTypeWsdlOperation> getOperations() {
        return operations;
    }

    /**
     * @param operations List of defined abstract operations.
     */
    public void setOperations(List<WebserviceTypeWsdlOperation> operations) {
        this.operations = operations;
    }
    
}
