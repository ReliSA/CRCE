package cz.zcu.kiv.crce.metadata.dao;

import java.io.IOException;
import java.net.URI;

import javax.annotation.CheckForNull;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.zcu.kiv.crce.metadata.Repository;

/**
 * This type of plugin defines DAO class for reading and storing the metadata of
 * a repository.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface RepositoryDAO {

    /**
     * Reads metadata of resources stored in repository on the given URI.
     * @param uri Path to repository or a repository identificator.
     * @return Loaded repository or null if such repository doesn't exist.
     * @throws IOException
     */
    @CheckForNull
    Repository loadRepository(URI uri) throws IOException;

    /**
     * Deletes existing repository (if it exists) including all contained resources.
     * @param repository
     * @throws IOException
     */
    void deleteRepository(Repository repository) throws IOException;

    /**
     * Stores metadata of resources stored in the given repository. Typical
     * target is a repository.xml in the root folder of the repository.
     * @param repository
     * @throws IOException
     */
    void saveRepository(Repository repository) throws IOException;
}
