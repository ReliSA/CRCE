package cz.zcu.kiv.crce.metadata.combined.internal;

import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.plugin.ResourceDAO;
import cz.zcu.kiv.crce.plugin.ResourceDAOFactory;
import cz.zcu.kiv.crce.plugin.AbstractPlugin;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class CombinedResourceDAOFactory extends AbstractPlugin implements ResourceDAOFactory {

    private volatile PluginManager m_pluginManager;

    @Override
    public ResourceDAO getResourceDAO() {
        ResourceDAO[] daos = m_pluginManager.getPlugins(ResourceDAO.class);

        if (daos.length < 1) {
            throw new IllegalStateException("No registered ResourceDAOs to create CombinedResourceDAO.");
        }

        if (daos.length == 1) {
            return daos[0];
        }

        return new CombinedResourceDAO(daos[0], daos[1]);
    }

    @Override
    public int getPluginPriority() {
        return 10;  // TODO configure
    }
}
