package cz.zcu.kiv.crce.rest.v2.internal.ws.jersey;

import cz.zcu.kiv.crce.rest.v2.internal.ws.ApiCompRes;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/apicomp")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class ApiCompResJersey implements ApiCompRes {

    @Path("/compare")
    @Override
    public Response compareApis(@QueryParam("id1") String id1, @QueryParam("id2") String id2) {
        return Response.ok("To do :)").build();
    }
}
