package cz.zcu.kiv.crce.exampleplugin.internal.rest;

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

import org.osgi.framework.InvalidSyntaxException;

import cz.zcu.kiv.crce.exampleplugin.internal.Activator;
import cz.zcu.kiv.crce.metadata.Resource;


/**
 * Server will provide a single bundle.
 * @author Jan Reznicek
 *
 */
@Path("/bundle")
public class BundleResource {
	
	/**
	 * size of buffer between input an output stream for a bundle
	 */
	private static final int BUFSIZE = 1024;
	
	/**
	 * Create output stream from bundle
	 * @param resourceFile file with bundle
 	 * @return output stream with bundle
	 * @throws WebApplicationException 
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
					throw new WebApplicationException(e);
				} finally {
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
	
	
	private StreamingOutput bundlebyFilter(String filter) {
		
		System.out.println("Get bundle by filter: " + filter);
		
		try {
			Resource[] storeResources;
			storeResources = Activator.instance().getStore().getRepository().getResources(filter);
			
			if(storeResources.length < 1) {
				System.out.println("Resource was not found.");
				return null;
			}
			
			Resource resource;
			
			if(storeResources.length > 1) {
				resource = resourceWithHighestVersion(storeResources);
			} else {
				resource = storeResources[0];
			}
			
			final File resourceFile = new File(resource.getUri());
			
			return getBundleAsStream(resourceFile);
			

		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			return null;
		} catch (WebApplicationException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get bundle by id.
	 * URI is /bundle/id.
	 * @param id id of a bundle
	 * @return bundle or error response
	 */
	@GET @Path("{id}")
	@Produces({MediaType.APPLICATION_OCTET_STREAM})
	public Response getBundle(@PathParam("id") String id) {
	    
		String filter = "(id="+id+")";
		
		StreamingOutput output = bundlebyFilter(filter);
		
		if(output != null) {
			
			return Response.ok(output).header("content-disposition","attachment; filename = " + id + ".jar").build();
		} else {
			return Response.status(404).build();
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
		StreamingOutput output = null;

		if ((name != null) & (version != null)) {
			String filter = "(&(symbolicname=" + name + ")(version=" + version + "))";

			output = bundlebyFilter(filter);

		} else if ((name != null) & (version == null)) {

			String filter = "(symbolicname=" + name + ")";

			output = bundlebyFilter(filter);

		} else {
			//wrong request
			return Response.status(400).build();
		}

		if (output != null) {

			return Response
					.ok(output)
					.header("content-disposition",
							"attachment; filename = " + name + ".jar").build();
		} else {
			//bundle was not found
			return Response.status(404).build();
		}
	}
	
	/**
	 * Select from array of resources the one with highest version
	 * @param storeResources array of resources
	 * @return resource with highest version
	 */
	private Resource resourceWithHighestVersion(Resource[] storeResources) {		
		
		
		if(storeResources.length < 1) {
			return null;
		}
		Resource resourceWithHighestVersion = storeResources[0];
		
		for(Resource res: storeResources) {
			if(resourceWithHighestVersion.getVersion().compareTo(res.getVersion()) < 0) {
				resourceWithHighestVersion = res;
			}
		}
		
		return resourceWithHighestVersion;
	}



}
