package cz.zcu.kiv.crce.apicomp.impl.webservice;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 * Checker for crce-webservices-indexer
 *
 */
public class WebservicesCompatibilityChecker implements ApiCompatibilityChecker {

    @Override
    public String getRootCapabilityNamespace() {
        return null;
    }

    @Override
    public boolean isApiSupported(Resource resource) {
        return false;
    }

    @Override
    public CompatibilityCheckResult compareApis(Resource api1, Resource api2) {
        return null;
    }
}
