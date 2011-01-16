package cz.zcu.kiv.crce.plugin.internal;

import cz.zcu.kiv.crce.metadata.ResourceDAO;
import cz.zcu.kiv.crce.metadata.ResourceDAOFactory;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;

/**
 *
 * @author kalwi
 */
public class PluginManagerImpl implements PluginManager {

    @Override
    public Plugin[] getPlugins() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Callback method called on adding new plugin.
     * @param plugin 
     */
    public void added(Plugin plugin) {
        System.out.println("\nplugin added: " + plugin.getClass().getName() + " " + plugin.getName());
        System.out.println("instance of factory: " + (plugin instanceof ResourceDAOFactory));
        System.out.println("");
    }
    
    /**
     * Callback method called on removing existing plugin.
     * @param plugin 
     */
    public void removed(Plugin plugin) {
        System.out.println("\nplugin removed: " + plugin.getClass().getName() + " " + plugin.getName());
        System.out.println("");
    }

    @Override
    public ResourceDAO getResourceDAO() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
