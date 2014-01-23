package cz.zcu.kiv.crce.handler.metrics.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.concurrency.service.TaskRunnerService;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.service.validation.MetadataValidator;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;
import cz.zcu.kiv.crce.repository.plugins.AbstractActionHandler;

public class MetricsIndexerActionHandler extends AbstractActionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(MetricsIndexerActionHandler.class);
	
	private volatile TaskRunnerService taskRunnerService;
	private volatile MetadataService metadataService;
	private volatile MetadataValidator metadataValidator;
	private volatile ResourceFactory resourceFactory;
	private volatile ResourceDAO resourceDAO;
	
	public Resource afterUploadToBuffer(Resource resource, Buffer buffer, String name) throws RefusedArtifactException {
		
		logger.debug("afterUploadToBuffer called.");
		
		MetricsIndexer metricsIndexer = new MetricsIndexer(resourceFactory, metadataService);
		
		MetricsIndexerTask task = new MetricsIndexerTask(resource.getId(), metadataService, metadataValidator, metricsIndexer, resource, resourceDAO);
		
		taskRunnerService.scheduleTask(task);
		
		logger.debug("task scheduled");
		
		return resource;
	}
}
