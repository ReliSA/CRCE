package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Type;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.osgi.framework.Version;

/**
 *
 * @author kalwi
 */
public class CapabilityImpl implements Capability {

    private final Map<String, Property> m_properties = new HashMap<String, Property>();
    private final List<Property> m_propertiesList = new ArrayList<Property>();
    
    private String m_name;
    
    public CapabilityImpl(String name) {
        m_name = name.intern();
    }
    
    @Override
    public Property getProperty(String name) {
        return m_properties.get(name.toLowerCase());
    }

    @Override
    public void setProperty(Property property) {
        m_properties.put(property.getName().toLowerCase(), property);
        m_propertiesList.add(property);
    }

    @Override
    public String getName() {
        return m_name;
    }

    @Override
    public Property[] getProperties() {
        return m_propertiesList.toArray(new Property[0]);
    }

    @Override
    public void setProperty(String name, String value, Type type) {
        obtainProperty(name).setValue(value, type);
    }

    @Override
    public void setProperty(String name, String string) {
        obtainProperty(name).setValue(string);
    }

    @Override
    public void setProperty(String name, Version version) {
        obtainProperty(name).setValue(version);
    }

    @Override
    public void setProperty(String name, URL url) {
        obtainProperty(name).setValue(url);
    }

    @Override
    public void setProperty(String name, URI uri) {
        obtainProperty(name).setValue(uri);
    }

    @Override
    public void setProperty(String name, long llong) {
        obtainProperty(name).setValue(llong);
    }

    @Override
    public void setProperty(String name, double ddouble) {
        obtainProperty(name).setValue(ddouble);
    }

    @Override
    public void setProperty(String name, Set values) {
        obtainProperty(name).setValue(values);
    }

    private Property obtainProperty(String name) {
        Property prop;
        if ((prop = m_properties.get(name.toLowerCase())) == null) {
            prop = new PropertyImpl(name);
            m_properties.put(name.toLowerCase(), prop);
            m_propertiesList.add(prop);
        }
        return prop;
    }

    @Override
    public void unsetProperty(String name) {
        Property prop = m_properties.remove(name.toLowerCase());
        m_propertiesList.remove(prop);
    }

}
