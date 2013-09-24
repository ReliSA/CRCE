package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.PropertyProvider;
import cz.zcu.kiv.crce.metadata.Type;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.osgi.framework.Version;

/**
 * Abstract implementatio of <code>PropertyProvider</code> interface. It serves
 * to provide unique implementation of providing Properties.
 * @param <T> 
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public abstract class AbstractPropertyProvider<T extends PropertyProvider<T>> implements PropertyProvider<T> {

    final Map<String, Property> m_map = new TreeMap<String, Property>();
    
    /**
     * Returns <code>this</code>. For generic casting purpose.
     * @return instance of this object.
     */
    abstract T getThis();
    
    @Override
    public synchronized Property getProperty(String name) {
        return m_map.get(name);
    }

    @Override
    public synchronized String getPropertyString(String name) {
        Property property = m_map.get(name);
        return property == null ? null : property.toString();
    }

    @Override
    public synchronized T setProperty(Property property) {
        m_map.put(property.getName(), property);
        return getThis();
    }

    @Override
    public synchronized Property[] getProperties() {
        return m_map.values().toArray(new Property[0]);
    }

    @Override
    public synchronized T setProperty(String name, String value, Type type) {
        obtainProperty(name).setValue(value, type);
        return getThis();
    }

    @Override
    public synchronized T setProperty(String name, String string) {
        obtainProperty(name).setValue(string);
        return getThis();
    }

    @Override
    public synchronized T setProperty(String name, Version version) {
        obtainProperty(name).setValue(version);
        return getThis();
    }

    @Override
    public synchronized T setProperty(String name, URL url) {
        obtainProperty(name).setValue(url);
        return getThis();
    }

    @Override
    public synchronized T setProperty(String name, URI uri) {
        obtainProperty(name).setValue(uri);
        return getThis();
    }

    @Override
    public synchronized T setProperty(String name, long llong) {
        obtainProperty(name).setValue(llong);
        return getThis();
    }

    @Override
    public synchronized T setProperty(String name, double ddouble) {
        obtainProperty(name).setValue(ddouble);
        return getThis();
    }

    @Override
    public synchronized T setProperty(String name, Set values) {
        obtainProperty(name).setValue(values);
        return getThis();
    }

    private PropertyImpl obtainProperty(String name) {
        Property prop;
        if ((prop = m_map.get(name)) == null) {
            prop = new PropertyImpl(name);
            m_map.put(name, prop);
        }
        return (PropertyImpl) prop;
    }

    @Override
    public synchronized T unsetProperty(String name) {
        m_map.remove(name);
        return getThis();
    }
}
