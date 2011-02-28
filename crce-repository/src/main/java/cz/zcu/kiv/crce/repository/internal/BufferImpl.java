package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.plugins.ResourceDAO;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.plugins.ResourceDAOFactory;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.SessionFactory;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;
import cz.zcu.kiv.crce.repository.plugins.RepositoryDAO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Dictionary;
import java.util.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class BufferImpl implements Buffer, ManagedService {

    private volatile BundleContext m_context; /* injected by dependency manager */
    private volatile PluginManager m_pluginManager; /* injected by dependency manager */
    private volatile LogService m_log; /* injected by dependency manager */
    
    private final int BUFFER_SIZE = 8 * 1024;
    private final Properties m_sessionProperties;
    
    private Store m_store = null;
    private File m_baseDir;
    private Repository m_repository;
    
    public BufferImpl(String sessionId) {
        m_sessionProperties = new Properties();
        m_sessionProperties.put(SessionFactory.SERVICE_SESSION_ID, sessionId);
    }
    
    private void setUpBaseDir() {
        m_baseDir = m_context.getDataFile(m_sessionProperties.getProperty(SessionFactory.SERVICE_SESSION_ID));
        if (!m_baseDir.exists()) {
            m_baseDir.mkdir();
        } else if (!m_baseDir.isDirectory()) {
            m_baseDir.delete();
            m_baseDir.mkdir();
        }
        try {
            m_repository = m_pluginManager.getPlugin(RepositoryDAO.class).getRepository(m_baseDir.toURI());
        } catch (IOException ex) {
            ex.printStackTrace(); // XXX
        }
    }

    @Override
    public synchronized Resource put(String name, InputStream artifact) throws IOException {
        if (name == null || artifact == null || "".equals(name)) {
            return null;
        }
        if (m_baseDir == null) {
            setUpBaseDir();
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
    public synchronized void commit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Resource[] getStoredResources() {
        if (m_baseDir == null) {
            setUpBaseDir();
        }
        return m_repository.getResources();
    }

    @Override
    public void executeOnStored(Plugin[] plugins) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updated(Dictionary dict) throws ConfigurationException {
        if (dict == null) {
            return;
        }
        URI uri;
        try {
            uri = new URI((String) dict.get("uri"));
        } catch (URISyntaxException ex) {
            throw new ConfigurationException("uri", "Invalid URI: " + dict.get("uri"), ex);
        }
        
        ServiceReference[] refs;
        try {
            refs = m_context.getServiceReferences(Store.class.getName(), "(" + "scheme" + "=" + uri.getScheme() + ")");
        } catch (InvalidSyntaxException ex) {
            throw new IllegalArgumentException("Unexpected InvalidSyntaxException caused by invalid filter", ex);
        }
        
        if (refs != null && refs.length > 0) {
            m_store = (Store) m_context.getService(refs[0]);
            System.out.println("m_store: " + m_store.toString());
        } else {
            throw new ConfigurationException("uri", "No registered Store service for given uri: " + uri);
        }
    }

    Dictionary getSessionProperties() {
        return m_sessionProperties;
    }
}
