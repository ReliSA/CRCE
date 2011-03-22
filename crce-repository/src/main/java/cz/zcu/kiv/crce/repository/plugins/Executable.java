package cz.zcu.kiv.crce.repository.plugins;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;
import java.util.Collection;
import java.util.Properties;

/**
 * This interface specifies the kind of plugin which can be executed at any time
 * by external event, e.g. on user's request.
 * 
 * <p>Calling of executables can be <i>asynchronous</i> so there is no guarantee
 * that the calling method will wait for the end of execution.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface Executable extends Plugin {

    /**
     * Executes the plugin on set of resources.
     * 
     * <p> The set of resources contains resources that are to be e.g. tested.
     * Other resources, that are not the point of execution but can be used as 
     * supporting resources, can be obtained from given repository. This
     * repository contains all available resources in store and buffer.
     * @param resources the set of resources that this plugin will run on.
     * @param repository the repository of all available resources.
     * @param properties plugin configuration properties.
     */
    void execute(Collection<Resource> resources, Repository repository, Properties properties);
}
