package cz.zcu.kiv.crce.plugin;

import cz.zcu.kiv.crce.metadata.Resource;
import java.io.IOException;
import java.net.URI;

/**
 * Plugin implementing this class manages retrieving and storing metadata of an
 * artifact.
 * 
 * <p> Typical usage of <code>ResourceDAO</code> plugin is in repository core to
 * manipulate and manage metadata resource while uploading artifacts, retrieving
 * them, copying etc.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface ResourceDAO extends Plugin {

    /**
     * Returns <code>Resource</code> object for a given resource. Returns
     * <code>null</code> if <code>Resource</code> object can not be created
     * (e.g. resource not exists).
     * @param uri URI of a res
     * @return
     * @throws IOException  
     */
    public Resource getResource(URI uri) throws IOException;
    
    // TODO getResource() with basedir arg returning resource with relative uri

    /**
     * Saves metadata of <code>Resource</code>.
     * 
     * @param resource
     * @throws IOException 
     */
    public void save(Resource resource) throws IOException;

    public void copy(Resource resource, URI uri) throws IOException;
    
}
