package cz.zcu.kiv.crce.metadata.combined.internal;

import cz.zcu.kiv.crce.plugin.ResourceDAO;
import cz.zcu.kiv.crce.plugin.ResourceDAOFactory;
import cz.zcu.kiv.crce.plugin.Plugin;
import java.net.URI;

/**
 *
 * @author kalwi
 */
public class CombinedResourceDAOFactory implements ResourceDAOFactory, Plugin {

    private ResourceDAO m_staticDAO; // TODO = new StaticResourceCreator();
    private ResourceDAO m_writableDAO; // TODO = new MetafileResourceCreator();
    
    @Override
    public ResourceDAO getResourceDAO(URI baseUri) {
        // TODO baseUri
        ResourceDAO combinedCreator = new CombinedResourceDAO(m_staticDAO, m_writableDAO);
        return combinedCreator;
    }

    @Override
    public String getName() {
        return "Combined resource creator factory";
    }

    @Override
    public String getPluginId() {
        return "combined";
    }

    @Override
    public int getPluginPriority() {
        return 10;
    }

}
