package cz.zcu.kiv.crce.webservices.indexer.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Resource;

/**
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebservicesDescriptionImpl implements WebserviceDescription {
    
    private static final Logger logger = LoggerFactory.getLogger(WebservicesDescriptionImpl.class);

    public WebservicesDescriptionImpl() {
    }
    
    @Override
    public Resource parseWebserviceDescription(String url) {
        
        logger.debug("Webservice Indexer: Attempting to access IDL at \"" + url + "\".");
        
        return null;
    }
    
    @Override
    public boolean saveResourceIntoRepository() {
        return true;
    }
    
}
