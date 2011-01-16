package cz.zcu.kiv.crce.metadata.combined.internal;

import cz.zcu.kiv.crce.metadata.ResourceDAO;
import cz.zcu.kiv.crce.metadata.ResourceDAOFactory;
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

}
