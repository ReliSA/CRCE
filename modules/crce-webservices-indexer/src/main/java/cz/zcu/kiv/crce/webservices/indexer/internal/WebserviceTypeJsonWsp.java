package cz.zcu.kiv.crce.webservices.indexer.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.webservices.indexer.structures.WebserviceEndpoint;
import cz.zcu.kiv.crce.webservices.indexer.structures.WebserviceEndpointParameter;
import cz.zcu.kiv.crce.webservices.indexer.structures.WebserviceEndpointResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This class can recognize and parse remote IDL documents representing JSON-WSP description objects
 * (see <a href="https://en.wikipedia.org/wiki/JSON-WSP#Description_object">https://en.wikipedia.org/wiki/JSON-WSP#Description_object</a>).
 * 
 * <p>Parsed JSON-WSP description object describing webservice is then represented by {@link cz.zcu.kiv.crce.metadata.Capability} returned in a
 * {@link cz.zcu.kiv.crce.metadata.Resource}.
 *
 * <p>Following aspects of IDL are parsed by this class and stored in CRCE repository as meta-data to the IDL document. Everything that is not explicitly
 * mentioned here is not being processed by this class.
 * 
 * <p>IDL aspects parsed by this class:
 * <ul>
 *     <li>MIME format (json-wsp/description)</li>
 *     <li>IDL version</li>
 *     <li>webservice name</li>
 *     <li>webservice url</li>
 *     <li>webservice endpoints:</li>
 *     <ul>
 *         <li>endpoint name</li>
 *         <li>parameters (for each parameter it's name, type, order, whether it's array and whether it's optional is being processed)</li>
 *         <li>type of returned response</li>
 *     </ul>
 * </ul>
 * 
 * <p>Defined non-primitive types are NOT PROCESSED by this class at the moment.
 * 
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public class WebserviceTypeJsonWsp extends WebserviceTypeBase implements WebserviceType {
    
    private static final Logger logger = LoggerFactory.getLogger(WebserviceDescriptionResourceIndexer.class);
    
    private final static String JSON_WSP_TYPE = "type";
    private final static String JSON_WSP_TYPE_VALUE = "jsonwsp/description";
    
    private final static String JSON_WSP_VERSION = "version";
    private final static String JSON_WSP_SERVICENAME = "servicename";
    private final static String JSON_WSP_URL = "url";
    private final static String JSON_WSP_TYPES = "types";
    private final static String JSON_WSP_METHODS = "methods";
    private final static String JSON_WSP_METHOD_PARAMETERS = "params";
    private final static String JSON_WSP_METHOD_PARAMETER_TYPE = "type";
    private final static String JSON_WSP_METHOD_PARAMETER_ORDER = "def_order";
    private final static String JSON_WSP_METHOD_PARAMETER_OPTIONAL = "optional";
    private final static String JSON_WSP_METHOD_RESPONSE = "ret_info";
    private final static String JSON_WSP_METHOD_RESPONSE_TYPE = "type";
    
    /**
     * Constructor
     *
     * @param mf
     * @param ms
     */
    public WebserviceTypeJsonWsp(MetadataFactory mf, MetadataService ms) {
        super(mf, ms);
    }
    
    @Override
    public String getSpecificIdlCategory() {
        return "json-wsp";
    }
    
    @Override
    public String getSpecificWebserviceType() {
        return "rpc";
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
    public int parseIDL(String idl, Resource resource) {
        
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
            return -1;
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
        
        // get webservice mime format
        String webserviceMime = null;
        try {
            webserviceMime = jsonObj.getString(JSON_WSP_TYPE);
        } catch (JSONException ex) {
            logger.warn("IDL does not have \"{}\" key defined in root JSON structure.", JSON_WSP_TYPE, ex);
        }
        
        // get webservice idl version
        String webserviceIdlVersion = null;
        try {
            webserviceIdlVersion = jsonObj.getString(JSON_WSP_VERSION);
        } catch (JSONException ex) {
            logger.warn("IDL does not have \"{}\" key defined in root JSON structure.", JSON_WSP_VERSION, ex);
        }
        
        // process idl endpoints (i.e. methods)
        List<WebserviceEndpoint> processedEndpoints = new ArrayList<>();
        if (jsonObj.has(JSON_WSP_METHODS)) {
            try {
                JSONObject jsonEndpoints = jsonObj.getJSONObject(JSON_WSP_METHODS);
                Iterator enpoints = jsonEndpoints.keys();
                
                // iterate through all endpoints (i.e. methods)
                while (enpoints.hasNext()) {
                    String endpointName = (String)enpoints.next(); // get endpoint name
                    JSONObject jsonEndpoint = jsonEndpoints.getJSONObject(endpointName);
                    
                    // get endpoint parameters
                    JSONObject jsonParams = jsonEndpoint.getJSONObject(JSON_WSP_METHOD_PARAMETERS);
                    Iterator params = jsonParams.keys();
                    
                    // iterate through all parameters of current endpoint
                    List<WebserviceEndpointParameter> processedParams = new ArrayList<>();
                    while(params.hasNext()) {
                        try {
                            // get info about parameter
                            String paramName = (String)params.next();
                            JSONObject jsonParam = jsonParams.getJSONObject(paramName);
                            String paramType;
                            boolean paramArray;
                            if (jsonParam.optJSONArray(JSON_WSP_METHOD_PARAMETER_TYPE) == null){
                                paramType = jsonParam.getString(JSON_WSP_METHOD_PARAMETER_TYPE);
                                paramArray = false;
                            } else {
                                JSONArray jsonArray = jsonParam.getJSONArray(JSON_WSP_METHOD_PARAMETER_TYPE);
                                paramType = jsonArray.getString(0);
                                paramArray = true;
                            }
                            long paramOrder = jsonParam.getLong(JSON_WSP_METHOD_PARAMETER_ORDER);
                            boolean paramOptional = jsonParam.getBoolean(JSON_WSP_METHOD_PARAMETER_OPTIONAL);

                            // save parameter info into list
                            processedParams.add(new WebserviceEndpointParameter(paramName, paramType, paramOrder, paramOptional, paramArray));
                        } catch (JSONException ex) {
                            logger.warn("Error while processing parameters of endpoint \"{}\".", endpointName, ex);
                        }
                    }
                    
                    // get endpoint response
                    JSONObject jsonEndpointResponse = jsonEndpoint.getJSONObject(JSON_WSP_METHOD_RESPONSE);
                    String responseType;
                    boolean responseArray;
                    if (jsonEndpointResponse.optJSONArray(JSON_WSP_METHOD_RESPONSE_TYPE) == null){
                        responseType = jsonEndpointResponse.getString(JSON_WSP_METHOD_PARAMETER_TYPE);
                        responseArray = false;
                    } else {
                        JSONArray jsonArray = jsonEndpointResponse.getJSONArray(JSON_WSP_METHOD_PARAMETER_TYPE);
                        responseType = jsonArray.getString(0);
                        responseArray = true;
                    }
                    WebserviceEndpointResponse processedResponse = new WebserviceEndpointResponse(responseType, responseArray);
                    
                    // add endpoint info into list
                    processedEndpoints.add(new WebserviceEndpoint(endpointName, null, processedParams, processedResponse));
                    
                }
            } catch (JSONException ex) {
                logger.warn("Error while processing endpoints.", ex);
            }
        }
        else {
            logger.warn("IDL does not have \"{}\" key defined in root JSON structure.", JSON_WSP_METHODS);
        }
        
        
        
        ////////////////////////////////////////////////////////////////////////////
        // create CRCE metadata structures and fill it by retrieved info from idl //
        ////////////////////////////////////////////////////////////////////////////

        // create new variables for holding references to current capabilities
        Capability capability, endpoint_capability;
        
        // Capability - CRCE Identity
        capability = metadataService.getIdentity(resource);
        metadataService.setPresentationName(resource, webserviceName);
        metadataService.setSize(resource, idl.length());
        capability.setAttribute(ATTRIBUTE__CRCE_IDENTITY__MIME, webserviceMime);
        capability.setAttribute(ATTRIBUTE__CRCE_IDENTITY__HASH, getIdlHash(idl));
        
        // Capability - Webserviceschema Identity
        capability = metadataFactory.createCapability(NAMESPACE__WEBSERVICESCHEMA_IDENTITY);
        capability.setAttribute(ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__IDL_VERSION, webserviceIdlVersion);
        resource.addCapability(capability);
        resource.addRootCapability(capability);
        
        // Capability - Webserviceschema Webservice
        capability = metadataFactory.createCapability(NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE);
        capability.setAttribute(ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__NAME, webserviceName);
        capability.setAttribute(ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE, getSpecificWebserviceType());
        capability.setAttribute(ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__URI, webserviceUrl);
        resource.addCapability(capability);
        resource.addRootCapability(capability);
        
        // Capabilities - Webservice Endpoint
        for (int i = 0; i < processedEndpoints.size(); i++) {
            endpoint_capability = metadataFactory.createCapability(NAMESPACE__WEBSERVICE_ENDPOINT);
            endpoint_capability.setAttribute(ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME, processedEndpoints.get(i).getName());
            
            // Properties - Webservice Enpoint Parameter
            List<WebserviceEndpointParameter> processedParams = processedEndpoints.get(i).getParameters();
            for (int j = 0; j < processedParams.size(); j++) {
                Property property = metadataFactory.createProperty(NAMESPACE__WEBSERVICE_ENDPOINT_PARAMETER);
                property.setAttribute(ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__NAME, processedParams.get(j).getName());
                property.setAttribute(ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__TYPE, processedParams.get(j).getType());
                property.setAttribute(ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__ORDER, processedParams.get(j).getOrder());
                property.setAttribute(ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__OPTIONAL, (long)(processedParams.get(j).isOptional() ? 1 : 0));
                property.setAttribute(ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__ARRAY, (long)(processedParams.get(j).isArray() ? 1 : 0));
                endpoint_capability.addProperty(property);
            }
            
            // Property - Webservice Endpoint Response
            Property property = metadataFactory.createProperty(NAMESPACE__WEBSERVICE_ENDPOINT_RESPONSE);
            property.setAttribute(ATTRIBUTE__WEBSERVICE_ENDPOINT_RESPONSE__TYPE, processedEndpoints.get(i).getResponse().getType());
            property.setAttribute(ATTRIBUTE__WEBSERVICE_ENDPOINT_RESPONSE__ARRAY, (long)(processedEndpoints.get(i).getResponse().isArray() ? 1 : 0));
            endpoint_capability.addProperty(property);

            resource.addCapability(endpoint_capability);
            capability.addChild(endpoint_capability);
            endpoint_capability.setParent(capability);
        }
        
        // return "1" as a number of parsed WSs, since JSON-WSP description document can describe exactly one webservice at once
        return 1;
    }

    @Override
    public String generateIDL(Resource resource) {
        final String INDENT = "    ";
        StringBuilder sb = new StringBuilder();
        
        // get all neccesary info from resource and it's capabilities
        Capability capability = metadataService.getIdentity(resource);
        String mime = capability.getAttributeStringValue(ATTRIBUTE__CRCE_IDENTITY__MIME);
        capability = resource.getRootCapabilities(NAMESPACE__WEBSERVICESCHEMA_IDENTITY).get(0);
        String idlVersion = capability.getAttributeStringValue(ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__IDL_VERSION);
        capability = resource.getRootCapabilities(NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE).get(0);
        String webserviceName = capability.getAttributeStringValue(ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__NAME);
        String webserviceUrl = capability.getAttributeStringValue(ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__URI);
        List<Capability> endpoints = capability.getChildren();
        
        // construct IDL from retrieved info according to the JSON-WSP description object specification
        sb.append("{\n");
        sb.append(INDENT).append("\"type\": \"").append(mime).append("\",\n");
        sb.append(INDENT).append("\"version\": \"").append(idlVersion).append("\",\n");
        sb.append(INDENT).append("\"servicename\": \"").append(webserviceName).append("\",\n");
        sb.append(INDENT).append("\"url\": \"").append(webserviceUrl).append("\",\n");
        sb.append(INDENT).append("\"types\": {},\n"); // complex types are not currently being stored in CRCE
        sb.append(INDENT).append("\"methods\": {\n");
        for (int i = 0; i < endpoints.size(); i++) { // endpoints
            sb.append(INDENT).append(INDENT).append("\"").append(endpoints.get(i).getAttributeValue(ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME)).append("\": {\n");
            List<Property> params = endpoints.get(i).getProperties(NAMESPACE__WEBSERVICE_ENDPOINT_PARAMETER);
            sb.append(INDENT).append(INDENT).append(INDENT).append("\"params\": {\n");
            for (int j = 0; j < params.size(); j++) { // params
                Property param = params.get(j);
                String paramType = param.getAttributeStringValue(ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__TYPE);
                if (param.getAttributeValue(ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__ARRAY) == 1) {
                    paramType = "[" + paramType + "]";
                }
                sb.append(INDENT).append(INDENT).append(INDENT).append(INDENT).append("\"").append(param.getAttributeValue(ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__NAME)).append("\": {\n");
                sb.append(INDENT).append(INDENT).append(INDENT).append(INDENT).append(INDENT).append("\"def_order\": ").append(param.getAttributeValue(ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__ORDER)).append(",\n");
                sb.append(INDENT).append(INDENT).append(INDENT).append(INDENT).append(INDENT).append("\"type\": \"").append(paramType).append("\",\n");
                if (param.getAttributeValue(ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__OPTIONAL) == 1) {
                    sb.append(INDENT).append(INDENT).append(INDENT).append(INDENT).append(INDENT).append("\"optional\": true\n");
                } else {
                    sb.append(INDENT).append(INDENT).append(INDENT).append(INDENT).append(INDENT).append("\"optional\": false\n");
                }
                if (params.size() != j + 1) {
                    sb.append(INDENT).append(INDENT).append(INDENT).append(INDENT).append("},\n");
                } else {
                    sb.append(INDENT).append(INDENT).append(INDENT).append(INDENT).append("}\n");
                }
            }
            Property response = endpoints.get(i).getProperties(NAMESPACE__WEBSERVICE_ENDPOINT_RESPONSE).get(0);
            String responseType = response.getAttributeValue(ATTRIBUTE__WEBSERVICE_ENDPOINT_RESPONSE__TYPE);
            if (response.getAttributeValue(ATTRIBUTE__WEBSERVICE_ENDPOINT_RESPONSE__ARRAY) == 1) {
                responseType = "[" + responseType + "]";
            }
            sb.append(INDENT).append(INDENT).append(INDENT).append("},\n");
            sb.append(INDENT).append(INDENT).append(INDENT).append("\"ret_info\": {\n");
            sb.append(INDENT).append(INDENT).append(INDENT).append(INDENT).append("\"type\": \"").append(responseType).append("\"\n");
            sb.append(INDENT).append(INDENT).append(INDENT).append("}\n");
            if (endpoints.size() != i + 1) {
                sb.append(INDENT).append(INDENT).append("},\n");
            } else {
                sb.append(INDENT).append(INDENT).append("}\n");
            }
        }
        sb.append(INDENT).append("}\n");
        sb.append("}\n");
        
        // return generated IDL
        return sb.toString();
    }
    
}
