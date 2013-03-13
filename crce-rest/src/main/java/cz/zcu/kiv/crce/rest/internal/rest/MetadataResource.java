package cz.zcu.kiv.crce.rest.internal.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import cz.zcu.kiv.crce.rest.internal.rest.convertor.ConvertorToBeans;
import cz.zcu.kiv.crce.rest.internal.rest.convertor.IncludeMetadata;
import cz.zcu.kiv.crce.rest.internal.rest.generated.Trepository;

/**
 * Server will provide a metadata information about resources in the repository.
 * @author Jan Reznicek
 *
 */
@Path("/metadata")
public class MetadataResource extends ResourceParent{
	
	private final Logger log = LoggerFactory.getLogger(MetadataResource.class);
    
	
	/**
	 * Returns XML with metadata of resources from the store repository.
	 * If the request is without filter query parameter, return all resources.
	 * If the request have filter parameter, return resources that met the filter.
	 * @param filter obligatory LDAP filter
	 * @return XML with metadata of resources from the store repository
	 */
    @GET
    @Produces({MediaType.APPLICATION_XML })
    public Response getMetadata(@QueryParam("filter") String filter, @QueryParam("core") String core, @QueryParam("cap") String cap, @QueryParam("req") String req, @QueryParam("prop") String prop) {
    	requestId++;
    	log.debug("Request ({}) - Get metadata request was received.", requestId);
    	
    	Resource[] storeResources;
    	
    	IncludeMetadata include = new IncludeMetadata();
    	
    	if(core == null && cap == null && req == null && prop == null) {
    		//include all
    		include.includeAll();
    	} else {
    		if(core != null) {
    			include.setIncludeCore(true);
    		}
    		if(cap != null) {
    			include.setIncludeCaps(true);
    			if(cap.length() > 0) {
    				include.setIncludeCapseByName(cap);
    			}
    		}
    		if(req != null) {
    			include.setIncludeReqs(true);
    			if(req.length() > 0) {
    				include.setIncludeReqsByName(req);
    			}
    		}
    		if(prop != null) {
    			include.setIncludeProps(true);
    			if(prop.length() > 0) {
    				include.setIncludePropsByName(prop);
    			}
    		}
    	}    	
    	
    	
    	try {
        	if(filter != null) {
        		storeResources = Activator.instance().getStore().getRepository().getResources(filter);
        	} else {
        		storeResources = Activator.instance().getStore().getRepository().getResources();
        	}
    		
			if(storeResources.length > 0) {
				ConvertorToBeans conv = new ConvertorToBeans();
				Trepository repository = conv.convertRepository(storeResources, include);
				Response response = Response.ok(createXML(repository)).build();				
				log.debug("Request ({}) - Response was successfully created.", requestId);
				return response;
			} else {				
				log.debug("Request ({}) - No resource was found.", requestId);
				return Response.status(404).build();
			}
		} catch (WebApplicationException e) {			
			return e.getResponse();
			
		} catch (InvalidSyntaxException e) {
			log.warn("Request ({}) - Invalid syntax of request LDAP filter.", requestId);
			log.debug(e.getMessage(), e);
			return Response.status(400).build();
		}

    }
    

    /**
     * Return  xml with metadata of one resource, that is specified by id.
     * @param id id of the resource
     * @return xml with metadata about the resource
     */
    @GET @Path("{id}")
    @Produces({MediaType.APPLICATION_XML })
    public Response getMetadataById(@PathParam("id") String id, @QueryParam("core") String core, @QueryParam("cap") String cap, @QueryParam("req") String req, @QueryParam("prop") String prop) {
    	requestId++;
    	log.debug("Request ({}) - Get metadata request for resource with id {} was received.",requestId ,id);
    	
    	IncludeMetadata include = new IncludeMetadata();
    	
    	if(core == null && cap == null && req == null && prop == null) {
    		//include all
    		include.includeAll();
    	} else {
    		if(core != null) {
    			include.setIncludeCore(true);
    		}
    		if(cap != null) {
    			include.setIncludeCaps(true);
    			if(cap.length() > 0) {
    				include.setIncludeCapseByName(cap);
    			}
    		}
    		if(req != null) {
    			include.setIncludeReqs(true);
    			if(req.length() > 0) {
    				include.setIncludeReqsByName(req);
    			}
    		}
    		if(prop != null) {
    			include.setIncludeProps(true);
    			if(prop.length() > 0) {
    				include.setIncludePropsByName(prop);
    			}
    		}
    	}
    	
    	try {
			Resource[] storeResources;
			String filter = "(id="+id+")";
			storeResources = Activator.instance().getStore().getRepository().getResources(filter);
			
	    	try {
				if(storeResources.length > 0) {
					ConvertorToBeans conv = new ConvertorToBeans();
					Trepository repository = conv.convertRepository(storeResources, include);
					Response response = Response.ok(createXML(repository)).build();
					log.debug("Request ({}) - Response was successfully created.", requestId);
					return response;
				} else {
					log.debug("Request ({}) - No resource was found.", requestId);
					return Response.status(404).build();
				}
			} catch (WebApplicationException e) {
				
				return e.getResponse();
			}
			
		} catch (InvalidSyntaxException e) {
			log.warn("Request ({}) - Invalid syntax of request LDAP filter.", requestId);
			log.debug(e.getMessage(), e);
			return Response.status(400).build();
		}    	
        
    }
 

}
