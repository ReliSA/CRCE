package cz.zcu.kiv.crce.metadata.indexer.internal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.legacy.LegacyMetadataHelper;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.dao.AbstractResourceDAO;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.metadata.indexer.ResourceIndexer;
import cz.zcu.kiv.crce.plugin.Plugin;

/**
 * Implementation of <code>ResourceDAO</code> which provides indexing of artifacts
 * with indexers to a static (read-only) metadata representation.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class FileIndexingResourceDAO extends AbstractResourceDAO implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(FileIndexingResourceDAO.class);

    private volatile PluginManager pluginManager;
    private volatile ResourceFactory resourceFactory; /* injected by dependency manager */

    private static final int PLUGIN_PRIORITY = 100;

    @Override
    public Resource loadResource(URI uri) throws IOException {
        Resource resource = resourceFactory.createResource();

        URL url = uri.toURL();

        // TODO - may be optimized for remote protocols by copying resource to local file and opening local url

        Set<String> usedKeywords = new HashSet<>();
        Set<String> keywords = new HashSet<>();
        Set<ResourceIndexer> usedIndexers = new HashSet<>();

        keywords.add(PluginManager.NO_KEYWORDS);

        while (!keywords.isEmpty()) {
            for (String keyword : keywords) {
                keywords.remove(keyword);
                if (usedKeywords.contains(keyword)) {
                    continue;
                }
                List<ResourceIndexer> indexers = pluginManager.getPlugins(ResourceIndexer.class, keyword);
                for (ResourceIndexer indexer : indexers) {
                    if (usedIndexers.contains(indexer)) {
                        continue;
                    }
                    List<String> newKeywords = indexer.index(url.openStream(), resource);
                    keywords.addAll(newKeywords);
                    usedIndexers.add(indexer);
                }
                usedKeywords.add(keyword);
            }
        }

        if ("file".equals(url.getProtocol())) {
            File f = new File(uri);
            LegacyMetadataHelper.setSize(resourceFactory, resource, f.length());
        }

        LegacyMetadataHelper.setUri(resourceFactory, resource, uri);

        return resource;

    }

    @Override
    public Resource moveResource(Resource resource, URI uri) {
        Resource out = resourceFactory.cloneResource(resource);
        LegacyMetadataHelper.setUri(resourceFactory, out, uri);
        return out;
    }

    @Override
    public int getPluginPriority() {
        return PLUGIN_PRIORITY;
    }

    @Override
    public List<Resource> loadResources(Repository repository) throws IOException {
        logger.warn("Method loadResources is not planned for resource indexer after a refactoring, returning empty list.");
        return Collections.emptyList();
    }

    @Override
    public boolean existsResource(URI uri) throws IOException {
        logger.warn("Method existsResource is not planned for resource indexer after a refactoring, returning false.");
        return false;
    }
}
