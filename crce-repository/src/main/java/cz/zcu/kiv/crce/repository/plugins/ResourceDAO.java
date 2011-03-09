package cz.zcu.kiv.crce.repository.plugins;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;
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
     * @param uri URI 
     * @return
     * @throws IOException  
     */
    public Resource getResource(URI uri) throws IOException;
    
//    /**
//     * TODO
//     * @param uri
//     * @param baseUri
//     * @return
//     * @throws IOException 
//     */
//    public Resource getResource(URI uri, URI baseUri) throws IOException;
//    

    /**
     * Saves metadata of <code>Resource</code>.
     * 
     * @param resource
     * @throws IOException 
     */
    public void save(Resource resource) throws IOException;

    /**
     * Removes metadata of <code>Resource</code>.
     * @param resource 
     */
    public void remove(Resource resource) throws IOException;
    
}
