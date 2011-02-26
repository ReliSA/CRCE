package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.plugins.ResourceDAO;
import cz.zcu.kiv.crce.plugin.Plugin;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.plugins.ResourceDAOFactory;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;
import cz.zcu.kiv.crce.repository.plugins.RepositoryDAO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.log.LogService;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class ResourceBufferImpl implements Buffer {
    
    private int BUFFER_SIZE = 8 * 1024;
    
    private volatile BundleContext m_context; /* injected by dependency manager */
    private volatile PluginManager m_pluginManager; /* injected by dependency manager */
    private volatile LogService m_log; /* injected by dependency manager */
    
    private File m_baseDir;
    
    private Repository m_repository;
    
    private void setUpBaseDir() {
        m_baseDir = m_context.getDataFile("buffer");
        if (!m_baseDir.exists()) {
            m_baseDir.mkdir();
        } else if (!m_baseDir.isDirectory()) {
            m_baseDir.delete();
            m_baseDir.mkdir();
        }
        try {
            m_repository = m_pluginManager.getPlugin(RepositoryDAO.class).getRepository(m_baseDir.toURI());
        } catch (IOException ex) {
            ex.printStackTrace();
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
    public void runTestsOnComponent(Object component) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Resource[] getStoredResources() {
        if (m_baseDir == null) {
            setUpBaseDir();
        }
//        if (m_repository == null) {
//            try {
//                m_repository = m_pluginManager.getPlugin(RepositoryDAO.class).getRepository(m_baseDir.toURI());
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
        return m_repository.getResources();
    }

    @Override
    public void executeOnStored(Plugin[] plugins) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updated(Dictionary dctnr) throws ConfigurationException {
        // do nothing yet
    }
}
