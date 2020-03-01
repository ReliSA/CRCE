package cz.zcu.kiv.crce.apicomp.impl.webservice;

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
 * Class used to compare parameters of two methods.
 *
 * New instance should be created for every comparison.
 *
 */
public class MethodParameterComparator extends MethodFeatureComparator {

    public MethodParameterComparator(Capability method1, Capability method2) {
        super(method1, method2);
    }

    @Override
    protected String getFeatureNamespace() {
        return WebserviceIndexerConstants.NAMESPACE__WEBSERVICE_ENDPOINT_PARAMETER;
    }

    /**
     * Compares parameters of two methods.
     *
     * @return Collection of parameter diffs. New diff is added for every parameter comparison.
     */
    @Override
    public List<Diff> compare() {
        List<Diff> diffs = new ArrayList<>();

        Iterator<Property> p1i = method1Features.iterator();
        Iterator<Property> p2i = method2Features.iterator();
        while(p1i.hasNext() && p2i.hasNext()) {
            compareParameters(p1i.next(), p2i.next(), diffs);
            p1i.remove();
            p2i.remove();
        }

        // add INS and DEL parameters to diff
        while(p1i.hasNext()) {
            Property param = p1i.next();
            diffs.add(DiffUtils.createDELDiff(
                    param.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__NAME),
                    DifferenceLevel.FIELD
            ));
        }

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
        Diff parameterDiff = new DefaultDiffImpl();
        parameterDiff.setName(param1.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__NAME));
        parameterDiff.setLevel(DifferenceLevel.FIELD);
        diffs.add(parameterDiff);

        // list of attribute types that are ought to be equal
        List<AttributeType> equalAttributesTypes = Arrays.asList(
                WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__NAME,
                WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__ORDER,
                WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_RESPONSE__ARRAY
        );
        boolean equalAttributes = true;
        for (AttributeType at : equalAttributesTypes) {
            equalAttributes &= areAttributesEqual(param1, param2, at);
        }
        if (!equalAttributes) {
            // some of the attributes that are to be equal are not -> UNK
            parameterDiff.addChild(DiffUtils.createDiff("metadata", DifferenceLevel.FIELD, Difference.UNK));
        } else {
            // compare types
            parameterDiff.addChild(compareTypes(param1, param2));

            // compare optional attributes
            Attribute<Long> optional1 = param1.getAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__OPTIONAL);
            Attribute<Long> optional2 = param2.getAttribute(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__OPTIONAL);

            Diff optionalDiff = DiffUtils.createDiff(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__OPTIONAL.getName(), DifferenceLevel.FIELD, Difference.NON);
            if (optional1 == null || optional2 == null) {
                // should happen, optional should be set
                // todo: log
                optionalDiff.setValue(Difference.UNK);
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

        parameterDiff.setValue(DifferenceAggregation.calculateFinalDifferenceFor(parameterDiff.getChildren()));
    }

    private Diff compareTypes(Property param1, Property param2) {
        String type1 = param1.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__TYPE);
        String type2 = param2.getAttributeStringValue(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__TYPE);

        Diff typeDiff = DiffUtils.createDiff(WebserviceIndexerConstants.ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__TYPE.getName(), DifferenceLevel.FIELD, Difference.NON);
        compareDataTypes(type1, type2, typeDiff);

        return typeDiff;
    }
}
