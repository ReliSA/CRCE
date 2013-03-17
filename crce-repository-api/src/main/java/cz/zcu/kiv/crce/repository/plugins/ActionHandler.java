package cz.zcu.kiv.crce.repository.plugins;

import java.util.List;
import java.util.Properties;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;

/**
 * This interface defines an executable handler of various events which can
 * occur during resource life-time.
 *
 * <p>Default execution of handlers is <i>synchronous</i> so the calling method
 * will wait for the result of execution.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface ActionHandler extends Plugin {

    String beforeUploadToBuffer(String name, Buffer buffer) throws RevokedArtifactException;

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

    Resource afterUploadToBuffer(Resource resource, Buffer buffer, String name) throws RevokedArtifactException;


    Resource beforeDownloadFromBuffer(Resource resource, Buffer buffer);

    Resource afterDownloadFromBuffer(Resource resource, Buffer buffer);


    List<Resource> beforeExecuteInBuffer(List<Resource> resources, Executable executable, Properties properties, Buffer buffer);

    List<Resource> afterExecuteInBuffer(List<Resource> resources, Executable executable, Properties properties, Buffer buffer);


    Resource beforeDeleteFromBuffer(Resource resource, Buffer buffer);

    /**
     * Invoked when resource is revoked deleted from buffer.
     *
     * @param resource
     * @param buffer
     * @return
     */
    Resource afterDeleteFromBuffer(Resource resource, Buffer buffer);


    List<Resource> beforeBufferCommit(List<Resource> resources, Buffer buffer, Store store);

    List<Resource> afterBufferCommit(List<Resource> resources, Buffer buffer, Store store);


    Resource beforePutToStore(Resource resource, Store store) throws RevokedArtifactException;

    /**
     * Invoked when resource is commited from buffer to repository.
     *
     * @param resource
     * @param store
     * @return
     * @throws RevokedArtifactException
     */
    Resource afterPutToStore(Resource resource, Store store) throws RevokedArtifactException;


    Resource beforeDownloadFromStore(Resource resource, Store store);

    Resource afterDownloadFromStore(Resource resource, Store store);


    Resource beforeDeleteFromStore(Resource resource, Store store);

    Resource afterDeleteFromStore(Resource resource, Store store);

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
    boolean isExclusive();
}
