package cz.zcu.kiv.crce.apicomp.result;

import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.compatibility.Contract;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.type.Version;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Object describing the differences between compared APIs.
 *
 * In the naming convention of the Compatibility interface,
 * resource and baseResource are compared APIs.
 */
public class CompatibilityCheckResult implements Compatibility {

    /**
     * @see Compatibility#getResourceName()
     */
    private String resourceName;

    /**
     * @see Compatibility#getBaseResourceName()
     */
    private String baseResourceName;

    /**
     * @see Compatibility#getResourceVersion()
     */
    private Version resourceVersion;

    /**
     * @see Compatibility#getBaseResourceVersion()
     */
    private Version baseResourceVersion;

    /**
     * Details on differences between APIs.
     */
    private List<Diff> diffDetails;

    /**
     * Initializes this object with empty diffDetails and NON difference.
     */
    public CompatibilityCheckResult() {
        diffDetails = new ArrayList<>();
    }

    /**
     * Diff object of two resources.
     *
     * @param baseResource Base resource.
     * @param resource Resource that is being compared to the base one.
     */
    public CompatibilityCheckResult(Resource baseResource, Resource resource) {
        this();
        baseResourceName = baseResource.getId();
        resourceName = resource.getId();
    }

    @Override
    public String getId() {
        return null;
    }

    @Nonnull
    @Override
    public String getResourceName() {
        return resourceName;
    }

    @Nonnull
    @Override
    public Version getResourceVersion() {
        return resourceVersion;
    }

    @Nullable
    @Override
    public String getBaseResourceName() {
        return baseResourceName;
    }

    @Nonnull
    @Override
    public Version getBaseResourceVersion() {
        return baseResourceVersion;
    }

    /**
     * Final verdict regarding the difference of two APIs.
     */
    @Nonnull
    @Override
    public Difference getDiffValue() {
        // all children should have their values set properly by compatibility checker
        return DifferenceAggregation.calculateFinalDifferenceFor(getDiffDetails());
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
        return null;
    }
}
