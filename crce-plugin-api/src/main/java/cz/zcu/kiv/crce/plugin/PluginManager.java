package cz.zcu.kiv.crce.plugin;

import java.net.URI;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface PluginManager {

    /**
     * Returns all registered plugins.
     * @return an array containing all registered plugins.
     */
    Plugin[] getPlugins();

    /**
     * Returns an instance of <code>ResourceDAO</code> for specified base URI,
     * e.g. path to root folder of repository.
     * @param baseUri
     * @return 
     */
    ResourceDAO getResourceDAO(URI baseUri);

    /**
     * Returns all <code>ResourceIndexer</code>s which can index resource tagged
     * with given category.
     * @param category
     * @return an array of <code>ResourceIndexer</code>s for given category.
     */
    ResourceIndexer[] getResourceIndexers(String category);
    
    /**
     * Returns all registered <code>ResourceIndexer</code>s.
     * @return an array containing all registered resource indexers.
     */
    ResourceIndexer[] getResourceIndexers();
    
}
