package cz.zcu.kiv.crce.efp.indexer.internal;

import java.io.IOException;

import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.dao.ResourceDAO;
import cz.zcu.kiv.crce.plugin.MetadataIndexingResultService;
import cz.zcu.kiv.crce.plugin.PluginManager;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;
import cz.zcu.kiv.crce.repository.plugins.AbstractActionHandler;
import cz.zcu.kiv.crce.repository.plugins.ActionHandler;

/**
 * ResourceActionHandler class ensures general tasks about efp-indexing process.
 * Verification OSGi bundle, initialization indexing process
 * and saving modified OBR metadata of resource.
 */
public class ResourceActionHandler extends AbstractActionHandler implements ActionHandler {

    /** PluginManager injected by dependency manager. */
    private volatile PluginManager mPluginManager;

    /** Variable carries boolean information whether some error occurred
     * during loading list of features and EFP information from OSGi bundle.
     * True value is reason for aborting indexing process. */
    private boolean initialLoadingException;

    /** Variable carries boolean information whether some EFP metadata
     * was found into the bundle.
     * If there is no EFP found so there is no reason to save resource metadata. */
    private boolean foundedEFP;

    /** MetadataIndexingResultService injected by dependency manager. */
    private volatile MetadataIndexingResultService mMetadataIndexingResult;

    private static final Logger logger = LoggerFactory.getLogger(ResourceActionHandler.class);

    @Override
    // Indexing process starts in afterUploadToBuffer trigger.
    public final Resource afterUploadToBuffer(final Resource resource,
            final Buffer buffer, final String name) throws RevokedArtifactException {

        if (!resource.hasCategory("osgi")) {
            //mEfpIndexer.addMessage("EFP metadata was not found in the artifact.");
            return resource;
        }

        setInjectedInstances();

        initialLoadingException = false;
        foundedEFP = false;

        try {
            handleNewResource(resource);

            if (initialLoadingException) {
                return resource;
            }

            if (foundedEFP) {
                if (!saveResourceOBR(resource)) { // Saving modified OBR metadata.
                    logMessage("All EFP metadata was not saved.", IndexerDataContainer.NO_LOGSERVICE_MESSAGE);
                }
            } else {
                logMessage("EFP metadata was not found in the bundle.", LogService.LOG_INFO);
            }

        } catch (Exception e) {
            String message = "Unexpected error " + e.getClass().getName()
                    + " in module crce-efp-indexer during handling with a resource " + resource.getPresentationName();
            logMessage(message, LogService.LOG_ERROR);
        }

        return resource;
    }


    /**
     * In case that input resource file is JAR file and OSGi bundle,
     * there is started indexing process.
     *
     * @param resource - Resource uploaded to buffer, which enters into indexing process.
     */
    public final void handleNewResource(final Resource resource) {

        IndexerHandler indexer = new IndexerHandler();

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

        if (!resourceRequirementFilterCheck(resource)) {
            return false;
        }

        try {
            ResourceDAO rd = mPluginManager.getPlugin(ResourceDAO.class);
            rd.save(resource);
            logMessage("EFP metadata was succesfully saved.", LogService.LOG_INFO);
            return true;

        } catch (IOException e) {
            logMessage("IOException during saving process!", LogService.LOG_ERROR);
            return false;
        }
    }

    /**
     * Method checks resource requirements for null filter value.
     * Null filter would cause NPE during metadata saving process.
     *
     * @param resource - Resource instance for check.
     * @return - true if all requirements are fine, false if there is some filter null value occurrence.
     */
    public final boolean resourceRequirementFilterCheck(final Resource resource) {
        Requirement [] reqArray = resource.getRequirements();
        for (Requirement req : reqArray) {
            if (req.getFilter() == null) {
                String message = "There is 'null' filter string in '" + req.getName() + "' requirement!";
                logMessage(message, LogService.LOG_WARNING);
                return false;
            }
        }
        return true;
    }

    /**
     * Method provides logging of input message by both sl4j and MetadataIndexingResultService.
     *
     * @param message - information message
     * @param logServiceValue - message type value according to org.osgi.service.log.LogService.
     */
    public static final void logMessage(final String message, final int logServiceValue) {
        if (logServiceValue != 0) {
        	switch (logServiceValue) {
			case LogService.LOG_ERROR:
				logger.error(message);
				break;
			case LogService.LOG_WARNING:
				logger.warn(message);
				break;
			case LogService.LOG_INFO:
				logger.info(message);
				break;
			case LogService.LOG_DEBUG:
				logger.debug(message);
				break;
			default:
				logger.info(message);
				break;
			}

        }
        Activator.instance().getMetadataIndexerResult().addMessage(IndexerDataContainer.EFP_INDEXER_MODULE + message);
    }

    /**
     * This method sets instances injected by dependency manager back to instances in Activator class,
     * which are used by ResourceActionHandler and IndexerHandler classes.
     * This kind of solution is not good. Previously there was used commented (now non active) code in Activator class,
     * which ensured proper dependency injection of these instances, but this piece of code caused
     * problem with pax-exam test container. An effort to resolve this problem with pax-exam had a negative impact
     * on the functionality of the test container. This solution is only temporary.
     */
    public final void setInjectedInstances() {
        Activator.activatorInstance = new Activator();
        Activator.instance().setmMetadataIndexingResult(mMetadataIndexingResult);
    }
}
