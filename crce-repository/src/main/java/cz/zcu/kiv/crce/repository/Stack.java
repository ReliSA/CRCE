package cz.zcu.kiv.crce.repository;

import java.io.InputStream;
import org.osgi.service.obr.Resource;

/**
 *
 * @author kalwi
 */
public interface Stack {
    public void store(String name, InputStream resource);

    public Resource[] getStoredResources();
    
    public void executeOnStored(Plugin[] plugins);
    
    public void commit();
    
    public void runTestsOnComponent(Object component);
    
}
