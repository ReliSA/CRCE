package cz.zcu.kiv.crce.plugin.internal;

import cz.zcu.kiv.crce.plugin.ActionHandler;
import cz.zcu.kiv.crce.plugin.ResourceDAO;
import cz.zcu.kiv.crce.plugin.ResourceDAOFactory;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.plugin.ResourceIndexer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.osgi.service.log.LogService;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class PluginManagerImpl implements PluginManager {

    private volatile LogService m_log; /* injected by dependency manager */

    private final Set<Plugin> m_plugins = new TreeSet<Plugin>();
    private final Set<ResourceDAO> m_resourceDAOs = new TreeSet<ResourceDAO>();
    private final Set<ResourceDAOFactory> m_resourceDAOFactories = new TreeSet<ResourceDAOFactory>();
    private final Set<ResourceIndexer> m_resourcesIndexers = new TreeSet<ResourceIndexer>();
    private final Set<ActionHandler> m_actionHandlers = new TreeSet<ActionHandler>();
    private final Map<String, Set<ResourceIndexer>> m_resourceIndexersMap = new HashMap<String, Set<ResourceIndexer>>();
    
    @Override
    public synchronized Plugin[] getAllPlugins() {
        return m_plugins.toArray(new Plugin[m_plugins.size()]);
    }

    @Override
    public ResourceDAO[] getAllResourceDAOs() {
        return m_resourceDAOs.toArray(new ResourceDAO[m_resourceDAOs.size()]);
    }
    
    @Override
    public synchronized ResourceIndexer[] getAllResourceIndexers() {
        return m_resourcesIndexers.toArray(new ResourceIndexer[m_resourcesIndexers.size()]);
    }

    @Override
    public ActionHandler[] getAllActionHandlers() {
        return m_actionHandlers.toArray(new ActionHandler[m_actionHandlers.size()]);
    }

    @Override
    public synchronized ResourceDAO getResourceDAO() {
        if (!m_resourceDAOFactories.isEmpty()) {
            ResourceDAOFactory f = m_resourceDAOFactories.iterator().next();
            return f.getResourceDAO();
        }
        if (!m_resourceDAOs.isEmpty()) {
            return m_resourceDAOs.iterator().next();
        }
        throw new IllegalStateException("No ResourceDAOs or ResourceDAOFactories installed");
    }

    @Override
    public synchronized ResourceIndexer[] getResourceIndexers(String category) {
        Set<ResourceIndexer> set = m_resourceIndexersMap.get(category != null ? category : "");
        if (set != null) {
            return set.toArray(new ResourceIndexer[set.size()]);
        } else {
            return new ResourceIndexer[0];
        }
    }
    
    @Override
    public synchronized ActionHandler getActionHandler() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Adds resource indexer to hierarchical structure of resource indexers.
     * @param indexer 
     */
    private void addResourceIndexer(ResourceIndexer indexer) {
        String[] reqs = indexer.getRequiredCategories();
        if (reqs.length == 0) {
            reqs = new String[]{""};
        }
        for (String req : reqs) {
            Set<ResourceIndexer> set = m_resourceIndexersMap.get(req);
            if (set == null) {
                set = new TreeSet<ResourceIndexer>();
                m_resourceIndexersMap.put(req, set);
            }
            set.add(indexer);
        }
    }

    /**
     * Removes resource indexer from hierarchical structure of resource indexers.
     * @param indexer 
     */
    private void removeResourceIndexer(ResourceIndexer indexer) {
        String[] reqs = indexer.getRequiredCategories();
        if (reqs.length == 0) {
            reqs = new String[]{""};
        }
        for (String req : reqs) {
            Set<ResourceIndexer> set = m_resourceIndexersMap.get(req);
            if (set != null) {
                set.remove(indexer);
                if (set.isEmpty()) {
                    m_resourceIndexersMap.remove(req);
                }
            }
        }
    }
    
    /*
     * ================
     * CALLBACK METHODS
     * ================
     */
    
    /**
     * Callback method called on adding new plugin.
     * @param plugin 
     */
    synchronized void add(Plugin plugin) {
        if (plugin instanceof ResourceDAO) {
            add((ResourceDAO) plugin);
            return;
        }
        if (plugin instanceof ResourceDAOFactory) {
            add((ResourceDAOFactory) plugin);
            return;
        }
        if (plugin instanceof ResourceIndexer) {
            add((ResourceIndexer) plugin);
        }
        if (plugin instanceof ActionHandler) {
            add((ActionHandler) plugin);
        }
        m_log.log(LogService.LOG_ERROR, "Unknown plugin tried to be registered: " + plugin.getPluginId());
    }

    synchronized void add(ResourceDAO plugin) {
        m_resourceDAOs.add(plugin);
        m_plugins.add(plugin);
        m_log.log(LogService.LOG_INFO, "ResourceDAO plugin registered: " + plugin.getPluginId());
    }

    synchronized void add(ResourceDAOFactory plugin) {
        m_resourceDAOFactories.add(plugin);
        m_plugins.add(plugin);
        m_log.log(LogService.LOG_INFO, "ResourceDAOFactory plugin registered: " + plugin.getPluginId());
    }

    synchronized void add(ResourceIndexer plugin) {
        m_resourcesIndexers.add(plugin);
        m_plugins.add(plugin);
        addResourceIndexer(plugin);
        m_log.log(LogService.LOG_INFO, "ResourceIndexer plugin registered: " + plugin.getPluginId());
    }

    synchronized void add(ActionHandler plugin) {
        m_actionHandlers.add(plugin);
        m_plugins.add(plugin);
        m_log.log(LogService.LOG_INFO, "ActionHandler plugin registered: " + plugin.getPluginId());
    }
    
    /**
     * Callback method called on removing existing plugin.
     * @param plugin 
     */
    synchronized void remove(Plugin plugin) {
        if (plugin instanceof ResourceDAO) {
            remove((ResourceDAO) plugin);
            return;
        }
        if (plugin instanceof ResourceDAOFactory) {
            remove((ResourceDAOFactory) plugin);
            return;
        }
        if (plugin instanceof ResourceIndexer) {
            remove((ResourceIndexer) plugin);
        }
        if (plugin instanceof ActionHandler) {
            remove((ActionHandler) plugin);
        }
    }

    synchronized void remove(ResourceDAO plugin) {
        m_resourceDAOs.remove(plugin);
        m_plugins.remove(plugin);
        m_log.log(LogService.LOG_INFO, "ResourceDAO plugin unregistered: " + plugin.getPluginId());
    }

    synchronized void remove(ResourceDAOFactory plugin) {
        m_resourceDAOFactories.remove(plugin);
        m_plugins.remove(plugin);
        m_log.log(LogService.LOG_INFO, "ResourceDAOFactory plugin unregistered: " + plugin.getPluginId());
    }

    synchronized void remove(ResourceIndexer plugin) {
        m_resourcesIndexers.remove(plugin);
        m_plugins.remove(plugin);
        removeResourceIndexer(plugin);
        m_log.log(LogService.LOG_INFO, "ResourceIndexer plugin unregistered: " + plugin.getPluginId());
    }

    synchronized void remove(ActionHandler plugin) {
        m_actionHandlers.remove(plugin);
        m_plugins.remove(plugin);
        m_log.log(LogService.LOG_INFO, "ActionHandler plugin unregistered: " + plugin.getPluginId());
    }
}
