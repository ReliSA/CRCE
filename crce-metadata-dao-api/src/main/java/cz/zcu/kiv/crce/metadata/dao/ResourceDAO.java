package cz.zcu.kiv.crce.metadata.dao;

import java.io.IOException;
import java.net.URI;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;

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
    Resource loadResource(@Nonnull URI uri) throws IOException;

    /**
     * Saves metadata of <code>Resource</code>.
     * 
     * @param resource
     * @throws IOException 
     */
    void saveResource(@Nonnull Resource resource) throws IOException;
    
    /**
     * Removes metadata of <code>Resource</code>.
     * @param uri
     * @throws IOException  
     */
    void deleteResource(@Nonnull URI uri) throws IOException;
    
    /**
     * Moves (reassignes) the <code>Resource</code> to the artifact with the
     * given URI.
     * 
     * PENDING will be needed?
     * 
     * @param resource
     * @param artifact URI of artifact.
     * @return 
     */
    Resource moveResource(Resource resource, URI artifact);
}
