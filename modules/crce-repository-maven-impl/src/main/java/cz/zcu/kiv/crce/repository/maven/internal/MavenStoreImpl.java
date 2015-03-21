package cz.zcu.kiv.crce.repository.maven.internal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.concurrency.service.TaskRunnerService;
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
import cz.zcu.kiv.crce.repository.RefusedArtifactException;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.Executable;
import cz.zcu.kiv.crce.resolver.ResourceLoader;

/**
 *
 * @author Miroslav Brozek
 */
public class MavenStoreImpl implements Store {

    private static final Logger logger = LoggerFactory.getLogger(MavenStoreImpl.class);

	private volatile RepositoryDAO repositoryDAO;
	private volatile ResourceDAO resourceDAO;
	private volatile TaskRunnerService taskRunnerService;
    private volatile ResourceIndexerService resourceIndexerService;
    private volatile MetadataFactory metadataFactory;
    private volatile MetadataService metadataService;
    private volatile MetadataValidator metadataValidator;
    private volatile IdentityIndexer identityIndexer;
    private volatile ResourceLoader resourceLoader;
    private volatile LocalMavenRepositoryIndexer repositoryIndexer;

    private Repository repository;
    private final URI baseUri;

    MavenStoreImpl(URI baseUri) throws IOException {
        this.baseUri = baseUri;
        File mvnStorePath = new File(baseUri);
       
        if (!mvnStorePath.exists()) {
            if (!mvnStorePath.mkdirs()) {
                logger.error("Could not create maven store directory {}", mvnStorePath);
            }
        } else if (!mvnStorePath.isDirectory()) {
            throw new IOException("Base directory is not a directory: " + mvnStorePath);
        }
        if (!mvnStorePath.exists()) {
            throw new IllegalStateException("Base direcotory could be not created: " + mvnStorePath, new IOException("Can not create directory"));
        }
    }

    @Override
    public Resource put(Resource resource) throws IOException, RefusedArtifactException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean remove(Resource resource) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Resource> getResources() {
        try {
            return resourceDAO.loadResources(repository);
        } catch (IOException e) {
            logger.error("Could not load resources of repository {}.", baseUri, e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Resource> getResources(Requirement requirement) {
        List<Resource> resources = Collections.emptyList();
        try {
            resources = resourceLoader.getResources(repository, requirement);
        } catch (IOException e) {
            logger.error("Could not load resources for requirement ({})", requirement.getNamespace(), e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("getResources(requirement={}) returns {}", requirement.getNamespace(), resources.size());
        }
        return resources;
    }

    @Override
    public void execute(List<Resource> resources, Executable executable, Properties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Called by dependency manager.
     */
    synchronized void start() {
        try {
            repository = repositoryDAO.loadRepository(baseUri);
        } catch (IOException ex) {
            logger.error("Could not load repository for {}", baseUri, ex);
        }

        if (repository == null) {
            repository = metadataFactory.createRepository(baseUri);
            try {
                repositoryDAO.saveRepository(repository);
            } catch (IOException ex) {
                logger.error("Could not save repository for {}", baseUri, ex);
            }
        }               
        
        index();             
    }    

    /**
     * Main method for start indexing local maven repository
     */    
    private void index() {
        taskRunnerService.scheduleTask(new LocalMavenRepositoryIndexer(baseUri, new MetadataIndexerCallback() {

            @Override
            public void index(File file) {
                try {
                    if (resourceIndexerService != null && !resourceDAO.existsResource(file.toURI())) {
                        Resource resource;
                        try {
                            resource = resourceIndexerService.indexResource(file);
                        } catch (IOException e) {
                            logger.error("Could not index file {}", file, e);
                            return;
                        }
                        metadataService.getIdentity(resource).setAttribute("repository-id", String.class, repository.getId());

                        identityIndexer.preIndex(file, file.getName(), resource);
                        identityIndexer.postIndex(file, resource);

                        ResourceValidationResult validationResult = metadataValidator.validate(resource);
                        if (!validationResult.isContextValid()) {
                            logger.error("Indexed Resource {} is not valid:\r\n{}", resource.getId(), validationResult);
                            return;
                        }
                        logger.info("Indexed resource {} is valid.", resource.getId());

                        try {
                            resourceDAO.saveResource(resource);
                        } catch (IOException e) {
                            logger.error("Could not save indexed resource for file {}: {}", file, resource, e);
                        }
                    }
                } catch (IOException e) {
                    logger.error("Could not check that resource exists: {}", file, e);
                }
            }

			@Override
			public void setIndexer(Object indexer) {
				repositoryIndexer = (LocalMavenRepositoryIndexer)indexer;
				logger.debug("Maven Repository Indexer created: {}",repositoryIndexer.getClass() );
			}
        }));     
   
    }
}
