package cz.zcu.kiv.crce.repository;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.plugins.Executable;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface Store {
    
    /**
     * Puts resource to the <code>Store</code>.
     * 
     * @param resource 
     * @return
     * @throws IOException
     * @throws RevokedArtifactException  
     */
    public Resource put(Resource resource) throws IOException, RevokedArtifactException;
   
    /**
     * Removes the resource from this buffer and returns <code>true</code>, if
     * the buffer contained the given resource before removing, <code>false</code>
     * otherwise.
     * 
     * @param resource Resource to be removed from this buffer.
     * @return <code>true</code>, if the buffer contained the resource before
     * removing.
     */
    public boolean remove(Resource resource);

    /**
     * Returns resources stored in buffer.
     * @return array of resources.
     */
    public Repository getRepository();
    
    /**
     * TODO analyze requirements for this method:
     * - add an executable plugin
     * @param resource 
     * @param plugin 
     * @param properties 
     */
    public void execute(List<Resource> resource, Executable plugin, Properties properties);
    
}
