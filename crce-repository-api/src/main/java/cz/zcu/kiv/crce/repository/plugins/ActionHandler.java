package cz.zcu.kiv.crce.repository.plugins;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;
import java.util.List;
import java.util.Properties;

/**
 * This interface defines an executable handler of various events which can
 * occur during resource life-time.
 * 
 * <p>Default execution of handlers is <i>synchronous</i> so the calling method
 * will wait for the result of execution.
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
     * @throws RevokedArtifactException  
     */
    Resource onUploadToBuffer(Resource resource, Buffer buffer, String name) throws RevokedArtifactException;

    Resource onDownloadFromBuffer(Resource resource, Buffer buffer);
    
    List<Resource> beforeExecuteInBuffer(List<Resource> resources, Executable executable, Properties properties, Buffer buffer);
    
    List<Resource> afterExecuteInBuffer(List<Resource> resources, Executable executable, Properties properties, Buffer buffer);
    
    /**
     * Invoked when resource is revoked deleted from buffer.
     * 
     * @param resource
     * @param buffer
     * @return  
     */
    Resource onDeleteFromBuffer(Resource resource, Buffer buffer);
    
    List<Resource> onBufferCommit(List<Resource> resources, Buffer buffer, Store store);
    
    /**
     * Invoked when resource is commited from buffer to repository.
     * 
     * @param resource
     * @param store
     * @return
     * @throws RevokedArtifactException  
     */
    Resource onPutToStore(Resource resource, Store store) throws RevokedArtifactException;

    Resource onDownloadFromStore(Resource resource, Store store);
    
    Resource onDeleteFromStore(Resource resource, Store store);
    
    /**
     * TODO proposed variant:
     * Resource[] onExecuteInStore(Resource[] resources, Store store);
     * 
     * TODO how to choose, which plugins will be executed? do an Executable plugin
     * and pass it (or set of such plugins) as method parameter?
     * or all plugins will be executed with no choice?
     * 
     * 
     * @param resources 
     * @param executable 
     * @param properties 
     * @param store
     * @return 
     */
    List<Resource> beforeExecuteInStore(List<Resource> resources, Executable executable, Properties properties, Store store);
    
    List<Resource> afterExecuteInStore(List<Resource> resources, Executable executable, Properties properties, Store store);
    
    /**
     * Returns <code>true</code> if implementing action handler modifies binary
     * content of processed artifacts (not metadata!), <code>false</code>
     * otherwise (content read-only handler).
     * @return
     */
    boolean isModifying();
    
}
