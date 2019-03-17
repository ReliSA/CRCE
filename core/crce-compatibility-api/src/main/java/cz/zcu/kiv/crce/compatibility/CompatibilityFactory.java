package cz.zcu.kiv.crce.compatibility;

import cz.zcu.kiv.crce.metadata.type.Version;

import java.util.List;

/**
 * Factory interface for Compatibility.
 *
 * Date: 17.11.13
 *
 * @author Jakub Danek
 */
public interface CompatibilityFactory {

    /**
     *
     * Factory method for complete initialization of Compatibility implementation.
     *
     * @param id unique id
     * @param resourceName compared resource name (crce.identity)
     * @param resourceVersion compared resource version in OSGi representation
     * @param baseName name of the resource used as base
     * @param baseVersion OSGi representation of the version of the resource used as base
     * @param diffValue aggregated difference value of the two resources
     * @param diffValues complete diff of the two resources
     * @param contract contract the instance is related to
     * @return new Compatibility instance
     */
    Compatibility createCompatibility(String id, String resourceName, Version resourceVersion, String baseName,
                                      Version baseVersion, Difference diffValue, List<Diff> diffValues, Contract contract);

    /**
     *
     * Factory method for complete initialization of Compatibility implementation. Base name is set to the same
     * value as resource name.
     *
     * @param id unique id
     * @param resourceName compared resource name (crce.identity)
     * @param resourceVersion compared resource version in OSGi representation
     * @param baseVersion OSGi representation of the version of the resource used as base
     * @param diffValue aggregated difference value of the two resources
     * @param diffValues complete diff of the two resources
     * @param contract contract the instance is related to
     * @return new Compatibility instance
     */
    Compatibility createCompatibility(String id, String resourceName, Version resourceVersion,
                                      Version baseVersion, Difference diffValue, List<Diff> diffValues, Contract contract);

    /**
     * Create blank diff instance.
     *
     * @return empty instance of Diff
     */
    Diff createEmptyDiff();
}
