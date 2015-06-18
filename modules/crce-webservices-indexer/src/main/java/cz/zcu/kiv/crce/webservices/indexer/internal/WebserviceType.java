package cz.zcu.kiv.crce.webservices.indexer.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import java.util.List;

/**
 * This interface serves as a basic API for processing of IDLs of different types of web services. Any class implementing this interface should represent one
 * particular type of web service standard (e.g. SOAP, JSON-WSP, etc...).
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public interface WebserviceType {
    
    /**
     * Determines whether given IDL satisfies all conditions in order to be valid representation of a web service. For example if SOAP was implemented as web
     * service type of this interface. This method would check whether given IDL is a valid WSDL document.
     *
     * @param idl Textual representation of web service IDL.
     * @return Returns true if given IDL was recognized as the type of implemented web service standard. Returns false otherwise.
     */
    boolean recognizeIDL(String idl);
    
    /**
     * Processes given IDL into CRCE {@link cz.zcu.kiv.crce.metadata.Resource} representation of a web service capabilities represented by the given IDL.
     *
     * @param idl Textual representation of web service IDL.
     * @return Returns CRCE {@link cz.zcu.kiv.crce.metadata.Resource} representation of parsed web service. In case of any error null is returned instead.
     */
    List<Resource> parseIDL(String idl);
    
}
