package cz.zcu.kiv.crce.apicomp.impl.webservice;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityChecker;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.metadata.Capability;

import java.util.Set;

/**
 * Checker for crce-webservices-indexer
 *
 */
public class WebservicesCompatibilityChecker implements ApiCompatibilityChecker {

    @Override
    public boolean isApiSupported(Set<Capability> apiMetadata) {
        return false;
    }

    @Override
    public CompatibilityCheckResult compareApis(Set<Capability> api1, Set<Capability> api2) {
        return null;
    }
}
