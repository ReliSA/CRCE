package cz.zcu.kiv.crce.metadata;

import java.io.IOException;
import java.net.URI;

/**
 *
 * @author kalwi
 */
public interface ResourceDAO {

    /**
     * Returns <code>Resource</code> object for a given resource. Returns
     * <code>null</code> if <code>Resource</code> object can not be created
     * (e.g. resource not exists).
     * @param uri URI of a res
     * @return
     * @throws IOException  
     */
    public Resource getResource(URI uri) throws IOException;
    
    public void save(Resource resource) throws IOException;

    public void copy(Resource resource, URI uri) throws IOException;
}
