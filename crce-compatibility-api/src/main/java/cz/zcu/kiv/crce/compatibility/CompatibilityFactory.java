package cz.zcu.kiv.crce.compatibility;

import cz.zcu.kiv.crce.metadata.type.Version;
import cz.zcu.kiv.typescmp.Difference;

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
     * Factory method for complete initialization of Compability implementation.
     *
     * @param id unique id
     * @param resourceName compared resource name (crce.identity)
     * @param resourceVersion compared resource version in OSGi representation
     * @param baseName name of the resource used as base
     * @param baseVersion OSGi representation of the version of the resource used as base
     * @param diffValue aggregated difference value of the two resources
     * @param diffTree complete diff of the two resources
     * @return new Compatibility instance
     */
    Compatibility createCompatibility(String id, String resourceName, Version resourceVersion, String baseName,
                                      Version baseVersion, Difference diffValue, Diff diffTree);

    /**
     *
     * Factory method for complete initialization of Compability implementation. Base name is set to the same
     * value as resource name.
     *
     * @param id unique id
     * @param resourceName compared resource name (crce.identity)
     * @param resourceVersion compared resource version in OSGi representation
     * @param baseVersion OSGi representation of the version of the resource used as base
     * @param diffValue aggregated difference value of the two resources
     * @param diffTree complete diff of the two resources
     * @return new Compatibility instance
     */
    Compatibility createCompatibility(String id, String resourceName, Version resourceVersion,
                                      Version baseVersion, Difference diffValue, Diff diffTree);
}
