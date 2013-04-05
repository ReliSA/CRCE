package cz.zcu.kiv.crce.rest.internal.rest.xml;

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

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.rest.internal.rest.GetReplaceBundle;
import cz.zcu.kiv.crce.rest.internal.rest.convertor.ConvertorToBeans;
import cz.zcu.kiv.crce.rest.internal.rest.convertor.IncludeMetadata;
import cz.zcu.kiv.crce.rest.internal.rest.generated.Trepository;

@Path("/replace-bundle")
public class ReplaceBundleResource extends ResourceParent implements GetReplaceBundle {
	
	private static final Logger log = LoggerFactory.getLogger(ReplaceBundleResource.class);
	
	public static final String UPGRADE_OP = "upgrade";
	public static final String DOWNGRADE_OP = "downgrade";
	public static final String LOWER_OP = "lower";
	public static final String HIGHER_OP = "higher";
	public static final String ANY_OP =  "any";
	
	/**
	 * Find resource in repository by its id.
	 * @param id  id of resource
	 * @return the resource
	 * @throws WebApplicationException the resource was not found.
	 */
	private Resource findResource(String id) throws WebApplicationException {

		String filter = "(id=" + id + ")";

		return findSingleBundleByFilter(filter);

	}
	
	/**
	 * Find resource with nearest lower version from clientResource
	 * @param resourcesWithSameName all resources with same names as clientResource
	 * @param clientResource resource from client request
	 * @return nearest lower resource
	 * @throws WebApplicationException
	 */
	private Resource nearestLowerResource(Resource[] resourcesWithSameName, Resource clientResource) throws WebApplicationException{
		if(resourcesWithSameName.length < 1) {
			//should not occurred, at least client resource should be present
			throw new WebApplicationException(404);
		}
		Resource lowerRes = resourceWithLowestVersion(resourcesWithSameName);
		
		if(lowerRes.getId().equals(clientResource.getId())) {
			log.info("Request ({}) - Nearist lower - in repository is no resource " +
					"with lower version than resource from client request ({})."
					, requestId, clientResource.getId());
			//throw new WebApplicationException(404);
		}
		
		for(Resource res: resourcesWithSameName) {
			//find lowest from all resources higher than clientResource
			if(res.getVersion().compareTo(clientResource.getVersion())<0 && res.getVersion().compareTo(lowerRes.getVersion())>0) {
				lowerRes = res;
			}
		}
		
		log.debug("Request ({}) - Bundle with nearest lower version is: {}.", requestId, lowerRes.getId());
		
		return lowerRes;
	}
	
	/**
	 * Find resource with nearest higher version from clientResource
	 * @param resourcesWithSameName all resources with same names as clientResource
	 * @param clientResource resource from client request
	 * @return nearest higher resource
	 * @throws WebApplicationException
	 */
	private Resource nearestHigherResource(Resource[] resourcesWithSameName, Resource clientResource) throws WebApplicationException{
		if(resourcesWithSameName.length < 1) {
			//should not occurred, at least client resource should be present
			throw new WebApplicationException(404);
		}
		Resource higherRes = resourceWithHighestVersion(resourcesWithSameName);
		
		if(higherRes.getId().equals(clientResource.getId())) {
			log.info("Request ({}) - Nearist higher - in repository is no resource " +
					"with higher version than resource from client request ({})."
					, requestId, clientResource.getId());
			//throw new WebApplicationException(404);
		}
		
		for(Resource res: resourcesWithSameName) {
			//find lowest from all resources higher than clientResource
			if(res.getVersion().compareTo(clientResource.getVersion())>0 && res.getVersion().compareTo(higherRes.getVersion())<0) {
				higherRes = res;
			}
		}
		
		log.debug("Request ({}) - Bundle with nearest lower version is: {}.", requestId, higherRes.getId());
		
		return higherRes;
	}
	
	/**
	 * Return resource, that could replace client bundle.
	 * Kind of returned resource depends on operation op.
	 * 
	 * @param op operation
	 * @param filter filter to all bundles with same name as client bundle.
	 * @return resource, that could replace client bundle.
	 * @throws WebApplicationException unsupported operation
	 */
	private Resource findResourceToReturn(String op, Resource clientResource) throws WebApplicationException {
		Resource resourceToReturn = null;
		String nameFilter = "(symbolicName=" + clientResource.getSymbolicName() + ")";
		Resource[] resourcesWithSameName = findBundlesByFilter(nameFilter);
		
		if(op!= null) {
			switch (op) {
			case DOWNGRADE_OP:
				resourceToReturn = findSingleBundleByFilterWithLowestVersion(nameFilter);
				break;
			case UPGRADE_OP:
				resourceToReturn = findSingleBundleByFilterWithHighestVersion(nameFilter);
				break;
			case LOWER_OP:				
				resourceToReturn = nearestLowerResource(resourcesWithSameName, clientResource);
				break;
			case HIGHER_OP:
				resourceToReturn = nearestHigherResource(resourcesWithSameName, clientResource);
				break;
			case ANY_OP:
				//use highest (if there is no higher, use nearest lowest)
				resourceToReturn = findSingleBundleByFilterWithHighestVersion(nameFilter);
				if(resourceToReturn.getId().equals(clientResource.getId())) {
					resourceToReturn = nearestLowerResource(resourcesWithSameName, clientResource);
				}
				break;
			default:
				log.warn("Request ({}) - Unsupported operation (op) : {}.", requestId, op);
				throw new WebApplicationException(300);
			}
			

		} else {
			resourceToReturn = findSingleBundleByFilterWithHighestVersion(nameFilter);
		}
		
		return resourceToReturn;
	}
	
	
	/**
	 * In current version return resource with same name as in id and highest possible version.
	 * @param id
	 * @return resource
	 */
    @GET
    @Produces({MediaType.APPLICATION_XML })
    public Response replaceBundle(@QueryParam("id") String id, @QueryParam("op") String op, @Context UriInfo ui) {
    	requestId++;
    	log.debug("Request ({}) - Get replace bundle request was received.", requestId);
    	

    		try {
				log.debug("Request ({}) -  Replace bundle with id: {}", requestId, id);
				
				
				Resource clientResource = findResource(id);				
				
				Resource resourceToReturn = findResourceToReturn(op, clientResource);
				
				Resource[] resourcesToReturn = new Resource[]{resourceToReturn};
				
		    	IncludeMetadata include = new IncludeMetadata();
		    	include.includeAll();
		    		
		    	ConvertorToBeans convertor = new ConvertorToBeans();
				Trepository repositoryBean = convertor.convertRepository(resourcesToReturn, include, ui);
				
				Response response = Response.ok(createXML(repositoryBean)).build();
				log.debug("Request ({}) - Response was successfully created.", requestId);
				return response;
				
			} catch (WebApplicationException e) {
				return e.getResponse();
			} 		

    }

}
