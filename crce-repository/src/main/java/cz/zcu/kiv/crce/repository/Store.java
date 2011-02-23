package cz.zcu.kiv.crce.repository;

import cz.zcu.kiv.crce.metadata.Resource;
import java.io.IOException;

/**
 *
 * @author kalwi
 */
public interface Store {

    public void put(Resource resource) throws IOException;
    
    public Resource[] get(String filter);
    
    public Resource getNewestVersion(String symbolicName);
    
    public boolean isCompatible(Resource resource);
    
}
