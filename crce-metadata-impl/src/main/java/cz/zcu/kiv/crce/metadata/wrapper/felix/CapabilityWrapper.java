package cz.zcu.kiv.crce.metadata.wrapper.felix;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class CapabilityWrapper implements org.apache.felix.bundlerepository.Capability {

    final Capability m_capability;
//    Map m_properties;
    
    CapabilityWrapper(Capability capability) {
        m_capability = capability;
    }

    @Override
    public String getName() {
        return m_capability.getName();
    }

    @Override
    public org.apache.felix.bundlerepository.Property[] getProperties() {
        Property[] properties = m_capability.getProperties();
        
        org.apache.felix.bundlerepository.Property[] out = new org.apache.felix.bundlerepository.Property[properties.length];
        for (int i = 0; i < properties.length; i++) {
            out[i] = new PropertyWrapper(properties[i]);
        }
        return out;
    }

    @Override
    public Map getPropertiesAsMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        for (Property prop : m_capability.getProperties()) {
            map.put(prop.getName().toLowerCase(), prop.getConvertedValue());
        }
        return map;
    }

}
