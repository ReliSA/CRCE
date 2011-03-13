package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.WritableRepository;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;
import cz.zcu.kiv.crce.repository.plugins.Executable;
import cz.zcu.kiv.crce.repository.plugins.RepositoryDAO;
import cz.zcu.kiv.crce.repository.plugins.ResourceDAO;
import cz.zcu.kiv.crce.repository.plugins.ResourceDAOFactory;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.codehaus.plexus.util.FileUtils;
import org.osgi.service.log.LogService;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class FilebasedStoreImpl implements Store {
    
    private volatile PluginManager m_pluginManager;
    private volatile ResourceCreator m_resourceCreator;
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
        
        RepositoryDAO rd = m_pluginManager.getPlugin(RepositoryDAO.class);
        
        try {
            m_repository = rd.getRepository(m_baseDir.toURI());
        } catch (IOException ex) {
            m_log.log(LogService.LOG_ERROR, "Could not get repository for URI: " + m_baseDir.toURI(), ex);
        }
    }
    
    public Resource move(Resource resource) throws IOException {
        if (resource == null) {
            return resource;
        }
        resource = m_pluginManager.getPlugin(ActionHandler.class).onStorePut(resource, this);
        if ("file".equals(resource.getUri().getScheme())) {
            return putFile(resource, true);
        } else {
            return putAnother(resource, true);
        }
    }
    
    @Override
    public Resource put(Resource resource) throws IOException {
        if (resource == null) {
            return resource;
        }
        resource = m_pluginManager.getPlugin(ActionHandler.class).onStorePut(resource, this);
        if ("file".equals(resource.getUri().getScheme())) {
            return putFile(resource, false);
        } else {
            return putAnother(resource, false);
        }
    }
    
    private Resource putFile(Resource resource, boolean move) throws IOException {
        File sourceFile = new File(resource.getUri());
        File targetFile = File.createTempFile("res", "", m_baseDir);
        
        ResourceDAO resourceDao = m_pluginManager.getPlugin(ResourceDAOFactory.class).getResourceDAO();
        
        Resource out = resourceDao.moveResource(resource, targetFile.toURI());
        if (move) {
            FileUtils.rename(sourceFile, targetFile);
        } else {
            FileUtils.copyFile(sourceFile, targetFile);
        }

        resourceDao.save(out);
        
        m_repository.addResource(out);
        
        m_pluginManager.getPlugin(RepositoryDAO.class).saveRepository(m_repository);
        
        return out;
    }
    
    private Resource putAnother(Resource resource, boolean move) {
        throw new UnsupportedOperationException("Put resource from another URI than file not supported yet: " + resource.getId() + ": " + resource.getUri());
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
