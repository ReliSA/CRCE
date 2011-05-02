package cz.zcu.kiv.crce.metadata.dao;

import cz.zcu.kiv.crce.plugin.AbstractPlugin;
import cz.zcu.kiv.crce.metadata.Resource;
import java.io.IOException;
import java.net.URI;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public abstract class AbstractResourceDAO extends AbstractPlugin implements ResourceDAO {

    @Override
    abstract public Resource getResource(URI uri) throws IOException;

    @Override
    public void save(Resource resource) throws IOException {
        // do nothing
    }

    @Override
    public void remove(Resource resource) throws IOException {
        // do nothing
    }
}
