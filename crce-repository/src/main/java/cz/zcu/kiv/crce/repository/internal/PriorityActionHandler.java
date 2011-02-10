package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Repository;
import cz.zcu.kiv.crce.repository.ResourceBuffer;
import cz.zcu.kiv.crce.repository.plugins.AbstractActionHandler;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class PriorityActionHandler extends AbstractActionHandler {

    private volatile PluginManager m_pluginManager;

    private static Method ON_BUFFER_DELETE;
    private static Method ON_BUFFER_DOWNLOAD;
    private static Method ON_BUFFER_EXECUTE;
    private static Method ON_DELETE;
    private static Method ON_DOWNLOAD;
    private static Method ON_EXECUTE;
    private static Method ON_STORE;
    private static Method ON_UPLOAD;
    
    static {
        try {
            ON_BUFFER_DELETE = ActionHandler.class.getMethod("onBufferDelete", Resource.class, ResourceBuffer.class);
            ON_BUFFER_DOWNLOAD = ActionHandler.class.getMethod("onBufferDownload", Resource.class, ResourceBuffer.class);
            ON_BUFFER_EXECUTE = ActionHandler.class.getMethod("onBufferExecute", Resource.class, ResourceBuffer.class);
            ON_DELETE = ActionHandler.class.getMethod("onDelete", Resource.class, Repository.class);
            ON_DOWNLOAD = ActionHandler.class.getMethod("onDownload", Resource.class, Repository.class);
            ON_EXECUTE = ActionHandler.class.getMethod("onExecute", Resource.class, Repository.class);
            ON_STORE = ActionHandler.class.getMethod("onStore", Resource.class, Repository.class);
            ON_UPLOAD = ActionHandler.class.getMethod("onUpload", Resource.class, String.class, ResourceBuffer.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Can not create method", ex);
        }
    }
    
    @Override
    public void onBufferDelete(Resource resource, ResourceBuffer buffer) {
        execute(ON_BUFFER_DELETE, new Object[]{resource, buffer});
    }

    @Override
    public void onBufferDownload(Resource resource, ResourceBuffer buffer) {
        execute(ON_BUFFER_DOWNLOAD, new Object[]{resource, buffer});
    }

    @Override
    public void onBufferExecute(Resource resource, ResourceBuffer buffer) {
        execute(ON_BUFFER_EXECUTE, new Object[]{resource, buffer});
    }

    @Override
    public void onDelete(Resource resource, Repository repository) {
        execute(ON_DELETE, new Object[]{resource, repository});
    }

    @Override
    public void onDownload(Resource resource, Repository repository) {
        execute(ON_DOWNLOAD, new Object[]{resource, repository});
    }

    @Override
    public void onExecute(Resource resource, Repository repository) {
        execute(ON_EXECUTE, new Object[]{resource, repository});
    }

    @Override
    public void onStore(Resource resource, Repository repository) {
        execute(ON_STORE, new Object[]{resource, repository});
    }

    @Override
    public void onUpload(Resource resource, String name, ResourceBuffer buffer) {
        execute(ON_UPLOAD, new Object[]{resource, name, buffer});
    }
    
    private void execute(Method method, Object[] args) {
        ActionHandler[] handlers = m_pluginManager.getPlugins(ActionHandler.class);
        
        for (ActionHandler handler : handlers) {
            if (!handler.equals(this)) {
                try {
                    method.invoke(handler, args);
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (InvocationTargetException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public int getPluginPriority() {
        return Integer.MAX_VALUE / 2;
    }
}
