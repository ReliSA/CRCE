package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.AbstractPlugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;
import cz.zcu.kiv.crce.repository.plugins.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Properties;
import org.osgi.service.log.LogService;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class PriorityActionHandler extends AbstractPlugin implements ActionHandler {

    private volatile LogService m_log;  /* injected by dependency manager */
    private volatile PluginManager m_pluginManager; /* injected by dependency manager */
    
    private static final Method ON_BUFFER_UPLOAD;
    private static final Method ON_BUFFER_DELETE;
    private static final Method ON_BUFFER_DOWNLOAD;
    private static final Method BEFORE_BUFFER_EXECUTE;
    private static final Method AFTER_BUFFER_EXECUTE;
    private static final Method ON_BUFFER_COMMIT;
    private static final Method ON_STORE_DELETE;
    private static final Method ON_STORE_DOWNLOAD;
    private static final Method BEFORE_STORE_EXECUTE;
    private static final Method AFTER_STORE_EXECUTE;
    private static final Method ON_STORE_PUT;

    static {
        try {
            ON_BUFFER_UPLOAD = ActionHandler.class.getMethod("onUploadToBuffer", Resource.class, Buffer.class, String.class);
            ON_BUFFER_DELETE = ActionHandler.class.getMethod("onDeleteFromBuffer", Resource.class, Buffer.class);
            ON_BUFFER_DOWNLOAD = ActionHandler.class.getMethod("onDownloadFromBuffer", Resource.class, Buffer.class);
            BEFORE_BUFFER_EXECUTE = ActionHandler.class.getMethod("beforeExecuteInBuffer", Collection.class, Executable.class, Properties.class, Buffer.class);
            AFTER_BUFFER_EXECUTE = ActionHandler.class.getMethod("afterExecuteInBuffer", Collection.class, Executable.class, Properties.class, Buffer.class);
            ON_BUFFER_COMMIT = ActionHandler.class.getMethod("onBufferCommit", Collection.class, Buffer.class, Store.class);
            ON_STORE_DELETE = ActionHandler.class.getMethod("onDeleteFromStore", Resource.class, Store.class);
            ON_STORE_DOWNLOAD = ActionHandler.class.getMethod("onDownloadFromStore", Resource.class, Store.class);
            BEFORE_STORE_EXECUTE = ActionHandler.class.getMethod("beforeExecuteInStore", Collection.class, Executable.class, Properties.class, Store.class);
            AFTER_STORE_EXECUTE = ActionHandler.class.getMethod("afterExecuteInStore", Collection.class, Executable.class, Properties.class, Store.class);
            ON_STORE_PUT = ActionHandler.class.getMethod("onPutToStore", Resource.class, Store.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Can not create method: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Resource onUploadToBuffer(Resource resource, Buffer buffer, String name) throws RevokedArtifactException {
        Object[] out = execute(ON_BUFFER_UPLOAD, new Object[]{resource, buffer, name});
        if (out[1] != null) {
            if (out[1] instanceof RevokedArtifactException) {
                throw (RevokedArtifactException) out[1];
            } else {
                throw new IllegalStateException("onPutToStore throw unexpected exception", (Throwable) out[1]);
            }
        }
        return (Resource) out[0];
    }

    @Override
    public Resource onDeleteFromBuffer(Resource resource, Buffer buffer) {
        return (Resource) execute(ON_BUFFER_DELETE, new Object[]{resource, buffer})[0];
    }

    @Override
    public Resource onDownloadFromBuffer(Resource resource, Buffer buffer) {
        return (Resource) execute(ON_BUFFER_DOWNLOAD, new Object[]{resource, buffer})[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Resource> beforeExecuteInBuffer(Collection<Resource> resources, Executable executable, Properties properties, Buffer buffer) {
        return (Collection<Resource>) execute(BEFORE_BUFFER_EXECUTE, new Object[]{resources, executable, properties, buffer})[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Resource> afterExecuteInBuffer(Collection<Resource> resources, Executable executable, Properties properties, Buffer buffer) {
        return (Collection<Resource>) execute(AFTER_BUFFER_EXECUTE, new Object[]{resources, executable, properties, buffer})[0];
    }
    
    @Override
    public Resource onDeleteFromStore(Resource resource, Store repository) {
        return (Resource) execute(ON_STORE_DELETE, new Object[]{resource, repository})[0];
    }

    @Override
    public Resource onDownloadFromStore(Resource resource, Store repository) {
        return (Resource) execute(ON_STORE_DOWNLOAD, new Object[]{resource, repository})[0];
    }

    @Override
    public Resource onPutToStore(Resource resource, Store repository) throws RevokedArtifactException {
        Object[] out = execute(ON_STORE_PUT, new Object[]{resource, repository});
        if (out[1] != null) {
            if (out[1] instanceof RevokedArtifactException) {
                throw (RevokedArtifactException) out[1];
            } else {
                throw new IllegalStateException("onPutToStore throw unexpected exception", (Throwable) out[1]);
            }
        }
        return (Resource) out[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Resource> onBufferCommit(Collection<Resource> resources, Buffer buffer, Store store) {
        return (Collection<Resource>) execute(ON_BUFFER_COMMIT, new Object[]{resources, buffer, store})[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Resource> beforeExecuteInStore(Collection<Resource> resources, Executable executable, Properties properties, Store store) {
        return (Collection<Resource>) execute(BEFORE_STORE_EXECUTE, new Object[]{resources, executable, properties, store})[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Resource> afterExecuteInStore(Collection<Resource> resources, Executable executable, Properties properties, Store store) {
        return (Collection<Resource>) execute(AFTER_STORE_EXECUTE, new Object[]{resources, executable, properties, store})[0];
    }
    
    /**
     * Works for all ActionHandler methods whose the first parameter is equivalent
     * to returned object (e.g. the first parameter is Resource, returned parameter
     * is Resource too.)
     * 
     * @return An array of returned object and thrown exception.
     */
    private Object[] execute(Method method, Object[] args) {
        ActionHandler[] handlers = m_pluginManager.getPlugins(ActionHandler.class);

        Object out = args[0];

        boolean modifying = true;
        Throwable exception = null;
        do {
            for (ActionHandler handler : handlers) {
                if (handler.isModifying() == modifying && !handler.equals(this)) {
                    args[0] = out;
                    try {
                        out = method.invoke(handler, args);
                    } catch (IllegalAccessException ex) {
                        m_log.log(LogService.LOG_ERROR, "Unexpected IllegalAccessException", ex);
                    } catch (InvocationTargetException ex) {
                        exception = ex.getTargetException();
                    } catch (Exception e) {
                        m_log.log(LogService.LOG_ERROR, "Unexpected exception on calling handler", e);
                    }
                }
            }
        } while (!(modifying = !modifying));    // runs twice, the first for modifying true

        return new Object[] {out, exception};
    }

    @Override
    public int getPluginPriority() {
        return Integer.MAX_VALUE / 2;
    }

    @Override
    public boolean isModifying() {
        return true;
    }

}
