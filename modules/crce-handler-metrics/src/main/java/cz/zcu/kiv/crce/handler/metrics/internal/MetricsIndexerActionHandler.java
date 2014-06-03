package cz.zcu.kiv.crce.handler.metrics.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.concurrency.service.TaskRunnerService;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.service.validation.MetadataValidator;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.repository.plugins.AbstractActionHandler;

/**
 * Implementation of <code>AbstractActionHandler</code> which start MetricsIndexerTask.
 *
 * @author Jan Smajcl (smajcl@students.zcu.cz)
 */
public class MetricsIndexerActionHandler extends AbstractActionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(MetricsIndexerActionHandler.class);
	
	private volatile TaskRunnerService taskRunnerService;
	private volatile MetadataService metadataService;
	private volatile MetadataValidator metadataValidator;
	private volatile MetadataFactory metadataFactory;
	private volatile ResourceDAO resourceDAO;
	
	@Override
	public Resource afterPutToStore(Resource resource, Store store) throws RefusedArtifactException {
		
		logger.debug("afterPutToStore called.");
		
		MetricsIndexer metricsIndexer = new MetricsIndexer(metadataFactory, metadataService);
		
		MetricsIndexerTask task = new MetricsIndexerTask(resource.getId(), metadataService, metadataValidator, metricsIndexer, resource, resourceDAO);
		
		taskRunnerService.scheduleTask(task);
		
		logger.debug("task scheduled");
		
		return resource;
	}
}
