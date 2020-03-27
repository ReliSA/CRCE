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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Object describing the differences between compared APIs.
 *
 * In the naming convention of the Compatibility interface,
 * resource and baseResource are compared APIs.
 */
public class CompatibilityCheckResult implements Compatibility {

    /**
     * Database id.
     */
    private String id;

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
     * Additional info map used to store MOV flag.
     */
    private Map<String, Object> additionalInfo;

    /**
     * Final difference between comapred resources.
     */
    private Difference finalDifference;

    /**
     * Initializes this object with empty diffDetails and NON difference.
     */
    public CompatibilityCheckResult() {
        diffDetails = new ArrayList<>();
        additionalInfo = new HashMap<>();
        finalDifference = Difference.UNK;
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

        // we have no means of getting resource version at this point
        // and in the context of comparing APIs, this version is irrelevant
        baseResourceVersion = Version.emptyVersion;
        resourceVersion = Version.emptyVersion;
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

    public void setFinalDifference(Difference finalDifference) {
        this.finalDifference = finalDifference;
    }

    /**
     * Final verdict regarding the difference of two APIs.
     */
    @Nonnull
    @Override
    public Difference getDiffValue() {
        return finalDifference;
    }

    /**
     * Recalculates final difference from child diffs.
     */
    public void recalculateFinalDifference() {
        finalDifference = DifferenceAggregation.calculateFinalDifferenceFor(getDiffDetails());
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
        return Contract.INTERACTION;
    }

    @Nonnull
    @Override
    public Map<String, Object> getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Sets the move flag for this compatibility object.
     *
     * @param description Machine-readable description of why the MOV flag was set.
     */
    public void setMoveFlag(String description) {
        // todo: key as interface
        additionalInfo.put("MOV", description);
    }

    public boolean isMov() {
        return additionalInfo != null && additionalInfo.containsKey("MOV");
    }
}
