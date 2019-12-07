package cz.zcu.kiv.crce.apicomp;

import cz.zcu.kiv.crce.apicomp.impl.DifferenceAggregation;
import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.compatibility.Contract;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
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
        DifferenceAggregation aggregation = new DifferenceAggregation();

        // expects that the first level children have their values
        // set properly
        getDiffDetails().forEach(d -> aggregation.addDifference(d.getValue()));

        return aggregation.getResultDifference();

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
