package cz.zcu.kiv.crce.repository;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.metadata.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.osgi.service.cm.ManagedService;

/**
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface Buffer extends ManagedService {

    /**
     * Puts a new resource into resource buffer.
     * 
     * @param name A name of uploaded file.
     * @param resource Uploaded resource.
     * @return 
     * @throws IOException 
     */
    public Resource put(String name, InputStream resource) throws IOException;

    /**
     * Returns resources stored in buffer.
     * @return array of resources.
     */
    public Repository getRepository();
    
    /**
     * TODO analyze requirements for this method:
     * - add an executable plugin
     * @param resources 
     * @param plugins 
     */
    public void execute(List<Resource> resources, List<Plugin> plugins);
    
    public void commit();
}
