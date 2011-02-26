package cz.zcu.kiv.crce.repository.plugins;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.Buffer;

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
     * @param buffer
     * @return  
     */
    Resource onBufferUpload(Resource resource, Buffer buffer, String name);

    Resource onBufferDownload(Resource resource, Buffer buffer);
    
    Resource onBufferExecute(Resource resource, Buffer buffer);
    
    /**
     * Invoked when resource is revoked deleted from buffer.
     * 
     * @param resource
     * @param buffer
     * @return  
     */
    Resource onBufferDelete(Resource resource, Buffer buffer);
    
    Resource[] onBufferCommit(Resource[] resource, Buffer buffer, Store store);
    
    /**
     * Invoked when resource is commited from buffer to repository.
     * 
     * @param resource
     * @param store
     * @return  
     */
    Resource onStorePut(Resource resource, Store store);

    Resource onStoreDownload(Resource resource, Store store);
    
    Resource onStoreDelete(Resource resource, Store store);
    
    /**
     * TODO proposed variant:
     * Resource[] onStoreExecute(Resource[] resources, Store store);
     * 
     * TODO how to choose, which plugins will be executed? do an Executable plugin
     * and pass it (or set of such plugins) as method parameter?
     * or all plugins will be executed with no choice?
     * 
     * 
     * @param resource
     * @param store
     * @return 
     */
    Resource onStoreExecute(Resource resource, Store store);
    
    /**
     * Returns <code>true</code> if implementing action handler modifies binary
     * content of processed artifacts (not metadata!), <code>false</code>
     * otherwise (content read-only handler).
     * @return
     */
    boolean isModifying();
    
}
