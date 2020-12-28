package cz.zcu.kiv.crce.apicomp.impl.webservice.common;

import cz.zcu.kiv.crce.apicomp.impl.webservice.common.xsd.XsdTypeComparator;
import cz.zcu.kiv.crce.compatibility.Diff;
import cz.zcu.kiv.crce.compatibility.Difference;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for endpoint features (parameters, repsonse) comparators.
 *
 * It is expected that the particular 'feature' is a property of capability.
 *
 */
public abstract class EndpointFeatureComparator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Capability endpoint1, endpoint2;

    List<Property> endpoint1Features = new ArrayList<>();
    List<Property> endpoint2Features = new ArrayList<>();

    public EndpointFeatureComparator(Capability endpoint1, Capability endpoint2) {
        this.endpoint1 = endpoint1;
        this.endpoint2 = endpoint2;

        initFeatures();
    }

    protected void initFeatures() {
        String featureNamespace = getFeatureNamespace();
        if (featureNamespace == null) {
            return;
        }

        endpoint1Features.addAll(endpoint1.getProperties(featureNamespace));
        endpoint2Features.addAll(endpoint2.getProperties(featureNamespace));
    }

    /**
     * Namespace used to get capability properties representing feature to compare.
     *
     * @return Namespace or null if no features are to be initialized.
     */
    protected abstract String getFeatureNamespace();

    /**
     * Actually compares two given features.
     * @return
     */
    public abstract List<Diff> compare();

    /**
     * Returns true if both parameters have equal attributes. The equality is
     * evaluated as follows: a1 != null && a1.equals(s2).
     *
     * If attributes are missing from both parameters, still returns true.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @param parameterAttributeType Type of the attribute to be compared.
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
