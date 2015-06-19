package cz.zcu.kiv.crce.webservices.indexer;

import cz.zcu.kiv.crce.metadata.Resource;
import java.util.List;

/**
 * This interface serves as API to the CRCE - Webservices Indexer bundle.
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public interface WebservicesDescription {

    /**
     * This function should parse content of IDL document at remote location and return list of CRCE resources. Each CRCE resource should represent one
     * particular webservice described in original IDL document.
     *
     * @param url Location of IDL document.
     * @return List of CRCE resources representing webservices defined in IDL document.
     */
    List<Resource> createWebserviceRepresentations(String url);

}
