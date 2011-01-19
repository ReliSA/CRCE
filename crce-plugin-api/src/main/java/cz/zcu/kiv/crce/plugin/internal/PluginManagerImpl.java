package cz.zcu.kiv.crce.plugin.internal;

import cz.zcu.kiv.crce.plugin.ResourceDAO;
import cz.zcu.kiv.crce.plugin.ResourceDAOFactory;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import java.net.URI;

/**
 *
 * @author kalwi
 */
public class PluginManagerImpl implements PluginManager {

    public PluginManagerImpl() {
        System.out.println("plugin manager started\n");
    }
    
    @Override
    public Plugin[] getPlugins() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Callback method called on adding new plugin.
     * @param plugin 
     */
    public void add(Plugin plugin) {
        System.out.println("\n*** some plugin added ***\n");
        if (plugin instanceof ResourceDAO) {
            add((ResourceDAO) plugin);
            return;
        }
        if (plugin instanceof ResourceDAOFactory) {
            add((ResourceDAOFactory) plugin);
            return;
        }
    }
    
    public void add(ResourceDAO plugin) {
        System.out.println("\nResourceDAO added: " + plugin.getClass().getName() + " " + plugin.getPluginId());
        System.out.println("instance of Plugin: " + (plugin instanceof Plugin));
        System.out.println("instance of ResourceDAO: " + (plugin instanceof ResourceDAO));
        System.out.println("instance of ResourceDAOFactory: " + (plugin instanceof ResourceDAOFactory));
    }
    
    public void add(ResourceDAOFactory plugin) {
        System.out.println("\nResourceDAOFactory added: " + plugin.getClass().getName() + " " + plugin.getPluginId());
        System.out.println("instance of Plugin: " + (plugin instanceof Plugin));
        System.out.println("instance of ResourceDAO: " + (plugin instanceof ResourceDAO));
        System.out.println("instance of ResourceDAOFactory: " + (plugin instanceof ResourceDAOFactory));
    }
    
    /**
     * Callback method called on removing existing plugin.
     * @param plugin 
     */
    public void remove(Plugin plugin) {
        if (plugin instanceof ResourceDAO) {
            remove((ResourceDAO) plugin);
            return;
        }
        if (plugin instanceof ResourceDAOFactory) {
            remove((ResourceDAOFactory) plugin);
            return;
        }
    }
    
    public void remove(ResourceDAO plugin) {
        System.out.println("\nResourceDAO removed: " + plugin.getClass().getName() + " " + plugin.getPluginId());
    }

    public void remove(ResourceDAOFactory plugin) {
        System.out.println("\nResourceDAOFactory removed: " + plugin.getClass().getName() + " " + plugin.getPluginId());
    }

    @Override
    public ResourceDAO getResourceDAO(URI baseUri) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
