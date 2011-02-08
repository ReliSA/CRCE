package cz.zcu.kiv.crce.repository;

import cz.zcu.kiv.crce.metadata.Resource;
import java.io.IOException;
import java.net.URL;

/**
 *
 * @author kalwi
 */
public interface Repository {

    public void put(Resource resource) throws IOException;
    
    public Resource[] get(String filter);
    
    public URL getNewestVersion(String symbolicName);  // ??
    
    public boolean isCompatible(Resource resource);
    
}
