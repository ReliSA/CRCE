package cz.zcu.kiv.crce.metadata.indexer;

import cz.zcu.kiv.crce.plugin.AbstractPlugin;

/**
 * Abstract implementation of <code>ResourceIndexer</code> which can be extended
 * by other implementations.
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public abstract class AbstractResourceIndexer extends AbstractPlugin implements ResourceIndexer {

    @Override
    public String[] getProvidedCategories() {
        return new String[0];
    }

    @Override
    public String[] getRequiredCategories() {
        return new String[0];
    }

    @Override
    public String[] getPluginKeywords() {
        return getRequiredCategories();
    }
    
    @Override
    public String getPluginDescription() {
        return "ResourceIndexer plugin implementation";
    }
}
