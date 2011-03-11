package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.WritableRepository;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.Executable;
import cz.zcu.kiv.crce.repository.plugins.RepositoryDAO;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.osgi.service.log.LogService;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class FilebasedStoreImpl implements Store {
    
    private volatile PluginManager m_pluginManager;
    private volatile LogService m_log;

    private WritableRepository m_repository;
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
    
    void init() {
        RepositoryDAO repDao = m_pluginManager.getPlugin(RepositoryDAO.class);
        try {
            m_repository = repDao.getRepository(m_baseDir.toURI());
        } catch (IOException ex) {
            m_log.log(LogService.LOG_ERROR, "Could not get repository for URI: " + m_baseDir.toURI(), ex);
        }
    }
    
    @Override
    public Resource put(Resource resource) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean remove(Resource resource) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Repository getRepository() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void execute(List<Resource> resources, List<Executable> plugins) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
