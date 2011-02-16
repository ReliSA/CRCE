package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Repository;
import cz.zcu.kiv.crce.repository.ResourceBuffer;
import cz.zcu.kiv.crce.repository.plugins.AbstractActionHandler;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.osgi.service.log.LogService;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class PriorityActionHandler extends AbstractActionHandler {

    LogService m_log; /* injected by dependency manager */

    private volatile PluginManager m_pluginManager;
    private static Method ON_BUFFER_DELETE;
    private static Method ON_BUFFER_DOWNLOAD;
    private static Method ON_BUFFER_EXECUTE;
    private static Method ON_REPOSITORY_DELETE;
    private static Method ON_REPOSITORY_DOWNLOAD;
    private static Method ON_REPOSITORY_EXECUTE;
    private static Method ON_REPOSITORY_STORE;
    private static Method ON_BUFFER_UPLOAD;

    static {
        try {
            ON_BUFFER_UPLOAD = ActionHandler.class.getMethod("onBufferUpload", Resource.class, ResourceBuffer.class, String.class);
            ON_BUFFER_DELETE = ActionHandler.class.getMethod("onBufferDelete", Resource.class, ResourceBuffer.class);
            ON_BUFFER_DOWNLOAD = ActionHandler.class.getMethod("onBufferDownload", Resource.class, ResourceBuffer.class);
            ON_BUFFER_EXECUTE = ActionHandler.class.getMethod("onBufferExecute", Resource.class, ResourceBuffer.class);
            ON_REPOSITORY_DELETE = ActionHandler.class.getMethod("onRepositoryDelete", Resource.class, Repository.class);
            ON_REPOSITORY_DOWNLOAD = ActionHandler.class.getMethod("onRepositoryDownload", Resource.class, Repository.class);
            ON_REPOSITORY_EXECUTE = ActionHandler.class.getMethod("onRepositoryExecute", Resource.class, Repository.class);
            ON_REPOSITORY_STORE = ActionHandler.class.getMethod("onRepositoryStore", Resource.class, Repository.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Can not create method", ex);
        }
    }

    @Override
    public Resource onBufferDelete(Resource resource, ResourceBuffer buffer) {
        return execute(ON_BUFFER_DELETE, new Object[]{resource, buffer});
    }

    @Override
    public Resource onBufferDownload(Resource resource, ResourceBuffer buffer) {
        return execute(ON_BUFFER_DOWNLOAD, new Object[]{resource, buffer});
    }

    @Override
    public Resource onBufferExecute(Resource resource, ResourceBuffer buffer) {
        return execute(ON_BUFFER_EXECUTE, new Object[]{resource, buffer});
    }

    @Override
    public Resource onRepositoryDelete(Resource resource, Repository repository) {
        return execute(ON_REPOSITORY_DELETE, new Object[]{resource, repository});
    }

    @Override
    public Resource onRepositoryDownload(Resource resource, Repository repository) {
        return execute(ON_REPOSITORY_DOWNLOAD, new Object[]{resource, repository});
    }

    @Override
    public Resource onRepositoryExecute(Resource resource, Repository repository) {
        return execute(ON_REPOSITORY_EXECUTE, new Object[]{resource, repository});
    }

    @Override
    public Resource onRepositoryStore(Resource resource, Repository repository) {
        return execute(ON_REPOSITORY_STORE, new Object[]{resource, repository});
    }

    @Override
    public Resource onBufferUpload(Resource resource, ResourceBuffer buffer, String name) {
        return execute(ON_BUFFER_UPLOAD, new Object[]{resource, buffer, name});
    }

    private Resource execute(Method method, Object[] args) {
        ActionHandler[] handlers = m_pluginManager.getPlugins(ActionHandler.class);

        Resource out = (Resource) args[0];

        boolean modifying = true;

        do {
            for (ActionHandler handler : handlers) {
                if (handler.isModifying() == modifying && !handler.equals(this)) {
                    args[0] = out;
                    try {
                        out = (Resource) method.invoke(handler, args);
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
