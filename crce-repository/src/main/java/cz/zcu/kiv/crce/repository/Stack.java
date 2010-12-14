package cz.zcu.kiv.crce.repository;

import java.io.IOException;
import java.io.InputStream;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.obr.Resource;

/**
 *
 * @author kalwi
 */
public interface Stack extends ManagedService {
    
    public boolean store(String name, InputStream resource) throws IOException;

    public Resource[] getStoredResources();
    
    public void executeOnStored(Plugin[] plugins);
    
    public void commit();
    
    public void runTestsOnComponent(Object component);
    
}
