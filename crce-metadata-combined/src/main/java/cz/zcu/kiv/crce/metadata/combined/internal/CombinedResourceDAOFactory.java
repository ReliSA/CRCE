package cz.zcu.kiv.crce.metadata.combined.internal;

import cz.zcu.kiv.crce.plugin.stub.AbstractPlugin;
import cz.zcu.kiv.crce.plugin.ResourceDAO;
import cz.zcu.kiv.crce.plugin.ResourceDAOFactory;
import java.net.URI;

/**
 *
 * @author kalwi
 */
public class CombinedResourceDAOFactory extends AbstractPlugin implements ResourceDAOFactory {

    private ResourceDAO m_staticDAO; // TODO = new StaticResourceCreator();
    private ResourceDAO m_writableDAO; // TODO = new MetafileResourceCreator();
    
    @Override
    public ResourceDAO getResourceDAO(URI baseUri) {
        // TODO baseUri
        ResourceDAO combinedCreator = new CombinedResourceDAO(m_staticDAO, m_writableDAO);
        return combinedCreator;
    }

    @Override
    public int getPluginPriority() {
        return 10;
    }

}
