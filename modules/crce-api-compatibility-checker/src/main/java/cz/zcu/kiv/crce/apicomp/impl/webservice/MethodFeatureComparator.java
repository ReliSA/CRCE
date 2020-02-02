package cz.zcu.kiv.crce.apicomp.impl.webservice;

import cz.zcu.kiv.crce.compatibility.Diff;
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
     * @param param1
     * @param param2
     * @param parameterAttributeType
     * @return
     */
    protected boolean areAttributesEqual(Property param1, Property param2, AttributeType parameterAttributeType) {
        Attribute a1 = param1.getAttribute(parameterAttributeType);
        Attribute a2 = param2.getAttribute(parameterAttributeType);

        return a1 != null && a1.equals(a2);
    }
}
