package cz.zcu.kiv.crce.rest.v2.internal.ws.jersey;

import cz.zcu.kiv.crce.apicomp.ApiCompatibilityCheckerService;
import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Store;
import cz.zcu.kiv.crce.rest.v2.internal.Activator;
import cz.zcu.kiv.crce.rest.v2.internal.ws.ApiCompRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/apicomp")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class ApiCompResJersey implements ApiCompRes {

    private static final Logger logger = LoggerFactory.getLogger(ApiCompResJersey.class);

    @Path("/compare")
    @GET
    @Override
    public Response compareApis(@QueryParam("id1") String id1, @QueryParam("id2") String id2, @DefaultValue("false") @QueryParam("force") boolean force) {
        logger.info("Comparing APIS with ids: '{}' and '{}'.", id1, id2);

        try {
            logger.debug("Getting resources from store.");
            Store store = Activator.instance().getStore();
            Resource api1 = store.getResource(id1, true);
            Resource api2 = store.getResource(id2, true);

            logger.debug("Calling compatibility checker service.");
            ApiCompatibilityCheckerService compatibilityCheckerService = Activator.instance().getApiCompatibilityCheckerService();

            Compatibility c = null;
            if (force) {
                logger.info("Force flag is set, skipping DB lookup.");
            } else {
                c = compatibilityCheckerService.findExistingCompatibility(api1, api2);
            }

            if (c == null) {
                logger.debug("No existing compatibility found for resource pair {};{}. Calculating new.", api1.getId(), api2.getId());
                c = compatibilityCheckerService.compareApis(api1, api2);

                if (c != null) {
                    logger.debug("Saving new compatibility data.");
                    compatibilityCheckerService.saveCompatibility(c);
                }
            }

            logger.debug("Done.");
            if (c == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            return Response.ok(c).build();
        } catch (Exception ex) {
            logger.error("Unexpected error occurred.", ex);
            return Response.serverError().build();
        }
    }
}
