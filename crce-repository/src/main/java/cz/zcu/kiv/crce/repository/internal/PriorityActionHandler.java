package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.plugins.AbstractActionHandler;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.osgi.service.log.LogService;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class PriorityActionHandler extends AbstractActionHandler {

    private volatile LogService m_log;  /* injected by dependency manager */
    private volatile PluginManager m_pluginManager; /* injected by dependency manager */
    
    private static final Method ON_BUFFER_UPLOAD;
    private static final Method ON_BUFFER_DELETE;
    private static final Method ON_BUFFER_DOWNLOAD;
    private static final Method ON_BUFFER_EXECUTE;
    private static final Method ON_BUFFER_COMMIT;
    private static final Method ON_STORE_DELETE;
    private static final Method ON_STORE_DOWNLOAD;
    private static final Method ON_STORE_EXECUTE;
    private static final Method ON_STORE_PUT;

    static {
        try {
            ON_BUFFER_UPLOAD = ActionHandler.class.getMethod("onBufferUpload", Resource.class, Buffer.class, String.class);
            ON_BUFFER_DELETE = ActionHandler.class.getMethod("onBufferDelete", Resource.class, Buffer.class);
            ON_BUFFER_DOWNLOAD = ActionHandler.class.getMethod("onBufferDownload", Resource.class, Buffer.class);
            ON_BUFFER_EXECUTE = ActionHandler.class.getMethod("onBufferExecute", Resource.class, Buffer.class);
            ON_BUFFER_COMMIT = ActionHandler.class.getMethod("onBufferCommit", Array.newInstance(Resource.class, 0).getClass(), Buffer.class, Store.class);
            ON_STORE_DELETE = ActionHandler.class.getMethod("onStoreDelete", Resource.class, Store.class);
            ON_STORE_DOWNLOAD = ActionHandler.class.getMethod("onStoreDownload", Resource.class, Store.class);
            ON_STORE_EXECUTE = ActionHandler.class.getMethod("onStoreExecute", Resource.class, Store.class);
            ON_STORE_PUT = ActionHandler.class.getMethod("onStorePut", Resource.class, Store.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Can not create method", ex);
        }
    }

    @Override
    public Resource onBufferUpload(Resource resource, Buffer buffer, String name) {
        return (Resource) execute(ON_BUFFER_UPLOAD, new Object[]{resource, buffer, name});
    }

    @Override
    public Resource onBufferDelete(Resource resource, Buffer buffer) {
        return (Resource) execute(ON_BUFFER_DELETE, new Object[]{resource, buffer});
    }

    @Override
    public Resource onBufferDownload(Resource resource, Buffer buffer) {
        return (Resource) execute(ON_BUFFER_DOWNLOAD, new Object[]{resource, buffer});
    }

    @Override
    public Resource onBufferExecute(Resource resource, Buffer buffer) {
        return (Resource) execute(ON_BUFFER_EXECUTE, new Object[]{resource, buffer});
    }

    @Override
    public Resource[] onBufferCommit(Resource[] resources, Buffer buffer, Store store) {
        return (Resource[]) execute(ON_BUFFER_COMMIT, new Object[]{resources, buffer, store});
    }
    
    @Override
    public Resource onStoreDelete(Resource resource, Store repository) {
        return (Resource) execute(ON_STORE_DELETE, new Object[]{resource, repository});
    }

    @Override
    public Resource onStoreDownload(Resource resource, Store repository) {
        return (Resource) execute(ON_STORE_DOWNLOAD, new Object[]{resource, repository});
    }

    @Override
    public Resource onStoreExecute(Resource resource, Store repository) {
        return (Resource) execute(ON_STORE_EXECUTE, new Object[]{resource, repository});
    }

    @Override
    public Resource onStorePut(Resource resource, Store repository) {
        return (Resource) execute(ON_STORE_PUT, new Object[]{resource, repository});
    }

    /**
     * Works for all ActionHandler methods whose the first parameter is equivalent
     * to returned object (e.g. the first parameter is Resource, returned parameter
     * is Resource too.)
     */
    private Object execute(Method method, Object[] args) {
        ActionHandler[] handlers = m_pluginManager.getPlugins(ActionHandler.class);

        Object out = args[0];

        boolean modifying = true;

        do {
            for (ActionHandler handler : handlers) {
                if (handler.isModifying() == modifying && !handler.equals(this)) {
                    args[0] = out;
                    try {
                        out = method.invoke(handler, args);
                    } catch (IllegalAccessException ex) {
                        m_log.log(LogService.LOG_ERROR, "Unexpected IllegalAccessException", ex);
                    } catch (InvocationTargetException ex) {
                        m_log.log(LogService.LOG_ERROR, handler.getPluginId() + " handler threw an exception in method " + method.getName(), ex);
                    } catch (Exception e) {
                        m_log.log(LogService.LOG_ERROR, "Unexpected exception on calling handler", e);
                    }
                }
            }
        } while (!(modifying = !modifying));    // runs twice, the first for modifying true

        return out;
    }

    @Override
    public int getPluginPriority() {
        return Integer.MAX_VALUE / 2;
    }

}
