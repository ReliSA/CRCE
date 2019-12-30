package cz.zcu.kiv.crce.rest.v2.internal.ws.jersey;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityCheckerService;
import cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.rest.v2.internal.Activator;
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

        // todo: resolve resources by their ids (and return error if needed)
        Store store = Activator.instance().getStore();
        Resource api1 = store.getResource(id1, false);
        Resource api2 = store.getResource(id2, false);


        // todo: call compatibility checker service
        ApiCompatibilityCheckerService compatibilityCheckerService = Activator.instance().getApiCompatibilityCheckerService();
        CompatibilityCheckResult result = compatibilityCheckerService.compareApis(api1, api2);

        // return result
        return Response.ok(result).build();
    }
}
