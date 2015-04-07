package cz.zcu.kiv.crce.repository.maven.internal;

import java.io.File;
import java.io.IOException;

import org.eclipse.aether.resolution.ArtifactResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.indexer.ResourceIndexerService;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.service.validation.MetadataValidator;
import cz.zcu.kiv.crce.metadata.service.validation.ResourceValidationResult;

public class MetadataIndexerCallbackImpl implements MetadataIndexerCallback {
	
	
	private volatile ResourceDAO resourceDAO;	
    private volatile ResourceIndexerService resourceIndexerService;    
    private volatile MetadataService metadataService;
    private volatile MetadataValidator metadataValidator;
    private volatile IdentityIndexer identityIndexer;    
    
    Logger logger;    
    private Repository repository;
    
    
	public MetadataIndexerCallbackImpl(ResourceDAO resourceDAO, ResourceIndexerService resourceIndexerService,
			MetadataService metadataService, MetadataValidator metadataValidator, IdentityIndexer identityIndexer,
			LocalMavenRepositoryIndexer repositoryIndexer, Repository repository) {
		super();
		this.resourceDAO = resourceDAO;
		this.resourceIndexerService = resourceIndexerService;
		this.metadataService = metadataService;
		this.metadataValidator = metadataValidator;
		this.identityIndexer = identityIndexer;		
		this.logger = LoggerFactory.getLogger(MetadataIndexerCallbackImpl.class);
	}	

	private void postProcessing(File file, Resource resource) {
		metadataService.getIdentity(resource).setAttribute("repository-id", String.class, repository.getId());

		identityIndexer.preIndex(file, file.getName(), resource);
		identityIndexer.postIndex(file, resource);
	}

	private void validate(Resource resource) {
		ResourceValidationResult validationResult = metadataValidator.validate(resource);
		if (!validationResult.isContextValid()) {
		    logger.error("Indexed Resource {} is not valid:\r\n{}", resource.getId(), validationResult);
		    
		}
		else
		{			
			logger.info("Indexed resource {} is valid.", resource.getId());
		}
	}

	private void saveToDB(File file, Resource resource) {
		try {
		    resourceDAO.saveResource(resource);
		} catch (IOException e) {
		    logger.error("Could not save indexed resource for file {}: {}", file, resource, e);
		}
	}	

	@Override
	public void index(ArtifactResult result, LocalMavenRepositoryIndexer caller) {
		File file = result.getArtifact().getFile().getAbsoluteFile();
		try {

			logger.debug("Indexing artifact {}", file);

			if (resourceIndexerService != null && !resourceDAO.existsResource(file.toURI())) {
				Resource resource;

				resource = resourceIndexerService.indexResource(file);

				updateResourceMetadataFromAether(resource, caller, result);

				postProcessing(file, resource);

				validate(resource);

				saveToDB(file, resource);
			}

		} catch (IOException e) {
			logger.error("Could not index file {}", file, e);
		}
	}

	private void updateResourceMetadataFromAether(Resource resource, LocalMavenRepositoryIndexer caller, ArtifactResult result) {
		logger.debug("Mira to zatim neumi");

	} 		
	
}
