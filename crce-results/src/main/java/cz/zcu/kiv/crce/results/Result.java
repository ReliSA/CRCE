package cz.zcu.kiv.crce.results;

import java.net.URI;
import org.osgi.framework.Version;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface Result {
    
    URI getResult();
    
    String getResourceSymbolicName();
    
    Version getResourceVersion();
    
    String getPluginId();
    
    Version getPluginVersion();
    
}
