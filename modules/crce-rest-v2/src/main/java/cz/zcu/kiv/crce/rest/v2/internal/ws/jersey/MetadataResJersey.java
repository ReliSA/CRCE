package cz.zcu.kiv.crce.rest.v2.internal.ws.jersey;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.compatibility.Compatibility;
import cz.zcu.kiv.crce.compatibility.service.CompatibilitySearchService;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.rest.v2.internal.Activator;
import cz.zcu.kiv.crce.rest.v2.internal.Headers;
import cz.zcu.kiv.crce.rest.v2.internal.ws.MetadataRes;
import cz.zcu.kiv.crce.vo.model.compatibility.CompatibilityVO;
import cz.zcu.kiv.crce.vo.model.metadata.BasicResourceVO;
import cz.zcu.kiv.crce.vo.model.metadata.DetailedResourceVO;
import cz.zcu.kiv.crce.vo.model.metadata.RequirementListVO;

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
        List<Resource> resources = Activator.instance().getStore().getResources(r, false);
        List<BasicResourceVO> vos = Activator.instance().getMappingService().mapBasic(resources);

        return Response.ok().entity(new GenericEntity<List<BasicResourceVO>>(vos) {
        }).build();
    }

    @GET
    @Path("/catalogue/{name}/{version}")
    @Override
    public Response metadata(@PathParam("name") String name, @PathParam("version") String version) {
        logger.info("Serving /metadata/catalogue" + name + "/" + version + " GET request");
        Requirement r = Activator.instance().getMetadataService().createIdentityRequirement(name, version);
        List<Resource> resources = Activator.instance().getStore().getResources(r, true);
        List<BasicResourceVO> vos = Activator.instance().getMappingService().mapBasic(resources);

        return Response.ok().entity(new GenericEntity<List<BasicResourceVO>>(vos) {}).build();
    }

    @POST
    @Path("/catalogue/")
    @Override
    public Response metadata(RequirementListVO constraint) {
        List<Requirement> requirements = Activator.instance().getMappingService().map(constraint.getRequirements());

        List<Resource> resources;
        //ugly hack to measure experiment time
        long duration = System.nanoTime();
        if(constraint.andRequirements()) {
            resources = Activator.instance().getStore().getResources(new HashSet<>(requirements), false);
        } else {
            resources = Activator.instance().getStore().getPossibleResources(new HashSet<>(requirements), false);
        }
        duration = System.nanoTime() - duration;
        duration = TimeUnit.NANOSECONDS.toMillis(duration);

        List<BasicResourceVO> vos = Activator.instance().getMappingService().mapBasic(resources);

        Response.ResponseBuilder b =  Response.ok().entity(new GenericEntity<List<BasicResourceVO>>(vos) {});
        b.header(Headers.REQUEST_DURATION, duration);

        return b.build();
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
        Resource toRet = Activator.instance().getStore().getResource(uuid, true);

        if(toRet == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        DetailedResourceVO vo = Activator.instance().getMappingService().mapFull(toRet);
        return Response.ok(vo).build();
    }

    @GET
    @Path("/catalogue/{externalId}/{version}/diffs")
    @Override
    public Response diffs(@PathParam("externalId") String externalId,
                          @PathParam("version") String version,
                          @QueryParam("otherExternalId") String otherExternalId,
                          @QueryParam("otherVersion") String otherVersion) {
        Activator act = Activator.instance();

        CompatibilitySearchService service = act.getCompatibilityService();
        if(service == null) {
            return createNotAvailableResponse();
        }


        List<Resource> res = act.getStore().getResources(act.getMetadataService().createIdentityRequirement(externalId, version), true);
        List<Compatibility> diffs = new LinkedList<>();

        for (Resource re : res) {
            diffs.addAll(service.listLowerCompatibilities(re));
            diffs.addAll(service.listUpperCompatibilities(re));
        }

        List<CompatibilityVO> vos = act.getMappingService().mapCompatibility(diffs);

        return Response.ok().entity(new GenericEntity<List<CompatibilityVO>>(vos) {}).build();
    }

    @GET
    @Path("/catalogue/{externalId}/{version}/compatible")
    @Override
    public Response compatible(@PathParam("externalId") String externalId, @PathParam("version") String version, @QueryParam("op") Operation operation) {
        Activator act = Activator.instance();

        CompatibilitySearchService service = act.getCompatibilityService();
        if(service == null) {
            return createNotAvailableResponse();
        }

        List<Resource> res = act.getStore().getResources(act.getMetadataService().createIdentityRequirement(externalId, version), true);
        List<Resource> compatible = new LinkedList<>();
        Resource tmp;
        for (Resource re : res) {
            switch (operation) {
                case DOWNGRADE:
                    tmp = service.findNearestDowngrade(re);
                    break;
                case UPGRADE:
                    tmp = service.findNearestUpgrade(re);
                    break;
                case HIGHEST:
                    tmp = service.findHighestUpgrade(re);
                    break;
                case LOWEST:
                    tmp = service.findLowestDowngrade(re);
                    break;
                case ANY:
                default:
                    tmp = service.findHighestUpgrade(re);
                    if(tmp == null) {
                        tmp = service.findNearestDowngrade(re);
                    }
                    break;
            }

            if(tmp != null) {
                compatible.add(tmp);
            }
        }

        List<DetailedResourceVO> resources = act.getMappingService().mapFull(compatible);

        return Response.ok().entity(new GenericEntity<List<DetailedResourceVO>>(resources) {}).build();
    }

    private Response createNotAvailableResponse() {
        return Response.status(Response.Status.NOT_FOUND).entity("The request functionality is not supported by this instance.").build();
    }
}
