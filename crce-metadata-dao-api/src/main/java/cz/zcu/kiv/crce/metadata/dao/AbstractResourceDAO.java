package cz.zcu.kiv.crce.metadata.dao;

import cz.zcu.kiv.crce.plugin.AbstractPlugin;
import cz.zcu.kiv.crce.metadata.Resource;
import java.io.IOException;
import org.osgi.service.cm.ManagedService;

/**
 * This class can be extended by implementations of <code>ResourceDAO</code>.
 * It provides stub methods only.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public abstract class AbstractResourceDAO extends AbstractPlugin implements ResourceDAO, ManagedService {

    @Override
    public void save(Resource resource) throws IOException {
        // do nothing
    }

    @Override
    public void remove(Resource resource) throws IOException {
        // do nothing
    }
}
