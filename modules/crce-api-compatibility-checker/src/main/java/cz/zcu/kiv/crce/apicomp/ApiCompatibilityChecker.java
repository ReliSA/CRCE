package cz.zcu.kiv.crce.apicomp;

import cz.zcu.kiv.crce.apicomp.impl.webservice.WebserviceIndexerConstants;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.namespace.NsCrceIdentity;

import java.util.List;

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
        // resource is not null and capability with API metadata exists
        boolean apiCapability = resource != null &&
                !resource.getRootCapabilities(getRootCapabilityNamespace()).isEmpty();

        // check also category in the identity capability if necessary
        String expectedCategory = getApiCategory();
        if (expectedCategory != null) {
            Capability identity = resource.getRootCapabilities(WebserviceIndexerConstants.NAMESPACE__CRCE_IDENTITY).get(0);
            Attribute<List<String>> categoryAttr = identity.getAttribute(NsCrceIdentity.ATTRIBUTE__CATEGORIES);
            boolean categoryOk = false;
            if (categoryAttr != null) {
                for (String category : categoryAttr.getValue()) {
                    categoryOk |= expectedCategory.equals(category);
                    if (categoryOk) {
                        break;
                    }
                }
            }
            return apiCapability && categoryOk;
        } else {
            // mime type ignored
            return apiCapability;
        }
    }

    /**
     * Returns the category of supported API. This is compared to the value in identity capability.
     *
     * Ignored if null.
     *
     * @return
     */
    protected String getApiCategory() {
        return null;
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
