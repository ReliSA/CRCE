package cz.zcu.kiv.crce.rest.internal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Server provide metadata about other bundles (repository contents diff).
 *
 * When the client asks CRCE to provide bundle metadata of those bundles which
 * it does not know about and sends a list of bundle identifiers (= those
 * bundles it knows about) and optionally sends a filter criteria specifying
 * which subset of metadata it is interested in
 *
 * Then CRCE sends that (subset of) metadata only for the following bundles
 * currently available in repository in "stored" state:
 * <ul>
 * <li>are not in the list sent by client ("new bundles")</li>
 * <li>are in the list but have been removed from the repository
 * ("deleted bundles" - just a list of bundle identifiers).</li>
 * </ul>
 *
 * @author Jan Reznicek
 *
 */
public interface PostOtherBundlesMetadata {

	/**
	 * Server provide metadata about other bundles (repository contents diff).
	 * Response contains bundles, that:
	 * <ul>
     *  <li> are not in the list sent by client, but all in storage ("new bundles")</li>
     *  <li> are in the list but have been removed from the repository
     *  ("deleted bundles" - just a list of bundle identifiers).</li>
	 * </ul>
	 *
	 *
	 * @param knownBundles XML with information about bundles, that client knows.
	 * @param ui contextual info about URI
	 * @return Response with difference between knownBundles and state of server.
	 */
	Response otherBundles(String knownBundles, UriInfo ui);
}
