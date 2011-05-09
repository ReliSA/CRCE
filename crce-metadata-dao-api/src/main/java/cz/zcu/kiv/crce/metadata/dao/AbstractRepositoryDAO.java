package cz.zcu.kiv.crce.metadata.dao;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.plugin.AbstractPlugin;
import java.io.IOException;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public abstract class AbstractRepositoryDAO extends AbstractPlugin implements RepositoryDAO {

    @Override
    public void saveRepository(Repository repository) throws IOException {
        // do nothing
    }
}
