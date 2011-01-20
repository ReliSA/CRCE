package cz.zcu.kiv.crce.plugin.internal;

import cz.zcu.kiv.crce.plugin.ResourceDAO;
import cz.zcu.kiv.crce.plugin.ResourceDAOFactory;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.plugin.ResourceIndexer;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.osgi.service.log.LogService;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class PluginManagerImpl implements PluginManager {

    private volatile LogService m_log; /* injected by dependency manager */

    private final List<ResourceDAO> m_resourceDAOs = new ArrayList<ResourceDAO>();
    private final List<ResourceDAOFactory> m_resourceDAOFactories = new ArrayList<ResourceDAOFactory>();
    private final List<ResourceIndexer> m_resourcesIndexers = new ArrayList<ResourceIndexer>();
    private final Map<String, List<ResourceIndexer>> m_resourceIndexersMap = new HashMap<String, List<ResourceIndexer>>();
    private final List<Plugin> m_plugins = new ArrayList<Plugin>();

    @Override
    public synchronized Plugin[] getPlugins() {
        return m_plugins.toArray(new Plugin[m_plugins.size()]);
    }

    @Override
    public synchronized ResourceDAO getResourceDAO(URI baseUri) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public synchronized ResourceIndexer[] getResourceIndexers() {
        return m_resourcesIndexers.toArray(new ResourceIndexer[m_resourcesIndexers.size()]);
    }

    @Override
    public ResourceIndexer[] getResourceIndexers(String category) {
        List<ResourceIndexer> list = m_resourceIndexersMap.get(category != null ? category : "");
        if (list != null) {
            return list.toArray(new ResourceIndexer[list.size()]);
        } else {
            return new ResourceIndexer[0];
        }
        
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
            List<ResourceIndexer> list = m_resourceIndexersMap.get(req);
            if (list == null) {
                list = new ArrayList<ResourceIndexer>();
                m_resourceIndexersMap.put(req, list);
            }
            list.add(indexer);
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
            List<ResourceIndexer> list = m_resourceIndexersMap.get(req);
            if (list != null) {
                list.remove(indexer);
                if (list.isEmpty()) {
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
    public synchronized void add(Plugin plugin) {
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
    }

    public synchronized void add(ResourceDAO plugin) {
        m_resourceDAOs.add(plugin);
        m_plugins.add(plugin);
        m_log.log(LogService.LOG_INFO, "ResourceDAO plugin registered: " + plugin.getPluginId());
    }

    public synchronized void add(ResourceDAOFactory plugin) {
        m_resourceDAOFactories.add(plugin);
        m_plugins.add(plugin);
        m_log.log(LogService.LOG_INFO, "ResourceDAOFactory plugin registered: " + plugin.getPluginId());
    }

    public synchronized void add(ResourceIndexer plugin) {
        m_resourcesIndexers.add(plugin);
        m_plugins.add(plugin);
        addResourceIndexer(plugin);
        m_log.log(LogService.LOG_INFO, "ResourceIndexer plugin registered: " + plugin.getPluginId());
    }

    /**
     * Callback method called on removing existing plugin.
     * @param plugin 
     */
    public synchronized void remove(Plugin plugin) {
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
    }

    public synchronized void remove(ResourceDAO plugin) {
        m_resourceDAOs.remove(plugin);
        m_plugins.add(plugin);
        m_log.log(LogService.LOG_INFO, "ResourceDAO plugin unregistered: " + plugin.getPluginId());
    }

    public synchronized void remove(ResourceDAOFactory plugin) {
        m_resourceDAOFactories.remove(plugin);
        m_plugins.add(plugin);
        m_log.log(LogService.LOG_INFO, "ResourceDAOFactory plugin unregistered: " + plugin.getPluginId());
    }

    public synchronized void remove(ResourceIndexer plugin) {
        m_resourcesIndexers.remove(plugin);
        m_plugins.add(plugin);
        removeResourceIndexer(plugin);
        m_log.log(LogService.LOG_INFO, "ResourceIndexer plugin unregistered: " + plugin.getPluginId());
    }
}
