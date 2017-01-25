package cz.zcu.kiv.crce.rest.internal;

import javax.ws.rs.core.Response;

/**
 * When the client asks CRCE to send metadata of provider(s)
 * and sends a meta-data descriptor of the requirement to be satisfied.
 *
 * Then CRCE sends a set of meta-data (the "core" part and "capability" which matches the requirement) of bundles which provides the capability and satisfies the criteria
 * or replies "no such bundle available".
 *
 * @author Jan Reznicek
 */
public interface PostProviderOfCapability {

	/**
	 * When the client asks CRCE to send metadata of provider(s)
	 * and sends a meta-data descriptor of the requirement to be satisfied.
	 *
	 * Then CRCE sends a set of meta-data (the "core" part and "capability" which matches the requirement) of bundles which provides the capability and satisfies the criteria
	 * or replies "no such bundle available".
	 *
	 *
	 * @param requirement requirement to be satisfied
	 * --@param ui contextual info about URI
	 * @return Response metadata of bundles, that matches the requirement
	 */
	Response providerOfCapability(String requirement/*, UriInfo ui*/);
}
