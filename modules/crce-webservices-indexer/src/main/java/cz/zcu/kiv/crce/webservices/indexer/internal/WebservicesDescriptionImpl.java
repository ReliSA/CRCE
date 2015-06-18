package cz.zcu.kiv.crce.webservices.indexer.internal;

import cz.zcu.kiv.crce.webservices.indexer.WebservicesDescription;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.plugin.AbstractPlugin;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    public List<Resource> createWebserviceRepresentations(String url_string) {
        
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
        
        // create specialized IDL handling classes for all web service types
        List<WebserviceType> webserviceTypes = new ArrayList<>();
        webserviceTypes.add(new WebserviceTypeJsonWsp(metadataFactory, metadataService));
        webserviceTypes.add(new WebserviceTypeWsdl(metadataFactory, metadataService));

        // recognize IDL type
        WebserviceType recognizedWebserviceType = null;
        for (WebserviceType webserviceType : webserviceTypes) {
            if (webserviceType.recognizeIDL(idl)) {
                logger.debug("IDL type at \"{}\" recognized as {}.", url_string, webserviceType.getSpecificIdlCategory());
                recognizedWebserviceType = webserviceType;
                break; // end the search, we have found out what type of IDL are we dealing with
            }
        }
        if (recognizedWebserviceType == null) {
            logger.error("IDL type at \"{}\" unrecognizable.", url_string);
            return null;
        }

        //////////////////////////////////////
        // parse IDL according to it's type //
        //////////////////////////////////////
        logger.debug("Attempting to parse IDL at \"{}\" (recognized as {} type).", url_string, recognizedWebserviceType.getSpecificIdlCategory());
        List<Resource> resources;
        resources = recognizedWebserviceType.parseIDL(idl); // parse IDL according to it's type
        if (resources == null) {
            logger.error("Could not parse IDL at \"{}\" (recognized as {} type).", url_string, recognizedWebserviceType.getSpecificIdlCategory());
            return null;
        }
        
        // label all resources with main category and other common attributes
        for (Resource resource : resources) {
            metadataService.addCategory(resource, MAIN_CATEGORY); // assign main category tag
            Capability capability = resource.getCapabilities(WebserviceTypeBase.NAMESPACE__WEBSERVICE_IDENTITY).get(0); // get webservice.idetntity capability
            capability.setAttribute(WebserviceTypeBase.ATTRIBUTE__WEBSERVICE_IDENTITY__TIMESTAMP, new Date().getTime()); // save timestamp of when the websevice was parsed
            capability.setAttribute(WebserviceTypeBase.ATTRIBUTE__WEBSERVICE_IDENTITY__IDL_URI, url_string);
        }
        
        ////////////////////////////////////////////////////////////
        // all done; return parsed IDL in form of a CRCE Resource //
        ////////////////////////////////////////////////////////////
        logger.debug("IDL at \"{}\" successfully parsed (recognized as {} type).", url_string, recognizedWebserviceType.getSpecificIdlCategory());
        return resources;
    }
    
}
