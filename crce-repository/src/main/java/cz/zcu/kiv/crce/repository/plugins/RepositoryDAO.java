package cz.zcu.kiv.crce.repository.plugins;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.WritableRepository;
import cz.zcu.kiv.crce.plugin.Plugin;
import java.io.IOException;
import java.net.URI;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface RepositoryDAO extends Plugin {

    WritableRepository getRepository(URI uri) throws IOException;
    
    void saveRepository(Repository repository) throws IOException;
}
