package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.plugin.AbstractPlugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.plugins.ResourceDAO;
import cz.zcu.kiv.crce.repository.plugins.ResourceDAOFactory;

/**
 * Simple <code>ResourceDAOFactory</code> implementation which returns default
 * implementation of <code>ResourceDAO</code>.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class DefaultResourceDAOFactory extends AbstractPlugin implements ResourceDAOFactory {

    private volatile PluginManager m_pluginManager; /* injected by dependency manager */
    
    @Override
    public ResourceDAO getResourceDAO() {
        return m_pluginManager.getPlugin(ResourceDAO.class);
    }
}
