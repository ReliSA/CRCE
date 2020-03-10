package cz.zcu.kiv.crce.apicomp.impl.mov;

import java.util.List;
import java.util.Map;

public class WsdlMovDetector {

    private ApiDescription api1;
    private ApiDescription api2;

    public WsdlMovDetector(ApiDescription api1, ApiDescription api2) {
        this.api1 = api1;
        this.api2 = api2;
    }

    public MovDetectionResult detectMov() {

        boolean hostDiff = false;
        boolean pathDiff = false;
        boolean operationDiff = false;

        // determine whether hosts are same in both APIs
        for (String hostName : api1.keySet()) {
            hostDiff |= !api2.containsKey(hostName);
        }

        // determine if paths to endpoints are same in both APIs
        pathDiff = determinePathDiff(api1, api2);

        // determine if the operation sets of endpoints are same in both APIs
        operationDiff = determineOperationDiff(api1, api2);


        return new MovDetectionResult(hostDiff, pathDiff, operationDiff);
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
}
