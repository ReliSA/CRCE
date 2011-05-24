package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.WritableRepository;
import cz.zcu.kiv.crce.metadata.dao.RepositoryDAO;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.SessionRegister;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;
import cz.zcu.kiv.crce.repository.plugins.Executable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

/**
 * Filebased implementation of <code>Buffer</code>.
 * 
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class BufferImpl implements Buffer, EventHandler {

    private volatile BundleContext m_context;   /* injected by dependency manager */
    private volatile PluginManager m_pluginManager; /* injected by dependency manager */
    private volatile LogService m_log;  /* injected by dependency manager */
    private volatile Store m_store;     /* injected by dependency manager */
    private volatile ResourceCreator m_resourceCreator;     /* injected by dependency manager */
    
    private final int BUFFER_SIZE = 8 * 1024;
    private final Properties m_sessionProperties;
    
    private File m_baseDir;
    private WritableRepository m_repository;
    
    public BufferImpl(String sessionId) {
        m_sessionProperties = new Properties();
        m_sessionProperties.put(SessionRegister.SERVICE_SESSION_ID, sessionId);
    }
    
    /*
     * Called by dependency manager
     */
    @SuppressWarnings({"UseOfObsoleteCollectionType", "unchecked"})
    void init() {
        Dictionary props = new java.util.Hashtable();
        props.put(EventConstants.EVENT_TOPIC, PluginManager.class.getName().replace(".", "/") + "/*");
        props.put(EventConstants.EVENT_FILTER, "(" + PluginManager.PROPERTY_PLUGIN_TYPES + "=*" + ResourceDAO.class.getName() + "*)");
        m_context.registerService(EventHandler.class.getName(), this, props);
        
        m_baseDir = m_context.getDataFile(m_sessionProperties.getProperty(SessionRegister.SERVICE_SESSION_ID));
        if (!m_baseDir.exists()) {
            m_baseDir.mkdirs();
        } else if (!m_baseDir.isDirectory()) {
            throw new IllegalStateException("Base directory is not a directory: " + m_baseDir);
        }
        if (!m_baseDir.exists()) {
            throw new IllegalStateException("Base directory for Buffer was not created: " + m_baseDir, new IOException("Can not create directory"));
        }
        
    }

    /*
     * Called by dependency manager
     */
    synchronized void stop() {
        m_repository = null;
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
    public void handleEvent(final Event event) {
        final Object lock = this;
        new Thread(new Runnable() {

            @Override
            public void run() {
                synchronized (lock) {
                    m_repository = null;
                }
            }
        }).start();
    }

    private synchronized void loadRepository() {
        RepositoryDAO rd = m_pluginManager.getPlugin(RepositoryDAO.class);
        
        try {
            m_repository = rd.getRepository(m_baseDir.toURI());
        } catch (IOException ex) {
            m_log.log(LogService.LOG_ERROR, "Could not get repository for URI: " + m_baseDir.toURI(), ex);
            m_repository = m_pluginManager.getPlugin(ResourceCreator.class).createRepository(m_baseDir.toURI());
        }
    }
    
    @Override
    public synchronized Resource put(String name, InputStream artifact) throws IOException, RevokedArtifactException {
        String name2 = m_pluginManager.getPlugin(ActionHandler.class).beforeUploadToBuffer(name, this);
        if (name2 == null || artifact == null || "".equals(name2)) {
            throw new RevokedArtifactException("No file name was given on uploading to buffer");
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
        
        ResourceDAO resourceDao = m_pluginManager.getPlugin(ResourceDAO.class);

        Resource resource = resourceDao.getResource(file.toURI());
        
        // TODO alternatively can be moved to some plugin
        resource.createCapability("file").setProperty("name", name2);
        resource.setSymbolicName(name2);
        if (resource.getPresentationName() == null || "".equals(resource.getPresentationName().trim())) {
            resource.setPresentationName(name2);
        }
        
        Resource tmp;
        try {
            tmp = m_pluginManager.getPlugin(ActionHandler.class).onUploadToBuffer(resource, this, name2);
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
        
        if (m_repository == null) {
            loadRepository();
        }
        if (!m_repository.addResource(resource)) {
            throw new RevokedArtifactException("Resource with the same symbolic name and version already exists in buffer: " + resource.getId());
        }
        
        resourceDao.save(resource);
        
        m_pluginManager.getPlugin(RepositoryDAO.class).saveRepository(m_repository);
        
        return m_pluginManager.getPlugin(ActionHandler.class).afterUploadToBuffer(resource, this, name2);
    }

    @Override
    public synchronized boolean remove(Resource resource) throws IOException {
        resource = m_pluginManager.getPlugin(ActionHandler.class).beforeDeleteFromBuffer(resource, this);
        
        if (!isInBuffer(resource)) {
            if (m_repository != null && m_repository.contains(resource)) {
                m_log.log(LogService.LOG_WARNING, "Resource to be removed is not in buffer but it is in internal repository: " + resource.getId() + ", cleaning up");
                m_repository.removeResource(resource);
            }
            return false;
        }
        
        // if URI scheme is not 'file', it is detected in previous isInBuffer() check
        File file = new File(resource.getUri());
        if (!file.delete()) {
            throw new IOException("Can not delete artifact file from buffer: " + resource.getUri());
        }
        
        ResourceDAO resourceDao = m_pluginManager.getPlugin(ResourceDAO.class);
        try {
            resourceDao.remove(resource);
        } finally {
            // once the artifact file was removed, the resource has to be removed
            // from the repository even in case of exception on removing metadata
            // to keep consistency of repository with stored artifact files
            if (m_repository == null) {
                loadRepository();
            }
            if (!m_repository.removeResource(resource)) {
                m_log.log(LogService.LOG_WARNING, "Buffer's internal repository does not contain removing resource: " + resource.getId());
            }
            m_pluginManager.getPlugin(RepositoryDAO.class).saveRepository(m_repository);
        }
        
        m_pluginManager.getPlugin(ActionHandler.class).afterDeleteFromBuffer(resource, this);
        
        return true;
    }

    @Override
    public synchronized List<Resource> commit(boolean move) throws IOException {
        List<Resource> resourcesToCommit = m_pluginManager.getPlugin(ActionHandler.class).beforeBufferCommit(Arrays.asList(m_repository.getResources()), this, m_store);
        
        List<Resource> commited = new ArrayList<Resource>();
        List<Resource> resourcesToRemove = new ArrayList<Resource>();
        Map<String, String[]> toRemoveNonrenamed = new HashMap<String, String[]>(); // K: new ID, V: old sn, old ver
        ResourceDAO resourceDao = m_pluginManager.getPlugin(ResourceDAO.class);
        
        // put resources to store
        if (move && (m_store instanceof FilebasedStoreImpl)) {
            for (Resource resource : resourcesToCommit) {
                Resource putResource;
                try {
                    String[] old = new String[] {resource.getSymbolicName(), resource.getVersion().toString()};
                    putResource = ((FilebasedStoreImpl) m_store).move(resource);
                    toRemoveNonrenamed.put(resource.getId(), old);
                } catch (RevokedArtifactException ex) {
                    m_log.log(LogService.LOG_INFO, "Resource can not be commited, it was revoked by store: " + resource.getId());
                    continue;
                }
                commited.add(putResource);
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
                commited.add(putResource);
                if (move) {
                    resourcesToRemove.add(resource);
                }
            }
        }
        
        // remove resources from buffer
        if (move) {
            if (m_repository == null) {
                loadRepository();
            }
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
                        // cleanup (after renamed resource)
                        Resource fake = m_resourceCreator.createResource();
                        fake.setSymbolicName(toRemoveNonrenamed.get(resource.getId())[0]);
                        fake.setVersion(toRemoveNonrenamed.get(resource.getId())[1]);
                        if (!m_repository.removeResource(fake)) {
                            m_log.log(LogService.LOG_WARNING, "Buffer's internal repository does not contain removing resource: " + resource.getId());
                        }
                    }
                    m_pluginManager.getPlugin(RepositoryDAO.class).saveRepository(m_repository);
                    throw e;
                }
                if (!m_repository.removeResource(resource)) {
                    // cleanup (after renamed resource)
                    Resource fake = m_resourceCreator.createResource();
                    fake.setSymbolicName(toRemoveNonrenamed.get(resource.getId())[0]);
                    fake.setVersion(toRemoveNonrenamed.get(resource.getId())[1]);
                    if (!m_repository.removeResource(fake)) {
                        m_log.log(LogService.LOG_WARNING, "Buffer's internal repository does not contain removing resource: " + resource.getId());
                    }
                }
            }
            m_pluginManager.getPlugin(RepositoryDAO.class).saveRepository(m_repository);
        }
        
        m_pluginManager.getPlugin(ActionHandler.class).afterBufferCommit(commited, this, m_store);
        
        return commited;
    }

    @Override
    public Repository getRepository() {
        if (m_repository == null) {
            loadRepository();
        }
        return m_repository;
    }

    @Override
    public synchronized void execute(List<Resource> resources, final Executable executable, final Properties properties) {
        final ActionHandler ah = m_pluginManager.getPlugin(ActionHandler.class);
        final List<Resource> res = ah.beforeExecuteInBuffer(resources, executable, properties, this);
        final Buffer buffer = this;

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    executable.executeOnBuffer(res, m_store, buffer, properties);
                } catch (Exception e) {
                    m_log.log(LogService.LOG_ERROR, "Executable plugin threw an exception while executed in buffer: " + executable.getPluginDescription(), e);
                }
                ah.afterExecuteInBuffer(res, executable, properties, buffer);
            }
        }).start();
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
