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
import java.util.List;
import java.util.Properties;
import org.osgi.service.log.LogService;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class PriorityActionHandler extends AbstractPlugin implements ActionHandler {

    private volatile LogService m_log;  /* injected by dependency manager */
    private volatile PluginManager m_pluginManager; /* injected by dependency manager */
    
    private static final Method BEFORE_BUFFER_UPLOAD;
    private static final Method ON_BUFFER_UPLOAD;
    private static final Method AFTER_BUFFER_UPLOAD;
    
    private static final Method BEFORE_BUFFER_DOWNLOAD;
    private static final Method AFTER_BUFFER_DOWNLOAD;
    
    private static final Method BEFORE_BUFFER_EXECUTE;
    private static final Method AFTER_BUFFER_EXECUTE;
    
    private static final Method BEFORE_BUFFER_DELETE;
    private static final Method AFTER_BUFFER_DELETE;
    
    private static final Method BEFORE_BUFFER_COMMIT;
    private static final Method AFTER_BUFFER_COMMIT;

    
    private static final Method BEFORE_STORE_PUT;
    private static final Method AFTER_STORE_PUT;
    
    private static final Method BEFORE_STORE_DELETE;
    private static final Method AFTER_STORE_DELETE;
    
    private static final Method BEFORE_STORE_DOWNLOAD;
    private static final Method AFTER_STORE_DOWNLOAD;
    
    private static final Method BEFORE_STORE_EXECUTE;
    private static final Method AFTER_STORE_EXECUTE;

    static {
        try {
            BEFORE_BUFFER_UPLOAD = ActionHandler.class.getMethod("beforeUploadToBuffer", String.class, Buffer.class);
            ON_BUFFER_UPLOAD = ActionHandler.class.getMethod("onUploadToBuffer", Resource.class, Buffer.class, String.class);
            AFTER_BUFFER_UPLOAD = ActionHandler.class.getMethod("afterUploadToBuffer", Resource.class, Buffer.class, String.class);
            
            BEFORE_BUFFER_DOWNLOAD = ActionHandler.class.getMethod("beforeDownloadFromBuffer", Resource.class, Buffer.class);
            AFTER_BUFFER_DOWNLOAD = ActionHandler.class.getMethod("afterDownloadFromBuffer", Resource.class, Buffer.class);
            
            BEFORE_BUFFER_EXECUTE = ActionHandler.class.getMethod("beforeExecuteInBuffer", List.class, Executable.class, Properties.class, Buffer.class);
            AFTER_BUFFER_EXECUTE = ActionHandler.class.getMethod("afterExecuteInBuffer", List.class, Executable.class, Properties.class, Buffer.class);

            BEFORE_BUFFER_DELETE = ActionHandler.class.getMethod("beforeDeleteFromBuffer", Resource.class, Buffer.class);
            AFTER_BUFFER_DELETE = ActionHandler.class.getMethod("afterDeleteFromBuffer", Resource.class, Buffer.class);
            
            BEFORE_BUFFER_COMMIT = ActionHandler.class.getMethod("beforeBufferCommit", List.class, Buffer.class, Store.class);
            AFTER_BUFFER_COMMIT = ActionHandler.class.getMethod("afterBufferCommit", List.class, Buffer.class, Store.class);
            
            BEFORE_STORE_PUT = ActionHandler.class.getMethod("beforePutToStore", Resource.class, Store.class);
            AFTER_STORE_PUT = ActionHandler.class.getMethod("afterPutToStore", Resource.class, Store.class);
            
            BEFORE_STORE_DELETE = ActionHandler.class.getMethod("beforeDeleteFromStore", Resource.class, Store.class);
            AFTER_STORE_DELETE = ActionHandler.class.getMethod("afterDeleteFromStore", Resource.class, Store.class);
            
            BEFORE_STORE_DOWNLOAD = ActionHandler.class.getMethod("beforeDownloadFromStore", Resource.class, Store.class);
            AFTER_STORE_DOWNLOAD = ActionHandler.class.getMethod("afterDownloadFromStore", Resource.class, Store.class);
            
            BEFORE_STORE_EXECUTE = ActionHandler.class.getMethod("beforeExecuteInStore", List.class, Executable.class, Properties.class, Store.class);
            AFTER_STORE_EXECUTE = ActionHandler.class.getMethod("afterExecuteInStore", List.class, Executable.class, Properties.class, Store.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Can not create method: " + ex.getMessage(), ex);
        }
    }

    @Override
    public String beforeUploadToBuffer(String name, Buffer buffer) throws RevokedArtifactException {
        Object[] out = execute(BEFORE_BUFFER_UPLOAD, new Object[]{name, buffer});
        if (out[1] != null) {
            if (out[1] instanceof RevokedArtifactException) {
                throw (RevokedArtifactException) out[1];
            } else {
                throw new IllegalStateException("beforeUploadToBuffer threw unexpected exception", (Throwable) out[1]);
            }
        }
        return (String) out[0];
    }
    
    @Override
    public Resource onUploadToBuffer(Resource resource, Buffer buffer, String name) throws RevokedArtifactException {
        Object[] out = execute(ON_BUFFER_UPLOAD, new Object[]{resource, buffer, name});
        if (out[1] != null) {
            if (out[1] instanceof RevokedArtifactException) {
                throw (RevokedArtifactException) out[1];
            } else {
                throw new IllegalStateException("onUploadToBuffer threw unexpected exception", (Throwable) out[1]);
            }
        }
        return (Resource) out[0];
    }

    @Override
    public Resource afterUploadToBuffer(Resource resource, Buffer buffer, String name) throws RevokedArtifactException {
        Object[] out = execute(AFTER_BUFFER_UPLOAD, new Object[]{resource, buffer, name});
        if (out[1] != null) {
            if (out[1] instanceof RevokedArtifactException) {
                throw (RevokedArtifactException) out[1];
            } else {
                throw new IllegalStateException("afterUploadToBuffer threw unexpected exception", (Throwable) out[1]);
            }
        }
        return (Resource) out[0];
    }
    
    @Override
    public Resource beforeDownloadFromBuffer(Resource resource, Buffer buffer) {
        return (Resource) execute(BEFORE_BUFFER_DOWNLOAD, new Object[]{resource, buffer})[0];
    }

    @Override
    public Resource afterDownloadFromBuffer(Resource resource, Buffer buffer) {
        return (Resource) execute(AFTER_BUFFER_DOWNLOAD, new Object[]{resource, buffer})[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Resource> beforeExecuteInBuffer(List<Resource> resources, Executable executable, Properties properties, Buffer buffer) {
        return (List<Resource>) execute(BEFORE_BUFFER_EXECUTE, new Object[]{resources, executable, properties, buffer})[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Resource> afterExecuteInBuffer(List<Resource> resources, Executable executable, Properties properties, Buffer buffer) {
        return (List<Resource>) execute(AFTER_BUFFER_EXECUTE, new Object[]{resources, executable, properties, buffer})[0];
    }
    
    @Override
    public Resource beforeDeleteFromBuffer(Resource resource, Buffer buffer) {
        return (Resource) execute(BEFORE_BUFFER_DELETE, new Object[]{resource, buffer})[0];
    }

    @Override
    public Resource afterDeleteFromBuffer(Resource resource, Buffer buffer) {
        return (Resource) execute(AFTER_BUFFER_DELETE, new Object[]{resource, buffer})[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Resource> beforeBufferCommit(List<Resource> resources, Buffer buffer, Store store) {
        return (List<Resource>) execute(BEFORE_BUFFER_COMMIT, new Object[]{resources, buffer, store})[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Resource> afterBufferCommit(List<Resource> resources, Buffer buffer, Store store) {
        return (List<Resource>) execute(AFTER_BUFFER_COMMIT, new Object[]{resources, buffer, store})[0];
    }

    @Override
    public Resource beforePutToStore(Resource resource, Store repository) throws RevokedArtifactException {
        Object[] out = execute(BEFORE_STORE_PUT, new Object[]{resource, repository});
        if (out[1] != null) {
            if (out[1] instanceof RevokedArtifactException) {
                throw (RevokedArtifactException) out[1];
            } else {
                throw new IllegalStateException("beforePutToStore threw unexpected exception", (Throwable) out[1]);
            }
        }
        return (Resource) out[0];
    }

    @Override
    public Resource afterPutToStore(Resource resource, Store repository) throws RevokedArtifactException {
        Object[] out = execute(AFTER_STORE_PUT, new Object[]{resource, repository});
        if (out[1] != null) {
            if (out[1] instanceof RevokedArtifactException) {
                throw (RevokedArtifactException) out[1];
            } else {
                throw new IllegalStateException("afterPutToStore threw unexpected exception", (Throwable) out[1]);
            }
        }
        return (Resource) out[0];
    }

    @Override
    public Resource beforeDeleteFromStore(Resource resource, Store store) {
        return (Resource) execute(BEFORE_STORE_DELETE, new Object[]{resource, store})[0];
    }

    @Override
    public Resource afterDeleteFromStore(Resource resource, Store store) {
        return (Resource) execute(AFTER_STORE_DELETE, new Object[]{resource, store})[0];
    }
    
    @Override
    public Resource beforeDownloadFromStore(Resource resource, Store store) {
        return (Resource) execute(BEFORE_STORE_DOWNLOAD, new Object[]{resource, store})[0];
    }

    @Override
    public Resource afterDownloadFromStore(Resource resource, Store store) {
        return (Resource) execute(AFTER_STORE_DOWNLOAD, new Object[]{resource, store})[0];
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Resource> beforeExecuteInStore(List<Resource> resources, Executable executable, Properties properties, Store store) {
        return (List<Resource>) execute(BEFORE_STORE_EXECUTE, new Object[]{resources, executable, properties, store})[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Resource> afterExecuteInStore(List<Resource> resources, Executable executable, Properties properties, Store store) {
        return (List<Resource>) execute(AFTER_STORE_EXECUTE, new Object[]{resources, executable, properties, store})[0];
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
                if (handler.isExclusive() == modifying && !handler.equals(this)) {
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
    public boolean isExclusive() {
        return true;
    }

}
