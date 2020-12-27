package cz.zcu.kiv.crce.compatibility.impl;

import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.compatibility.Contract;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.metadata.type.Version;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Default implementation of Compatibility interface. Serves as a starting point
 * for other implementations.
 */
public class DefaultCompatibilityImpl implements Compatibility {

    private String id;
    private String resourceName;
    private Version resourceVersion;
    private String baseResourceName;
    private Version baseResourceVersion;
    private Difference diffValue;
    private List<Diff> diffDetails;
    private Contract contract;

    /**
     * Default constructor. Leaves all fields empty.
     */
    public DefaultCompatibilityImpl() {
    }

    /**
     * Fully initialized instance.
     * @param id
     * @param resourceName
     * @param resourceVersion
     * @param baseResourceName
     * @param baseResourceVersion
     * @param diffValue
     * @param diffDetails
     * @param contract
     */
    public DefaultCompatibilityImpl(String id, String resourceName, Version resourceVersion, String baseResourceName, Version baseResourceVersion, Difference diffValue, List<Diff> diffDetails, Contract contract) {
        this.id = id;
        this.resourceName = resourceName;
        this.resourceVersion = resourceVersion;
        this.baseResourceName = baseResourceName;
        this.baseResourceVersion = baseResourceVersion;
        this.diffValue = diffValue;
        this.diffDetails = diffDetails;
        this.contract = contract;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Nonnull
    @Override
    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    @Nonnull
    @Override
    public Version getResourceVersion() {
        return resourceVersion;
    }

    public void setResourceVersion(Version resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    @Nullable
    @Override
    public String getBaseResourceName() {
        return baseResourceName;
    }

    public void setBaseResourceName(String baseResourceName) {
        this.baseResourceName = baseResourceName;
    }

    @Nonnull
    @Override
    public Version getBaseResourceVersion() {
        return baseResourceVersion;
    }

    public void setBaseResourceVersion(Version baseResourceVersion) {
        this.baseResourceVersion = baseResourceVersion;
    }

    @Nonnull
    @Override
    public Difference getDiffValue() {
        return diffValue;
    }

    public void setDiffValue(Difference diffValue) {
        this.diffValue = diffValue;
    }

    @Nullable
    @Override
    public List<Diff> getDiffDetails() {
        return diffDetails;
    }

    public void setDiffDetails(List<Diff> diffDetails) {
        this.diffDetails = diffDetails;
    }

    @Nonnull
    @Override
    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultCompatibilityImpl that = (DefaultCompatibilityImpl) o;

        if (baseResourceName != null ? !baseResourceName.equals(that.baseResourceName) : that.baseResourceName != null)
            return false;
        if (baseResourceVersion != null ? !baseResourceVersion.equals(that.baseResourceVersion) : that.baseResourceVersion != null)
            return false;
        if (diffDetails != null ? !diffDetails.equals(that.diffDetails) : that.diffDetails != null) return false;
        if (diffValue != that.diffValue) return false;
        if (contract != that.contract) return false;
        if (resourceName != null ? !resourceName.equals(that.resourceName) : that.resourceName != null) return false;
        if (resourceVersion != null ? !resourceVersion.equals(that.resourceVersion) : that.resourceVersion != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = resourceName != null ? resourceName.hashCode() : 0;
        result = 31 * result + (resourceVersion != null ? resourceVersion.hashCode() : 0);
        result = 31 * result + (baseResourceName != null ? baseResourceName.hashCode() : 0);
        result = 31 * result + (baseResourceVersion != null ? baseResourceVersion.hashCode() : 0);
        result = 31 * result + (diffValue != null ? diffValue.hashCode() : 0);
        result = 31 * result + (diffDetails != null ? diffDetails.hashCode() : 0);
        result = 31 * result + (contract != null ? contract.hashCode() : 0);
        return result;
    }
}
