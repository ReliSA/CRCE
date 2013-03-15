package cz.zcu.kiv.crce.rest.internal.rest.xml;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.rest.internal.rest.GetBundle;


/**
 * Server will provide a single bundle.
 * @author Jan Reznicek
 *
 */
@Path("/bundle")
public class BundleResource extends ResourceParent implements GetBundle {
	
	private static final Logger log = LoggerFactory.getLogger(BundleResource.class);
	
	/**
	 * size of buffer between input an output stream for a bundle
	 */
	private static final int BUFSIZE = 1024;
	
	/**
	 * Create output stream from bundle
	 * @param resourceFile file with bundle
 	 * @return output stream with bundle
	 * @throws WebApplicationException exception during converting bundle to output stream
	 */
	private StreamingOutput getBundleAsStream(final File resourceFile) throws WebApplicationException {
		return new StreamingOutput() {
		    public void write(OutputStream output) throws IOException, WebApplicationException {
		    	DataInputStream resourceInput = null;
		    	OutputStream resourceOutput = null;
				try {
					resourceInput = new DataInputStream(new FileInputStream(resourceFile));
					resourceOutput = new BufferedOutputStream(output);
					
					byte[] buffer = new byte[BUFSIZE]; 
					int bytesRead;
					while ((bytesRead = resourceInput.read(buffer)) != -1) {
						resourceOutput.write(buffer, 0, bytesRead);
					}
					resourceOutput.flush();
				} catch (Exception e) {

					log.warn("Request ({}) - Converting bundle to output stream failed.", requestId);
			
					throw new WebApplicationException(e, 500);
				} 
				
				
				finally {
					if (resourceInput != null) {
						resourceInput.close();
					}
					if (resourceOutput != null) {
						resourceOutput.close();
					}
				}
		    }
		};
	}
	
	
	/**
	 * Find bundle according LDAP filter in store repository and return him as output stream.
	 * @param filter LDAP filter
	 * @return bundle as output stream
	 * @throws WebApplicationException some exception, contains html error status
	 */
	private StreamingOutput bundlebyFilterAsStream(String filter) throws WebApplicationException {

		log.debug("Request ({}) - Get bundle by filter: {}", requestId, filter);

		Resource resource = findSingleBundleByFilterWithHighestVersion(filter);

		final File resourceFile = new File(resource.getUri());

		return getBundleAsStream(resourceFile);

	}
	
	/**
	 * Get bundle by id.
	 * URI is /bundle/id.
	 * @param id id of a bundle
	 * @return bundle or error response
	 */
	@GET @Path("{id}")
	@Produces({MediaType.APPLICATION_OCTET_STREAM})
	public Response getBundleById(@PathParam("id") String id) {
		requestId++;
		log.debug("Request ({}) - Get bundle by id request was received.", requestId );
		
		String filter = "(id="+id+")";
		
		try {
			StreamingOutput output = bundlebyFilterAsStream(filter);

			Response response = Response.ok(output).header("content-disposition",
							"attachment; filename = " + id + ".jar").build();
			
			log.debug("Request ({}) - Response was successfully created.",requestId);
			
			return response;

		} catch (WebApplicationException e) {
			return e.getResponse();
		}
		
	}
	
	/**
	 * Return bundle specified by name and version. 
	 * If version is not set, select the one with highest version.
	 * @param name name of bundle
	 * @param version version of bundle
	 * @return bundle or error response
	 */
	@GET
	@Produces({MediaType.APPLICATION_OCTET_STREAM})
	public Response getBundlebyNameAndVersion(@QueryParam("name") String name, @QueryParam("version") String version) {
		requestId++;
		log.debug("Request ({}) - Get bundle by name and version request was received.", requestId );
		
		StreamingOutput output = null;

		
		String filter;
		
		if ((name != null) & (version != null)) {
			filter = "(&(symbolicname=" + name + ")(version=" + version + "))";
			
		} else if ((name != null) & (version == null)) {
			filter = "(symbolicname=" + name + ")";

		} else {
			log.debug(
					"Request ({}) - Wrong request, name of requested bundle has to be set.",
					requestId);
			return Response.status(400).build();
		}
		
		
		try {
			output = bundlebyFilterAsStream(filter);
			
			Response response = Response.ok(output).header("content-disposition",
					"attachment; filename = " + name + ".jar").build();
			log.debug("Request ({}) - Response was successfully created.", requestId);
			
			return response;
		
		} catch (WebApplicationException e) {
			return e.getResponse();
		}
	}



}
