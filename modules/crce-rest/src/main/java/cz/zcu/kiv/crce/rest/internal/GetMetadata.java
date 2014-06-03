package cz.zcu.kiv.crce.rest.internal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Server will provide a metadata information about resources in the repository.
 * @author Jan Reznicek
 *
 */
public interface GetMetadata {


	/**
	 * Returns response with metadata of resources from the store repository.
	 * All parameters all obligatory (can be null).
	 * If the request is without filter query parameter, return all resources.
	 * If the request have filter parameter, return resources that met the filter.
	 * Parameters core, cap, req and prop determines, what part shloud xml contains.
	 * If all of those parameters(core, cap, req and prop) are null, xml will contains all parts.
	 * @param filter obligatory LDAP filter
	 * @param core core metadata
	 * @param cap all capabilities
	 * @param req all requirement
	 * @param prop all properties
	 * @param ui contextual info about URI
	 * @return Response with metadata of resources from the store repository, or error with html status.
	 */
	Response getMetadata(String filter, String core, String cap,String req, String prop, UriInfo ui);


	/**
	 * Return response with metadata of one resource, that is specified by id.
	 * If the request have filter parameter, return resources that met the filter.
	 * Parameters core, cap, req and prop determines, what part shloud xml contains.
	 * If all of those parameters(core, cap, req and prop) are null, xml will contains all parts.
	 * @param id compulsory id of the resource
	 * @param core core metadata
	 * @param cap all capabilities
	 * @param req all requirement
	 * @param prop all properties
	 * @param ui contextual info about URI
	 * @return Response with metadata of resource from repository with the id, or error with html status.
	 */
	Response getMetadataById(String id, String core, String cap, String req, String prop, UriInfo ui);
}
