package cz.zcu.kiv.crce.repository.maven.internal.metadata;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.indexer.ResourceIndexerService;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.service.validation.MetadataValidator;
import cz.zcu.kiv.crce.metadata.service.validation.ResourceValidationResult;
import cz.zcu.kiv.crce.repository.maven.internal.IdentityIndexer;
import cz.zcu.kiv.crce.repository.maven.internal.LocalMavenRepositoryIndexer;

/**
* Implementation class of MetadataaIndexerCallback interface
* @author Miroslav Brožek
*/
public class MetadataIndexerCallbackImpl implements MetadataIndexerCallback {
		
	private volatile ResourceDAO resourceDAO;	
    private volatile ResourceIndexerService resourceIndexerService;    
    private volatile MetadataService metadataService;
    private volatile MetadataValidator metadataValidator;
    private volatile IdentityIndexer identityIndexer;    
    private MavenArtifactMetadataIndexer mami;
    
    Logger logger;    
    private Repository repository;
    
    
	public MetadataIndexerCallbackImpl(ResourceDAO resourceDAO, ResourceIndexerService resourceIndexerService,
			MetadataService metadataService, MetadataFactory metadataFactory, MetadataValidator metadataValidator, IdentityIndexer identityIndexer,
			LocalMavenRepositoryIndexer repositoryIndexer, Repository repository) {
		this.resourceDAO = resourceDAO;
		this.resourceIndexerService = resourceIndexerService;
		this.metadataService = metadataService;
		this.metadataValidator = metadataValidator;
		this.identityIndexer = identityIndexer;		
		this.logger = LoggerFactory.getLogger(MetadataIndexerCallbackImpl.class);
		this.repository = repository;
		this.mami = new MavenArtifactMetadataIndexer(metadataService,metadataFactory);
	}	

	private void postProcessing(File file, Resource resource, MavenArtifactWrapper maw) {
		metadataService.getIdentity(resource).setAttribute("repository-id", String.class, repository.getId());

		identityIndexer.preIndex(file, file.getName(), resource);
		
		updateResourceMetadataFromAether(resource, maw);
		
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
	public void index(MavenArtifactWrapper maw) {
		File file = maw.getArtifact().getFile().getAbsoluteFile();
		try {

			logger.debug("Indexing artifact {}", file);

			if (resourceIndexerService != null && !resourceDAO.existsResource(file.toURI())) {
				Resource resource;

				resource = resourceIndexerService.indexResource(file);				

				postProcessing(file, resource, maw);

				validate(resource);

				saveToDB(file, resource);
			}

		} catch (IOException e) {
			logger.error("Could not index file {}", file, e);
		}
	}

	private void updateResourceMetadataFromAether(Resource resource, MavenArtifactWrapper maw) {		
		mami.createMavenArtifactMetadata(resource, maw);

	} 		
	
}
