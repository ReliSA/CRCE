package cz.zcu.kiv.crce.apicomp.impl.mov;

import cz.zcu.kiv.crce.apicomp.impl.webservice.WebserviceIndexerConstants;
import cz.zcu.kiv.crce.metadata.Capability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WsdlMovDetector {

    private Capability api1;
    private Capability api2;

    public WsdlMovDetector(Capability api1, Capability api2) {
        this.api1 = api1;
        this.api2 = api2;
    }

    public boolean[] detectMov(ApiDescription a1, ApiDescription a2) {
        Map<String, Map<String, List<String>>> api1EndpointUrls = a1;
        Map<String, Map<String, List<String>>> api2EndpointUrls = a2;

        boolean hostDiff = false;
        boolean pathDiff = false;
        boolean operationDiff = false;

        // determine whether hosts are same in both APIs
        for (String hostName : api1EndpointUrls.keySet()) {
            hostDiff |= !api2EndpointUrls.containsKey(hostName);
        }

        // determine if paths to endpoints are same in both APIs
        pathDiff = determinePathDiff(api1EndpointUrls, api2EndpointUrls);

        // determine if the operation sets of endpoints are same in both APIs
        operationDiff = determineOperationDiff(api1EndpointUrls, api2EndpointUrls);


        return new boolean[] {hostDiff, pathDiff, operationDiff};
    }

    private boolean determinePathDiff(Map<String, Map<String, List<String>>> api1EndpointUrls, Map<String, Map<String, List<String>>> api2EndpointUrls) {

        int samePathSets = 0;
        int totalPathSets = 0;

        // Basically, we take a host from api1 (which is a set of endpoints)
        // and try to find host in api2 such that is has same number of endpoints
        // and the paths to endpoints are all the same
        //
        // samePathSets counter is incremented every time a host (endpoint set)
        // from api1 is found in api 2.
        //
        // We can't pick the hosts by hostnames because they may be different
        // while everything else (paths and operation names) are still the same
        for (Map<String, List<String>> endpoints1 : api1EndpointUrls.values()) {
            totalPathSets++;
            for (Map<String, List<String>> endpoints2 : api2EndpointUrls.values()) {
                boolean pathDiffTmp = false;
                if (endpoints1.size() == endpoints2.size()) {
                    // same number of endpoints, may be worth a try
                    for (String endpoint1Path : endpoints1.keySet()) {
                        if (!endpoints2.containsKey(endpoint1Path)) {
                            pathDiffTmp = true;
                            break;
                        }
                    }

                    if (!pathDiffTmp) {
                        // all endpoint paths from host 1 are in host 2
                        samePathSets++;
                        break;
                    }
                }
            }
        }

        // number of endpoint path sets in api1 is the same as
        // the number of same endpoint path sets found in api2
        // -> there's no diff in endpoint paths
        // if these numbers are not the same, diff in path was found
        return totalPathSets != samePathSets;
    }

    /**
     * If host1 exists in api2,
     *
     * @param api1EndpointUrls
     * @param api2EndpointUrls
     * @return
     */
    private boolean determineOperationDiff(Map<String, Map<String, List<String>>> api1EndpointUrls, Map<String, Map<String, List<String>>> api2EndpointUrls) {
        int sameOperationSets = 0;
        int totalOperationSets = 0;

        for (Map<String, List<String>> endpoints1 : api1EndpointUrls.values()) {
            for (List<String> operations1 : endpoints1.values()) {
                totalOperationSets++;

                for (Map<String, List<String>> endpoints2 : api2EndpointUrls.values()) {
                    if (endpoints2.size() == endpoints1.size()) {
                        // same number of endpoints in host
                        boolean operationDiff = false;

                        for (List<String> operations2 : endpoints2.values()) {
                            if (operations1.size() == operations2.size()) {
                                // same number of operations in endpoints
                                for (String operation1Name : operations1) {
                                    if (!operations2.contains(operation1Name)) {
                                        operationDiff = true;
                                        break;
                                    }
                                }

                                if (!operationDiff) {
                                    // same operations found
                                    sameOperationSets++;
                                    break;
                                }
                            }
                        }

                    }
                }
            }
        }

        return sameOperationSets != totalOperationSets;
    }

    // ws -> endpoint -> operations
    private Map<String, Map<String, List<String>>> collectEndpointUrls(Capability api1) {
        Map<String, Map<String, List<String>>> urls = new HashMap<>();

        // wsdl has following structure identity -> webservice -> endpoint
        for (Capability wsCapability : api1.getChildren()) {
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
}
