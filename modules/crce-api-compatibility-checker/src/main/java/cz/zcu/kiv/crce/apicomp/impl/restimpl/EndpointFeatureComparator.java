package cz.zcu.kiv.crce.apicomp.impl.restimpl;

import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;

import java.util.List;

/**
 * Base class for comparing endpoint features - parameters, responses, ...
 *
 */
public abstract class EndpointFeatureComparator {

    protected Capability endpoint1;

    protected Capability endpoint2;

    public EndpointFeatureComparator(Capability endpoint1, Capability endpoint2) {
        this.endpoint1 = endpoint1;
        this.endpoint2 = endpoint2;
    }

    /**
     * Compares given features of both endpoints and returns list of diffs
     * describing the differences.
     *
     * Typically, you want one Diff per feature (one diff per one response, one diff per one parameter, ...).
     */
    public abstract List<Diff> compare();

    /**
     * Compares ATTR__RESTIMPL_DATETYPE of two properties.
     *
     * This may be a bit tricky because e.g. long <: short and long <: int
     * but in java Long, Short, Integer are subclasses of Number and can't be
     * compared between each other (not assignable, not instance of).
     *
     * For unknown types (those outside java.lang) UNK is returned.
     *
     * @param p1 First property.
     * @param p2 Second property.
     * @return
     */
    protected Difference compareDateTypeAttributes(Property p1, Property p2) {
        Attribute dt1 = p1.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_DATETYPE);
        Attribute dt2 = p2.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_DATETYPE);

        String c1Name = dt1 != null ? dt1.getStringValue() : null;
        String c2Name = dt2 != null ? dt2.getStringValue() : null;

        if (c1Name == null || c1Name.isEmpty() || c2Name == null || c2Name.isEmpty()) {
            return Difference.UNK;
        }

        if (c1Name.equals(c2Name)) {
            return Difference.NON;
        }

        JavaTypeWrapper type1 = new JavaTypeWrapper(c1Name);
        JavaTypeWrapper type2 = new JavaTypeWrapper(c2Name);

        if (type1.equals(type2)
                || type1.fitsInto(type2)
                || type2.fitsInto(type1)
        ) {
            return Difference.NON;
        } else if (type1.isExtendedBy(type2)) {
            return Difference.SPE;
        } else if (type2.isExtendedBy(type1)){
            return Difference.GEN;
        }

        try {
            Class<?> c1 = Class.forName(c1Name);
            Class<?> c2 = Class.forName(c2Name);

            if (c1.isAssignableFrom(c2)) {
                // c2 <: c1
                return Difference.SPE;
            } else if (c2.isAssignableFrom(c1)) {
                // c1 <: c2
                return Difference.GEN;
            }
        } catch (ClassNotFoundException e) {
            // since this method only works with "java.lang", this
            // exception should not be thrown
            // todo: log exception
            e.printStackTrace();
        }

        return Difference.UNK;
    }
}
