package cz.zcu.kiv.crce.rest.internal.xml;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiIdentity;
import cz.zcu.kiv.crce.rest.internal.Activator;
import cz.zcu.kiv.crce.rest.internal.GetMetadata;
import cz.zcu.kiv.crce.rest.internal.convertor.IncludeMetadata;
import cz.zcu.kiv.crce.rest.internal.generated.Trepository;

/**
 * Server will provide a metadata information about resources in the repository.
 * @author Jan Reznicek
 *
 */
@Path("/metadata")
public class MetadataResource extends ResourceParent implements GetMetadata {

	private static final Logger logger = LoggerFactory.getLogger(MetadataResource.class);


	/**
	  * {@inheritDoc}
	  */
    @GET
    @Produces({MediaType.APPLICATION_XML })
    @Override
    public Response getMetadata(
            @QueryParam("filter") String filter,
            @QueryParam("core") String core,
            @QueryParam("cap") String cap,
            @QueryParam("req") String req,
            @QueryParam("prop") String prop,
            @Context UriInfo ui) {

    	newRequest();
    	logger.debug("Request ({}) - Get metadata request was received.", getRequestId());

    	List<Resource> storeResources;

    	IncludeMetadata include = new IncludeMetadata();

        if (core == null && cap == null && req == null && prop == null) {
            //include all
            include.includeAll();
        } else {
            if (core != null) {
                include.setIncludeCore(true);
            }
            if (cap != null) {
                include.setIncludeCaps(true);
                if (cap.length() > 0) {
                    include.setIncludeCapByName(cap);
                }
            }
            if (req != null) {
                include.setIncludeReqs(true);
                if (req.length() > 0) {
                    include.setIncludeReqByName(req);
                }
            }
            if (prop != null) {
                include.setIncludeProps(true);
                if (prop.length() > 0) {
                    include.setIncludePropByName(prop);
                }
            }
        }


        try {
            if (filter != null) {
                logger.debug("Filter used to get metadata: {}.", filter);
                logger.warn("OBR filter is not supported in CRCE 2, all resources will be returned."); // TODO API incompatibility
//                storeResources = Activator.instance().getStore().getResources(filter);
                storeResources = Activator.instance().getStore().getResources();
            } else {
                storeResources = Activator.instance().getStore().getResources();
            }

            if (!storeResources.isEmpty()) {
                Trepository repository = Activator.instance().getConvertorToBeans().convertRepository(storeResources, include, ui);
                Response response = Response.ok(createXML(repository)).build();
                logger.debug("Request ({}) - Response was successfully created.", getRequestId());
                return response;
            } else {
                logger.debug("Request ({}) - No resource was found.", getRequestId());
                return Response.status(404).build();
            }
        } catch (WebApplicationException e) {
            return e.getResponse();

//        } catch (InvalidSyntaxException e) {
//            logger.warn("Request ({}) - Invalid syntax of request LDAP filter.", getRequestId());
//            logger.debug(e.getMessage(), e);
//            return Response.status(400).build();
        }

    }


	/**
	 * {@inheritDoc}
	 */
	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_XML })
    @Override
    public Response getMetadataById(
            @PathParam("id") String id,
            @QueryParam("core") String core,
            @QueryParam("cap") String cap,
            @QueryParam("req") String req,
            @QueryParam("prop") String prop,
            @Context UriInfo ui) {

    	newRequest();
        logger.debug("Request ({}) - Get metadata request for resource with id {} was received.", getRequestId(), id);

    	IncludeMetadata include = new IncludeMetadata();

        if (core == null && cap == null && req == null && prop == null) {
            //include all
            include.includeAll();
        } else {
            if (core != null) {
                include.setIncludeCore(true);
            }
            if (cap != null) {
                include.setIncludeCaps(true);
                if (cap.length() > 0) {
                    include.setIncludeCapByName(cap);
                }
            }
            if (req != null) {
                include.setIncludeReqs(true);
                if (req.length() > 0) {
                    include.setIncludeReqByName(req);
                }
            }
            if (prop != null) {
                include.setIncludeProps(true);
                if (prop.length() > 0) {
                    include.setIncludePropByName(prop);
                }
            }
        }

        try {
            Requirement requirement = Activator.instance().getResourceFactory().createRequirement(NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
            requirement.addAttribute(NsOsgiIdentity.ATTRIBUTE__NAME, id);

            List<Resource> storeResources = Activator.instance().getStore().getResources(requirement);

            try {
                if (!storeResources.isEmpty()) {
                    Trepository repository = Activator.instance().getConvertorToBeans().convertRepository(storeResources, include, ui);
                    Response response = Response.ok(createXML(repository)).build();
                    logger.debug("Request ({}) - Response was successfully created.", getRequestId());
                    return response;
                } else {
                    logger.debug("Request ({}) - No resource was found.", getRequestId());
                    return Response.status(404).build();
                }
            } catch (WebApplicationException e) {
                return e.getResponse();
            }
        } catch (Exception e) {
			logger.error("Request ({}) - Could not get resources from store.", getRequestId(), e);
			throw new WebApplicationException(500);
		}
    }
}
