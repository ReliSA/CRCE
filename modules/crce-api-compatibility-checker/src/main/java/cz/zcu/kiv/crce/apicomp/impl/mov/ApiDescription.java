package cz.zcu.kiv.crce.apicomp.impl.mov;

import cz.zcu.kiv.crce.apicomp.impl.webservice.WebserviceIndexerConstants;
import cz.zcu.kiv.crce.metadata.Capability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiDescription extends HashMap<String, Map<String, List<String>>> {

    public static ApiDescription fromWsdl(Capability wsdlApiRoot) {
        ApiDescription urls = new ApiDescription();

        // wsdl has following structure identity -> webservice -> endpoint
        for (Capability wsCapability : wsdlApiRoot.getChildren()) {
            for (Capability wsEndpoint : wsCapability.getChildren()) {
                String url = wsEndpoint.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__URL);
                String[] urlSplit = url.split("/", 2);
                String hostPart = urlSplit[0];
                String pathPart = urlSplit[1];
                String operationName = wsEndpoint.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME);

                if (!urls.containsKey(hostPart)) {
                    urls.put(hostPart, new HashMap<>());
                }

                if (!urls.get(hostPart).containsKey(pathPart)) {
                    urls.get(hostPart).put(pathPart, new ArrayList<>());
                }

                urls.get(hostPart).get(pathPart).add(operationName);
            }
        }

        return urls;
    }

    public void addOperations(String hostname, String pathToEndpoint, List<String> operations) {
        if (!containsKey(hostname)) {
            put(hostname, new HashMap<>());
        }

        if (!get(hostname).containsKey(pathToEndpoint)) {
            get(hostname).put(pathToEndpoint, operations);
        } else {
            get(hostname).get(pathToEndpoint).addAll(operations);
        }
    }
}
