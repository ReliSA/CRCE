package cz.zcu.kiv.crce.metadata.indexer;

import cz.zcu.kiv.crce.plugin.AbstractPlugin;

import java.util.Collections;
import java.util.List;

/**
 * Abstract implementation of <code>ResourceIndexer</code> which can be extended
 * by other implementations.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public abstract class AbstractResourceIndexer extends AbstractPlugin implements ResourceIndexer {

    @Override
    public List<String> getProvidedCategories() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getRequiredCategories() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getPluginKeywords() {
        return getRequiredCategories();
    }

    @Override
    public String getPluginDescription() {
        return "ResourceIndexer plugin implementation";
    }

}
