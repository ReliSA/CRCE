package cz.zcu.kiv.crce.repository;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.plugins.Executable;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author kalwi
 */
public interface Store {
    
    public Resource put(Resource resource) throws IOException;
   
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
     * @param resources 
     * @param plugins 
     */
    public void execute(List<Resource> resources, List<Executable> plugins);
    
}
