package cz.zcu.kiv.crce.metadata.dao;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 * Plugin implementing this class manages retrieving and storing metadata of an
 * artifact.
 *
 * <p> Typical usage of <code>ResourceDAO</code> plugin is in repository core to
 * manipulate and manage metadata resource while uploading artifacts, retrieving
 * them, copying etc.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface ResourceDAO {

    /**
     * Returns <code>Resource</code> object for the given resource. Returns
     * <code>null</code> if <code>Resource</code> object can not be created
     * (e.g. resource not exists).
     * @param uri URI
     * @return
     * @throws IOException
     */
    @CheckForNull
    Resource loadResource(@Nonnull URI uri) throws IOException;

    @Nonnull
    List<Resource> loadResources(@Nonnull Repository repository) throws IOException;

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

    boolean existsResource(@Nonnull URI uri) throws IOException;

    boolean existsResource(@Nonnull URI uri, @Nonnull Repository repository) throws IOException;
}
