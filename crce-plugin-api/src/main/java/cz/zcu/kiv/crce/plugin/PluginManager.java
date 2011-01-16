package cz.zcu.kiv.crce.plugin;

import cz.zcu.kiv.crce.metadata.ResourceDAO;

/**
 *
 * @author kalwi
 */
public interface PluginManager {
    Plugin[] getPlugins();
    
    ResourceDAO getResourceDAO();
    
    
}
