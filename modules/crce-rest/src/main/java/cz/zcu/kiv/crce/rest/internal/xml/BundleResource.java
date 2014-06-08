package cz.zcu.kiv.crce.rest.internal.xml;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiIdentity;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.type.Version;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;
import cz.zcu.kiv.crce.rest.internal.Activator;
import cz.zcu.kiv.crce.rest.internal.GetBundle;


/**
 * Server will provide a single bundle.
 *
 * @author Jan Reznicek
 */
@Path("/bundle")
public class BundleResource extends ResourceParent implements GetBundle {

    private static final Logger logger = LoggerFactory.getLogger(BundleResource.class);

    /**
     * size of buffer between input an output stream for a bundle
     */
    private static final int BUFSIZE = 1024;

    /**
     * Create output stream from bundle
     *
     * @param resourceFile file with bundle
     * @return output stream with bundle
     * @throws WebApplicationException exception during converting bundle to output stream
     */
    private StreamingOutput getBundleAsStream(final File resourceFile) throws WebApplicationException {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {

                try (DataInputStream resourceInput = new DataInputStream(new FileInputStream(resourceFile));
                     OutputStream resourceOutput = new BufferedOutputStream(output)) {

                    byte[] buffer = new byte[BUFSIZE];
                    int bytesRead;
                    while ((bytesRead = resourceInput.read(buffer)) != -1) {
                        resourceOutput.write(buffer, 0, bytesRead);
                    }
                    resourceOutput.flush();
                } catch (RuntimeException e) {

                    logger.warn("Request ({}) - Converting bundle to output stream failed.", getRequestId());

                    throw new WebApplicationException(e, 500);
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

        List<String> categories = Activator.instance().getMetadataService().getCategories(resource);
        if (categories.contains("osgi")) {
            return id + ".jar";
        }
        if (categories.contains("zip")) {
            return id + ".zip";
        } else {
            return id;
        }
    }

    /**
     * Get file name from resource.
     * If original file name is unknown, create name from resource id.
     *
     * @param resource resource
     * @return resource file name
     */
    private String getFileName(Resource resource) {
        try {
            return Activator.instance().getMetadataService().getFileName(resource);
        } catch (IllegalStateException e) {
            logger.warn("File name is unknown, it will be generated", e);
            return createFileName(resource);
        }
    }

    /**
     * Create response with bundle by filter.
     * Find bundle according LDAP filter in store repository and return him as output stream.
     *
     * @param requirement LDAP filter
     * @return bundle as output stream
     * @throws WebApplicationException some exception, contains html error status
     */
    private Response responseByRequirement(Requirement requirement) throws WebApplicationException {

        logger.debug("Request ({}) - Get bundle by filter: {}", getRequestId(), requirement);

        Resource resource = findSingleBundleByFilterWithHighestVersion(requirement);

        final File resourceFile = new File(Activator.instance().getMetadataService().getUri(resource));

        StreamingOutput output = getBundleAsStream(resourceFile);

        MetadataService metadataService = Activator.instance().getMetadataService();
        Attribute<String> attribute = metadataService.getIdentity(resource)
                .getAttribute(new SimpleAttributeType<>("mime", String.class)); // TODO create attribute type

        Response response =
                Response.ok(output)
                        .type(attribute != null ? attribute.getValue() : MediaType.APPLICATION_OCTET_STREAM)
                        .header("content-disposition", "attachment; filename = " + getFileName(resource))
                        .build();

        return response;
    }

    /**
     * Get bundle by id.
     * URI is /bundle/id.
     *
     * @param id id of a bundle
     * @return bundle or error response
     */
    @GET
    @Path("{id}")
    @Override
    public Response getBundleById(@PathParam("id") String id) {
        newRequest();
        logger.debug("Request ({}) - Get bundle by id request was received.", getRequestId());

        Requirement requirement = Activator.instance().getMetadataFactory().createRequirement(NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);
        requirement.addAttribute(NsOsgiIdentity.ATTRIBUTE__NAME, id);

        try {
            Response response = responseByRequirement(requirement);

            logger.debug("Request ({}) - Response was successfully created.", getRequestId());

            return response;

        } catch (WebApplicationException e) {
            return e.getResponse();
        }
    }

    /**
     * Return bundle specified by name and version.
     * If version is not set, select the one with highest version.
     *
     * @param name    name of bundle
     * @param version version of bundle
     * @return bundle or error response
     */
    @GET
    @Override
    public Response getBundlebyNameAndVersion(@QueryParam("name") String name, @QueryParam("version") String version) {
        newRequest();

        logger.debug("Request ({}) - Get bundle by name and version request was received.", getRequestId());

        Requirement requirement = Activator.instance().getMetadataFactory().createRequirement(NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY);

        if (name == null) {
            logger.debug("Request ({}) - Wrong request, name of requested bundle has to be set.", getRequestId());
            return Response.status(400).build();
        }

        requirement.addAttribute(NsOsgiIdentity.ATTRIBUTE__SYMBOLIC_NAME, name);

        if (version != null) {
            requirement.addAttribute(NsOsgiIdentity.ATTRIBUTE__VERSION, new Version(version));
        }

        try {
            Response response = responseByRequirement(requirement);

            logger.debug("Request ({}) - Response was successfully created.", getRequestId());

            return response;
        } catch (WebApplicationException e) {
            return e.getResponse();
        }
    }

    /**
     * Allows sw upload of bundles into CRCE. Automatically saves the bundle into buffer and commits it to store.
     *
     * @param uploadedInputStream file stream
     * @param fileDetail          file headers
     * @param req                 request
     * @return OK if success, 403 otherwise
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadBundle(@FormDataParam("file") InputStream uploadedInputStream,
                                 @FormDataParam("file") FormDataContentDisposition fileDetail,
                                 @Context HttpServletRequest req) {
        newRequest();
        logger.debug("Get buffer for uploadBundle request {}.", getRequestId());
        Buffer b = Activator.instance().getBuffer(req);

        String filename = fileDetail.getFileName();
        logger.debug("Uploaded filename: {}", filename);
        try {
            b.put(filename, uploadedInputStream);
            b.commit(true);
        } catch (IOException | RefusedArtifactException e) {
            logger.error("Error during resource upload.", e);
            return Response.status(403).build();
        }

        logger.debug("Upload of bundle via REST successful.");
        return Response.ok().build();
    }
}
