package cz.zcu.kiv.crce.repository.plugins;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public interface ActionHandler extends Plugin {
    /**
     * Invoked when an artifact is uploaded into upload buffer.
     * 
     * @param resource 
     * @param name  
     */
    void onUploaded(Resource resource, String name);

    /**
     * Invoked when resource is commited from buffer to repository.
     * 
     * @param resource 
     */
    void onCommited(Resource resource);

    /**
     * Invoked when resource is revoked deleted from buffer.
     * 
     * @param resource 
     */
    void onRevoked(Resource resource);
    
}
