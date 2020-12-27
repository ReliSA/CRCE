package cz.zcu.kiv.crce.apicomp.impl.mov.common;

import cz.zcu.kiv.crce.apicomp.impl.webservice.common.WebserviceIndexerConstants;
import cz.zcu.kiv.crce.metadata.Capability;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class used in MOV detection algorithm. The Capability tree is transformed
 * into simpler hostname -> path -> operation tree.
 */
public class ApiDescription extends HashMap<String, Map<String, List<String>>> {

    /**
     * Converts API described by WSDL to ApiDescription object.
     *
     * @param wsdlApiRoot Root capability representing API described by WSDL.
     * @return Api description object.
     * @throws MalformedURLException
     */
    public static ApiDescription fromWsdl(Capability wsdlApiRoot) throws MalformedURLException {
        ApiDescription description = new ApiDescription();

        // wsdl has following structure: identity -> webservice -> endpoint
        for (Capability webService : wsdlApiRoot.getChildren()) {
            for (Capability endpoint : webService.getChildren()) {
                processEndpoint(endpoint, description);
            }
        }

        return description;
    }

    /**
     * Converts API described by WADL to ApiDescription object.
     * @param wadlApiRoot Root capability representing API described by WADL.
     * @return Api description object.
     * @throws MalformedURLException
     */
    public static ApiDescription fromWadl(Capability wadlApiRoot) throws MalformedURLException {
        ApiDescription description = new ApiDescription();

        // wadl has following structure: root -> endpoint
        for(Capability endpoint : wadlApiRoot.getChildren()) {
            processEndpoint(endpoint, description);
        }
        return description;
    }

    /**
     * Converts API described by Json-WSP to ApiDescription object.
     * @param jsonWspApiRoot Root capability representing API described by Json-WSP.
     * @return Api description object.
     */
    public static ApiDescription fromJsonWsp(Capability jsonWspApiRoot) throws MalformedURLException {
        ApiDescription description = new ApiDescription();

        // Json-WSP has url in its root
        String urlStr = jsonWspApiRoot.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__URI);
        URL url = new URL(urlStr);
        String hostPart = url.getHost();
        String pathPart = url.getPath();

        // structure: root -> endpoint
        for(Capability endpoint : jsonWspApiRoot.getChildren()) {
            String operationName = endpoint.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME);
            description.addOperation(hostPart, pathPart, operationName);
        }

        return description;
    }

    /**
     * Processes one endpoint's url.
     *
     * @param endpoint Capability with URL attribute.
     * @param description Description object to store results to.
     */
    private static void processEndpoint(Capability endpoint, ApiDescription description) throws MalformedURLException {
        String urlStr = endpoint.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__URL);
        URL url = new URL(urlStr);

        String hostPart = url.getHost();
        String pathPart = url.getPath();
        String operationName = endpoint.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME);

        description.addOperation(hostPart, pathPart, operationName);
    }

    /**
     * Adds one operation to this descriptions object. If the hostname or pathToEndpoint keys do
     * not exist, they will be created.
     *
     * @param hostname
     * @param pathToEndpoint
     * @param operationName
     */
    public void addOperation(String hostname, String pathToEndpoint, String operationName) {

        if (!containsKey(hostname)) {
            put(hostname, new HashMap<>());
        }

        if (!get(hostname).containsKey(pathToEndpoint)) {
            get(hostname).put(pathToEndpoint, new ArrayList<>());
        }

        get(hostname).get(pathToEndpoint).add(operationName);
    }

    public void addOperations(String hostname, String pathToEndpoint, List<String> operationNames) {
        if (!containsKey(hostname)) {
            put(hostname, new HashMap<>());
        }

        if (!get(hostname).containsKey(pathToEndpoint)) {
            get(hostname).put(pathToEndpoint, new ArrayList<>());
        }

        get(hostname).get(pathToEndpoint).addAll(operationNames);
    }
}
