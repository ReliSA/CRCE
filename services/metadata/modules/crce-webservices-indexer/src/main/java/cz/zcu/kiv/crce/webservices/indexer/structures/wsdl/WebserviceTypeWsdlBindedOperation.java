package cz.zcu.kiv.crce.webservices.indexer.structures.wsdl;

/**
 * This is a data class for inner representation of WSDL binding element.
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceTypeWsdlBindedOperation {
    
    private String name;
    
    // attributes specific for SOAP XML namespace
    private String soapAction;
    private String soapStyle;
    
    /**
     * Constructor.
     *
     * @param name Name of this binded operation.
     * @param soapAction URL from which this functionality can be accessed at.
     * @param soapStyle Either "rpc" for RPC communication pattern or "document" for messaging communication pattern.
     */
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
     * @return Name of this binded operation.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Name of this binded operation.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return URL from which this functionality can be accessed at.
     */
    public String getSoapAction() {
        return soapAction;
    }

    /**
     * @param soapAction URL from which this functionality can be accessed at.
     */
    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    /**
     * @return Either "rpc" for RPC communication pattern or "document" for messaging communication pattern.
     */
    public String getSoapStyle() {
        return soapStyle;
    }

    /**
     * @param soapStyle Either "rpc" for RPC communication pattern or "document" for messaging communication pattern.
     */
    public void setSoapStyle(String soapStyle) {
        this.soapStyle = soapStyle;
    }

    public boolean hasSoapAction() {
        return soapAction != null && !soapAction.isEmpty();
    }
}
