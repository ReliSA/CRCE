package cz.zcu.kiv.crce.metadata.dao;

import java.io.IOException;
import java.net.URI;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.WritableRepository;
import cz.zcu.kiv.crce.plugin.Plugin;

/**
 * This type of plugin defines DAO class for reading and storing the metadata of
 * a repository.
 * 
 * PENDING this interface probably won't be needed.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public interface RepositoryDAO extends Plugin {

    /**
     * Reads metadata of resources stored in repository on the given URI.
     * @param uri Path to repository or a repository identificator.
     * @return Writable 
     * @throws IOException 
     */
    WritableRepository getRepository(URI uri) throws IOException;
    
    /**
     * Stores metadata of resources stored in the given repository. Typical
     * target is a repository.xml in the root folder of the repository.
     * @param repository
     * @throws IOException 
     */
    void saveRepository(Repository repository) throws IOException;
}
