package cz.zcu.kiv.crce.apicomp.impl.mov.common;

/**
 * Base class for MOV detectors.
 */
public abstract class AbstractMovDetector {

    private ApiDescription api1;
    private ApiDescription api2;

    public AbstractMovDetector(ApiDescription api1, ApiDescription api2) {
        this.api1 = api1;
        this.api2 = api2;
    }

    public MovDetectionResult detectMov() {

        boolean hostDiff = false;
        boolean pathDiff = false;
        boolean operationDiff = false;

        // determine whether hosts are same in both APIs
        hostDiff = determineHostDiff(api1, api2);

        // determine if paths to endpoints are same in both APIs
        pathDiff = determinePathDiff(api1, api2);

        // determine if the operation sets of endpoints are same in both APIs
        operationDiff = determineOperationDiff(api1, api2);


        return new MovDetectionResult(hostDiff, pathDiff, operationDiff);
    }

    protected boolean determineHostDiff(ApiDescription api1, ApiDescription api2) {
        boolean res = false;
        for (String hostName : api1.keySet()) {
            res |= !api2.containsKey(hostName);
        }

        return res;
    }

    protected abstract boolean determineOperationDiff(ApiDescription api1, ApiDescription api2);

    protected abstract boolean determinePathDiff(ApiDescription api1, ApiDescription api2);
}
