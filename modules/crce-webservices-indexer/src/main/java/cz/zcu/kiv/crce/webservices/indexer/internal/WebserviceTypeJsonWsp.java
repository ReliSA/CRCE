package cz.zcu.kiv.crce.webservices.indexer.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import java.util.logging.Level;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceTypeJsonWsp extends WebserviceTypeBase implements WebserviceType {
    
    private static final Logger logger = LoggerFactory.getLogger(WebservicesDescriptionImpl.class);
    
    private final static String JSON_WSP_TYPE = "type";
    private final static String JSON_WSP_TYPE_VALUE = "jsonwsp/description";
    
    private final static String JSON_WSP_VERSION = "version";
    private final static String JSON_WSP_SERVICENAME = "servicename";
    private final static String JSON_WSP_URL = "url";
    private final static String JSON_WSP_TYPES = "types";
    private final static String JSON_WSP_METHODS = "methods";
    
    public WebserviceTypeJsonWsp(MetadataFactory mf, MetadataService ms) {
        super(mf, ms);
    }
    
    @Override
    public String getSpecificWebserviceCategory() {
        return "json-wsp";
    }
    
    @Override
    public boolean recognizeIDL(String idl) {
        
        // check whether IDL is a valid JSON object
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(idl);
        } catch (JSONException ex) {
            logger.debug("IDL is not a valid JSON object", ex);
        }
        if (jsonObj == null) {
            return false;
        }
        
        // check whether "type" key is defined and has a proper value of "jsonwsp/description"
        try {
            if (!(jsonObj.getString(JSON_WSP_TYPE).compareTo(JSON_WSP_TYPE_VALUE) == 0)) {
                logger.debug("IDL's \"{}\" key does not have proper value of \"{}\".", JSON_WSP_TYPE, JSON_WSP_TYPE_VALUE);
                return false;
            }
        } catch (JSONException ex) {
            logger.debug("IDL does not have \"{}\" key defined in root JSON structure.", JSON_WSP_TYPE, ex);
            return false;
        }
        
        //check for other required root elements of JSON-WSP description object
        if (!jsonObj.has(JSON_WSP_VERSION)) {
            logger.debug("IDL does not have \"{}\" key defined in root JSON structure.", JSON_WSP_VERSION);
            return false;
        }
        if (!jsonObj.has(JSON_WSP_SERVICENAME)) {
            logger.debug("IDL does not have \"{}\" key defined in root JSON structure.", JSON_WSP_SERVICENAME);
            return false;
        }
        if (!jsonObj.has(JSON_WSP_URL)) {
            logger.debug("IDL does not have \"{}\" key defined in root JSON structure.", JSON_WSP_URL);
            return false;
        }
        if (!jsonObj.has(JSON_WSP_TYPES)) {
            logger.debug("IDL does not have \"{}\" key defined in root JSON structure.", JSON_WSP_TYPES);
            return false;
        }
        if (!jsonObj.has(JSON_WSP_METHODS)) {
            logger.debug("IDL does not have \"{}\" key defined in root JSON structure.", JSON_WSP_METHODS);
            return false;
        }

        // check whether there is at least one method defined
        try {
            if (jsonObj.getJSONObject(JSON_WSP_METHODS).length() == 0) {
                logger.debug("IDL's \"{}\" key does not have at least one member (i.e. method).", JSON_WSP_METHODS);
            }
        } catch (JSONException ex) {
            logger.debug("IDL does not have \"{}\" key defined in root JSON structure or \"{}\" key is not a valid JSON object.", JSON_WSP_METHODS, JSON_WSP_METHODS, ex);
        }

        // at this point we can safely declare that we have detected JSON-WSP description object
        return true;
    }

    @Override
    public Resource parseIDL(String idl) {
        
        ////////////////////////////////////////////
        // process idl and get all necessary info //
        ////////////////////////////////////////////
        
        // process idl into jsonObject
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(idl);
        } catch (JSONException ex) {
            logger.error("IDL is not a valid JSON object", ex);
        }
        if (jsonObj == null) {
            return null;
        }
        
        // get webservice name
        String webserviceName = null;
        try {
            webserviceName = jsonObj.getString(JSON_WSP_SERVICENAME);
        } catch (JSONException ex) {
            logger.warn("IDL does not have \"{}\" key defined in root JSON structure.", JSON_WSP_SERVICENAME, ex);
        }
        
        // get webservice url
        String webserviceUrl = null;
        try {
            webserviceUrl = jsonObj.getString(JSON_WSP_URL);
        } catch (JSONException ex) {
            logger.warn("IDL does not have \"{}\" key defined in root JSON structure.", JSON_WSP_URL, ex);
        }
        
        // get webservice url
        String webserviceMime = null;
        try {
            webserviceMime = jsonObj.getString(JSON_WSP_TYPE);
        } catch (JSONException ex) {
            logger.warn("IDL does not have \"{}\" key defined in root JSON structure.", JSON_WSP_TYPE, ex);
        }
        
        // process idl endpoints (i.e. methods)
        
        
        
        ////////////////////////////////////////////////////////////////////////////
        // create CRCE metadata structures and fill it by retrieved info from idl //
        ////////////////////////////////////////////////////////////////////////////

        // create new resource and varible for holding reference to current capability
        Resource resource = metadataFactory.createResource();
        Capability capability;
        
        // Capability - CRCE Identity
        capability = metadataService.getIdentity(resource);
        metadataService.addCategory(resource, getSpecificWebserviceCategory()); // add specific category for this type of web service
        metadataService.setPresentationName(resource, webserviceName);
        metadataService.setSize(resource, idl.length());
        metadataService.setUri(resource, webserviceUrl);
        capability.setAttribute(ATTRIBUTE__MIME, webserviceMime);
        capability.setAttribute(ATTRIBUTE__HASH, getIdlHash(idl));
        
        
        //Capability capability = metadataFactory.createCapability(NAMESPACE__CRCE_IDENTITY);
        //resource.addCapability(capability);
        //resource.addRootCapability(capability);
        
        
        return resource;
    }
    
}
