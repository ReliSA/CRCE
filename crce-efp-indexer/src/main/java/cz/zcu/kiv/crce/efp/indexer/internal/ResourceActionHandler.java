package cz.zcu.kiv.crce.efp.indexer.internal;

import java.io.IOException;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;

import org.osgi.service.log.LogService;

import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.plugins.AbstractActionHandler;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;

import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;

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

	/**
	 * EFPIndexer class ensures loading all features of resource
	 * and initial steps of indexing EFP properties into resource OBR metadata.
	 */
	private EFPIndexer indexer;


	@Override
	// Indexing process starts in afterUploadToBuffer trigger.
	public final Resource afterUploadToBuffer(Resource resource,
			final Buffer buffer, final String name) throws RevokedArtifactException {

		try {
			resource = handleNewResource(resource, name);
		} catch (Exception e) {
			mLog.log(LogService.LOG_ERROR, "Unexpected error in module crce-efp-indexer during handling with a resource!");
			mLog.log(LogService.LOG_WARNING, "Maybe there was a resource with old EFP format verison!");
		}

		return resource;
	}


	/**
	 * Method checks whether uploaded artifact is or is not JAR file.
	 * @param artefactName - Name of processed artifact file.
	 * @return boolean result whether artifact is or is not JAR file.
	 */
	public final boolean jarFileArtefact(final String artefactName) {
		if (artefactName.endsWith(".jar")) {
			// Test of input file, whether it is JAF file.
			mLog.log(LogService.LOG_INFO, "-- Resource is jar file. --");
			return true;
		}
		return false;
	}


	/**
	 * Method creates instance of the EFPIndexer class and tries to load features of OSGi resource.
	 * @param resourcePath - Path to processed resource.
	 * @return boolean result of feature loading process.
	 */
	public final boolean indexerInitialization(final String resourcePath) {
		this.indexer = new EFPIndexer(resourcePath, mLog);

		if (!indexer.loadFeatures()) {
			// In case that resource is not OSGi bundle, indexing process fails.
			return false;
		}
		return true;
	}


	/**
	 * In case that input resource file is JAR file and OSGi bundle,
	 * there is started indexing process.
	 *
	 * @param resource - Resource uploaded to buffer, which enters into indexing process.
	 * @param artefactName - Name of resource file.
	 * @return resource - Modified resource with indexed EFP data in OBR format
	 * or original resource in case of indexing fault.
	 */
	public final Resource handleNewResource(Resource resource, final String artefactName) {

		if (!jarFileArtefact(artefactName)) { // To indexing process continue only JAR files.
			return resource;
		}

		String resourcePath = resource.getUri().getPath(); // Path of resource artifact.
		mLog.log(LogService.LOG_INFO, "Resource path: " + resourcePath);

		if (!indexerInitialization(resourcePath)) {
			return resource;
		}

		indexer.getContainer().setResource(resource);		// Setting of resource into indexer instance.
		indexer.initTranscriptEFPtoOBR();					// Method initializes indexing process.
		resource = indexer.getContainer().getResource();	// Getting modified resource from indexer instance.

		saveResourceOBR(resource); // Saving modified OBR metadata.

		return resource;
	}

	/**
	 * Method saves indexed EFP data. Without saving would be modified OBR metadata lost.
	 *
	 * @param resource - Modified instance with indexed EFP data.
	 */
	public final void saveResourceOBR(final Resource resource) {
		try {
			ResourceDAO rd = mPluginManager.getPlugin(ResourceDAO.class);
			rd.save(resource);
			mLog.log(LogService.LOG_INFO, "-- Resource was saved. --");

		} catch (IOException e) {
			mLog.log(LogService.LOG_ERROR, "IOException during saving process!");
		} catch (NullPointerException e) { 	// Info about error during saving process.
			mLog.log(LogService.LOG_ERROR, "NullPointerException during saving process!");
			mLog.log(LogService.LOG_WARNING, "Maybe there is 'null' some requirement filter!");
		}
	}

	//--- Setter

	/**
	 * @param mLog the mLog to set
	 */
	public final void setmLog(final LogService mLog) {
		this.mLog = mLog;
	}


	/**
	 * @return the indexer
	 */
	public final EFPIndexer getIndexer() {
		return indexer;
	}

}