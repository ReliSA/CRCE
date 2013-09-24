package cz.zcu.kiv.crce.metadata.wrapper.felix;

import cz.zcu.kiv.crce.metadata.Repository;
import org.apache.felix.bundlerepository.Resource;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class RepositoryWrapper implements org.apache.felix.bundlerepository.Repository {

    Repository m_repository;
    
    public RepositoryWrapper(Repository repository) {
        m_repository = repository;
    }
    
    @Override
    public String getURI() {
        return m_repository.getURI().toString();
    }

    @Override
    public Resource[] getResources() {
        return Wrapper.wrap(m_repository.getResources());
    }

    @Override
    public String getName() {
        return m_repository.getName();
    }

    @Override
    public long getLastModified() {
        return m_repository.getLastModified();
    }
    
}
