package cz.zcu.kiv.crce.webservices.indexer.structures;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceTypeWsdlInterface {
    
    private String name;
    private List<WebserviceTypeWsdlOperation> operations = new ArrayList<>();
    
    public WebserviceTypeWsdlInterface(String name, List<WebserviceTypeWsdlOperation> operations) {
        this.name = name;
        this.operations = operations;
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
     * @return the operations
     */
    public List<WebserviceTypeWsdlOperation> getOperations() {
        return operations;
    }

    /**
     * @param operations the operations to set
     */
    public void setOperations(List<WebserviceTypeWsdlOperation> operations) {
        this.operations = operations;
    }
    
}
