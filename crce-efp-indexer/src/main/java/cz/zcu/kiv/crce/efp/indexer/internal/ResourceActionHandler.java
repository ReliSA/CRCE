package cz.zcu.kiv.crce.efp.indexer.internal;

import java.io.IOException;

import cz.zcu.kiv.crce.efp.indexer.EfpIndexerResultService;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;

import org.osgi.service.log.LogService;

import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.plugins.AbstractActionHandler;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;

import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;
import cz.zcu.kiv.efps.assignment.core.AssignmentRTException;
import cz.zcu.kiv.efps.assignment.osgi.OSGiAssignmentRTException;


/**
 * ResourceActionHandler class ensures general tasks about efp-indexing process.
 * Verification OSGi bundle, initialization indexing process
 * and saving modified OBR metadata of resource.
 */
public class ResourceActionHandler extends AbstractActionHandler implements ActionHandler {

	/** LogService injected by dependency manager. */
	private volatile LogService mLog;

	/** PluginManager injected by dependency manager. */
	private volatile PluginManager mPluginManager;

	/** EfpIndexerResultService injected by dependency manager. */
	private volatile EfpIndexerResultService mEfpIndexer;

	/** Variable carries boolean information whether some error occurred
	 * during loading list of features and EFP information from OSGi bundle.
	 * True value is reason for aborting indexing process. */
	private boolean initialLoadingException;

	/** Variable carries boolean information whether some EFP metadata
	 * was found into the bundle.
	 * If there is no EFP found so there is no reason to save resource metadata. */
	private boolean foundedEFP;


	@Override
	// Indexing process starts in afterUploadToBuffer trigger.
	public final Resource afterUploadToBuffer(final Resource resource,
			final Buffer buffer, final String name) throws RevokedArtifactException {

		if (!resource.hasCategory("osgi")) {
			mEfpIndexer.setMessage("EFP metadata was not found in the artifact.");
			return resource;
		}

		initialLoadingException = false;
		foundedEFP = false;

		try {
			handleNewResource(resource);

			if (initialLoadingException) {
				return resource;
			}

			if (foundedEFP) {

				if (saveResourceOBR(resource)) { // Saving modified OBR metadata.
					mEfpIndexer.setMessage("EFP metadata were succesfully saved.");
				} else {
					mEfpIndexer.setMessage("All EFP metadata was not saved.");
				}
			} else {
				mEfpIndexer.setMessage("EFP metadata was not found in the bundle.");
			}

		} catch (Exception e) {
			mLog.log(LogService.LOG_ERROR, "Unexpected error " + e.getClass().getName()
					+ " in module crce-efp-indexer during handling with a resource " + resource.getPresentationName());
			mLog.log(LogService.LOG_WARNING, "Maybe there was a resource with old EFP format verison!");

			mEfpIndexer.setMessage("Unexpected error " + e.getClass().getName()
					+ " in module crce-efp-indexer during handling with a resource " + resource.getPresentationName());
		}

		return resource;
	}


	/**
	 * In case that input resource file is JAR file and OSGi bundle,
	 * there is started indexing process.
	 *
	 * @param resource - Resource uploaded to buffer, which enters into indexing process.
	 */
	public void handleNewResource(final Resource resource) {

		IndexerHandler indexer = new IndexerHandler(mLog, mEfpIndexer);

		if (!indexer.indexerInitialization(resource)) {
			initialLoadingException = true;
			return;
		}

		foundedEFP = indexer.initTranscriptEFPtoOBR();
	}

	/**
	 * Method saves indexed EFP data. Without saving would be modified OBR metadata lost.
	 *
	 * @param resource - Modified instance with indexed EFP data.
	 * @return Result of saving process. True - success, False - fail.
	 */
	private boolean saveResourceOBR(final Resource resource) {
		try {
			ResourceDAO rd = mPluginManager.getPlugin(ResourceDAO.class);
			rd.save(resource);
			mLog.log(LogService.LOG_INFO, "Resource metadata was saved.");
			return true;

		} catch (IOException e) {
			mLog.log(LogService.LOG_ERROR, "IOException during saving process!");
		} catch (NullPointerException e) {
			mLog.log(LogService.LOG_ERROR, "NullPointerException during saving process!");
			mLog.log(LogService.LOG_WARNING, "Maybe there is 'null' some requirement filter!");
		}
		return false;
	}

	//--- Setters

	/**
	 * @param mLog2 the mLog to set
	 */
	public final void setmLog(final LogService mLog2) {
		this.mLog = mLog2;
	}


	/**
	 * @param mEfpIndexer the mEfpIndexer to set
	 */
	public final void setmEfpIndexer(EfpIndexerResultService mEfpIndexer) {
		this.mEfpIndexer = mEfpIndexer;
	}
	
	

}