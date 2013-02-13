package cz.zcu.kiv.crce.rest.internal.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.rest.internal.Activator;

/**
 * Test class for REST and Jersey.
 * Will be removed later.
 * @author Jan Reznicek
 *
 */
@Path("/helloworld")
public class HelloWorldResource {
	
	// The Java method will process HTTP GET requests
	@GET
	// The Java method will produce content identified by the MIME Media
	// type "text/plain"
	@Produces("text/plain")
	public String getClichedMessage() {
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
		Resource[] storeResources;
		storeResources = Activator.instance().getStore().getRepository()
				.getResources();

		

		String output = "";
		for (Resource res: storeResources) {
			output += res.getId() + "\n";
		}
		return output;

	}

}
