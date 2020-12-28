package cz.zcu.kiv.crce.apicomp;

import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.compatibility.Compatibility;
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

    /**
     * Tries to find compatibility object for two given resources.
     *
     * Note: the fact that compatibility obj doesn't exist for [api1,api2]
     * combination does not mean it doesn't exist for [api2,api1].
     *
     * @param api1 First resource.
     * @param api2 Second resource.
     * @return Found compatibility result or null if no is found.
     */
    Compatibility findExistingCompatibility(Resource api1, Resource api2);

    /**
     * Saves compatibility object.
     *
     * @param compatibilityCheckResult Object to be saved.
     * @return Saved object.
     */
    Compatibility saveCompatibility(Compatibility compatibilityCheckResult);

    /**
     * Returns checker compatible for given resource.
     *
     * @param resource Resource.
     * @return Checker able to compare this resource.
     */
    ApiCompatibilityChecker pickChecker(Resource resource);

    /**
     * Removes given compatibility object from DB.
     * @param compatibility
     */
    void removeCompatibility(Compatibility compatibility);
}
