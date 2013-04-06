package cz.zcu.kiv.crce.repository.internal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Dictionary;
import java.util.List;
import java.util.Properties;

import org.codehaus.plexus.util.FileUtils;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.legacy.LegacyMetadataHelper;
//import cz.zcu.kiv.crce.metadata.dao.RepositoryDAO;
//import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;
import cz.zcu.kiv.crce.repository.plugins.Executable;

/**
 * Filebased implementation of <code>Store</code>.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class FilebasedStoreImpl implements Store, EventHandler {

    private volatile BundleContext m_context;
    private volatile PluginManager m_pluginManager;
    private volatile ResourceDAO m_ResourceDAO;

    private static final Logger logger = LoggerFactory.getLogger(FilebasedStoreImpl.class);

//    private WritableRepository m_repository;
    private File m_baseDir;


    public FilebasedStoreImpl(File baseDir) throws IOException {
        m_baseDir = baseDir;
        if (!m_baseDir.exists()) {
            m_baseDir.mkdirs();
        } else if (!m_baseDir.isDirectory()) {
            throw new IOException("Base directory is not a directory: " + m_baseDir);
        }
        if (!m_baseDir.exists()) {
            throw new IllegalStateException("Base directory for Buffer was not created: " + m_baseDir, new IOException("Can not create directory"));
        }
    }

    /*
     * Called by dependency manager
     */
    @SuppressWarnings({"UseOfObsoleteCollectionType", "unchecked"})
    void init() {
        Dictionary<String, String> props = new java.util.Hashtable<>();
        props.put(EventConstants.EVENT_TOPIC, PluginManager.class.getName().replace(".", "/") + "/*");
        props.put(EventConstants.EVENT_FILTER, "(" + PluginManager.PROPERTY_PLUGIN_TYPES + "=*" + ResourceDAO.class.getName() + "*)");
        m_context.registerService(EventHandler.class.getName(), this, props);
    }

    @Override
    public void handleEvent(final Event event) {
    // TODO why was that there?
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
    }

