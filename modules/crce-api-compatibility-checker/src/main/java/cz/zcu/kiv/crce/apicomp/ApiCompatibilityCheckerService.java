package cz.zcu.kiv.crce.apicomp;

import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 * A service responsible for calling correct compatibility comparator for
 * given metadata.
 */
public interface ApiCompatibilityCheckerService {

    /**
     * Compares two APIS and returns the comparison result.
     *
     * @param api1 Metadata of the first API.
     * @param api2 Metadata of the second API.
     * @return Comparison result.
     */
    CompatibilityCheckResult compareApis(Resource api1, Resource api2);
}
