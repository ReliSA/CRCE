package cz.zcu.kiv.crce.metadata.dao;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.WritableRepository;
import cz.zcu.kiv.crce.plugin.AbstractPlugin;
import java.io.IOException;
import java.net.URI;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public abstract class AbstractRepositoryDAO extends AbstractPlugin implements RepositoryDAO {

    @Override
    public abstract WritableRepository getRepository(URI uri) throws IOException;

    @Override
    public abstract void saveRepository(Repository repository) throws IOException;
}
