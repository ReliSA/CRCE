package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.WritableRepository;
import cz.zcu.kiv.crce.repository.plugins.ResourceDAO;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.plugins.ResourceDAOFactory;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.SessionFactory;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;
import cz.zcu.kiv.crce.repository.plugins.Executable;
import cz.zcu.kiv.crce.repository.plugins.RepositoryDAO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.service.log.LogService;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class BufferImpl implements Buffer {

    private volatile BundleContext m_context;   /* injected by dependency manager */
    private volatile PluginManager m_pluginManager; /* injected by dependency manager */
    private volatile LogService m_log;  /* injected by dependency manager */
    
    private final int BUFFER_SIZE = 8 * 1024;
    private final Properties m_sessionProperties;
    
    private File m_baseDir;
    private WritableRepository m_repository;
    
    public BufferImpl(String sessionId) {
        m_sessionProperties = new Properties();
        m_sessionProperties.put(SessionFactory.SERVICE_SESSION_ID, sessionId);
    }
    
    /*
     * Called by dependency manager
     */
    void init() {
        m_baseDir = m_context.getDataFile(m_sessionProperties.getProperty(SessionFactory.SERVICE_SESSION_ID));
        if (!m_baseDir.exists()) {
            m_baseDir.mkdir();
        } else if (!m_baseDir.isDirectory()) {
            m_baseDir.delete();
            m_baseDir.mkdir();
        }
        
        if (!m_baseDir.exists() || !m_baseDir.isDirectory()) {
            throw new IllegalStateException("Base directory for Buffer was not created: " + m_baseDir.getAbsolutePath());
        }
        
        RepositoryDAO rd = m_pluginManager.getPlugin(RepositoryDAO.class);
        
        try {
            m_repository = rd.getRepository(m_baseDir.toURI());
        } catch (IOException ex) {
            m_log.log(LogService.LOG_ERROR, "Could not get repository for URI: " + m_baseDir.toURI(), ex);
        }
    }

    /*
     * Called by dependency manager
     */
    void stop() {
        for (File file : m_baseDir.listFiles()) {
            if (!file.delete()) {
                file.deleteOnExit();
                m_log.log(LogService.LOG_WARNING, "Can not delete file from destroyed buffer, deleteOnExit was set: " + file.getAbsolutePath());
            }
        }
        if (!m_baseDir.delete()) {
            m_baseDir.deleteOnExit();
            m_log.log(LogService.LOG_WARNING, "Can not delete file from destroyed buffer's base dir, deleteOnExit was set: " + m_baseDir.getAbsolutePath());
        }
    }
    
    @Override
    public synchronized Resource put(String name, InputStream artifact) throws IOException {
        if (name == null || artifact == null || "".equals(name)) {
            return null;
        }
        FileOutputStream output = null;
        File file = null;
        try {
            file = File.createTempFile("res", ".tmp", m_baseDir);
            output = new FileOutputStream(file);
            byte[] buffer = new byte[BUFFER_SIZE];
            for (int count = artifact.read(buffer); count != -1; count = artifact.read(buffer)) {
                output.write(buffer, 0, count);
            }

        } finally {
            if (output != null) {
                output.flush();
                output.close();
            }
        }
        
        ResourceDAOFactory factory = m_pluginManager.getPlugin(ResourceDAOFactory.class);
        
        ResourceDAO resourceDao;
        if (factory == null) {
            resourceDao = m_pluginManager.getPlugin(ResourceDAO.class);
        } else {
            resourceDao = factory.getResourceDAO();
        }

        Resource resource = resourceDao.getResource(file.toURI());
        
        // TODO maybe move to some plugin
        resource.createCapability("file").setProperty("name", name);
        resource.setSymbolicName(name);
        
        resource = m_pluginManager.getPlugin(ActionHandler.class).onBufferUpload(resource, this, name);
        
        resourceDao.save(resource);
        
        Version version = resource.getVersion();
        for (int i = 2; !m_repository.addResource(resource); i++) {
            resource.setVersion(new Version(version.getMajor(), version.getMinor(), version.getMicro(), version.getQualifier() + "_" + i));
        }
        
        m_pluginManager.getPlugin(RepositoryDAO.class).saveRepository(m_repository);
        
        return resource;
    }

    @Override
    public boolean remove(Resource resource) {
        return m_repository.removeResource(resource);
    }

    @Override
    public synchronized List<Resource> commit() {
        List<Resource> out = new ArrayList<Resource>();
        
        Store store = Activator.instance().getStore();
        if (store == null) {
            throw new IllegalStateException("No Store registered, probably missing configuration for PID: " + Activator.PID);
        }
        
        Resource[] resourcesToCommit = m_pluginManager.getPlugin(ActionHandler.class).onBufferCommit(m_repository.getResources(), this, store);
        
        for (Resource resource : resourcesToCommit) {
            Resource res;
            try {
                res = store.put(resource);
            } catch (IOException e) {
                m_log.log(LogService.LOG_ERROR, "Could not put resource to store: " + resource.getId(), e);
                continue;
            }
            m_repository.removeResource(resource);
            out.add(res);
        }
        
        return out;
    }

    @Override
    public Repository getRepository() {
        return m_repository;
    }

    @Override
    public void execute(List<Resource> resources, List<Executable> plugins) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    Dictionary getSessionProperties() {
        return m_sessionProperties;
    }
}
