package cz.zcu.kiv.crce.apicomp.impl.webservice.common;

import cz.zcu.kiv.crce.apicomp.internal.DiffUtils;
import cz.zcu.kiv.crce.apicomp.result.DifferenceAggregation;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.compatibility.DifferenceLevel;
import cz.zcu.kiv.crce.compatibility.impl.DefaultDiffImpl;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Class used to compare parameters of two endpoints.
 *
 * Note: Instance of this object is not reusable and
 * new instance should be thus created for every comparison.
 *
 */
public class EndpointParameterComparator extends EndpointFeatureComparator {

    /**
     * Attribute used to determine whether two endpoint parameters are comparable.
     */
    private AttributeType comparableAttributeType;

    public EndpointParameterComparator(Capability endpoint1, Capability endpoint2, AttributeType comparableAttributeType) {
        super(endpoint1, endpoint2);
        this.comparableAttributeType = comparableAttributeType;
    }

    public EndpointParameterComparator(Capability endpoint1, Capability endpoint2) {
        this(endpoint1, endpoint2, WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__ORDER);
    }

    @Override
    protected String getFeatureNamespace() {
        return WebserviceIndexerConstants.NAMESPACE__WEBSERVICE_ENDPOINT_PARAMETER;
    }

    /**
     * Compares parameters of two endpoints.
     *
     * @return Collection of parameter diffs. New diff is added for every parameter comparison.
     */
    @Override
    public List<Diff> compare() {
        List<Diff> diffs = new ArrayList<>();

        Iterator<Property> p1i = endpoint1Features.iterator();

        while(p1i.hasNext() && !endpoint2Features.isEmpty()) {

            // pick
            Property param1 = p1i.next();
            Property param2 = pullComparableParameter(param1, endpoint2Features);

            if (param2 != null) {
                compareParameters(param1, param2, diffs);
                p1i.remove();
            }
        }

        // add INS and DEL parameters to diff
        while(p1i.hasNext()) {
            Property param = p1i.next();
            diffs.add(DiffUtils.createDELDiff(
                    param.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__NAME),
                    DifferenceLevel.FIELD
            ));
        }

        Iterator<Property> p2i = endpoint2Features.iterator();
        while(p2i.hasNext()) {
            Property param = p2i.next();
            diffs.add(DiffUtils.createINSDiff(
                    param.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__NAME),
                    DifferenceLevel.FIELD
            ));
        }

        return diffs;
    }

    /**
     * Decides whether two parameters are comparable. This is usually done by comparing
     * their name or order.
     *
     * Order attribute is used by default.
     *
     * @param param1
     * @param param2
     * @return True if the parameters are comparable.
     */
    private boolean areParametersComparable(Property param1, Property param2) {
        Attribute a1 = param1.getAttribute(comparableAttributeType);
        Attribute a2 = param2.getAttribute(comparableAttributeType);

        return a1 != null && a1.equals(a2);
    }

    /**
     * Goes through the otherParams and returns the first parameter
     * for which {@link EndpointParameterComparator#areParametersComparable(Property, Property)}
     * returns true.
     *
     * If a match is found, parameter is removed from otherParams.
     *
     * @param param1 Parameter from the first endpoint.
     * @param otherParams Parameters of the second endpoint.
     * @return Parameter from endpoint 2 that is comparable with param1 or null if no is found.
     */
    private Property pullComparableParameter(Property param1, List<Property> otherParams) {
        logger.trace("Pulling comparable parameter for {}.", param1);
        Property param2 = null;
        Iterator<Property> otherParamsIt = otherParams.iterator();
        while (otherParamsIt.hasNext()) {
            Property p2Tmp = otherParamsIt.next();
            if (areParametersComparable(param1, p2Tmp)) {
                param2 = p2Tmp;
                otherParamsIt.remove();
                logger.trace("Comparable parameter found: {}.", param2);
                break;
            }
        }

        return param2;
    }

    /**
     * Creates a diff of two parameters.
     *
     * Following attributes must be equal:
     *
     *  - name
     *  - type (GEN/SPE allowed for some types)
     *  - order
     *  - isArray
     *  - optional (although if p1 is not optional and p2 is, then it's ok)
     *
     * @param param1
     * @param param2
     * @param diffs
     */
    private void compareParameters(Property param1, Property param2, List<Diff> diffs) {
        logger.trace("Comparing parameters: {}; {}.", param1, param2);
        Diff parameterDiff = new DefaultDiffImpl();
        parameterDiff.setName(param1.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__NAME));
        parameterDiff.setLevel(DifferenceLevel.FIELD);
        diffs.add(parameterDiff);

        // list of attribute types that are ought to be equal
        List<AttributeType> equalAttributesTypes = getEqualAttributeTypes();
        for (AttributeType at : equalAttributesTypes) {
            // add diff for each compared attribute
            boolean areEqual = areAttributesEqual(param1, param2, at);
            parameterDiff.addChild(DiffUtils.createDiff(
                    at.getName(),
                    DifferenceLevel.FIELD,
                    areEqual ? Difference.NON : Difference.UNK
            ));
        }

        // compare parameter data types
        parameterDiff.addChild(compareTypes(param1, param2));

        // compare optional attributes
        compareOptionalParameter(param1, param2, parameterDiff);

        DifferenceAggregation.calculateAndSetFinalDifferenceValueFor(parameterDiff);
        logger.trace("Result: {}.", parameterDiff.getValue());
    }

    protected void compareOptionalParameter(Property param1, Property param2, Diff parameterDiff) {
        // compare optional attributes
        Attribute<Long> optional1 = param1.getAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__OPTIONAL);
        Attribute<Long> optional2 = param2.getAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__OPTIONAL);

        Diff optionalDiff = DiffUtils.createDiff(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__OPTIONAL.getName(), DifferenceLevel.FIELD, Difference.NON);
        if (optional1 == null || optional2 == null) {
            // optional not set in both parameters, ok
            optionalDiff.setValue(Difference.NON);
        } else if (optional1.equals(optional2)) {
            optionalDiff.setValue(Difference.NON);
        } else if (optional1.getValue().equals(0L) && optional2.getValue().equals(1L)) {
            // the client is expecting parameter to be non-optional
            // but it's optional in the new version
            // that is ok as client can still use such API transparently
            optionalDiff.setValue(Difference.GEN);
        } else {
            // anything else is bad
            optionalDiff.setValue(Difference.UNK);
        }

        parameterDiff.addChild(optionalDiff);
    }

    /**
     * Returns list of types where strict equality is required.
     * @return
     */
    protected List<AttributeType> getEqualAttributeTypes() {
        return Arrays.asList(
                WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__NAME,
                WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__ORDER,
                WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__ARRAY
        );
    }

    protected Diff compareTypes(Property param1, Property param2) {
        String type1 = param1.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__TYPE);
        String type2 = param2.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__TYPE);

        Diff typeDiff = DiffUtils.createDiff(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__TYPE.getName(), DifferenceLevel.FIELD, Difference.NON);
        compareDataTypes(type1, type2, typeDiff);

        return typeDiff;
    }
}
