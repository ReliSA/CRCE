package cz.zcu.kiv.crce.apicomp.impl.webservice;

import cz.zcu.kiv.crce.apicomp.internal.DiffUtils;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;

import java.util.List;

/**
 * Contains logic for comparing apis described by either WADL or Json-WSP.
 *
 * Possible final diffs:
 * NON - no difference between APIs.
 * GEN - API 2 is more generic than API 1. Could be because of endpoint parameter GEN or
 *       there is at least one optional endpoint parameter in API 2 that was required in API 1.
 * SPE - API 2 is less generic than API 1. Caused by data types in endpoint responses and/or parameters.
 * INS - API 2 contains endpoint that API 1 does not.
 * DEL - API 1 contains endpoint that API 2 does not.
 * MUT - Combination of INS/DEL and/or GEN/SPEC or communication patterns do not match.
 * UNK - Could not determine diff, could be because of uncomparable data types (endpoint parameters/responses)
 *       or
 */
public class WadlJsonWspCompatibilityChecker extends WebservicesCompatibilityChecker {

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

        Diff communicationPatternDiff = compareCommunicationPatterns(root1, root2);
        checkResult.getDiffDetails().add(communicationPatternDiff);
        // communication pattern must be same
        if (!communicationPatternDiff.getValue().equals(Difference.NON)) {
            return;
        }

        // diff used to aggregate differences between all endpoints
        Diff endpointsDiff = DiffUtils.createDiff("endpoints", DifferenceLevel.PACKAGE, Difference.NON);
        compareEndpointsFromRoot(root1, root2, endpointsDiff);
        checkResult.getDiffDetails().add(endpointsDiff);
    }
}
