package cz.zcu.kiv.crce.metadata.indexer.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.stub.AbstractResourceDAO;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.metadata.indexer.ResourceIndexer;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class FileIndexingResourceDAO extends AbstractResourceDAO {

    private volatile PluginManager m_pluginManager;

    private final Set<ResourceIndexer> m_resourcesIndexers = new TreeSet<ResourceIndexer>();
    private final Map<String, Set<ResourceIndexer>> m_resourceIndexersMap = new HashMap<String, Set<ResourceIndexer>>();
    
    @Override
    public Resource getResource(URI uri) throws IOException {
        // TODO optimize via getAllResourceIndexers(category)

        URL url = uri.toURL();
        
        ResourceIndexer[] indexers = getAllResourceIndexers();

        if (indexers.length == 0) {
            return null;
        }

        Resource resource = indexers[0].index(url.openStream());

        if (indexers.length > 1) {
            for (int i = 1; i < indexers.length; i++) {
                resource = indexers[i].index(url.openStream(), resource);
            }
        }
        
        if ("file".equals(url.getProtocol())) {
            File f = new File(uri);
            resource.setSize(f.length());
        }
        
        resource.setUri(uri.normalize());
        
        resource.unsetWritable();
        return resource;

    }

    @Override
    public void save(Resource resource) throws IOException {
        // do nothing
    }

    @Override
    public void copy(Resource resource, URI uri) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getPluginPriority() {
        return 10;
    }
    
    /**
     * Returns all registered <code>ResourceIndexer</code>s ordered by their
     * priority.
     * @return an array containing all registered resource indexers.
     */
    public synchronized ResourceIndexer[] getAllResourceIndexers() {
        return m_resourcesIndexers.toArray(new ResourceIndexer[m_resourcesIndexers.size()]);
    }
    
    
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
    public synchronized ResourceIndexer[] getResourceIndexers(String category) {
        Set<ResourceIndexer> set = m_resourceIndexersMap.get(category != null ? category : "");
        if (set != null) {
            return set.toArray(new ResourceIndexer[set.size()]);
        } else {
            return new ResourceIndexer[0];
        }
    }
    
    synchronized void add(ResourceIndexer plugin) {
        m_resourcesIndexers.add(plugin);
//        m_plugins.add(plugin);
        addResourceIndexer(plugin);
//        m_log.log(LogService.LOG_INFO, "ResourceIndexer plugin registered: " + plugin.getPluginId());
    }
    
    synchronized void remove(ResourceIndexer plugin) {
        m_resourcesIndexers.remove(plugin);
//        m_plugins.remove(plugin);
        removeResourceIndexer(plugin);
//        m_log.log(LogService.LOG_INFO, "ResourceIndexer plugin unregistered: " + plugin.getPluginId());
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
    

    
    
}
