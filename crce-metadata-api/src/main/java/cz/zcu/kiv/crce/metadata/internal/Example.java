package cz.zcu.kiv.crce.metadata.internal;

import java.util.Map;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.MatchingAttribute;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.SimpleDataType;

/**
 * Not working demonstration of API usage.
 * 
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"DLS_DEAD_LOCAL_STORE", "NP_ALWAYS_NULL"}, justification="Just a non-runnable example.")
public class Example {

    private static final AttributeType<Long> ATTRIBUTE_A = new SimpleDataType<>("long.attribute", Long.class);
    private static final AttributeType<Double> ATTRIBUTE_B = new SimpleDataType<>("double.attribute", Double.class);

    
    @SuppressWarnings({"null", "ConstantConditions"})
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        Capability capability = null;
        
        // get attribute - type safe
        Attribute<Long> attribute = capability.getAttribute(ATTRIBUTE_A);
        Long value = attribute.getValue();
        AttributeType<Long> type = attribute.getAttributeType();
        Class<Long> type1 = type.getType();
        
        // get attribute value directly - type safe
        value = capability.getAttributeValue(ATTRIBUTE_A);
        
        String attributeStringValue = capability.getAttributeStringValue(ATTRIBUTE_A);
        
        // set attribute - type safe
        capability.setAttribute(ATTRIBUTE_B, new Double("0.0"));
        capability.setAttribute(ATTRIBUTE_B, 1.);

        // iteration - unknown data types
        for (Attribute<?> attr : capability.getAttributes()) {
            attr.getAttributeType();
            Attribute<?> attribute1 = capability.getAttribute(attr.getAttributeType());
        }
        // map of attributes - unknown data types
        Map<AttributeType<?>, ? extends Attribute<?>> map = capability.getAttributesMap();
        Attribute attribute2 = map.get(ATTRIBUTE_A);
        Object value2 = attribute2.getValue();
        
        // a similar for requirements
        Requirement requirement = null;
        
        MatchingAttribute<Double> mAttribute = requirement.getAttribute(ATTRIBUTE_B);
        
        Operator operator = mAttribute.getOperator();
        mAttribute.setOperator(Operator.EQUAL);
        
        operator = requirement.getAttributeOperator(ATTRIBUTE_A);
        
        
        
        // etc.
        
    }
}
