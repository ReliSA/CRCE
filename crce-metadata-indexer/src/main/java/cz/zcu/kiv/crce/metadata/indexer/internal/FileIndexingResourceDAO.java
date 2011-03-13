package cz.zcu.kiv.crce.metadata.indexer.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.repository.plugins.AbstractResourceDAO;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.metadata.indexer.ResourceIndexer;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class FileIndexingResourceDAO extends AbstractResourceDAO {

    private volatile PluginManager m_pluginManager;
    private volatile ResourceCreator m_resourceCreator; /* injected by dependency manager */


    @Override
    public Resource getResource(URI uri) throws IOException {
        Resource resource = m_resourceCreator.createResource();

        URL url = uri.toURL();
        
        // TODO - may be optimized for remote protocols by copying resource to local file and opening local url

        Set<String> usedKeywords = new HashSet<String>();
        Set<String> keywords = new HashSet<String>();
        Set<ResourceIndexer> usedIndexers = new HashSet<ResourceIndexer>();

        keywords.add(PluginManager.NO_KEYWORDS);

        while (!keywords.isEmpty()) {
            for (String keyword : keywords.toArray(new String[0])) {
                keywords.remove(keyword);
                if (usedKeywords.contains(keyword)) {
                    continue;
                }
                ResourceIndexer[] indexers = m_pluginManager.getPlugins(ResourceIndexer.class, keyword);
                for (ResourceIndexer indexer : indexers) {
                    if (usedIndexers.contains(indexer)) {
                        continue;
                    }
                    String[] newKeywords = indexer.index(url.openStream(), resource);
                    keywords.addAll(Arrays.asList(newKeywords));
                    usedIndexers.add(indexer);
                }
                usedKeywords.add(keyword);
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
    public Resource moveResource(Resource resource, URI uri) {
        Resource out = m_resourceCreator.createResource(resource);
        out.setUri(uri);
        out.unsetWritable();
        return out;
    }
    
    @Override
    public void save(Resource resource) throws IOException {
        // do nothing
    }

    @Override
    public void remove(Resource resource) throws IOException {
        // do nothing
        // TODO do nothing or delete resource file?
    }

    @Override
    public int getPluginPriority() {
        return 10;
    }
}
