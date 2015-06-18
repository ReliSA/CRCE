package cz.zcu.kiv.crce.webservices.indexer.structures.wsdl;

import java.util.List;

/**
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceTypeWsdlBinding {
    
    private String name;
    private String interface_;
    private List<WebserviceTypeWsdlBindedOperation> bindedOperations;
    
    // attributes specific for SOAP XML namespace
    private String soapStyle; // rpc / document (i.e. messaging)
    private String soapTransport;
    
    public WebserviceTypeWsdlBinding(String name, String interface_, List<WebserviceTypeWsdlBindedOperation> bindedOperations, String soapStyle, String soapTransport) {
        this.name = name;
        this.interface_ = interface_;
        this.bindedOperations = bindedOperations;
        this.soapStyle = soapStyle;
        this.soapTransport = soapTransport;
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
     * @return the interface_
     */
    public String getInterface_() {
        return interface_;
    }

    /**
     * @param interface_ the interface_ to set
     */
    public void setInterface_(String interface_) {
        this.interface_ = interface_;
    }

    /**
     * @return the bindedOperations
     */
    public List<WebserviceTypeWsdlBindedOperation> getBindedOperations() {
        return bindedOperations;
    }

    /**
     * @param operations the bindedOperations to set
     */
    public void setBindedOperationNames(List<WebserviceTypeWsdlBindedOperation> bindedOperations) {
        this.bindedOperations = bindedOperations;
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

    /**
     * @return the soapTransport
     */
    public String getSoapTransport() {
        return soapTransport;
    }

    /**
     * @param soapTransport the soapTransport to set
     */
    public void setSoapTransport(String soapTransport) {
        this.soapTransport = soapTransport;
    }
    
}
