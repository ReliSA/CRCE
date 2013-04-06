package cz.zcu.kiv.crce.webui.internal.legacy;

import java.net.URI;
import java.net.URL;
import java.util.Set;
import org.osgi.framework.Version;

/**
 * Common interface for subclasses that can provide Properties.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 * @param <T> 
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
