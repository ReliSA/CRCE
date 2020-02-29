package cz.zcu.kiv.crce.apicomp.impl.webservice;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.impl.ApiCompatibilityCheckerServiceImpl;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checker for crce-webservices-indexer
 *
 */
public abstract class WebservicesCompatibilityChecker extends ApiCompatibilityChecker {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String getRootCapabilityNamespace() {
        return WebserviceIndexerConstants.NAMESPACE__CRCE_IDENTITY;
    }

    @Override
    public CompatibilityCheckResult compareApis(Resource api1, Resource api2) {

        CompatibilityCheckResult checkResult = new CompatibilityCheckResult(api1, api2);

        // given the structure of metadata created in crce-restimpl-indexer, both
        // capability sets should contain exactly 1 capability
        if (!isApiSupported(api1)) {
            logger.warn("API 1 is not supported by this checker!");
            throw new RuntimeException("API 1 is not supported by this checker!");
        }

        if (!isApiSupported(api2)) {
            logger.warn("API 2 is not supported by this checker!");
            throw new RuntimeException("API 2 is not supported by this checker!");
        }

        Capability root1 = getOneRootCapability(api1);
        Capability root2 = getOneRootCapability(api2);

        if (root1  == null || root2 == null) {
            logger.warn("Root capability with API metadata not present in the first and/or the second API.");
            checkResult.setFinalDifference(Difference.UNK);
            return checkResult;
        }

        compare(checkResult, root1, root2);
        checkResult.recalculateFinalDifference();

        return checkResult;
    }

    /**
     * Returns the root capability containing the API meta-data tree.
     *
     * @param resource Resource with the root capability.
     *
     * @return Capability or null if resource has no such root capability.
     */
    protected abstract Capability getOneRootCapability(Resource resource);


    /**
     * TODO: work in progress, may change
     *
     * @param checkResult Result object used to set diffs.
     * @param root1
     * @param root2
     * @return
     */
    protected abstract void compare(CompatibilityCheckResult checkResult, Capability root1, Capability root2);
}
