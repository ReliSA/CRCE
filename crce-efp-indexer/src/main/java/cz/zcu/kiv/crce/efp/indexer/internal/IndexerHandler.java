package cz.zcu.kiv.crce.efp.indexer.internal;

import java.util.List;
import java.util.Set;

import org.osgi.service.log.LogService;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.MetadataIndexingResultService;
import cz.zcu.kiv.efps.assignment.api.EfpAwareComponentLoader;
import cz.zcu.kiv.efps.assignment.client.EfpAssignmentClient;
import cz.zcu.kiv.efps.assignment.core.AssignmentRTException;
import cz.zcu.kiv.efps.assignment.osgi.OSGiAssignmentRTException;
import cz.zcu.kiv.efps.assignment.types.Feature;
import cz.zcu.kiv.efps.types.lr.LR;
import cz.zcu.kiv.efps.types.properties.EFP;

/**
 * IndexerHandler class ensures methods for initialization of indexer,
 * calling method for loading of features and handling occurred exceptions.
 * Next there is method which starts process of transcription EFP to OBR.
 */
public class IndexerHandler {

	/** Contains instances and variables with data for indexing purpose. */
	private IndexerDataContainer container;

	/** LogService injected by dependency manager. */
	private volatile LogService mLog;

	/**
	 * IndexerHandler constructor.
	 */
	public IndexerHandler() {
		mLog = Activator.instance().getLog();
	}

	/**
	 * Method creates instance of the EFPIndexer class and tries to load features of OSGi resource.
	 * @param resource - Processed resource.
	 * @return boolean result of feature loading process.
	 */
	public final boolean indexerInitialization(final Resource resource) {

		String resourceFilePath = resource.getUri().getPath(); // Path of resource artifact moved to the buffer.
		mLog.log(LogService.LOG_DEBUG, "Resource path: " + resourceFilePath);

		container = new IndexerDataContainer();
		container.setResource(resource);

		mLog.log(LogService.LOG_INFO, "Initialising EfpAwareComponentLoader ...");
		EfpAwareComponentLoader loader = EfpAssignmentClient.initialiseComponentLoader("cz.zcu.kiv.efps.assignment.osgi.OSGiAssignmentImpl");
		mLog.log(LogService.LOG_INFO, "EfpAwareComponentLoader ok.");

		mLog.log(LogService.LOG_INFO, "Initialising ComponentEfpAccessor ...");
		container.setAccessor(loader.loadForRead(resourceFilePath));
		mLog.log(LogService.LOG_INFO, "ComponentEfpAccessor ok.");

		if (loadFeaturesAndLR()) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * Method for loading all features into list and loading list of LRs into array.
	 *
	 * @return boolean - Returns true in case that feature load process succeeded
	 * or false in case that process failed.
	 */
	public final boolean loadFeaturesAndLR() {
		try {
			container.setFeatureList(container.getAccessor().getAllFeatures());
			mLog.log(LogService.LOG_INFO, "Feature list loaded.");

		} catch (OSGiAssignmentRTException e) {
			String warningMessage = "The resource " + container.getResource().getPresentationName()
					+ " is not valid OSGi bundle.";
			ResourceActionHandler.logMessage(warningMessage, LogService.LOG_WARNING);
			return false;

		} catch (AssignmentRTException e) {
			String warningMessage = "The resource " + container.getResource().getPresentationName()
					+ " contains unsupported EFP metadata version.";
			ResourceActionHandler.logMessage(warningMessage, LogService.LOG_WARNING);
			return false;
		}

		Set<LR> lrSet = container.getAccessor().getLRs();
		container.setArrayLR(lrSet.toArray(new LR[0]));

		return true;
	}


	/**
	 * Method extracts list of EFP from each feature. Next there is called individual feature processing.
	 *
	 * @return True if there was found some EFP in a resource. False if there was not found any EFP
	 */
	public final boolean initTranscriptEFPtoOBR() {

		boolean foundedEFP = false;
		EFPAssignmentTypeResolver obrTF = new EFPAssignmentTypeResolver(container);

		for (Feature feature : container.getFeatureList()) {

			List<EFP> listEfp = container.getAccessor().getEfps(feature);

			if (listEfp.size() != 0) {
				obrTF.assignmentTypeResolving(listEfp, feature);
				foundedEFP = true;
			}
		}

		container.getAccessor().close(); // deleting temporary files

		return foundedEFP;
	}

	
	/**
	 * @return the container
	 */
	public IndexerDataContainer getContainer() {
		return container;
	}

}
