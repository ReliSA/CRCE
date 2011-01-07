package cz.zcu.kiv.crce.metadata;

import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import org.osgi.framework.Version;

/**
 *
 * @author kalwi
 */
public interface Capability {

    String getName();

    Property[] getProperties();

    Property getProperty(String name);

    void setProperty(Property property);
    
    void setProperty(String name, String value, Type type);

    void setProperty(String name, String string);
    
    void setProperty(String name, Version version);

    void setProperty(String name, URL url);
    
    void setProperty(String name, URI uri);
    
    void setProperty(String name, long llong);
    
    void setProperty(String name, double ddouble);
    
    void setProperty(String name, Set values);

    void unsetProperty(String name);
}
