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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
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
	 * Get bundle by id.
	 * URI is /budnle/id.
	 * @param id id of a bundle
	 * @return bundle
	 */
	@GET @Path("{id}")
	@Produces({MediaType.APPLICATION_OCTET_STREAM})
	public StreamingOutput getBundle(@PathParam("id") String id) {
	    
		
		try {
			Resource[] storeResources;
			String filter = "(id="+id+")";
			storeResources = Activator.instance().getStore().getRepository().getResources(filter);
			
			final File resourceFile = new File(storeResources[0].getUri());
			
			
			
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
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			return null;
		} catch (WebApplicationException e) {
			e.printStackTrace();
			return null;
		}
		
	}

}
