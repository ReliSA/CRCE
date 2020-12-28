package cz.zcu.kiv.crce.apicomp.impl.webservice.wadl;

import cz.zcu.kiv.crce.apicomp.impl.mov.common.AbstractMovDetector;
import cz.zcu.kiv.crce.apicomp.impl.mov.common.ApiDescription;
import cz.zcu.kiv.crce.apicomp.impl.mov.jsonwspwadl.JsonWspWadlMovDetector;
import cz.zcu.kiv.crce.apicomp.impl.webservice.WebservicesCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.impl.webservice.common.EndpointFeatureComparator;
import cz.zcu.kiv.crce.apicomp.impl.webservice.common.EndpointParameterComparator;
import cz.zcu.kiv.crce.apicomp.impl.webservice.common.WebserviceIndexerConstants;
import cz.zcu.kiv.crce.apicomp.internal.DiffUtils;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Contains logic for comparing apis described by WADL.
 *
 * Possible final diffs:
 * NON - no difference between APIs.
 * GEN - API 2 is more generic than API 1. Could be because of endpoint parameter GEN or
 *       there is at least one optional endpoint parameter in API 2 that was required in API 1.
 * SPE - API 2 is less generic than API 1. Caused by data types in endpoint responses and/or parameters.
 * INS - API 2 contains endpoint that API 1 does not.
 * DEL - API 1 contains endpoint that API 2 does not.
 * MUT - Combination of INS/DEL and/or GEN/SPEC.
 * UNK - Could not determine diff, could be because of uncomparable data types (endpoint parameters/responses)
 *       or communication patterns do not match.
 */
public class WadlCompatibilityChecker extends WebservicesCompatibilityChecker {

    @Override
    protected Capability getOneRootCapability(Resource resource) {
        List<Capability> capabilities = resource.getRootCapabilities(WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE);
        if (capabilities.isEmpty()) {
            return null;
        }

        return capabilities.get(0);
    }

    @Override
    protected AttributeType getCommunicationPatternAttributeName() {
        return WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE;
    }

    @Override
    protected void compare(CompatibilityCheckResult checkResult, Capability root1, Capability root2) {

        logger.info("Comparing WADL and/or Json-WSP APIs.");

        // diff used to aggregate differences between all endpoints
        Diff endpointsDiff = DiffUtils.createDiff("endpoints", DifferenceLevel.PACKAGE, Difference.NON);
        compareEndpointsFromRoot(root1, root2, endpointsDiff, movDetectionResult);
        checkResult.getDiffDetails().add(endpointsDiff);
    }

    @Override
    protected AbstractMovDetector getMovDetectorInstance(Capability root1, Capability root2) throws MalformedURLException {
        return new JsonWspWadlMovDetector(ApiDescription.fromWadl(root1), ApiDescription.fromWadl(root2));
    }

    @Override
    protected EndpointFeatureComparator getEndpointParameterComparatorInstance(Capability endpoint1, Capability endpoint2) {
        // endpoint parameters in WADL have no order and name has to be used
        return new EndpointParameterComparator(endpoint1, endpoint2, WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__NAME);
    }

    @Override
    protected String getApiCategory() {
        return "wadl";
    }
}
