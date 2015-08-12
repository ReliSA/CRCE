package cz.zcu.kiv.crce.rest.v2.internal.ws.jersey;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.rest.v2.internal.Activator;
import cz.zcu.kiv.crce.rest.v2.internal.ws.MetadataRes;
import cz.zcu.kiv.crce.vo.model.metadata.BasicResourceVO;
import cz.zcu.kiv.crce.vo.model.metadata.DetailedResourceVO;

/**
 * Date: 2.8.15
 *
 * @author Jakub Danek
 */

@Path("/metadata")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class MetadataResJersey implements MetadataRes {

    private static final Logger logger = LoggerFactory.getLogger(MetadataResJersey.class);

    @GET
    @Override
    public Response metadata() {
        logger.info("Serving /metadata GET request");
        List<Resource> resources = Activator.instance().getStore().getResources();
        List<BasicResourceVO> vos = Activator.instance().getMappingService().mapBasic(resources);

        return Response.ok().entity(new GenericEntity<List<BasicResourceVO>>(vos) {}).build();
    }

    @GET
    @Path("/catalogue/{name}")
    @Override
    public Response metadata(@PathParam("name") String name) {
        logger.info("Serving /metadata/catalogue" + name + " GET request");
        Requirement r = Activator.instance().getMetadataService().createIdentityRequirement(name);
        List<Resource> resources = Activator.instance().getStore().getResources(r);
        List<BasicResourceVO> vos = Activator.instance().getMappingService().mapBasic(resources);

        return Response.ok().entity(new GenericEntity<List<BasicResourceVO>>(vos) {}).build();
    }

    @GET
    @Path("/catalogue/{name}/{version}")
    @Override
    public Response metadata(@PathParam("name") String name, @PathParam("version") String version) {
        logger.info("Serving /metadata/catalogue" + name + "/" + version + " GET request");
        Requirement r = Activator.instance().getMetadataService().createIdentityRequirement(name, version);
        List<Resource> resources = Activator.instance().getStore().getResources(r);
        List<BasicResourceVO> vos = Activator.instance().getMappingService().mapBasic(resources);

        return Response.ok().entity(new GenericEntity<List<BasicResourceVO>>(vos) {}).build();
    }

    /**
     * Displays detailed information about a concrete resource.
     *
     * @param uuid id of the resource
     * @return
     */
    @GET
    @Path("/{id}")
    @Override
    public Response metadataDetails(@PathParam("id")String uuid) {
        logger.info("Serving /metadata/" + uuid + " GET request");
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

        DetailedResourceVO vo = Activator.instance().getMappingService().mapFull(toRet);
        return Response.ok(vo).build();
    }

    @Override
    public Response diffs(String name, String version, String otherName, String otherVersion) {
        return null;
    }

    @Override
    public Response compatible(String name, String version, Operation operation) {
        return null;
    }
}
