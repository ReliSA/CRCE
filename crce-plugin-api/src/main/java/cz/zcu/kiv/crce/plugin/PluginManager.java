package cz.zcu.kiv.crce.plugin;

/**
 * Plugin manager is responsible to register, unregister and provide plugins.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface PluginManager {

    /**
     * Returns all registered plugins ordered by their priority.
     * @return an array containing all registered plugins.
     */
    Plugin[] getAllPlugins();

    /**
     * Returns all registered instances of <code>ResourceDAO</code> ordered by
     * their priority.
     * 
     * <p><i>This method should not be used to obtain preffered instance of
     * <code>ResourceDAO</code> using zero index of returned array, because
     * the most preffered implementation may be instantiated by a registered
     * <code>ResourceDAOFactory</code> plugin. So use rather method
     * <code>getResourceDAO()</code> for this purpose.
     * 
     * @return an array containing all registered instances of
     * <code>ResourceDAO</code>.
     */
    ResourceDAO[] getAllResourceDAOs();
    
    /**
     * Returns all registered <code>ResourceIndexer</code>s ordered by their
     * priority.
     * @return an array containing all registered resource indexers.
     */
    ResourceIndexer[] getAllResourceIndexers();

    /**
     * Returns all registered <code>ActionHandler</code>s ordered by their
     * priority.
     * @return array of all registered action handlers.
     */
    ActionHandler[] getAllActionHandlers();
    
    /**
     * Returns an instance of preferred <code>ResourceDAO</code> implementation.
     * 
     * <p> If more implementations of <code>ResourceDAO</code> are present then
     * the one with hthe ighest priority is returned.
     * 
     * @return 
     */
    ResourceDAO getResourceDAO();

    /**
     * Returns all <code>ResourceIndexer</code>s which can be used to index
     * a resource tagged with given category.
     * 
     * <p> Returned indexers are ordered by their priority from the highest
     * to the lowest.
     * 
     * @param category
     * @return an array of <code>ResourceIndexer</code>s for given category.
     */
    ResourceIndexer[] getResourceIndexers(String category);
    
    /**
     * Returns an instance of preferred <code>ActionHandler</code> implementation.
     * 
     * <p> If more implementations of <code>ActionHandler</code> are present
     * then the one with the highest priority is returned.
     * 
     * @return 
     */
    ActionHandler getActionHandler();
}
