package cz.zcu.kiv.crce.plugin.stub;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.ResourceIndexer;
import java.io.InputStream;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public abstract class AbstractResourceIndexer extends AbstractPlugin implements ResourceIndexer {

    @Override
    abstract public Resource index(InputStream input, Resource resource);
    
    @Override
    public Resource index(InputStream input) {
        return index(input, null);
    }

    @Override
    public String[] getProvidedCategories() {
        return new String[0];
    }

    @Override
    public String[] getRequiredCategories() {
        return new String[0];
    }

}
