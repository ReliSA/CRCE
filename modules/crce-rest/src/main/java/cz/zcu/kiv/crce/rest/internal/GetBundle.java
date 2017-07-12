package cz.zcu.kiv.crce.rest.internal;


import javax.ws.rs.core.Response;

/**
 * Server will provide a single bundle.
 * @author Jan Reznicek
 *
 */
public interface GetBundle {


	/**
	 * Get bundle by id.
	 * URI is /bundle/id.
	 * @param id id of a bundle
	 * @return bundle or error response
	 */
	Response getBundleById(String id);

	/**
	 * Return bundle specified by name and version.
	 * If version is not set, select the one with highest version.
	 * @param name name of bundle
	 * @param version version of bundle
	 * @return bundle or error response
	 */
	Response getBundlebyNameAndVersion(String name, String version);
}
