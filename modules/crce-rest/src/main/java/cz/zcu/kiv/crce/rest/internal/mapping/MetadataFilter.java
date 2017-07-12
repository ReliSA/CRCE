package cz.zcu.kiv.crce.rest.internal.mapping;

/**
 * Filter criteria for GET Metadata operation.
 *
 * Filter criteria determines, which parts of XML with metedata should be included.
 *
 *
 * @author Jan Reznicek
 *
 */
public class MetadataFilter {

    private boolean coreCapabilities;

    private boolean includeCapabilities;
    private String capabilityNamespace;

    private boolean includeRequirements;
    private String requirementNamespace;

    private boolean includeProperties;
    private String propertyNamespace;

    /**
     * At default, including of each part of metadata is set to false.
     */
    public MetadataFilter() {
        coreCapabilities = false;

        includeCapabilities = false;
        capabilityNamespace = null;

        includeRequirements = false;
        requirementNamespace = null;

        includeProperties = false;
        propertyNamespace = null;
    }

    public boolean isCoreCapabilities() {
        return coreCapabilities;
    }

    public void setCoreCapabilities(boolean coreCapabilities) {
        this.coreCapabilities = coreCapabilities;
    }

    public boolean includeCapabilities() {
        return includeCapabilities;
    }

    public void setIncludeCapabilities(boolean includeCapabilities) {
        this.includeCapabilities = includeCapabilities;
    }

    public String getCapabilityNamespace() {
        return capabilityNamespace;
    }

    public void setCapabilityNamespace(String capabilityNamespace) {
        this.capabilityNamespace = capabilityNamespace;
    }

    public boolean includeRequirements() {
        return includeRequirements;
    }

    public void setIncludeRequirements(boolean includeRequirements) {
        this.includeRequirements = includeRequirements;
    }

    public String getRequirementNamespace() {
        return requirementNamespace;
    }

    public void setRequirementNamespace(String requirementNamespace) {
        this.requirementNamespace = requirementNamespace;
    }

    public boolean includeProperties() {
        return includeProperties;
    }

    public void setIncludeProperties(boolean includeProperties) {
        this.includeProperties = includeProperties;
    }

    public String getPropertyNamespace() {
        return propertyNamespace;
    }

    public void setPropertyNamespace(String propertyNamespace) {
        this.propertyNamespace = propertyNamespace;
    }

    /**
     * Include all parts of metadata.
     */
    public void includeAll() {
        coreCapabilities = true;
        includeCapabilities = true;
        includeRequirements = true;
        includeProperties = true;
    }
}