//    private synchronized void loadRepository() {
//        RepositoryDAO rd = m_pluginManager.getPlugin(RepositoryDAO.class);
//
//        try {
//            m_repository = rd.getRepository(m_baseDir.toURI());
//        } catch (IOException ex) {
//            logger.error("Could not get repository for URI: " + m_baseDir.toURI(), ex);
//            m_repository = m_pluginManager.getPlugin(ResourceCreator.class).createRepository(m_baseDir.toURI());
//        }
//    }

    public synchronized Resource move(Resource resource) throws IOException, RevokedArtifactException {
        Resource tmp = m_pluginManager.getPlugin(ActionHandler.class).beforePutToStore(resource, this);
        if (resource == null) {
            return null;
        }
        if (tmp == null) {
        	logger.error( "ActionHandler onPutToStore() returned null resource, using original");
        } else {
            resource = tmp;
        }
//        if (m_repository == null) {
//            loadRepository();
//        }
        if (m_ResourceDAO.existsResource(LegacyMetadataHelper.getUri(resource))) {
            throw new RevokedArtifactException("Resource with the same symbolic name and version already exists in Store: " + resource.getId());
        }
        if ("file".equals(LegacyMetadataHelper.getUri(resource).getScheme())) {
            return putFile(resource, true);
        } else {
            return putAnother(resource, true);
        }
    }

    @Override
    public synchronized Resource put(Resource resource) throws IOException, RevokedArtifactException {
        Resource tmp = m_pluginManager.getPlugin(ActionHandler.class).beforePutToStore(resource, this);
        if (resource == null) {
            return null;
        }
        if (tmp == null) {
        	logger.error( "ActionHandler onPutToStore returned null resource, using original");
        } else {
            resource = tmp;
        }
//        if (m_repository == null) {
//            loadRepository();
//        }
        if (m_ResourceDAO.existsResource(LegacyMetadataHelper.getUri(resource))) {
            throw new RevokedArtifactException("Resource with the same symbolic name and version already exists in Store: " + resource.getId());
        }
        if ("file".equals(LegacyMetadataHelper.getUri(resource).getScheme())) {
            return m_pluginManager.getPlugin(ActionHandler.class).afterPutToStore(putFile(resource, false), this);
        } else {
            return m_pluginManager.getPlugin(ActionHandler.class).afterPutToStore(putAnother(resource, false), this);
        }
    }

    private Resource putFile(Resource resource, boolean move) throws IOException, RevokedArtifactException {
        File sourceFile = new File(LegacyMetadataHelper.getUri(resource));
        if (!sourceFile.exists()) {
            throw new RevokedArtifactException("File to be put tu store does not exist: " + sourceFile.getPath());
        }
//        if (m_repository == null) {
//            loadRepository();
//        }
        File targetFile = File.createTempFile("res", "", m_baseDir);

//        ResourceDAO resourceDao = m_pluginManager.getPlugin(ResourceDAO.class);

        if (move) {
            FileUtils.rename(sourceFile, targetFile);
        } else {
            FileUtils.copyFile(sourceFile, targetFile);
        }
        Resource out = m_ResourceDAO.moveResource(resource, targetFile.toURI());
//        if (!m_repository.addResource(out)) {
//        	logger.warn( "Resource with the same symbolic name and version already exists in Store, but it has not been expected: {}", targetFile.getPath());
//            if (!targetFile.delete()) {
//                throw new IOException("Can not delete file of revoked artifact: " + targetFile.getPath());
//            }
//            return null;
//        }
        m_ResourceDAO.saveResource(out); // TODO is saving necessary after move? define exact contract

//        m_pluginManager.getPlugin(RepositoryDAO.class).saveRepository(m_repository);

        return out;
    }

    private Resource putAnother(Resource resource, boolean move) {
        throw new UnsupportedOperationException("Put resource from another URI than file not supported yet: " + resource.getId() + ": " + LegacyMetadataHelper.getUri(resource));
    }

    @Override
    public synchronized boolean remove(Resource resource) throws IOException {
        resource = m_pluginManager.getPlugin(ActionHandler.class).beforeDeleteFromStore(resource, this);

        if (!isInStore(resource)) {
            if (m_ResourceDAO.existsResource(LegacyMetadataHelper.getUri(resource))) {
            	logger.warn( "Removing resource is not in store but it is in internal repository: {}", resource.getId());
                m_ResourceDAO.deleteResource(LegacyMetadataHelper.getUri(resource));
            }
            m_pluginManager.getPlugin(ActionHandler.class).afterDeleteFromStore(resource, this);
            return false;
        }

        // if URI scheme is not 'file', it is detected in previous isInStore() check
        File file = new File(LegacyMetadataHelper.getUri(resource));
        if (!file.delete()) {
            throw new IOException("Can not delete artifact file from store: " + LegacyMetadataHelper.getUri(resource));
        }

//        ResourceDAO resourceDao = m_pluginManager.getPlugin(ResourceDAO.class);
        try {
            m_ResourceDAO.deleteResource(LegacyMetadataHelper.getUri(resource));
        } finally {
            // once the artifact file was removed, the resource has to be removed
            // from the repository even in case of exception on removing metadata
            // to keep consistency of repository with stored artifact files
//            if (m_repository == null) {
//                loadRepository();
//            }
//            if (!m_ResourceDAO.deleteResource(LegacyMetadataHelper.getUri(resource))) {
//            	logger.warn( "Store's internal repository does not contain removing resource: {}", resource.getId());
//            }
//            m_pluginManager.getPlugin(RepositoryDAO.class).saveRepository(m_repository);
        }
        m_pluginManager.getPlugin(ActionHandler.class).afterDeleteFromStore(resource, this);
        return true;
    }

    @Override
    public Repository getRepository() {
        throw new UnsupportedOperationException("Not supported yet.");
//        if (m_repository == null) {
//            loadRepository();
//        }
//        return m_repository;
    }

    @Override
    public synchronized void execute(List<Resource> resources, final Executable executable, final Properties properties) {
        final ActionHandler ah = m_pluginManager.getPlugin(ActionHandler.class);
        final List<Resource> res = ah.beforeExecuteInStore(resources, executable, properties, this);
        final Store store = this;

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    executable.executeOnStore(res, store, properties);
                } catch (Exception e) {
                	logger.error( "Executable plugin threw an exception while executed in buffer: {}", executable.getPluginDescription(), e);
                }
                ah.afterExecuteInStore(res, executable, properties, store);
            }
        }).start();
    }

    @Override
    public List<Resource> getResources() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Resource> getResources(Requirement requirement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private boolean isInStore(Resource resource) {
        URI uri = LegacyMetadataHelper.getUri(resource).normalize();
        if (!"file".equals(uri.getScheme())) {
            return false;
        }
        return new File(uri).getPath().startsWith(m_baseDir.getAbsolutePath());
    }
}
