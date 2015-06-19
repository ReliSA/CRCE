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
     * @param idl Textual representation of webservice IDL.
     * @return Returns <code>true</code> if given IDL was recognized as the type of implemented web service standard. Returns <code>false</code> otherwise.
     */
    boolean recognizeIDL(String idl);
    
    /**
     * Processes given IDL into list of CRCE {@link cz.zcu.kiv.crce.metadata.Resource} representations of a webservices capabilities represented by the given
     * IDL.
     *
     * @param idl Textual representation of webservice IDL.
     * @return Returns List of CRCE {@link cz.zcu.kiv.crce.metadata.Resource} representations of parsed web service. In case of any error null is returned
     * instead.
     */
    List<Resource> parseIDL(String idl);
    
    /**
     * Returns name of specific IDL of which class that implements this interface deals with. E.g. "json-wsp", "soap", "wadl", etc...
     *
     * @return Name of specific IDL of which class that implements this interface deals with.
     */
    String getSpecificIdlCategory();
    
}
