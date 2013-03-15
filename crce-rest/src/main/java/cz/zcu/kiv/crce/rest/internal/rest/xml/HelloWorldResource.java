package cz.zcu.kiv.crce.rest.internal.rest.xml;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.rest.internal.Activator;

/**
 * Test class for REST and Jersey.
 * Will be removed later.
 * @author Jan Reznicek
 *
 */
@Path("/helloworld")
public class HelloWorldResource extends ResourceParent{
	
	private static final Logger log = LoggerFactory.getLogger(HelloWorldResource.class);
	
	// The Java method will process HTTP GET requests
	@GET
	// The Java method will produce content identified by the MIME Media
	// type "text/plain"
	@Produces("text/plain")
	public String getClichedMessage() {
		requestId++;
		log.debug("Request ({}) - Hello world.", requestId);
		// Return some cliched textual content
		return "Hello World";
	}

	/**
	 * Return plain text with all resources in the store repository.
	 * @return plain text with all resources in the store repository
	 */
	@GET @Path("/bundles")
	@Produces({ MediaType.TEXT_PLAIN })
	public String getResources() {
		requestId++;
		log.debug("Request ({}) - Get resources as text.", requestId);
		Resource[] storeResources;
		storeResources = Activator.instance().getStore().getRepository()
				.getResources();
		

		String output = "";
		for (Resource res: storeResources) {
			output += res.getId() + "\n";
			
			Capability[] caps =  res.getCapabilities("package");
			for(Capability cap: caps) {
				Property[] props = cap.getProperties();
				for(Property prop: props) {
					output += "Pack cap: " + prop.getName() + " " + prop.getValue() +"\n";
				}
			}
			Requirement[] reqs  = res.getRequirements("package");
			for(Requirement req: reqs) {
			
				output += "Pack req: " + req.getName() + " " + req.getFilter() +"\n";

			}
			
		}
		return output;

	}
	
	@POST
	@Path("/post")
	@Consumes(MediaType.APPLICATION_XML)
	public Response testPost(String requestXml) {
		requestId++;
		log.debug("Request ({}) - Test post.", requestId);
		String result = "Post with repository accepted : " + requestXml;
		log.info("Request ({}) - Test post result: ", requestId, result);
		
		return Response.status(201).entity(result).build();
 
	}

}
