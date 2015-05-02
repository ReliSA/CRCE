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
import cz.zcu.kiv.crce.repository.RefusedArtifactException;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.maven.internal.metadata.MetadataIndexerCallbackImpl;
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

    private Repository repository;
    private final URI baseUri;

    MavenStoreImpl(URI baseUri, boolean remoteRepository) throws IOException {
        this.baseUri = baseUri;
        
		if (remoteRepository) {
			logger.debug("URI {} for Remote Maven repository set", baseUri);
		}
		else{
			File mvnStorePath = new File(baseUri);
			
			if (!mvnStorePath.exists()) {
				if (!mvnStorePath.mkdirs()) {
					logger.error("Could not create maven store directory {}", mvnStorePath);
				}
			} else if (!mvnStorePath.isDirectory()) {
				throw new IOException("Base directory is not a directory: " + mvnStorePath);
			}
			if (!mvnStorePath.exists()) {
				throw new IllegalStateException("Base direcotory could be not created: " + mvnStorePath, new IOException(
						"Can not create directory"));
			}			
		}
    }

    @Override
    public Resource put(Resource resource) throws IOException, RefusedArtifactException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	@Override
	public boolean remove(Resource resource) throws IOException {
		// resource = pluginManager.getPlugin(ActionHandler.class).beforeDeleteFromStore(resource,this);

		if (!isInStore(resource)) {
			if (resourceDAO.existsResource(metadataService.getUri(resource))) {
				logger.warn("Removing resource is not in store but it is in internal repository: {}", resource.getId());
				resourceDAO.deleteResource(metadataService.getUri(resource));
			}
			// pluginManager.getPlugin(ActionHandler.class).afterDeleteFromStore(resource, this);
			return false;
		}

		// if URI scheme is not 'file', it is detected in previous isInStore() check
		File file = new File(metadataService.getUri(resource));
		if (!file.delete()) {
			throw new IOException("Can not delete artifact file from store: " + metadataService.getUri(resource));
		}

		resourceDAO.deleteResource(metadataService.getUri(resource));

		// pluginManager.getPlugin(ActionHandler.class).afterDeleteFromStore(resource, this);
		//TODO: check if pluginManager is needed, or if whole Artifact DIR should be removed?
		return true;
	}
	
	private boolean isInStore(Resource resource) {
        URI uri = metadataService.getUri(resource).normalize();
        if (!"file".equals(uri.getScheme())) {
            return false;
        }
        return new File(uri).getPath().startsWith(MavenStoreConfig.getLocalRepository().getURItoPath());
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
		taskRunnerService.scheduleTask(new MavenRepositoryIndexer(baseUri, new MetadataIndexerCallbackImpl(resourceDAO,
				resourceIndexerService, metadataService, metadataFactory, metadataValidator, identityIndexer, repository)));
	}

}
