package cz.zcu.kiv.crce.apicomp.impl.mov.jsonwspwadl;

import cz.zcu.kiv.crce.apicomp.impl.mov.common.AbstractMovDetector;
import cz.zcu.kiv.crce.apicomp.impl.mov.common.ApiDescription;

import java.util.List;
import java.util.Map;

/**
 * Mov detector for APIs described by Json-WSP and WADL.
 * Main difference from WSDL mov detector is simpler structure of API description:
 *  - only one host
 *  - only one operation per endpoint
 *
 */
public class JsonWspWadlMovDetector extends AbstractMovDetector {

    public JsonWspWadlMovDetector(ApiDescription api1, ApiDescription api2) {
        super(api1, api2);
    }

    @Override
    protected boolean determineOperationDiff(ApiDescription api1, ApiDescription api2) {
        Map<String, List<String>> host1 = api1.values().iterator().next();
        Map<String, List<String>> host2 = api2.values().iterator().next();

        boolean opDiff = false;

        // pick each operation name in api1 and try to find it
        // in api2
        for(String pathToEndpointInApi1 : host1.keySet()) {
            String operation1 = host1.get(pathToEndpointInApi1).get(0);

            boolean operationFound = false;

            for(String pathToEndpointInApi2 : host2.keySet()) {
                String operation2 = host2.get(pathToEndpointInApi2).get(0);
                operationFound = operation2.equals(operation1);

                if (operationFound) {
                    break;
                }
            }

            opDiff |= !operationFound;
        }

        return opDiff;
    }

    @Override
    protected boolean determinePathDiff(ApiDescription api1, ApiDescription api2) {
        Map<String, List<String>> host1 = api1.values().iterator().next();
        Map<String, List<String>> host2 = api2.values().iterator().next();

        boolean pathDiff = false;

        for(String pathToEndpointInApi1 : host1.keySet()) {
            pathDiff |= !host2.containsKey(pathToEndpointInApi1);
        }

        return pathDiff;
    }
}
