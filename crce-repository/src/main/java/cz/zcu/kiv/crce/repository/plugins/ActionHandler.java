package cz.zcu.kiv.crce.repository.plugins;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.repository.Repository;
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
     */
    void onUpload(Resource resource, String name, ResourceBuffer buffer);

    void onDownload(Resource resource, Repository repository);
    
    void onBufferDownload(Resource resource, ResourceBuffer buffer);
    
    /**
     * Invoked when resource is commited from buffer to repository.
     * 
     * @param resource
     * @param repository  
     */
    void onStore(Resource resource, Repository repository);

    /**
     * Invoked when resource is revoked deleted from buffer.
     * 
     * @param resource
     * @param buffer  
     */
    void onBufferDelete(Resource resource, ResourceBuffer buffer);
    
    void onDelete(Resource resource, Repository repository);
    
    void onBufferExecute(Resource resource, ResourceBuffer buffer);
    
    void onExecute(Resource resource, Repository repository);
    
}
