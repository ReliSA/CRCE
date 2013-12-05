package cz.zcu.kiv.crce.rest.internal.rest.xml;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RevokedArtifactException;
import cz.zcu.kiv.crce.rest.internal.Activator;
import cz.zcu.kiv.crce.rest.internal.rest.GetBundle;
import cz.zcu.kiv.crce.rest.internal.rest.convertor.MimeTypeSelector;


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
				} catch (RuntimeException e) {

					log.warn("Request ({}) - Converting bundle to output stream failed.", getRequestId());

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
	 * Create file name of resource.
	 * Used for resources, whose original file name is unknown.
	 *
	 * @param resource resource
	 * @return file name of resource
	 */
	private String createFileName(Resource resource) {
		String id = resource.getId();

		if(resource.hasCategory("osgi")) {
			return id + ".jar";
		} if(resource.hasCategory("zip")) {
			return id + ".zip";
		} else return id;
	}

	/**
	 * Get file name from resource.
	 * If original file name is unknown, create name from resource id.
	 * @param resource resource
	 * @return resource file name
	 */
	private String getFileName(Resource resource) {
		Capability[] caps = resource.getCapabilities("file");

		if (caps.length > 0) {
			for(Capability cap:caps) {
				String orgFileName = cap.getPropertyString("name");
				if (orgFileName != null) {
					return orgFileName;
				}
			}
			return createFileName(resource);
		} else {
			return createFileName(resource);
		}
	}

	/**
	 * Create response with bundle by filter.
	 * Find bundle according LDAP filter in store repository and return him as output stream.
	 * @param filter LDAP filter
	 * @return bundle as output stream
	 * @throws WebApplicationException some exception, contains html error status
	 */
	private Response responseByFilter(String filter) throws WebApplicationException {

		log.debug("Request ({}) - Get bundle by filter: {}", getRequestId(), filter);

		Resource resource = findSingleBundleByFilterWithHighestVersion(filter);

		final File resourceFile = new File(resource.getUri());

		StreamingOutput output = getBundleAsStream(resourceFile);

		MimeTypeSelector mimeTypeSelector = new MimeTypeSelector();

		Response response = Response.ok(output).type(mimeTypeSelector.selectMimeType(resource)).header("content-disposition",
				"attachment; filename = " + getFileName(resource)).build();

		return response;

	}

	/**
	 * Get bundle by id.
	 * URI is /bundle/id.
	 * @param id id of a bundle
	 * @return bundle or error response
	 */
	@GET @Path("{id}")
	public Response getBundleById(@PathParam("id") String id) {
		newRequest();
		log.debug("Request ({}) - Get bundle by id request was received.", getRequestId());

		String filter = "(id="+id+")";

		try {
			Response response = responseByFilter(filter);

			log.debug("Request ({}) - Response was successfully created.",getRequestId());

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
	public Response getBundlebyNameAndVersion(@QueryParam("name") String name, @QueryParam("version") String version) {
		newRequest();
		log.debug("Request ({}) - Get bundle by name and version request was received.", getRequestId());


		String filter;

		if ((name != null) && (version != null)) {
			filter = "(&(symbolicname=" + name + ")(version=" + version + "))";

		} else if ((name != null) && (version == null)) {
			filter = "(symbolicname=" + name + ")";

		} else {
			log.debug(
					"Request ({}) - Wrong request, name of requested bundle has to be set.",
					getRequestId());
			return Response.status(400).build();
		}


		try {

			Response response = responseByFilter(filter);
			log.debug("Request ({}) - Response was successfully created.", getRequestId());

			return response;

		} catch (WebApplicationException e) {
			return e.getResponse();
		}
	}


    /**
     * Allows sw upload of bundles into CRCE. Automatically saves the bundle into buffer and commits it to store.
     * @param uploadedInputStream file stream
     * @param fileDetail file headers
     * @param req request
     * @return OK if success, 403 otherwise
     */
    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadBundle(@FormDataParam("file") InputStream uploadedInputStream,
                                 @FormDataParam("file") FormDataContentDisposition fileDetail,
                                 @Context HttpServletRequest req) {
        newRequest();
        log.debug("Get buffer for uploadBundle request {}.", getRequestId());
        Buffer b = Activator.instance().getBuffer(req);

        String filename = fileDetail.getFileName();
        log.debug("Uploaded filename: {}", filename);
        try {
            b.put(filename, uploadedInputStream);
            b.commit(true);
        } catch (IOException e) {
            log.error("Error during resource upload.", e);
            return  Response.status(403).build();
        } catch (RevokedArtifactException e) {
            log.error("Error during resource upload.", e);
            return Response.status(403).build();
        }

        log.debug("Upload of bundle via REST successful.");
        return Response.ok().build();
    }
}
