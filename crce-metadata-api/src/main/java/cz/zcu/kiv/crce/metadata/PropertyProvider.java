package cz.zcu.kiv.crce.metadata;

import java.net.URI;
import java.net.URL;
import java.util.Set;
import org.osgi.framework.Version;

/**
 *
 * @param <T> 
 * @author kalwi
 */
public interface PropertyProvider<T extends PropertyProvider<T>> {

    Property[] getProperties();

    Property getProperty(String name);
    
    String getPropertyString(String name);

    T setProperty(Property property);
    
    T setProperty(String name, String value, Type type);

    T setProperty(String name, String string);
    
    T setProperty(String name, Version version);

    T setProperty(String name, URL url);
    
    T setProperty(String name, URI uri);
    
    T setProperty(String name, long llong);
    
    T setProperty(String name, double ddouble);
    
    T setProperty(String name, Set values);

    T unsetProperty(String name);
}
