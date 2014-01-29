package cz.zcu.kiv.crce.handler.metrics.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.concurrency.model.Task;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.service.validation.MetadataValidator;
import cz.zcu.kiv.crce.metadata.service.validation.ResourceValidationResult;

/**
 * Implementation of <code>Task</code> which compute metrics for selected Resource.
 *
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
@SuppressWarnings("rawtypes")
public class MetricsIndexerTask extends Task {

	private static final Logger logger = LoggerFactory.getLogger(MetricsIndexerTask.class);
	
	private MetadataService metadataService;
	private MetadataValidator metadataValidator;
	private MetricsIndexer metricsIndexer;	
	private Resource resource;
	private ResourceDAO resourceDAO;
	
	/**
	 * New instance.
	 * 
	 * @param id Job ID.
	 * @param metadataService MetadataService.
	 * @param metadataValidator MetadataValidator.
	 * @param metricsIndexer MetricsIndexer.
	 * @param resource Resource.
	 * @param resourceDAO ResourceDAO.
	 */
	protected MetricsIndexerTask(@Nonnull String id, @Nonnull MetadataService metadataService, @Nonnull MetadataValidator metadataValidator,
			@Nonnull MetricsIndexer metricsIndexer, @Nonnull Resource resource, @Nonnull ResourceDAO resourceDAO) {
		
		super(id, "Calculates metrics for a provided resource.", "crce-handler-metrics");

		this.metadataService = metadataService;
		this.metadataValidator = metadataValidator;
		this.metricsIndexer = metricsIndexer;
		this.resource = resource;		
		this.resourceDAO = resourceDAO;
	}

	@Override
	protected Object run() throws Exception {
		
		logger.debug("Started computing metrics for resource {} ", resource.getId());
		
		if (metadataService.getCategories(resource).contains("osgi")) {
			
			URI uri = metadataService.getUri(resource);
			File file = new File(uri);
	        try (FileInputStream fis = new FileInputStream(file)) {
	        	metricsIndexer.index(fis, resource);
	        }
	        
	        ResourceValidationResult validationResult = metadataValidator.validate(resource);        
	        if (!validationResult.isContextValid()) {
	        	
	            logger.error("Indexed Resource {} is not valid:\r\n{}", resource.getId(), validationResult);
	        }
	        else {
	        	
		        logger.info("Indexed resource {} is valid.", resource.getId());
		        
		        try {
		        	resourceDAO.saveResource(resource);
		        } 
		        catch (IOException e) {
		            logger.error("Could not save indexed resource for file {}: {}", file, resource.getId(), e);
		        }
	        }
		}
		else {
			
			logger.debug("Resource {} not OSGI", resource.getId());
		}

		logger.debug("Finished computing metrics for resource {} ", resource.getId());
		return null;
	}

}
