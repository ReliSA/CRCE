package cz.zcu.kiv.crce.efp.indexer.internal;


import java.util.Set;
import org.osgi.service.log.LogService;

import cz.zcu.kiv.efps.assignment.api.EfpAwareComponentLoader;
import cz.zcu.kiv.efps.assignment.client.EfpAssignmentClient;
import cz.zcu.kiv.efps.types.lr.LR;

/**
 * EFPIndexer class ensures loading all features of resource
 * and initial steps of indexing EFP properties into resource OBR metadata.
 */
public class FeatureAndEfpLoader {

	/** LogService injected by dependency manager into ResourceActionHandler. */
	private LogService mLog;

	/**
	 * FeatureAndEfpLoader constructor.
	 *
	 * @param mLog2 - LogService.
	 */
	public FeatureAndEfpLoader(final LogService mLog2) {
		this.mLog = mLog2;
	}

	/**
	 * Method for loading all features into list and loading list of LRs into array.
	 *
	 * @param sourceFilePath - String of file path to resource artifact.
	 * @return boolean - Returns true in case that feature load process succeeded
	 * or false in case that process failed.
	 */

	public final IndexerDataContainer loadFeatures(final String sourceFilePath) {

		IndexerDataContainer container = new IndexerDataContainer();

		mLog.log(LogService.LOG_INFO, "Initialising EfpAwareComponentLoader ...");
		EfpAwareComponentLoader loader = EfpAssignmentClient.initialiseComponentLoader("cz.zcu.kiv.efps.assignment.osgi.OSGiAssignmentImpl");
		mLog.log(LogService.LOG_INFO, "EfpAwareComponentLoader ok.");

		mLog.log(LogService.LOG_INFO, "Initialising ComponentEfpAccessor ...");
		container.setAccessor(loader.loadForRead(sourceFilePath));
		mLog.log(LogService.LOG_INFO, "ComponentEfpAccessor ok.");

		container.setFeatureList(container.getAccessor().getAllFeatures());
		mLog.log(LogService.LOG_INFO, "Feature list loaded.");

		Set<LR> lrSet = container.getAccessor().getLRs();
		container.setArrayLR(lrSet.toArray(new LR[0]));

		return container;
	}

}
