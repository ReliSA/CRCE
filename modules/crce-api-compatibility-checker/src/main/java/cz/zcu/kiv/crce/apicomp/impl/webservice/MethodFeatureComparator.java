package cz.zcu.kiv.crce.apicomp.impl.webservice;

import cz.zcu.kiv.crce.apicomp.impl.webservice.xsd.XsdTypeComparator;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for method features (parameters, repsonse) comparators.
 *
 * It is expected that the particular 'feature' is a property of capability.
 *
 */
public abstract class MethodFeatureComparator {

    public List<Property> method1Features = new ArrayList<>();
    public List<Property> method2Features = new ArrayList<>();

    public MethodFeatureComparator(Capability method1, Capability method2) {
        method1Features.addAll(method1.getProperties(getFeatureNamespace()));
        method2Features.addAll(method2.getProperties(getFeatureNamespace()));
    }

    /**
     * Namespace used to get capability properties representing feature to compare.
     *
     * @return
     */
    protected abstract String getFeatureNamespace();

    /**
     * Actually compares two given features.
     * @return
     */
    public abstract List<Diff> compare();

    /**
     * Returns true if both parameters have equal attribtues.
     *
     * If attributes are missing from both parameters, still returns true.
     *
     * @param param1
     * @param param2
     * @param parameterAttributeType
     * @return
     */
    protected boolean areAttributesEqual(Property param1, Property param2, AttributeType parameterAttributeType) {

        if (!param1.getAttributesMap().containsKey(parameterAttributeType) && !param2.getAttributesMap().containsKey(parameterAttributeType)) {
            return true;
        }

        Attribute a1 = param1.getAttribute(parameterAttributeType);
        Attribute a2 = param2.getAttribute(parameterAttributeType);

        return a1 != null && a1.equals(a2);
    }

    /**
     * Compares two data types. Supports GEN/SPEC for xsd typed, otherwise strict equality is required.
     *
     * @param type1
     * @param type2
     * @param typeDiff This method will set value of this diff.
     * @return
     */
    protected void compareDataTypes(String type1, String type2, Diff typeDiff) {
        if (type1 == null || type2 == null) {
            typeDiff.setValue(Difference.UNK);
        } else {
            // use Xsd comparator for detecting GEN/SPEC
            if (XsdTypeComparator.isXsdDataType(type1) && XsdTypeComparator.isXsdDataType(type2)) {
                Difference d = XsdTypeComparator.compareTypes(type1, type2);
                if (typeDiff != null) {
                    typeDiff.setValue(d);
                } else {
                    typeDiff.setValue(Difference.UNK);
                }
            } else {
                // unkown types, equality is required
                typeDiff.setValue(type1.equals(type2) ? Difference.NON : Difference.UNK);
            }
        }
    }
}
