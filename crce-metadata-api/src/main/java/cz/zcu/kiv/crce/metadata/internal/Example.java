package cz.zcu.kiv.crce.metadata.internal;

import java.util.Map;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.DataType;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.SimpleDataType;

/**
 * Not working demonstration of API usage.
 * 
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class Example {

    private static final DataType<Long> ATTRIBUTE_A = new SimpleDataType<>("long.attribute", Long.class);
    private static final DataType<Double> ATTRIBUTE_B = new SimpleDataType<>("double.attribute", Double.class);

    
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        Capability capability = null;
        
        @SuppressWarnings({"null", "ConstantConditions"})
        // get attribute - type safe
        Attribute<Long> attribute = capability.getAttribute(ATTRIBUTE_A);
        Long value = attribute.getValue();
        DataType<Long> type = attribute.getDataType();
        Class<Long> type1 = type.getType();
        
        // get attribute value directly - type safe
        value = capability.getAttributeValue(ATTRIBUTE_A);

        // set attribute - type safe
        capability.setAttribute(ATTRIBUTE_B, new Double("0.0"));

        // iteration - unknown data types
        for (Attribute<?> pt : capability.getAttributes()) {
            pt.getDataType();
            Attribute<?> attribute1 = capability.getAttribute(pt.getDataType());
        }
        
        // map of attributes - unknown data types
        Map<DataType<?>, Attribute<?>> map = capability.getAttributesMap();
        Attribute attribute2 = map.get(ATTRIBUTE_A);
        Object value2 = attribute2.getValue();
        
        // the same for requirements
        Requirement requirement = null;
        
        @SuppressWarnings({"null", "ConstantConditions"})
        Attribute<Double> attribute1 = requirement.getAttribute(ATTRIBUTE_B);
        
        // etc.
        
    }
}
