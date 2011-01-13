package cz.zcu.kiv.crce.repository;

import cz.zcu.kiv.crce.metadata.Resource;
import java.io.IOException;
import java.io.InputStream;
import org.osgi.service.cm.ManagedService;

/**
 *
 * @author kalwi
 */
public interface ResourceBuffer extends ManagedService {
    
    public Resource put(String name, InputStream resource) throws IOException;

    public Resource[] getStoredResources();
    
    public void executeOnStored(Plugin[] plugins);
    
    public void commit();
    
    public void runTestsOnComponent(Object component);
    
}
