package cz.zcu.kiv.crce.plugin.stub;

import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.ResourceDAO;
import cz.zcu.kiv.crce.plugin.ResourceDAOFactory;
import cz.zcu.kiv.crce.plugin.ResourceIndexer;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public abstract class AbstractPlugin implements Plugin {

    @Override
    public String getPluginId() {
        return getClass().getName();
    }

    @Override
    public int getPluginPriority() {
        return 0;
    }

    @Override
    public String getPluginDescription() {
        if (this instanceof ResourceDAO) {
            return "ResourceDAO plugin implementation: " + getClass().getName();
        }
        if (this instanceof ResourceDAOFactory) {
            return "ResourceDAOFactory plugin implementation: " + getClass().getName();
        }
        if (this instanceof ResourceIndexer) {
            return "ResourceIndexer plugin implementation: " + getClass().getName();
        }
        return "Unknown plugin: " + getClass().getName();
    }
    
}
