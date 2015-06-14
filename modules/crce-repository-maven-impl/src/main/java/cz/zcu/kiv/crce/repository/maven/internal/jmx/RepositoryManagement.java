package cz.zcu.kiv.crce.repository.maven.internal.jmx;


import java.io.IOException;

import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.maven.internal.MavenStoreConfiguration;
import cz.zcu.kiv.crce.repository.maven.internal.MavenStoreImpl;
import cz.zcu.kiv.crce.repository.maven.internal.RepositoryConfiguration;

/**
 *
 * @author jkucera
 */
public class RepositoryManagement implements RepositoryManagementMXBean {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RepositoryManagement.class);
    
    private MavenStoreImpl mavenStoreImpl;
    private MavenStoreConfiguration mavenStoreConfiguration;
    private RepositoryConfiguration repositoryConfiguration;
            
    @Override
    public String getName() {
        return repositoryConfiguration.getName();
    }

    @Override
    public String getUri() {
        return repositoryConfiguration.getUri().toString();
    }

    @Override
    public String getType() {
        return mavenStoreConfiguration.getPrimaryRepository().toString();
    }

    @Override
    public int deleteResources() {
        int count = 0;
        for (Resource resource : mavenStoreImpl.getResources()) {
            try {
                mavenStoreImpl.remove(resource);
                count++;
            } catch (IOException e) {
                logger.error("Could not delete resource: " + resource.getId(), e);
            }
        }
        return count;
    }

    @Override
    public void index() {
        mavenStoreImpl.index();
    }

    public void setMavenStoreImpl(MavenStoreImpl mavenStoreImpl) {
        this.mavenStoreImpl = mavenStoreImpl;
    }

    public void setMavenStoreConfiguration(MavenStoreConfiguration mavenStoreConfiguration) {
        this.mavenStoreConfiguration = mavenStoreConfiguration;
    }

    public void setRepositoryConfiguration(RepositoryConfiguration repositoryConfiguration) {
        this.repositoryConfiguration = repositoryConfiguration;
    }
}
