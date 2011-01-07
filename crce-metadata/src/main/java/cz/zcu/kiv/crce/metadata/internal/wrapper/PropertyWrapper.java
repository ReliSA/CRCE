package cz.zcu.kiv.crce.metadata.internal.wrapper;

import cz.zcu.kiv.crce.metadata.Property;

/**
 *
 * @author kalwi
 */
public class PropertyWrapper implements org.apache.felix.bundlerepository.Property {

    Property m_property;
    
    public PropertyWrapper(Property property) {
        m_property = property;
    }
    
    @Override
    public String getName() {
        return m_property.getName();
    }

    @Override
    public String getType() {
        return m_property.getType().toString();
    }

    @Override
    public String getValue() {
        return m_property.getValue();
    }

    @Override
    public Object getConvertedValue() {
        return m_property.getConvertedValue();
    }

}
