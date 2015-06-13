package cz.zcu.kiv.crce.webservices.indexer.internal;

import cz.zcu.kiv.crce.metadata.MetadataFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.plugin.AbstractPlugin;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebservicesDescriptionImpl extends AbstractPlugin implements WebservicesDescription {
    
    private static final Logger logger = LoggerFactory.getLogger(WebservicesDescriptionImpl.class);
    
    // injected by dependency manager
    private volatile MetadataFactory metadataFactory; 
    private volatile MetadataService metadataService;

    private final static String MAIN_CATEGORY = "webservice";
    
    public enum IdlType {
        JSON_WSP, WSDL
    }

    public WebservicesDescriptionImpl() {
    }
    
    @Override
    public Resource parseWebserviceDescription(String url_string) {
        
        //////////////////////////////////////
        // try to access IDL content at URL //
        //////////////////////////////////////
        logger.debug("Attempting to access IDL at \"{}\".", url_string);
        URL url = null;
        try {
            url = new URL(url_string);
        } catch (MalformedURLException ex) {
            logger.error("MalformedURLException: {}", url_string, ex);
        }
        if (url == null) {
            return null;
        }
        
        ///////////////////////////////////
        // read the IDL content from URL //
        ///////////////////////////////////
        Scanner s = null;
        try {
            s = new Scanner(url.openStream());
        } catch (IOException ex) {
            logger.error("IOException: {}", url_string, ex);
        }
        if (s == null) {
            return null;
        }
        String idl = s.useDelimiter("\\Z").next(); // read entire file
        
        ////////////////////////
        // recognize IDL type //
        ////////////////////////
        logger.debug("Attempting to recognize IDL type at \"{}\".", url_string);
        IdlType idlType = null;
        
        // create specialized IDL handling classes for all web service types
        WebserviceTypeJsonWsp wstJsonWsp = new WebserviceTypeJsonWsp(metadataFactory, metadataService);
        
        if (wstJsonWsp.recognizeIDL(idl)) {
            logger.debug("IDL type at \"{}\" recognized as JSON-WSP.", url_string);
            idlType = IdlType.JSON_WSP;
        }
        
        if (idlType == null) {
            logger.error("IDL type at \"{}\" unrecognizable.", url_string);
            return null;
        }

        //////////////////////////////////////
        // parse IDL according to it's type //
        //////////////////////////////////////
        logger.debug("Attempting to parse IDL at \"{}\" (recognized as {} type).", url_string, idlType.toString());
        Resource resource = null;
        switch(idlType) {
            case JSON_WSP:
                resource = wstJsonWsp.parseIDL(idl);
                break;
        }
        if (resource == null) {
            logger.error("Could not parse IDL at \"{}\" (recognized as {} type).", url_string, idlType.toString());
            return null;
        }
        metadataService.addCategory(resource, MAIN_CATEGORY); // label resource with main category
        
        ////////////////////////////////////////////////////////////
        // all done; return parsed IDL in form of a CRCE Resource //
        ////////////////////////////////////////////////////////////
        logger.debug("IDL at \"{}\" successfully parsed (recognized as {} type).", url_string, idlType.toString());
        return resource;
    }
    
    @Override
    public boolean saveResourceIntoRepository() {
        return true;
    }
    
}
