package cz.zcu.kiv.crce.plugin;

import java.net.URI;

/**
 *
 * @author kalwi
 */
public interface PluginManager {

    Plugin[] getPlugins();

    ResourceDAO getResourceDAO(URI baseUri);
    
}
