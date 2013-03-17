package cz.zcu.kiv.crce.metadata.dao;

import java.io.IOException;

import org.osgi.service.cm.ManagedService;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.plugin.AbstractPlugin;

/**
 * This class can be extended by implementations of <code>RepositoryDAO</code>.
 * It provides stub methods only.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public abstract class AbstractRepositoryDAO extends AbstractPlugin implements RepositoryDAO, ManagedService {

    @Override
    public void saveRepository(Repository repository) throws IOException {
        // do nothing
    }
}
