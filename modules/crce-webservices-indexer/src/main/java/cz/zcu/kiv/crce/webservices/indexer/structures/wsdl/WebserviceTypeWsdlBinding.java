package cz.zcu.kiv.crce.webservices.indexer.structures.wsdl;

import java.util.List;

/**
 * This is a data class for inner representation of WSDL binding element.
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
    
    /**
     *
     * @param name Name of this binding.
     * @param interface_ Reference to a portType (WSDL 1.1) / interface (WSDL 2.0) definition.
     * @param bindedOperations List of all operation defined in this binding (i.e. being implemented by a webservice).
     * @param soapStyle Either "rpc" for RPC communication pattern or "document" for messaging communication pattern.
     * @param soapTransport Definition of used transport protocol.
     */
    public WebserviceTypeWsdlBinding(String name, String interface_, List<WebserviceTypeWsdlBindedOperation> bindedOperations, String soapStyle, String soapTransport) {
        this.name = name;
        this.interface_ = interface_;
        this.bindedOperations = bindedOperations;
        this.soapStyle = soapStyle;
        this.soapTransport = soapTransport;
    }

    /**
     * @return Name of this binding.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Name of this binding.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Reference to a portType (WSDL 1.1) / interface (WSDL 2.0) definition.
     */
    public String getInterface_() {
        return interface_;
    }

    /**
     * @param interface_ Reference to a portType (WSDL 1.1) / interface (WSDL 2.0) definition.
     */
    public void setInterface_(String interface_) {
        this.interface_ = interface_;
    }

    /**
     * @return List of all operation defined in this binding (i.e. being implemented by a webservice).
     */
    public List<WebserviceTypeWsdlBindedOperation> getBindedOperations() {
        return bindedOperations;
    }

    /**
     * @param bindedOperations List of all operation defined in this binding (i.e. being implemented by a webservice).
     */
    public void setBindedOperationNames(List<WebserviceTypeWsdlBindedOperation> bindedOperations) {
        this.bindedOperations = bindedOperations;
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

    /**
     * @return Definition of used transport protocol.
     */
    public String getSoapTransport() {
        return soapTransport;
    }

    /**
     * @param soapTransport Definition of used transport protocol.
     */
    public void setSoapTransport(String soapTransport) {
        this.soapTransport = soapTransport;
    }
    
}
