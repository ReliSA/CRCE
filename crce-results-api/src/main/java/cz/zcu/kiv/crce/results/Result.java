package cz.zcu.kiv.crce.results;

import java.net.URI;
import org.osgi.framework.Version;

/**
 * This interface defines a metadata for a file with results of some kind of tests.
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public interface Result {
    
    URI getResult();
    
    String getResourceSymbolicName();
    
    Version getResourceVersion();
    
    String getPluginId();
    
    Version getPluginVersion();
    
}
