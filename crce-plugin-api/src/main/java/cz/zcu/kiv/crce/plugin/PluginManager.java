package cz.zcu.kiv.crce.plugin;

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
     * Returns all registered instances of <code>ResourceDAO</code>.
     * @return an array containing all registered instances of
     * <code>ResourceDAO</code>.
     */
    ResourceDAO[] getResourceDAOs();
    
    /**
     * Returns an instance of <code>ResourceDAO</code> for specified for the
     * specified URI scheme. If no scheme is specified (zero-length string or
     * <code>null</code>) then implementation supporting installed URL protocol
     * handlers is returned.
     * 
     * <p> If more implementations of <code>ResourceDAO</code> is present for
     * specified scheme then the one with highest priority is returned.
     * 
     * @param scheme URI scheme.
     * @return 
     */
    ResourceDAO getResourceDAO();

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
