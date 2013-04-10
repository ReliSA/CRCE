package cz.zcu.kiv.crce.metadata.dao;

import java.io.IOException;
import java.net.URI;

import cz.zcu.kiv.crce.metadata.Resource;

/**
 * This class can be extended by implementations of <code>ResourceDAO</code>.
 * It provides stub methods only.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public abstract class AbstractResourceDAO implements ResourceDAO {

    @Override
    public void saveResource(Resource resource) throws IOException {
        // do nothing
    }

    @Override
    public void deleteResource(URI uri) throws IOException {
        // do nothing
    }
}
