package cz.zcu.kiv.crce.apicomp.impl;

import cz.zcu.kiv.crce.apicomp.internal.DiffUtils;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.impl.DefaultDiffImpl;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class used to compare parameters of two endpoint. Also better for testability.
 *
 * New instance should be created for every comparison.
 *
 */
public class EndpointParameterComparator extends EndpointFeatureComparator {

    private List<Property> endpoint1Params = new ArrayList<>();

    private List<Property> endpoint2Params = new ArrayList<>();

    public EndpointParameterComparator(Capability endpoint1, Capability endpoint2) {
        super(endpoint1, endpoint2);

        endpoint1Params.addAll(endpoint1.getProperties(RestimplIndexerConstants.NS_RESTIMPL_REQUESTPARAMETER));
        endpoint2Params.addAll(endpoint2.getProperties(RestimplIndexerConstants.NS_RESTIMPL_REQUESTPARAMETER));
    }

    /**
     * Compares parameters of two endpoints. Positive result is returned only if
     * all parameters of both endpoints have same order and same type. That is
     * parameters1[i].type == parameters2[i].type.
     *
     * @return Diffs between endpoint parameters. Empty collection if the endpoints have same parameters
     */
    public List<Diff> compare() {
        List<Diff> diffs = new ArrayList<>();
        Iterator<Property> p1i = endpoint1Params.iterator();
        Iterator<Property> p2i = endpoint2Params.iterator();
        while(p1i.hasNext() && p2i.hasNext()) {
            compareParameters(p1i.next(), p2i.next(), diffs);
            p1i.remove();
            p2i.remove();
        }

        // add INS and DEL parameters to diff
        while(p1i.hasNext()) {
            Property param = p1i.next();
            diffs.add(DiffUtils.createDELDiff(
                    param.getAttributeStringValue(RestimplIndexerConstants.ATTR__RESTIMPL_NAME),
                    DifferenceLevel.FIELD
            ));
        }

        while(p2i.hasNext()) {
            Property param = p2i.next();
            diffs.add(DiffUtils.createINSDiff(
                    param.getAttributeStringValue(RestimplIndexerConstants.ATTR__RESTIMPL_NAME),
                    DifferenceLevel.FIELD
            ));
        }

        return diffs;
    }

    /**
     * Creates a diff of two parameters.
     *
     * Parameter names must be equal: ?param1=5 vs. ?param1_2=5
     * Category must be equal: query vs path parameters
     * Type should be equal but may be just GEN/SPE
     *
     * @param param1
     * @param param2
     * @param parameterDiffs List of parameters to add diff to.
     */
    private void compareParameters(Property param1, Property param2, List<Diff> parameterDiffs) {
        Diff diff = new DefaultDiffImpl();
        diff.setLevel(DifferenceLevel.FIELD);
        diff.setValue(Difference.NON);

        // attributes to compare
        Attribute name1 = param1.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_NAME);
        Attribute name2 = param2.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_NAME);

        Attribute cat1 = param1.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_PARAMETER_CATEGEORY);
        Attribute cat2 = param2.getAttribute(RestimplIndexerConstants.ATTR__RESTIMPL_PARAMETER_CATEGEORY);

        if (name1 != null && name1.equals(name2)
            && cat1 != null && cat1.equals(cat2)) {
            // name and category are the same, compare data types
            diff.setValue(compareDateTypeAttributes(param1, param2));
        } else {
            diff.setValue(Difference.UNK);
            diff.setNamespace(RestimplIndexerConstants.NS_RESTIMPL_REQUESTPARAMETER);
        }

        parameterDiffs.add(diff);
    }
}
