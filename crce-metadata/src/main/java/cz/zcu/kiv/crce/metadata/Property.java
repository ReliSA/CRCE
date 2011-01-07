package cz.zcu.kiv.crce.metadata;

import java.net.URI;
import java.net.URL;
import java.util.Set;
import org.osgi.framework.Version;

/**
 *
 * @author kalwi
 */
public interface Property {

    String getName();

    Type getType();

    String getValue();

    Object getConvertedValue();
    
    void setValue(String value, Type type);
    
    void setValue(String string);
    
    void setValue(Version version);

    void setValue(URL url);
    
    void setValue(URI uri);
    
    void setValue(long llong);
    
    void setValue(double ddouble);
    
    void setValue(Set values);
    
    boolean isWritable();
            
}
