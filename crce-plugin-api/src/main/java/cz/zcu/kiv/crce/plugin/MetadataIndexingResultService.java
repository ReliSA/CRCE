package cz.zcu.kiv.crce.plugin;

/**
 * This interface provides by simple way information about result of metadata indexing process.
 *
 * User of CRCE repository is informed through GUI about bundle/artifact upload result,
 * but there is no information about metadata indexing result of given bundle.
 * At least since crce-efp-indexer module exists,
 * CRCE repository user should be informed about metadata indexing process result as well.
 *
 * Metadata Indexer module itself and others modules in general as well
 * have no way how to propagate information to CRCE web GUI (crce-webui module).
 * But indexer module can through this interface provide information about itself
 * to others modules or to module with GUI. So target (crce-webui) module
 * can take out result information and display it on the website.
 *
 * TODO Move this interface to higher-level module (not low-level API).
 */
public interface MetadataIndexingResultService {

    /**
     * Indexer module sets by this method information for user.
     *
     * @param message - Information about metadata indexing result.
     */
    void addMessage(String message);

    /**
     * Target module can use this method for loading information about metadata indexing result.
     *
     * @return String array of information about metadata indexing results.
     */
    String [] getMessages();

    /**
     * Method used by target module for removing of loaded messages.
     */
    void removeAllMessages();

    /**
     * Method return boolean value whether there is any message or not. Used by target module.
     * @return True when message list is empty. False when is not empty.
     */
    boolean isEmpty();

}
