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

// todo: document used diff levels
// todo: tests for JsonWSP data

/**
 * Supports WADL and Json-WSP.
 *
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

        return;
    }
}
