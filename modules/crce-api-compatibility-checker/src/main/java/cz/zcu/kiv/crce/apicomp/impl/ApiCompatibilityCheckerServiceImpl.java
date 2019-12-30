package cz.zcu.kiv.crce.apicomp.impl;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityCheckerService;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 * A service responsible for fetching resources' metadata and passing them to
 * the correct compatibility checker.
 */
public class ApiCompatibilityCheckerServiceImpl implements ApiCompatibilityCheckerService {

    @Override
    public CompatibilityCheckResult compareApis(Resource api1, Resource api2) {
        // todo: determine which checker to use

        // todo: actually use the checker
        return new CompatibilityCheckResult();
    }
}
