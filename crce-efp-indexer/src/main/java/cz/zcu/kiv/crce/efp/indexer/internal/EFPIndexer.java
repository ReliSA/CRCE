package cz.zcu.kiv.crce.efp.indexer.internal;
import java.util.List;
import java.util.Set;

import org.osgi.service.log.LogService;

import cz.zcu.kiv.efps.assignment.client.EfpAssignmentClient;
import cz.zcu.kiv.efps.assignment.core.AssignmentRTException;
import cz.zcu.kiv.efps.assignment.osgi.OSGiAssignmentRTException;
import cz.zcu.kiv.efps.assignment.types.Feature;
import cz.zcu.kiv.efps.types.lr.LR;
import cz.zcu.kiv.efps.types.properties.EFP;

/**
 * EFPIndexer class ensures loading all features of resource
 * and initial steps of indexing EFP properties into resource OBR metadata.
 */
public class EFPIndexer {

	/** Contains instances and variables with data for indexing purpose. */
	private IndexerDataContainer container;

	/** LogService injected by dependency manager into ResourceActionHandler. */
	private LogService mLog;

	/**
	 * Indexer constructor.
	 *
	 * @param sourceFilePath - Path of resource file entering indexing process.
	 * @param mLog - Provides logging service.
	 */
	public EFPIndexer(final String sourceFilePath, final LogService mLog) {
		container = new IndexerDataContainer();
		container.setSourceFilePath(sourceFilePath);
		container.setLogger(mLog);
		this.mLog = mLog;
	}

	/**
	 * Method for loading all features into list and loading list of LRs into array.
	 *
	 * @return boolean - Returns true in case that feature load process succeeded
	 * or false in case that process failed.
	 */
	public final boolean loadFeatures() {
		mLog.log(LogService.LOG_INFO, "Initialising EfpAwareComponentLoader ...");
		container.setLoader(EfpAssignmentClient.initialiseComponentLoader("cz.zcu.kiv.efps.assignment.osgi.OSGiAssignmentImpl"));
		mLog.log(LogService.LOG_INFO, "EfpAwareComponentLoader ok.");

		mLog.log(LogService.LOG_INFO, "Initialising ComponentEfpAccessor ...");
		container.setAccessor(container.getLoader().loadForRead(container.getSourceFilePath()));
		mLog.log(LogService.LOG_INFO, "ComponentEfpAccessor ok.");

		try {
			container.setFeatureList(container.getAccessor().getAllFeatures());
			mLog.log(LogService.LOG_INFO, "Feature list loaded.");

		} catch (OSGiAssignmentRTException e) {
			String warningMessage = "-- The resource " + container.getResource().getPresentationName()
					+ " is not valid OSGi bundle. --";
			mLog.log(LogService.LOG_WARNING, warningMessage);
			return false;
			
		} catch (AssignmentRTException e) {
			String warningMessage = "-- The resource " + container.getResource().getPresentationName()
					+ " contains unsupported EFP metadata version. --";
			mLog.log(LogService.LOG_WARNING, warningMessage);
			return false;
		}

		Set<LR> lrSet = container.getAccessor().getLRs();
		container.setArrayLR(lrSet.toArray(new LR[0]));

		return true;
	}

	/**
	 * Method extracts list of EFP from each feature. Next there is called individual feature OBR processing.
	 */
	public final void initTranscriptEFPtoOBR() {

		OBRTranscriptFormat obrTF = new OBRTranscriptFormat(container);

		for (Feature feature : container.getFeatureList()) {

			List<EFP> listEfp = container.getAccessor().getEfps(feature);

			if (listEfp.size() != 0) {
				
				obrTF.featureWithEfp(listEfp, feature);
				
			}
		}
	}

	//------------------------------------------
	// Getter and Setter

	/**
	 * Getter returning data container.
	 * @return IndexerDataContainer
	 */
	public final IndexerDataContainer getContainer() {
		return container;
	}

	/*public void setContainer(IndexerDataContainer container) {
		this.container = container;
	}*/



}
