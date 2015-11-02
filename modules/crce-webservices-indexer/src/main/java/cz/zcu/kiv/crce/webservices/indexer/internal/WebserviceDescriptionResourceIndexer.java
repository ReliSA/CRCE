package cz.zcu.kiv.crce.webservices.indexer.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.indexer.AbstractResourceIndexer;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is able to parse content of IDL document (originally stored at remote location) and store information about webservice descriptions found in IDL
 * right into metadata of CRCE {@link cz.zcu.kiv.crce.metadata.Resource}. It is a part of standard indexers chain.
 * 
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceDescriptionResourceIndexer extends AbstractResourceIndexer{

    private static final Logger logger = LoggerFactory.getLogger(WebserviceDescriptionResourceIndexer.class);
    
    // injected by dependency manager
    private volatile MetadataFactory metadataFactory; 
    private volatile MetadataService metadataService;

    public final static String MAIN_CATEGORY = "webserviceschema";
    
    /**
     * Constructor
     *
     */
    public WebserviceDescriptionResourceIndexer() {
    }
    
    @Override
    public List<String> index(InputStream input, Resource resource) {
        
        //////////////////////////////////////
        // read the IDL content to a string //
        //////////////////////////////////////
        Scanner s = new Scanner(input);
        String idl = s.useDelimiter("\\Z").next(); // read entire file

         ////////////////////////
        // recognize IDL type //
        ////////////////////////
        logger.debug("Attempting to recognize IDL type.");
        
        // create specialized IDL handling classes for all web service types
        List<WebserviceType> webserviceTypes = new ArrayList<>();
        webserviceTypes.add(new WebserviceTypeJsonWsp(metadataFactory, metadataService));
        webserviceTypes.add(new WebserviceTypeWsdl(metadataFactory, metadataService));
        webserviceTypes.add(new WebserviceTypeWadl(metadataFactory, metadataService));

        // recognize IDL type
        WebserviceType recognizedWebserviceType = null;
        for (WebserviceType webserviceType : webserviceTypes) {
            if (webserviceType.recognizeIDL(idl)) {
                logger.debug("IDL type recognized as {}.", webserviceType.getSpecificIdlCategory());
                recognizedWebserviceType = webserviceType;
                break; // end the search, we have found out what type of IDL are we dealing with
            }
        }
        if (recognizedWebserviceType == null) {
            logger.error("IDL type unrecognizable.");
            return Collections.emptyList();
        }
       
        //////////////////////////////////////
        // parse IDL according to it's type //
        //////////////////////////////////////
        logger.debug("Attempting to parse IDL (recognized as {} type).", recognizedWebserviceType.getSpecificIdlCategory());
        int parsed_ws = recognizedWebserviceType.parseIDL(idl, resource); // parse IDL according to it's type
        logger.debug("IDL parsed ({} webservice descriptions found).", parsed_ws);
        
        // uncomment this in order to test generation of IDL right after parsing. To see if inverseT(T(x)) = x (i.e. generation of parsed IDL results into IDL)
        // is the easiest way to test if you wrote your IDL parsing class correctly. Just make sure that both methods are implemented.
        //logger.debug(" * * * * * * * * * GENERATED IDL * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
        //logger.debug(recognizedWebserviceType.generateIDL(resource));
        //logger.debug(" * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
        
        // label the resource with main category and other common attributes
        metadataService.addCategory(resource, MAIN_CATEGORY); // assign main category tag
        Capability capability = resource.getCapabilities(WebserviceTypeBase.NAMESPACE__WEBSERVICESCHEMA_IDENTITY).get(0); // get webserviceschema.identity capability
        capability.setAttribute(WebserviceTypeBase.ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__TIMESTAMP, new Date().getTime()); // save timestamp of when the websevice was parsed
        
        ////////////////////////////////////////////////////////////
        // all done; return parsed IDL in form of a CRCE Resource //
        ////////////////////////////////////////////////////////////
        logger.debug("IDL successfully parsed (recognized as {} type).", recognizedWebserviceType.getSpecificIdlCategory());
        
        return Collections.singletonList(MAIN_CATEGORY);
    }
    
}
