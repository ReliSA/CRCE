package cz.zcu.kiv.crce.repository.plugins;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.ResourceBuffer;

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
    Resource onBufferUpload(Resource resource, ResourceBuffer buffer, String name);

    Resource onBufferDownload(Resource resource, ResourceBuffer buffer);
    
    Resource onBufferExecute(Resource resource, ResourceBuffer buffer);
    
    /**
     * Invoked when resource is revoked deleted from buffer.
     * 
     * @param resource
     * @param buffer
     * @return  
     */
    Resource onBufferDelete(Resource resource, ResourceBuffer buffer);
    
    /**
     * Invoked when resource is commited from buffer to repository.
     * 
     * @param resource
     * @param repository
     * @return  
     */
    Resource onRepositoryStore(Resource resource, Store repository);

    Resource onRepositoryDownload(Resource resource, Store repository);
    
    Resource onRepositoryDelete(Resource resource, Store repository);
    
    Resource onRepositoryExecute(Resource resource, Store repository);
    
    /**
     * Returns <code>true</code> if implementing action handler modifies binary
     * content of processed artifacts (not metadata!), <code>false</code>
     * otherwise (content read-only handler).
     * @return
     */
    boolean isModifying();
    
}
