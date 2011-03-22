package cz.zcu.kiv.crce.repository.internal;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.WritableRepository;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;
import cz.zcu.kiv.crce.repository.plugins.Executable;
import cz.zcu.kiv.crce.repository.plugins.RepositoryDAO;
import cz.zcu.kiv.crce.repository.plugins.ResourceDAO;
import cz.zcu.kiv.crce.repository.plugins.ResourceDAOFactory;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import org.codehaus.plexus.util.FileUtils;
import org.osgi.framework.Version;
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
        
        RepositoryDAO rd = m_pluginManager.getPlugin(RepositoryDAO.class);
        
        try {
            m_repository = rd.getRepository(m_baseDir.toURI());
        } catch (IOException ex) {
            m_log.log(LogService.LOG_ERROR, "Could not get repository for URI: " + m_baseDir.toURI(), ex);
        }
    }
    
    public synchronized Resource move(Resource resource) throws IOException, RevokedArtifactException {
        if (resource == null) {
            return resource;
        }
        Resource tmp = m_pluginManager.getPlugin(ActionHandler.class).onPutToStore(resource, this);
        if (tmp == null) {
            m_log.log(LogService.LOG_ERROR, "ActionHandler onPutToStore returned null resource, using original");
        } else {
            resource = tmp;
        }
        if (m_repository.contains(resource)) {
            throw new RevokedArtifactException("Resource with the same symbolic name and version already exists in Store: " + resource.getId());
        }
        if ("file".equals(resource.getUri().getScheme())) {
            return putFile(resource, true);
        } else {
            return putAnother(resource, true);
        }
    }
    
    @Override
    public synchronized Resource put(Resource resource) throws IOException, RevokedArtifactException {
        if (resource == null) {
            return null;
        }
        Resource tmp = m_pluginManager.getPlugin(ActionHandler.class).onPutToStore(resource, this);
        if (tmp == null) {
            m_log.log(LogService.LOG_ERROR, "ActionHandler onPutToStore returned null resource, using original");
        } else {
            resource = tmp;
        }
        if (m_repository.contains(resource)) {
            throw new RevokedArtifactException("Resource with the same symbolic name and version already exists in Store: " + resource.getId());
        }
        if ("file".equals(resource.getUri().getScheme())) {
            return putFile(resource, false);
        } else {
            return putAnother(resource, false);
        }
    }
    
    private Resource putFile(Resource resource, boolean move) throws IOException, RevokedArtifactException {
        File sourceFile = new File(resource.getUri());
        File targetFile = File.createTempFile("res", "", m_baseDir);
        
        ResourceDAO resourceDao = m_pluginManager.getPlugin(ResourceDAOFactory.class).getResourceDAO();
        
        Resource out = resourceDao.moveResource(resource, targetFile.toURI());
        if (move) {
            FileUtils.rename(sourceFile, targetFile);
        } else {
            FileUtils.copyFile(sourceFile, targetFile);
        }

        if (!m_repository.addResource(out)) {
            m_log.log(LogService.LOG_WARNING, "Resource with the same symbolic name and version already exists in Store, but it has not been expected: " + targetFile.getPath());
            if (!targetFile.delete()) {
                throw new IOException("Can not delete file of revoked artifact: " + targetFile.getPath());
            }
            return out;
        }
        
//        Version version = out.getVersion();
//        for (int i = 2; !m_repository.addResource(out); i++) {
//            out.setVersion(new Version(version.getMajor(), version.getMinor(), version.getMicro(), version.getQualifier() + "_" + i));
//            if (resource.getVersion().equals(version)) {
//                throw new RevokedArtifactException("Resource with the same symbolic name and version already exists in Store: " + out.getId());
//            }
//        }
        
        resourceDao.save(out);
        
        m_pluginManager.getPlugin(RepositoryDAO.class).saveRepository(m_repository);
        
        return out;
    }
    
    private Resource putAnother(Resource resource, boolean move) {
        throw new UnsupportedOperationException("Put resource from another URI than file not supported yet: " + resource.getId() + ": " + resource.getUri());
    }

    @Override
    public synchronized boolean remove(Resource resource) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public synchronized Repository getRepository() {
        return m_repository;
    }

    @Override
    public synchronized void execute(Collection<Resource> resource, Executable plugin, Properties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
