package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.WritableRepository;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;
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
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.List;
import java.util.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.service.log.LogService;

/**
 * Filebased implementation of <code>Buffer</code>.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class BufferImpl implements Buffer {

    private volatile BundleContext m_context;   /* injected by dependency manager */
    private volatile PluginManager m_pluginManager; /* injected by dependency manager */
    private volatile LogService m_log;  /* injected by dependency manager */
    private volatile Store m_store;     /* injected by dependency manager */
    
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
            m_baseDir.mkdirs();
        } else if (!m_baseDir.isDirectory()) {
            throw new IllegalStateException("Base directory is not a directory: " + m_baseDir);
        }
        if (!m_baseDir.exists()) {
            throw new IllegalStateException("Base directory for Buffer was not created: " + m_baseDir, new IOException("Can not create directory"));
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
                m_log.log(LogService.LOG_WARNING, "Can not delete file from destroyed buffer, deleteOnExit was set: " + file);
            }
        }
        if (!m_baseDir.delete()) {
            m_baseDir.deleteOnExit();
            m_log.log(LogService.LOG_WARNING, "Can not delete file from destroyed buffer's base dir, deleteOnExit was set: " + m_baseDir);
        }
    }
    
    @Override
    public synchronized Resource put(String name, InputStream artifact) throws IOException, RevokedArtifactException {
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
        
        ResourceDAO resourceDao = m_pluginManager.getPlugin(ResourceDAOFactory.class).getResourceDAO();

        Resource resource = resourceDao.getResource(file.toURI());
        
        // TODO alternatively can be moved to some plugin
        resource.createCapability("file").setProperty("name", name);
        resource.setSymbolicName(name);
        
        Resource tmp;
        try {
            tmp = m_pluginManager.getPlugin(ActionHandler.class).onUploadToBuffer(resource, this, name);
        } catch (RevokedArtifactException e) {
            if (!file.delete()) {
                m_log.log(LogService.LOG_ERROR, "Can not delete file of revoked artifact: " + file.getPath());
            }
            throw e;
        }
        
        if (tmp == null) {
            m_log.log(LogService.LOG_ERROR, "ActionHandler onUploadToBuffer returned null resource, using original");
        } else {
            resource = tmp;
        }
        
        Version version = resource.getVersion();
        for (int i = 2; !m_repository.addResource(resource); i++) {
            resource.setVersion(new Version(version.getMajor(), version.getMinor(), version.getMicro(), version.getQualifier() + "_" + i));
            if (resource.getVersion().equals(version)) {
                if (!file.delete()) {
                    m_log.log(LogService.LOG_ERROR, "Can not delete file of revoked artifact: " + file.getPath());
                }
                throw new RevokedArtifactException("Resource with the same symbolic name and version already exists in Buffer: " + resource.getId());
            }
        }
        resourceDao.save(resource);
        
        m_pluginManager.getPlugin(RepositoryDAO.class).saveRepository(m_repository);
        
        return resource;
    }

    @Override
    public synchronized boolean remove(Resource resource) throws IOException {
        if (!isInBuffer(resource)) {
            if (m_repository.contains(resource)) {
                m_log.log(LogService.LOG_WARNING, "Removing resource is not in buffer but it is in internal repository: " + resource.getId());
            }
            return false;
        }
        
        resource = m_pluginManager.getPlugin(ActionHandler.class).onDeleteFromBuffer(resource, this);
        
        // if URI scheme is not 'file', it is detected in previous isInBuffer() check
        File file = new File(resource.getUri());
        if (!file.delete()) {
            throw new IOException("Can not delete artifact file from buffer: " + resource.getUri());
        }
        
        ResourceDAO resourceDao = m_pluginManager.getPlugin(ResourceDAOFactory.class).getResourceDAO();
        try {
            resourceDao.remove(resource);
        } finally {
            // once the artifact file was removed, the resource has to be removed
            // from the repository even in case of exception on removing metadata
            // to keep consistency of repository with stored artifact files
            if (!m_repository.removeResource(resource)) {
                m_log.log(LogService.LOG_WARNING, "Buffer's internal repository does not contain removing resource: " + resource.getId());
            }
            m_pluginManager.getPlugin(RepositoryDAO.class).saveRepository(m_repository);
        }
        
        return true;
    }

    @Override
    public synchronized List<Resource> commit(boolean move) throws IOException {
        List<Resource> out = new ArrayList<Resource>();
        Collection<Resource> resourcesToCommit = m_pluginManager.getPlugin(ActionHandler.class).onBufferCommit(Arrays.asList(m_repository.getResources()), this, m_store);
        List<Resource> resourcesToRemove = new ArrayList<Resource>();
        ResourceDAO resourceDao = m_pluginManager.getPlugin(ResourceDAOFactory.class).getResourceDAO();
        
        // put resources to store
        if (move && (m_store instanceof FilebasedStoreImpl)) {
            for (Resource resource : resourcesToCommit) {
                Resource putResource;
                try {
                    putResource = ((FilebasedStoreImpl) m_store).move(resource);
                } catch (RevokedArtifactException ex) {
                    m_log.log(LogService.LOG_INFO, "Resource can not be commited, it was revoked by store: " + resource.getId());
                    continue;
                }
                out.add(putResource);
                resourcesToRemove.add(resource);
            }
        } else {
            for (Resource resource : resourcesToCommit) {
                Resource putResource;
                try {
                    putResource = m_store.put(resource);
                } catch (RevokedArtifactException ex) {
                    m_log.log(LogService.LOG_INFO, "Resource can not be commited, it was revoked by store: " + resource.getId(), ex);
                    continue;
                }
                out.add(putResource);
                if (move) {
                    resourcesToRemove.add(resource);
                }
            }
        }
        
        // remove resources from buffer
        if (move) {
            for (Resource resource : resourcesToRemove) {
                File resourceFile = new File(resource.getUri());
                if (resourceFile.exists()) {
                    if (!resourceFile.delete()) {
                        m_log.log(LogService.LOG_ERROR, "Can not delete artifact from buffer: " + resource.getUri());
                        continue;
                    }
                }
                try {
                    resourceDao.remove(resource);
                } catch (IOException e) {
                    // once the artifact file was removed, the resource has to be removed
                    // from the repository even in case of exception on removing metadata
                    // to keep consistency of repository with stored artifact files
                    if (!m_repository.removeResource(resource)) {
                        m_log.log(LogService.LOG_WARNING, "Buffer's internal repository does not contain removing resource: " + resource.getId());
                    }
                    m_pluginManager.getPlugin(RepositoryDAO.class).saveRepository(m_repository);
                    throw e;
                }
                if (!m_repository.removeResource(resource)) {
                    m_log.log(LogService.LOG_WARNING, "Buffer's internal repository does not contain removing resource: " + resource.getId());
                }
            }
            m_pluginManager.getPlugin(RepositoryDAO.class).saveRepository(m_repository);
        }
        
        return out;
    }

    @Override
    public synchronized Repository getRepository() {
        return m_repository;
    }

    @Override
    public synchronized void execute(Collection<Resource> resources, Executable executable, Properties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    Dictionary getSessionProperties() {
        return m_sessionProperties;
    }
    
    private boolean isInBuffer(Resource resource) {
        URI uri = resource.getUri().normalize();
        if (!"file".equals(uri.getScheme())) {
            return false;
        }
        return new File(uri).getPath().startsWith(m_baseDir.getAbsolutePath());
    }
}
