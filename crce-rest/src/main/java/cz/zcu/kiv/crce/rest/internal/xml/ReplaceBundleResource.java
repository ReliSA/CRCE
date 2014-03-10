package cz.zcu.kiv.crce.rest.internal.xml;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import cz.zcu.kiv.crce.metadata.type.Version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiIdentity;
import cz.zcu.kiv.crce.rest.internal.Activator;
import cz.zcu.kiv.crce.rest.internal.GetReplaceBundle;
import cz.zcu.kiv.crce.rest.internal.mapping.MetadataFilter;
import cz.zcu.kiv.crce.rest.internal.jaxb.Repository;

@Path("/replace-bundle")
public class ReplaceBundleResource extends ResourceParent implements GetReplaceBundle {

	private static final Logger log = LoggerFactory.getLogger(ReplaceBundleResource.class);

	public static final String UPGRADE_OP = "upgrade";
	public static final String DOWNGRADE_OP = "downgrade";
	public static final String LOWEST_OP = "lowest";
	public static final String HIGHEST_OP = "highest";
	public static final String ANY_OP =  "any";

	/**
	 * Find resource in repository by its id.
	 * @param id  id of resource
	 * @return the resource
	 * @throws WebApplicationException the resource was not found.
	 */
	private Resource findResource(String id) throws WebApplicationException {
        Requirement requirement = Activator.instance().getMetadataFactory().createRequirement(NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
        requirement.addAttribute(NsOsgiIdentity.ATTRIBUTE__NAME, id);

		return findSingleBundleByFilter(requirement);
	}

	/**
	 * Find resource with nearest lower version from clientResource
	 * @param resourcesWithSameName all resources with same names as clientResource
	 * @param clientResource resource from client request
	 * @return nearest lower resource
	 * @throws WebApplicationException
	 */
    private Resource nearestLowerResource(List<Resource> resourcesWithSameName, Resource clientResource) throws WebApplicationException {
        if (resourcesWithSameName.isEmpty()) {
            //should not occurred, at least client resource should be present
            throw new WebApplicationException(404);
        }
        Resource lowerRes = resourceWithLowestVersion(resourcesWithSameName);

        if (lowerRes.getId().equals(clientResource.getId())) {
            log.info("Request ({}) - Nearist lower - in repository is no resource "
                    + "with lower version than resource from client request ({}).", getRequestId(), clientResource.getId());
            //throw new WebApplicationException(404);
        }

        for (Resource res : resourcesWithSameName) {
            //find lowest from all resources higher than clientResource
            Version resVersion = getBundleVersion(res);
            Version clientVersion = getBundleVersion(clientResource);
            Version lowerResVersion = getBundleVersion(lowerRes);

            if (resVersion != null && clientVersion != null && lowerResVersion != null
                    && resVersion.compareTo(clientVersion) < 0
                    && resVersion.compareTo(lowerResVersion) > 0) {
                lowerRes = res;
            }
        }

        log.debug("Request ({}) - Bundle with nearest lower version is: {}.", getRequestId(), lowerRes.getId());

        return lowerRes;
    }

	/**
	 * Find resource with nearest higher version from clientResource
	 * @param resourcesWithSameName all resources with same names as clientResource
	 * @param clientResource resource from client request
	 * @return nearest higher resource
	 * @throws WebApplicationException
	 */
    private Resource nearestHigherResource(List<Resource> resourcesWithSameName, Resource clientResource) throws WebApplicationException {
        if (resourcesWithSameName.isEmpty()) {
            //should not occurred, at least client resource should be present
            throw new WebApplicationException(404);
        }
        Resource higherRes = resourceWithHighestVersion(resourcesWithSameName);

        if (higherRes.getId().equals(clientResource.getId())) {
            log.info("Request ({}) - Nearist higher - in repository is no resource "
                    + "with higher version than resource from client request ({}).", getRequestId(), clientResource.getId());
            //throw new WebApplicationException(404);
        }

        for (Resource res : resourcesWithSameName) {
            //find lowest from all resources higher than clientResource
            Version resVersion = getBundleVersion(res);
            Version clientResourceVersion = getBundleVersion(clientResource);
            Version higherResVersion = getBundleVersion(higherRes);

            if (resVersion != null && clientResourceVersion != null && higherResVersion != null
                    && resVersion.compareTo(clientResourceVersion) > 0
                    && resVersion.compareTo(higherResVersion) < 0) {
                higherRes = res;
            }
        }

        log.debug("Request ({}) - Bundle with nearest lower version is: {}.", getRequestId(), higherRes.getId());

        return higherRes;
    }

	/**
	 * Return resource, that could replace client bundle.
	 * Kind of returned resource depends on operation op.
	 *
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
	 * @param op operation
	 * @param filter filter to all bundles with same name as client bundle.
	 * @return resource, that could replace client bundle.
	 * @throws WebApplicationException unsupported operation
	 */
    private Resource findResourceToReturn(String op, Resource clientResource) throws WebApplicationException {
        Resource resourceToReturn = null;

        Requirement requirement = Activator.instance().getMetadataFactory().createRequirement(NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
        requirement.addAttribute(NsOsgiIdentity.ATTRIBUTE__SYMBOLIC_NAME, getBundleSymbolicName(clientResource));

        List<Resource> resourcesWithSameName = findBundlesByFilter(requirement);

        if (op != null) {
            switch (op) {
                case LOWEST_OP:
                    resourceToReturn = findSingleBundleByFilterWithLowestVersion(requirement);
                    break;
                case HIGHEST_OP:
                    resourceToReturn = findSingleBundleByFilterWithHighestVersion(requirement);
                    break;
                case DOWNGRADE_OP:
                    resourceToReturn = nearestLowerResource(resourcesWithSameName, clientResource);
                    break;
                case UPGRADE_OP:
                    resourceToReturn = nearestHigherResource(resourcesWithSameName, clientResource);
                    break;
                case ANY_OP:
                    //use highest (if there is no higher, use nearest lower)
                    resourceToReturn = findSingleBundleByFilterWithHighestVersion(requirement);
                    if (resourceToReturn.getId().equals(clientResource.getId())) {
                        resourceToReturn = nearestLowerResource(resourcesWithSameName, clientResource);
                    }
                    break;
                default:
                    log.warn("Request ({}) - Unsupported operation (op) : {}.", getRequestId(), op);
                    throw new WebApplicationException(300);
            }
        } else {
            resourceToReturn = nearestHigherResource(resourcesWithSameName, clientResource);
        }

        return resourceToReturn;
	}


	/**
	 * In current version return resource with same name as in id and highest possible version.
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

            List<Resource> resourcesToReturn = Collections.singletonList(resourceToReturn);

            MetadataFilter include = new MetadataFilter();
            include.includeAll();

            Repository repositoryBean = Activator.instance().getConvertorToBeans().mapRepository(resourcesToReturn, include, ui);

            Response response = Response.ok(createXML(repositoryBean)).build();
            log.debug("Request ({}) - Response was successfully created.", getRequestId());
            return response;

        } catch (WebApplicationException e) {
            return e.getResponse();
        }
    }
}
