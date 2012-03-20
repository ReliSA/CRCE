package cz.zcu.kiv.crce.efp.indexer;

import org.osgi.service.cm.ManagedService;

/**
 * This interface provides by simple way information about result of indexing process.
 *
 * User of CRCE repository is informed through GUI about bundle upload result,
 * but there is no information about result of indexing EFP metadata of given bundle.
 * CRCE repository user should be informed about result of indexing process.
 *
 * Crce-efp-indexer module itself has no way how to propagate information
 * to CRCE web GUI. But crce-efp-indexer can provide to others modules
 * through this interface access to information about itself. So crce-webui module
 * can take out information and display it on the website.
 */
public interface EfpIndexerResultService extends ManagedService{

	/**
	 * Crce-efp-indexer module sets by this method information for user.
	 *
	 * @param message - Information about indexing result.
	 */
	void setMessage(String message);

	/**
	 * Crce-webui module can use this method for loading information about indexing result
	 * and after information about bundle upload can display even this loaded information.
	 *
	 * @return Information about indexing result for user.
	 */
	String getMessage();

}
