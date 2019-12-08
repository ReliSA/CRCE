package cz.zcu.kiv.crce.apicomp;

import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.metadata.Capability;

import java.util.Set;

/**
 * Interface for compatibility checkers.
 */
public interface ApiCompatibilityChecker {

    /**
     * Verifies that the provided API is supported by this compatibility checker.
     * Verification is based on presence of identity capability and it's correct
     * namespace and value.
     *
     * @param apiMetadata Set of capabilities which describes the API.
     * @return True if the given API is supported.
     */
    boolean isApiSupported(Set<Capability> apiMetadata);

    /**
     * Compares two APIs using metadata stored in CRCE and returns result in form of
     * a CompatibilityCheckResult object.
     *
     * @param api1 Set of capabilities containing the metadata for the first API. This might be either single root
     *             capability which contains child capabilities with attributes and/or properties with metadata or set of root
     *             capabilities with child capabilities with attributes and/or properties.
     * @param api2 Set of capabilities containing the metadata for the second API. This might be either single root
     *             capability which contains child capabilities with attributes and/or properties with metadata or set of root
     *             capabilities with child capabilities with attributes and/or properties.
     * @return Result.
     */
    CompatibilityCheckResult compareApis(Set<Capability> api1, Set<Capability> api2);
}
