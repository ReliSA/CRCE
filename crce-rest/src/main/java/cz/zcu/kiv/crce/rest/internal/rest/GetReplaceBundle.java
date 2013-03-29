package cz.zcu.kiv.crce.rest.internal.rest;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public interface GetReplaceBundle {
	
	/**
	 * In current version return resource with same name as in id and highest possible version.
	 * @param id
	 * @param ui contextual info about URI
	 * @return resource
	 */
    public Response replaceBundle(@QueryParam("id") String id, UriInfo ui);

}
