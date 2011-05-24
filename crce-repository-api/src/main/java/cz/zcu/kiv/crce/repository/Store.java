package cz.zcu.kiv.crce.repository;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.plugins.Executable;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * This interface defines permanent store for artifacts.
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
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
     * @throws IOException  
     */
    public boolean remove(Resource resource) throws IOException;

    /**
     * Returns resources stored in buffer.
     * @return array of resources.
     */
    public Repository getRepository();
    
    /**
     * TODO analyze requirements for this method:
     * - add an executable plugin
     * @param resources
     * @param executable
     * @param properties 
     */
    public void execute(List<Resource> resources, Executable executable, Properties properties);
    
}
