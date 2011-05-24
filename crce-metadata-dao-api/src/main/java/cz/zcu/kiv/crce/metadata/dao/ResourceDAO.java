package cz.zcu.kiv.crce.metadata.dao;

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
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public interface ResourceDAO extends Plugin {

    /**
     * Returns <code>Resource</code> object for the given resource. Returns
     * <code>null</code> if <code>Resource</code> object can not be created
     * (e.g. resource not exists).
     * @param uri URI 
     * @return
     * @throws IOException  
     */
    public Resource getResource(URI uri) throws IOException;

    /**
     * Moves (reassignes) the <code>Resource</code> to the artifact with the
     * given URI.
     * 
     * @param resource
     * @param artifact URI of artifact.
     * @return 
     */
    public Resource moveResource(Resource resource, URI artifact);

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
     * @throws IOException  
     */
    public void remove(Resource resource) throws IOException;
    
}
