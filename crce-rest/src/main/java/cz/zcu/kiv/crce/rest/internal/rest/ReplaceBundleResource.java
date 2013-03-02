package cz.zcu.kiv.crce.rest.internal.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.osgi.framework.InvalidSyntaxException;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.rest.internal.Activator;
import cz.zcu.kiv.crce.rest.internal.rest.convertor.ConvertorToBeans;
import cz.zcu.kiv.crce.rest.internal.rest.convertor.IncludeMetadata;
import cz.zcu.kiv.crce.rest.internal.rest.generated.Trepository;

@Path("/replace_bundle")
public class ReplaceBundleResource {
	

	
	/**
	 * Find symbolic name of resource determined by id;
	 * Try to find resource in repository by id. 
	 * If resource is found, return symbolic name from that resource.
	 * Else try to split id to name and return it.
	 * @param id  id of resource
	 * @return symbolic name of resource or null, if id was not found
	 */
	private String findSymbolicName(String id) {
		
		try {
			Resource[] storeResources;
			String searchedName = null;
			String filter = "(id=" + id + ")";
			storeResources = Activator.instance().getStore().getRepository()
					.getResources(filter);

			if (storeResources.length > 0) {
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

				//no resource with any of proposed names was found in repository
				return null;

			}
		} catch (InvalidSyntaxException e) {
			return null;
		}
		
	}
	
	/**
	 * In current version return resource with same name as in id and highest possible version.
	 * @param id
	 * @return
	 */
    @GET
    @Produces({MediaType.APPLICATION_XML })
    public Response getMetadataById(@QueryParam("id") String id) {
    	try {
    		System.out.println("Replace bundle with id: " + id);
			Resource[] storeResources;
			
			String symbolicName = findSymbolicName(id);
			if(symbolicName == null) {
				//could not find symbolic name in id.
				return Response.status(400).build();
			}
			
			String filter = "(symbolicName=" + symbolicName + ")";

			storeResources = Activator.instance().getStore().getRepository().getResources(filter);
			
	    	try {
				if(storeResources.length > 0) {
					Resource resourceToReturn = Utils.resourceWithHighestVersion(storeResources);
					storeResources = new Resource[]{resourceToReturn};
					
			    	IncludeMetadata include = new IncludeMetadata();
			    		//include all
			    		include.includeAll();
			    		
			    	ConvertorToBeans convertor = new ConvertorToBeans();
					Trepository repositoryBean =convertor.convertRepository(storeResources, include);
					return Response.ok(Utils.createXML(repositoryBean)).build();
				} else {
					//no resource was found
					return Response.status(404).build();
				}
			} catch (JAXBException e) {
				//xml export of resource metadata failed
				e.printStackTrace();				
				return Response.serverError().build();
			}
			
		} catch (InvalidSyntaxException e) {
			//Invalid syntax of request
			e.printStackTrace();
			return Response.status(400).build();
		}    	
    }

}
