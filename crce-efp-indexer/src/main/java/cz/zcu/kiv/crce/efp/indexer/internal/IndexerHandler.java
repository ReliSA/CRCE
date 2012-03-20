package cz.zcu.kiv.crce.efp.indexer.internal;

import java.util.List;

import org.osgi.service.log.LogService;

import cz.zcu.kiv.crce.efp.indexer.EfpIndexerResultService;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.efps.assignment.core.AssignmentRTException;
import cz.zcu.kiv.efps.assignment.osgi.OSGiAssignmentRTException;
import cz.zcu.kiv.efps.assignment.types.Feature;
import cz.zcu.kiv.efps.types.properties.EFP;

/**
 * IndexerHandler class ensures methods for initialization of indexer,
 * calling method for loading of features and handling occurred exceptions.
 * Next there is method which starts process of transcription EFP to OBR.
 */
public class IndexerHandler {

	/** Contains instances and variables with data for indexing purpose. */
	private IndexerDataContainer container;

	/** EfpIndexerResultService injected by dependency manager. */
	private volatile EfpIndexerResultService mEfpIndexerResult;

	/** LogService injected by dependency manager. */
	private volatile LogService mLog;

	/**
	 * IndexerHandler constructor.
	 *
	 * @param mLog2 - LogService.
	 * @param mEfpIndexer - Provides interface for presentation indexing result to user.
	 */
	public IndexerHandler(final LogService mLog2, final EfpIndexerResultService mEfpIndexer) {
		this.mLog = mLog2;
		this.mEfpIndexerResult = mEfpIndexer;
	}

	/**
	 * Method creates instance of the EFPIndexer class and tries to load features of OSGi resource.
	 * @param resource - Processed resource, which can be the OSGi bundle or not.
	 * @return boolean result of feature loading process.
	 */
	public final boolean indexerInitialization(final Resource resource) {

		String resourcePath = resource.getUri().getPath(); // Path of resource artifact moved to the buffer.
		mLog.log(LogService.LOG_DEBUG, "Resource path: " + resourcePath);

		try {
			container = new FeatureAndEfpLoader(mLog).loadFeatures(resourcePath);
			container.setResource(resource);
			container.setLogger(mLog);
		} catch (OSGiAssignmentRTException e) {
			String warningMessage = "The resource " + resource.getPresentationName()
					+ " is not valid OSGi bundle.";
			mLog.log(LogService.LOG_WARNING, warningMessage);
			mEfpIndexerResult.setMessage(warningMessage);
			return false;

		} catch (AssignmentRTException e) {
			String warningMessage = "The resource " + resource.getPresentationName()
					+ " contains unsupported EFP metadata version.";
			mLog.log(LogService.LOG_WARNING, warningMessage);
			mEfpIndexerResult.setMessage(warningMessage);
			return false;
		}

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
		return foundedEFP;
	}

}
