package cz.zcu.kiv.crce.repository;

import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.metadata.Resource;
import java.io.IOException;
import java.io.InputStream;
import org.osgi.service.cm.ManagedService;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface ResourceBuffer extends ManagedService {

    /**
     * Puts a new resource into resource buffer.
     * 
     * @param name A name of uploaded file.
     * @param resource Uploaded resource.
     * @return 
     * @throws IOException 
     */
    public Resource put(String name, InputStream resource) throws IOException;

    public Resource[] getStoredResources();
    
    public void executeOnStored(Plugin[] plugins);
    
    public void commit();
    
    public void runTestsOnComponent(Object component);
    
}
