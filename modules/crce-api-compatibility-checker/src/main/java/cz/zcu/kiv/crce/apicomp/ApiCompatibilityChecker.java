package cz.zcu.kiv.crce.apicomp;

import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 * Interface for compatibility checkers.
 */
public abstract class ApiCompatibilityChecker {

    /**
     * Namespace of the root capability that is expected to hold all metadata relevant to this checker.
     *
     * @return Capability namespace.
     */
    public abstract  String getRootCapabilityNamespace();

    /**
     * Verifies that the provided API is supported by this compatibility checker.
     * Verification is based on presence of root capability with namespace equal to
     * {@link #getRootCapabilityNamespace()}.
     *
     * @param resource Resource that contains API metadata.
     * @return True if the given API is supported.
     */
    public boolean isApiSupported(Resource resource) {
        return resource != null &&
                !resource.getRootCapabilities(getRootCapabilityNamespace()).isEmpty();
    };

    /**
     * Compares two APIs using metadata stored in CRCE and returns result in form of
     * a CompatibilityCheckResult object.
     *
     * @param api1 Resource with capabilities containing the metadata for the first API. This might be either single root
     *             capability which contains child capabilities with attributes and/or properties with metadata or set of root
     *             capabilities with child capabilities with attributes and/or properties.
     * @param api2 Resource with capabilities containing the metadata for the second API. This might be either single root
     *             capability which contains child capabilities with attributes and/or properties with metadata or set of root
     *             capabilities with child capabilities with attributes and/or properties.
     * @return Result.
     */
    public abstract CompatibilityCheckResult compareApis(Resource api1, Resource api2);
}
