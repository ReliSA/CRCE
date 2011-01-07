package cz.zcu.kiv.crce.metadata;

import java.net.URI;

/**
 *
 * @author kalwi
 */
public interface ResourceCreator {

    /**
     * Returns <code>Resource</code> object for a given resource.
     * @param uri URI of a res
     * @return 
     */
    public Resource getResource(URI uri);
    
    public void save(Resource resource);

    public void move(Resource resource, URI uri);
}
