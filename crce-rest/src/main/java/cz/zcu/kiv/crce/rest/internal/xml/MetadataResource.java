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

import org.osgi.framework.InvalidSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiIdentity;
import cz.zcu.kiv.crce.rest.internal.Activator;
import cz.zcu.kiv.crce.rest.internal.GetMetadata;
import cz.zcu.kiv.crce.rest.internal.convertor.MetadataFilter;
import cz.zcu.kiv.crce.rest.internal.jaxb.Repository;

/**
 * Server will provide a metadata information about resources in the repository.
 * @author Jan Reznicek
 *
 */
@Path("/metadata")
public class MetadataResource extends ResourceParent implements GetMetadata {

	private static final Logger logger = LoggerFactory.getLogger(MetadataResource.class);


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

    	MetadataFilter include = new MetadataFilter();

        if (core == null && cap == null && req == null && prop == null) {
            //include all
            include.includeAll();
        } else {
            if (core != null) {
                include.setCoreCapabilities(true);
            }
            if (cap != null) {
                include.setIncludeCapabilities(true);
                if (cap.length() > 0) {
                    include.setCapabilityNamespace(cap);
                }
            }
            if (req != null) {
                include.setIncludeRequirements(true);
                if (req.length() > 0) {
                    include.setRequirementNamespace(req);
                }
            }
            if (prop != null) {
                include.setIncludeProperties(true);
                if (prop.length() > 0) {
                    include.setPropertyNamespace(prop);
                }
            }
        }


        try {
            if (filter != null) {
                Requirement requirement = Activator.instance().getFilterParser().parse(filter, NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
                logger.debug("Filter used to get metadata: {}.", filter);
                storeResources = Activator.instance().getStore().getResources(requirement);
            } else {
                storeResources = Activator.instance().getStore().getResources();
            }

            if (!storeResources.isEmpty()) {
                Repository repository = Activator.instance().getConvertorToBeans().mapRepository(storeResources, include, ui);
                Response response = Response.ok(createXML(repository)).build();
                logger.debug("Request ({}) - Response was successfully created.", getRequestId());
                return response;
            } else {
                logger.debug("Request ({}) - No resource was found.", getRequestId());
                return Response.status(404).build();
            }
        } catch (WebApplicationException e) {
            return e.getResponse();
        } catch (InvalidSyntaxException e) {
            logger.warn("Request ({}) - Invalid syntax of request LDAP filter.", getRequestId());
            logger.debug(e.getMessage(), e);
            return Response.status(400).build();
        }
    }


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
            @Context UriInfo uriInfo) {

    	newRequest();
        logger.debug("Request ({}) - Get metadata request for resource with id {} was received.", getRequestId(), id);

    	MetadataFilter filter = new MetadataFilter();

        if (core == null && cap == null && req == null && prop == null) {
            //include all
            filter.includeAll();
        } else {
            if (core != null) {
                filter.setCoreCapabilities(true);
            }
            if (cap != null) {
                filter.setIncludeCapabilities(true);
                if (cap.length() > 0) {
                    filter.setCapabilityNamespace(cap);
                }
            }
            if (req != null) {
                filter.setIncludeRequirements(true);
                if (req.length() > 0) {
                    filter.setRequirementNamespace(req);
                }
            }
            if (prop != null) {
                filter.setIncludeProperties(true);
                if (prop.length() > 0) {
                    filter.setPropertyNamespace(prop);
                }
            }
        }

        try {
            Requirement requirement = Activator.instance().getMetadataFactory().createRequirement(NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
            requirement.addAttribute(NsOsgiIdentity.ATTRIBUTE__NAME, id);

            List<Resource> resources = Activator.instance().getStore().getResources(requirement);

            try {
                if (!resources.isEmpty()) {
                    Repository repository = Activator.instance().getConvertorToBeans().mapRepository(resources, filter, uriInfo);
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
