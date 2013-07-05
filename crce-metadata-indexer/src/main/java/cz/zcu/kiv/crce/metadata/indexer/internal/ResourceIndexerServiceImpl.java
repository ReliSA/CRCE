package cz.zcu.kiv.crce.metadata.indexer.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.indexer.ResourceIndexer;
import cz.zcu.kiv.crce.metadata.indexer.ResourceIndexerService;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.plugin.PluginManager;

/**
 * Implementation of <code>ResourceDAO</code> which provides indexing of artifacts
 * with indexers to a static (read-only) metadata representation.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class ResourceIndexerServiceImpl implements ResourceIndexerService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceIndexerServiceImpl.class);

    private volatile PluginManager pluginManager;
    private volatile ResourceFactory resourceFactory; /* injected by dependency manager */
    private volatile MetadataService metadataService;

    @Override
    public Resource indexResource(File file) throws IOException {
        logger.debug("Indexing file {}", file);

        Resource resource = resourceFactory.createResource();

        // TODO - may be optimized for remote protocols by copying resource to local file and opening local url

        Set<String> usedKeywords = new HashSet<>();
        Set<String> keywords = new HashSet<>();
        Set<String> newKeywords = new HashSet<>();
        Set<ResourceIndexer> usedIndexers = new HashSet<>();

        keywords.add(PluginManager.NO_KEYWORDS);

        while (!keywords.isEmpty()) {
            Iterator<String> it = keywords.iterator();
            while (it.hasNext()) {
                String keyword = it.next();
                it.remove();
                if (usedKeywords.contains(keyword)) {
                    continue;
                }
                List<ResourceIndexer> indexers = pluginManager.getPlugins(ResourceIndexer.class, keyword);
                for (ResourceIndexer indexer : indexers) {
                    if (usedIndexers.contains(indexer)) {
                        continue;
                    }
                    try (FileInputStream fis = new FileInputStream(file)) {
                        if (logger.isTraceEnabled()) {
                            logger.trace("Using indexer: {}", indexer.getPluginId());
                        }
                        newKeywords.addAll(indexer.index(fis, resource));
//                        List<String> newKeywords = indexer.index(fis, resource);
//                        keywords.addAll(newKeywords);
                    }
                    usedIndexers.add(indexer);
                }
                usedKeywords.add(keyword);
            }
            keywords.addAll(newKeywords);
            newKeywords = new HashSet<>();
        }

        metadataService.setSize(resource, file.length());

        metadataService.setUri(resource, file.toURI().normalize()); // TODO move to Store logic

        return resource;
    }
}
