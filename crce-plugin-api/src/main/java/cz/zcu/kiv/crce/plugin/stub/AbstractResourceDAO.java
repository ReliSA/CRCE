package cz.zcu.kiv.crce.plugin.stub;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.ResourceDAO;
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
    public void copy(Resource resource, URI uri) throws IOException {
        // do nothing
    }
}
