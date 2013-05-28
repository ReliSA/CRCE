package cz.zcu.kiv.crce.metadata.dao;

import java.io.IOException;
import java.net.URI;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import cz.zcu.kiv.crce.metadata.Repository;

/**
 * This type of plugin defines DAO class for reading and storing the metadata of
 * a repository.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface RepositoryDAO {

    /**
     * Reads metadata of resources stored in repository on the given URI.
     * @param uri Path to repository or a repository identificator.
     * @return Writable
     * @throws IOException
     */
    @CheckForNull
    Repository loadRepository(@Nonnull URI uri) throws IOException;

    /**
     * Stores metadata of resources stored in the given repository. Typical
     * target is a repository.xml in the root folder of the repository.
     * @param repository
     * @throws IOException
     */
    void saveRepository(@Nonnull Repository repository) throws IOException;
}
