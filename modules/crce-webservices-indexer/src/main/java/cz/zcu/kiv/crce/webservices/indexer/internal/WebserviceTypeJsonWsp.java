package cz.zcu.kiv.crce.webservices.indexer.internal;

import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Resource;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceTypeJsonWsp implements WebserviceType {
    
    private static final Logger logger = LoggerFactory.getLogger(WebservicesDescriptionImpl.class);
    
    private MetadataFactory metadataFactory;
    
    final static String JSON_WSP_TYPE = "type";
    final static String JSON_WSP_TYPE_VALUE = "jsonwsp/description";
    
    final static String JSON_WSP_VERSION = "version";
    final static String JSON_WSP_SERVICENAME = "servicename";
    final static String JSON_WSP_URL = "url";
    final static String JSON_WSP_TYPES = "types";
    final static String JSON_WSP_METHODS = "methods";
    
    public WebserviceTypeJsonWsp(MetadataFactory mf) {
        metadataFactory = mf;
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
        
        Resource resource = metadataFactory.createResource();
        
        return null;
    }
    
}
