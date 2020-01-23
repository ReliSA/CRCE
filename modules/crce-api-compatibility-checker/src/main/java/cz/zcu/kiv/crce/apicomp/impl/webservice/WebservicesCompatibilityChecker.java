package cz.zcu.kiv.crce.apicomp.impl.webservice;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 * Checker for crce-webservices-indexer
 *
 */
public class WebservicesCompatibilityChecker extends ApiCompatibilityChecker {

    @Override
    public String getRootCapabilityNamespace() {
        return WebserviceIndexerConstants.NAMESPACE__WEBSERVICESCHEMA_IDENTITY;
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
        // todo: support various types of webservices (one asbtract class + subclass for every type?)

        return checkResult;
    }
}
