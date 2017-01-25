package cz.zcu.kiv.crce.rest.internal.xml;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.compatibility.service.CompatibilitySearchService;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiIdentity;
import cz.zcu.kiv.crce.rest.internal.Activator;
import cz.zcu.kiv.crce.rest.internal.GetReplaceBundle;
import cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Repository;
import cz.zcu.kiv.crce.rest.internal.mapping.MetadataFilter;

@Path("/replace-bundle")
public class ReplaceBundleResource extends ResourceParent implements GetReplaceBundle {

    private static final Logger log = LoggerFactory.getLogger(ReplaceBundleResource.class);

    public static final String UPGRADE_OP = "upgrade";
    public static final String DOWNGRADE_OP = "downgrade";
    public static final String LOWEST_OP = "lowest";
    public static final String HIGHEST_OP = "highest";
    public static final String ANY_OP = "any";

    /**
     * Find resource in repository by its id.
     *
     * @param id id of resource
     * @return the resource
     * @throws WebApplicationException the resource was not found.
     */
    private Resource findResource(String id) throws WebApplicationException {
        Requirement req = Activator.instance().getMetadataFactory().createRequirement(NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
        req.addAttribute(NsOsgiIdentity.ATTRIBUTE__NAME, id); //osgi name is crce id (symbolicname-version)

        return findSingleBundleByFilter(req);

    }

    /**
     * Return resource, that could replace client bundle.
     * Kind of returned resource depends on operation op.
     * <p/>
     * Operations:
     * <ul>
     * <li>upgrade (Default)- nearest higher version </li>
     * <li>downgrade - nearest lower</li>
     * <li>highest - highest version</li>
     * <li>lowest - lowest version</li>
     * <li>any - different than version from client</li>
     * </ul>
     * If no wanted version if available (ex no higher in upgrade op), resource from client is returned to client.
     *
     * @param op             operation
     * @param clientResource resource to replace
     * @return resource, that could replace client bundle.
     * @throws WebApplicationException unsupported operation
     */
    @Nullable
    private Resource findResourceToReturn(String op, Resource clientResource) throws WebApplicationException {
        Resource resourceToReturn;
        CompatibilitySearchService service = Activator.instance().getCompatibilityService();
        if(service == null) {
            return null;
        }

        if (op != null) {
            switch (op) {
                case LOWEST_OP:
                    resourceToReturn = service.findLowestDowngrade(clientResource);
                    break;
                case HIGHEST_OP:
                    resourceToReturn = service.findHighestUpgrade(clientResource);
                    break;
                case DOWNGRADE_OP:
                    resourceToReturn = service.findNearestDowngrade(clientResource);
                    break;
                case UPGRADE_OP:
                    resourceToReturn = service.findNearestUpgrade(clientResource);
                    break;
                case ANY_OP:
                    //use highest (if there is no higher, use nearest lower)
                    resourceToReturn = service.findHighestUpgrade(clientResource);
                    if (resourceToReturn == null) {
                        resourceToReturn = service.findNearestDowngrade(clientResource);
                    }
                    break;
                default:
                    log.warn("Request ({}) - Unsupported operation (op) : {}.", getRequestId(), op);
                    throw new WebApplicationException(300);
            }


        } else {
            resourceToReturn = service.findNearestUpgrade(clientResource);
        }

        return resourceToReturn;
    }


    /**
     * In current version return resource with same name as in id and highest possible version.
     *
     * @param id
     * @param op
     * @return resource
     */
    @GET
    @Produces({MediaType.APPLICATION_XML})
    @Override
    public Response replaceBundle(@QueryParam("id") String id, @QueryParam("op") String op, @Context UriInfo ui) {
        newRequest();
        log.debug("Request ({}) - Get replace bundle request was received.", getRequestId());

        try {
            log.debug("Request ({}) -  Replace bundle with id: {}", getRequestId(), id);

            Resource clientResource = findResource(id);

            Resource resourceToReturn = findResourceToReturn(op, clientResource);

            Response response;
            if (resourceToReturn != null) {

                List<Resource> resourcesToReturn = Collections.singletonList(resourceToReturn);

                MetadataFilter include = new MetadataFilter();
                include.includeAll();

                Repository repositoryBean = Activator.instance().getConvertorToBeans().mapRepository(resourcesToReturn, include, ui);

                response = Response.ok(createXML(repositoryBean)).build();
                log.debug("Request ({}) - Response was successfully created.", getRequestId());

            } else {

                response = Response.status(Response.Status.NOT_FOUND).build();
                log.debug("Request ({}) - no replacement found. ", getRequestId());

            }

            return response;


        } catch (WebApplicationException e) {
            return e.getResponse();
        }
    }
}
