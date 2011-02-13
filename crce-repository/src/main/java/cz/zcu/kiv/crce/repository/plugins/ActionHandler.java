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
     * @return  
     */
    Resource onUpload(Resource resource, ResourceBuffer buffer, String name);

    Resource onDownload(Resource resource, Repository repository);
    
    Resource onBufferDownload(Resource resource, ResourceBuffer buffer);
    
    /**
     * Invoked when resource is commited from buffer to repository.
     * 
     * @param resource
     * @param repository
     * @return  
     */
    Resource onStore(Resource resource, Repository repository);

    /**
     * Invoked when resource is revoked deleted from buffer.
     * 
     * @param resource
     * @param buffer
     * @return  
     */
    Resource onBufferDelete(Resource resource, ResourceBuffer buffer);
    
    Resource onDelete(Resource resource, Repository repository);
    
    Resource onBufferExecute(Resource resource, ResourceBuffer buffer);
    
    Resource onExecute(Resource resource, Repository repository);
    
}
