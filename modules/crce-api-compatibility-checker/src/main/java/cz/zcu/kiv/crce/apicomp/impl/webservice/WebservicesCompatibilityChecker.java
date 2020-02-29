package cz.zcu.kiv.crce.apicomp.impl.webservice;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 * Checker for crce-webservices-indexer
 *
 */
public abstract class WebservicesCompatibilityChecker extends ApiCompatibilityChecker {

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
            // todo: log error
            throw new RuntimeException("API 1 is not supported by this checker!");
        }

        if (!isApiSupported(api2)) {
            // todo: log error
            throw new RuntimeException("API 2 is not supported by this checker!");
        }

        // todo: comparison
        Capability root1 = getOneRootCapability(api1);
        Capability root2 = getOneRootCapability(api2);

        if (root1  == null || root2 == null) {
            // todo: log that root capabilities were not found
            checkResult.setFinalDifference(Difference.UNK);
            return checkResult;
        }

        compare(checkResult, root1, root2);
        checkResult.recalculateFinalDifference();
        // todo: support various types of webservices (one asbtract class + subclass for every type?)

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
