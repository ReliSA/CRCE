package cz.zcu.kiv.crce.repository.filebased.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Nonnull;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.RepositoryDAO;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.indexer.ResourceIndexerService;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.service.validation.MetadataValidator;
import cz.zcu.kiv.crce.metadata.service.validation.ResourceValidationResult;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;
import cz.zcu.kiv.crce.repository.SessionRegister;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;
import cz.zcu.kiv.crce.repository.plugins.Executable;

/**
 * Filebased implementation of <code>Buffer</code>.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class BufferImpl implements Buffer, EventHandler {

    // injected by dependency manager
    private volatile BundleContext context;
    private volatile PluginManager pluginManager;
    private volatile Store store;
    private volatile MetadataFactory metadataFactory;
    private volatile ResourceDAO resourceDAO;
    private volatile RepositoryDAO repositoryDAO;
    private volatile ResourceIndexerService resourceIndexerService;
    private volatile MetadataService metadataService;
    private volatile MetadataValidator metadataValidator;
    private volatile IdentityIndexer identityIndexer;
    // injected by dependency manager

    private final int BUFFER_SIZE = 8 * 1024;
    private final Dictionary<String, String> sessionProperties;

    private File baseDir;
    private Repository repository;

    private static final Logger logger = LoggerFactory.getLogger(BufferImpl.class);

    @SuppressWarnings("UseOfObsoleteCollectionType")
    public BufferImpl(String sessionId) {
        sessionProperties = new Hashtable<>();
        sessionProperties.put(SessionRegister.SERVICE_SESSION_ID, sessionId);
    }

    /*
     * Called by dependency manager
     */
    @SuppressWarnings({"UseOfObsoleteCollectionType", "unchecked"})
    void init() {
        Dictionary<String, String> props = new Hashtable<>();
        props.put(EventConstants.EVENT_TOPIC, PluginManager.class.getName().replace(".", "/") + "/*");
        props.put(EventConstants.EVENT_FILTER, "(" + PluginManager.PROPERTY_PLUGIN_TYPES + "=*" + ResourceDAO.class.getName() + "*)");
        context.registerService(EventHandler.class.getName(), this, props);

        baseDir = context.getDataFile(sessionProperties.get(SessionRegister.SERVICE_SESSION_ID));
        if (!baseDir.exists()) {
            if (!baseDir.mkdirs()) {
                logger.error("Could not create buffer directory {}, session: {}",
                        baseDir, sessionProperties.get(SessionRegister.SERVICE_SESSION_ID));
            }
        } else if (!baseDir.isDirectory()) {
            throw new IllegalStateException("Base directory is not a directory: " + baseDir);
        }
        if (!baseDir.exists()) {
            throw new IllegalStateException("Base directory for Buffer was not created: " + baseDir, new IOException("Can not create directory"));
        }
    }

    /*
     * Called by dependency manager
     */
    void start() {
        try {
            repository = repositoryDAO.loadRepository(baseDir.toURI());
        } catch (IOException ex) {
            logger.error("Could not load repository for {}", baseDir, ex);
        }

        if (repository == null) {
            repository = metadataFactory.createRepository(baseDir.toURI());
            try {
                repositoryDAO.saveRepository(repository);
            } catch (IOException ex) {
                logger.error("Could not save repository for {}", baseDir, ex);
            }
        }
    }

    /*
     * Called by dependency manager
     */
    synchronized void stop() {
//        m_repository = null;
        for (File file : baseDir.listFiles()) {
            if (!file.delete()) {
                file.deleteOnExit();
                logger.warn("Can not delete file from destroyed buffer, deleteOnExit was set: {}", file);
            }
        }
        if (!baseDir.delete()) {
            baseDir.deleteOnExit();
            logger.warn("Can not delete file from destroyed buffer's base dir, deleteOnExit was set: {}", baseDir);
        }
    }


    @Override
    public void handleEvent(final Event event) {
//        final Object lock = this;
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                synchronized (lock) {
//                    m_repository = null;
//                }
//            }
//        }).start();
//    }

//    private synchronized void loadRepository() {
//        RepositoryDAO rd = m_pluginManager.getPlugin(RepositoryDAO.class);
//
//        try {
//            m_repository = rd.getRepository(m_baseDir.toURI());
//        } catch (IOException ex) {
//        	logger.error("Could not get repository for URI: {}", m_baseDir.toURI(), ex);
//            m_repository = m_pluginManager.getPlugin(MetadataFactory.class).createRepository(m_baseDir.toURI());
//        }
    }

    @Override
    public Resource put(Resource resource) throws IOException, RefusedArtifactException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public synchronized Resource put(String name, InputStream artifact) throws IOException, RefusedArtifactException {
        String name2 = pluginManager.getPlugin(ActionHandler.class).beforeUploadToBuffer(name, this);
        if (name2 == null || artifact == null || "".equals(name2)) {
            throw new RefusedArtifactException("No file name was given on uploading to buffer");
        }

        File file = File.createTempFile("res", ".tmp", baseDir);
        try (FileOutputStream output = new FileOutputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            for (int count = artifact.read(buffer); count != -1; count = artifact.read(buffer)) {
                output.write(buffer, 0, count);
            }
        }

//        Resource resource = resourceDAO.loadResource(file.toURI());
        Resource resource = null;
        if (resource == null) {
            resource = resourceIndexerService.indexResource(file);
        }
        metadataService.getIdentity(resource).setAttribute("repository-id", String.class, repository.getId());

        identityIndexer.preIndex(file, name2, resource);

        String presentationName = metadataService.getPresentationName(resource);
        if (presentationName.trim().isEmpty() || presentationName.startsWith("unknown-name:")) {
            metadataService.setPresentationName(resource, name2);
        }

        Resource tmp;
        try {
            tmp = pluginManager.getPlugin(ActionHandler.class).onUploadToBuffer(resource, this, name2);
        } catch (RefusedArtifactException e) {
            if (!file.delete()) {
            	logger.error( "Can not delete file of revoked artifact: {}", file.getPath());
            }
            throw e;
        }

        identityIndexer.postIndex(file, resource);

        if (tmp == null) {
        	logger.error( "ActionHandler onUploadToBuffer returned null resource, using original");
        } else {
            resource = tmp;
        }

        ResourceValidationResult validationResult = metadataValidator.validate(resource);
        if (!validationResult.isContextValid()) {
            logger.error("Uploaded Resource {} is not valid:\r\n{}", resource.getId(), validationResult);
            throw new RefusedArtifactException("Resource is not valid.");
        }

        logger.info("Uploaded resource {} is valid.", resource.getId());

        metadataService.getIdentity(resource).setAttribute("status", String.class, "buffered");

        resourceDAO.saveResource(resource);

        return pluginManager.getPlugin(ActionHandler.class).afterUploadToBuffer(resource, this, name2);
    }

    @Override
    public synchronized boolean remove(Resource resource) throws IOException {
        resource = pluginManager.getPlugin(ActionHandler.class).beforeDeleteFromBuffer(resource, this);

        if (!isInBuffer(resource)) {
            // TODO is this logic correct with new API?
//            if (m_ResourceDAO.existsResource(LegacyMetadataHelper.getUri(resource))) {
//            	logger.warn( "Resource to be removed is not in buffer but it is in internal repository: {}, cleaning up", resource.getId());
//                m_ResourceDAO.deleteResource(LegacyMetadataHelper.getUri(resource));
//            }
            return false;
        }

        // if URI scheme is not 'file', it is detected in previous isInBuffer() check
        File file = new File(metadataService.getUri(resource));
        if (!file.delete()) {
            throw new IOException("Can not delete artifact file from buffer: " + metadataService.getUri(resource));
        }

//        ResourceDAO resourceDao = m_pluginManager.getPlugin(ResourceDAO.class);
//        try {
            resourceDAO.deleteResource(metadataService.getUri(resource));
//        } finally {
            // once the artifact file was removed, the resource has to be removed
            // from the repository even in case of exception on removing metadata
            // to keep consistency of repository with stored artifact files
//            if (m_repository == null) {
//                loadRepository();
//            }
//            if (!m_repository.removeResource(resource)) {
//            	logger.warn("Buffer's internal repository does not contain removing resource: {}", resource.getId());
//            }
//            m_pluginManager.getPlugin(RepositoryDAO.class).saveRepository(m_repository);
//        }

        pluginManager.getPlugin(ActionHandler.class).afterDeleteFromBuffer(resource, this);

        return true;
    }

    @Override
    public synchronized List<Resource> commit(boolean move) throws IOException {
        List<Resource> resources = resourceDAO.loadResources(repository);
        List<Resource> resourcesToCommit = pluginManager.getPlugin(ActionHandler.class).beforeBufferCommit(resources, this, store);

        List<Resource> commitedResources = new ArrayList<>();
        List<URI> resourcesToRemove = new ArrayList<>();

        // put resources to store
        if (move && store instanceof FilebasedStoreImpl) {
            for (Resource resource : resourcesToCommit) {
                Resource commitedResource;
                try {
                    resourcesToRemove.add(metadataService.getUri(resource));
                    commitedResource = ((FilebasedStoreImpl) store).move(resource);
                } catch (RefusedArtifactException ex) {
                	logger.info( "Resource can not be commited, it was revoked by store: {}", resource.getId(), ex);
                    continue;
                }
                commitedResources.add(commitedResource);
            }
        } else {
            for (Resource resource : resourcesToCommit) {
                Resource putResource;
                URI uri = metadataService.getUri(resource);
                try {
                    putResource = store.put(resource);
                } catch (RefusedArtifactException ex) {
                	logger.info( "Resource can not be commited, it was revoked by store: {}", resource.getId(), ex);
                    continue;
                }
                commitedResources.add(putResource);
                if (move) {
                    resourcesToRemove.add(uri);
                }
            }
        }

        // remove resources from buffer
        if (move) {
            for (URI resource : resourcesToRemove) {
                File resourceFile = new File(resource);
                if (resourceFile.exists() && !resourceFile.delete()) {
                    logger.error( "Can not delete artifact from buffer: {}", resource);
                    continue;
                }
                try {
                    resourceDAO.deleteResource(resource);
                } catch (IOException e) {
                    // once the artifact file was removed, the resource has to be removed
                    // from the repository even in case of exception on removing metadata
                    // to keep consistency of repository with stored artifact files
//                    if (!m_repository.removeResource(resource)) {
//                        // cleanup (after renamed resource)
//                        Resource fake = m_metadataFactory.createResource();
//                        fake.setSymbolicName(toRemoveNonrenamed.get(resource.getId())[0]);
//                        fake.setVersion(toRemoveNonrenamed.get(resource.getId())[1]);
//                        if (!m_repository.removeResource(fake)) {
//                        	logger.warn( "Buffer's internal repository does not contain removing resource: {}", resource.getId());
//                        }
//                    }
//                    m_pluginManager.getPlugin(RepositoryDAO.class).saveRepository(m_repository);
                    throw e;
                }
//                if (!m_repository.removeResource(resource)) {
//                    // cleanup (after renamed resource)
//                    Resource fake = m_metadataFactory.createResource();
//                    fake.setSymbolicName(toRemoveNonrenamed.get(resource.getId())[0]);
//                    fake.setVersion(toRemoveNonrenamed.get(resource.getId())[1]);
//                    if (!m_repository.removeResource(fake)) {
//                    	logger.warn("Buffer's internal repository does not contain removing resource: {}", resource.getId());
//                    }
//                }
            }
//            m_pluginManager.getPlugin(RepositoryDAO.class).saveRepository(m_repository);
        }

        pluginManager.getPlugin(ActionHandler.class).afterBufferCommit(commitedResources, this, store);

        return commitedResources;
    }

    @Override
    public List<Resource> commit(List<Resource> resources, boolean move) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized void execute(List<Resource> resources, final Executable executable, final Properties properties) {
        final ActionHandler ah = pluginManager.getPlugin(ActionHandler.class);
        final List<Resource> res = ah.beforeExecuteInBuffer(resources, executable, properties, this);
        final Buffer buffer = this;

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    executable.executeOnBuffer(res, store, buffer, properties);
                } catch (Exception e) {
                	logger.error( "Executable plugin2 threw an exception while executed in buffer: {}", executable.getPluginDescription(), e);
                }
                ah.afterExecuteInBuffer(res, executable, properties, buffer);
            }
        }).start();
    }

    Dictionary<String, String> getSessionProperties() {
        return sessionProperties;
    }

    @Override
    public List<Resource> getResources() {
        try {
            return resourceDAO.loadResources(repository);
        } catch (IOException e) {
            logger.error("Could not load resources of repository {}.", baseDir.toURI(), e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Resource> getResources(Requirement requirement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Nonnull
    @Override
    public List<Resource> getResources(Set<Requirement> requirement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private boolean isInBuffer(Resource resource) {
        URI uri = metadataService.getUri(resource).normalize();
        if (!"file".equals(uri.getScheme())) {
            return false;
        }
        return new File(uri).getPath().startsWith(baseDir.getAbsolutePath());
    }
}
