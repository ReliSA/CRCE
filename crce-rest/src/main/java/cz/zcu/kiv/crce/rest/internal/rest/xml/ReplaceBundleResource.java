package cz.zcu.kiv.crce.rest.internal.rest.xml;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.rest.internal.Activator;
import cz.zcu.kiv.crce.rest.internal.rest.GetReplaceBundle;
import cz.zcu.kiv.crce.rest.internal.rest.convertor.ConvertorToBeans;
import cz.zcu.kiv.crce.rest.internal.rest.convertor.IncludeMetadata;
import cz.zcu.kiv.crce.rest.internal.rest.generated.Trepository;

@Path("/replace_bundle")
public class ReplaceBundleResource extends ResourceParent implements GetReplaceBundle {
	
	private static final Logger log = LoggerFactory.getLogger(ReplaceBundleResource.class);
	
	/**
	 * Find symbolic name of resource determined by id;
	 * Try to find resource in repository by id. 
	 * If resource is found, return symbolic name from that resource.
	 * Else try to split id to name and return it.
	 * @param id  id of resource
	 * @return symbolic name of resource
	 * @throws WebApplicationException the name was not found.
	 */
	private String findSymbolicName(String id) throws WebApplicationException{
		
		try {
			Resource[] storeResources;
			String searchedName = null;
			String filter = "(id=" + id + ")";
			storeResources = Activator.instance().getStore().getRepository()
					.getResources(filter);

			if (storeResources.length > 0) {
				//if same resource is in repository, return its name
				searchedName = storeResources[0].getSymbolicName();
				return searchedName;
			} else {
				
				/*resource is not found in repository by id
				 * Try to separate symbolic name of resource from id by char '-'.
				 * Resources with proposed name are founded in repository.
				 * If no resource is found, new proposed name is proposed by next index of char '-'
				 * This cycle is repeated until proposed name is found or there are no left chars '-' in the id;
				 * */
				
				int proposedSeparatorIndex = id.indexOf('-');
				String proposedName;

				while (proposedSeparatorIndex >= 0) {
					proposedName = id.substring(0, proposedSeparatorIndex);
					filter = "(symbolicName=" + proposedName + ")";
					storeResources = Activator.instance().getStore()
							.getRepository().getResources(filter);

					if (storeResources.length > 0) {
						return proposedName;
					}

					proposedSeparatorIndex = id.indexOf('-',
							proposedSeparatorIndex);
				}

				log.info("Request ({}) - Symbolic name can not be determined or in repository is not a resource with that name.", requestId);
				throw new WebApplicationException(404);

			}
		} catch (InvalidSyntaxException e) {
			log.warn("Request ({}) - Wrong LDAP filter.", requestId);
			log.warn(e.getMessage(),e);
			throw new WebApplicationException(500);
		}
		
	}
	
	
	/**
	 * In current version return resource with same name as in id and highest possible version.
	 * @param id
	 * @return resource
	 */
    @GET
    @Produces({MediaType.APPLICATION_XML })
    public Response replaceBundle(@QueryParam("id") String id) {
    	requestId++;
    	log.debug("Request ({}) - Get replace bundle request was received.", requestId);
    	

    		try {
				log.debug("Request ({}) -  Replace bundle with id: {}", requestId, id);
				
				
				String symbolicName = findSymbolicName(id);
				
				
				String filter = "(symbolicName=" + symbolicName + ")";

				Resource resourceToReturn = findSingleBundleByFilterWithHighestVersion(filter);
				Resource[] resourcesToReturn = new Resource[]{resourceToReturn};
				
		    	IncludeMetadata include = new IncludeMetadata();
		    	//include all
		    	include.includeAll();
		    		
		    	ConvertorToBeans convertor = new ConvertorToBeans();
				Trepository repositoryBean = convertor.convertRepository(resourcesToReturn, include);
				
				Response response = Response.ok(createXML(repositoryBean)).build();
				log.debug("Request ({}) - Response was successfully created.", requestId);
				return response;
				
			} catch (WebApplicationException e) {
				return e.getResponse();
			} 		

    }

}
