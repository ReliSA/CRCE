package cz.zcu.kiv.crce.webservices.indexer.structures.wsdl;

/**
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceTypeWsdlBindedOperation {
    
    private String name;
    
    // attributes specific for SOAP XML namespace
    private String soapAction;
    private String soapStyle;
    
    public WebserviceTypeWsdlBindedOperation(String name, String soapAction, String soapStyle) {
        this.name = name;
        this.soapAction = soapAction;
        this.soapStyle = soapStyle;
    }

    @Override
    public String toString() {
        return String.format("name: %s, soapAction: %s, soapStyle: %s", name, soapAction, soapStyle);
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
     * @return the soapAction
     */
    public String getSoapAction() {
        return soapAction;
    }

    /**
     * @param soapAction the soapAction to set
     */
    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    /**
     * @return the soapStyle
     */
    public String getSoapStyle() {
        return soapStyle;
    }

    /**
     * @param soapStyle the soapStyle to set
     */
    public void setSoapStyle(String soapStyle) {
        this.soapStyle = soapStyle;
    }
}
