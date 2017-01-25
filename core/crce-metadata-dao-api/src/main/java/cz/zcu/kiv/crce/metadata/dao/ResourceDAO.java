package cz.zcu.kiv.crce.metadata.dao;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.filter.ResourceDAOFilter;

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
@ParametersAreNonnullByDefault
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
    Resource loadResource(URI uri) throws IOException;

    @Nonnull
    List<Resource> loadResources(Repository repository) throws IOException;

    /**
     * Loads all resources in the given repository and whose capabilities match the given filter (at least one capability must match).
     * @param repository
     * @param filter
     * @return
     * @throws IOException
     */
    @Nonnull
    List<Resource> loadResources(Repository repository, ResourceDAOFilter filter) throws IOException;

    /**
     * Saves metadata of <code>Resource</code>.
     *
     * @param resource
     * @throws IOException
     */
    void saveResource(Resource resource) throws IOException;

    /**
     * Removes metadata of <code>Resource</code>.
     * @param uri
     * @throws IOException
     */
    void deleteResource(URI uri) throws IOException;

    boolean existsResource(URI uri) throws IOException;

    boolean existsResource(URI uri, Repository repository) throws IOException;
}
