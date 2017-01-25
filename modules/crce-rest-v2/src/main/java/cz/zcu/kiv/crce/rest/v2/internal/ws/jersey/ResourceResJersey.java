package cz.zcu.kiv.crce.rest.v2.internal.ws.jersey;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.repository.Buffer;
import cz.zcu.kiv.crce.repository.RefusedArtifactException;
import cz.zcu.kiv.crce.rest.v2.internal.Activator;
import cz.zcu.kiv.crce.rest.v2.internal.ws.ResourceRes;
import cz.zcu.kiv.crce.rest.v2.internal.ws.util.ResponseUtil;
import cz.zcu.kiv.crce.vo.model.metadata.BasicResourceVO;


/**
 * Date: 5.5.15
 *
 * @author Jakub Danek
 */
@Path("/resources")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class ResourceResJersey implements ResourceRes {

    private static final Logger logger = LoggerFactory.getLogger(ResourceResJersey.class);

    @Override
    @GET
    public Response resources() {
        List<Resource> resources = Activator.instance().getStore().getResources();
        List<BasicResourceVO> vos = Activator.instance().getMappingService().mapBasic(resources);

        return Response.ok().entity(new GenericEntity<List<BasicResourceVO>>(vos) {}).build();
    }

    @Override
    @GET
    @Path("/catalogue/{name}")
    public Response resources(@PathParam("name")String name) {
        logger.info("Serving /resources/catalogue" + name + " GET request");
        Requirement r = Activator.instance().getMetadataService().createIdentityRequirement(name);
        List<Resource> resources = Activator.instance().getStore().getResources(r);
        List<BasicResourceVO> vos = Activator.instance().getMappingService().mapBasic(resources);

        return Response.ok().entity(new GenericEntity<List<BasicResourceVO>>(vos) {}).build();
    }

    @Override
    @GET
    @Path("/catalogue/{name}/{version}")
    public Response resources(@PathParam("name")String name, @PathParam("version")String version) {
        logger.info("Serving /resources/catalogue" + name + "/" + version + " GET request");
        Requirement r = Activator.instance().getMetadataService().createIdentityRequirement(name, version);
        List<Resource> resources = Activator.instance().getStore().getResources(r);
        List<BasicResourceVO> vos = Activator.instance().getMappingService().mapBasic(resources);

        return Response.ok().entity(new GenericEntity<List<BasicResourceVO>>(vos) {}).build();
    }

    /**
     * Returns concrete resource binary based on provided UUID
     *
     * @param uuid resource id in crce
     * @return
     */
    @Override
    @GET
    @Path("{id}")
    public Response resourceBinary(@PathParam("id") String uuid) {
        logger.info("Serving /resources/" + uuid + " GET request");
        List<Resource> resources = Activator.instance().getStore().getResources();

        Resource toRet = null;
        for (Resource resource : resources) {
            if(uuid.equals(resource.getId())) {
                toRet = resource;
                break;
            }
        }

        if(toRet == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return ResponseUtil.serveResourceAsFile(toRet);
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
    @Override
    public Response uploadResource(@FormDataParam("file") InputStream uploadedInputStream,
                                 @FormDataParam("file") FormDataContentDisposition fileDetail,
                                 @Context HttpServletRequest req) {

        logger.debug("Get buffer for uploadBundle request {}.", req.getRequestURI());
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
