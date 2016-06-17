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

import cz.zcu.kiv.crce.resolver.optimizer.CostFunctionFactory;
import cz.zcu.kiv.crce.rest.v2.internal.Activator;
import cz.zcu.kiv.crce.rest.v2.internal.ws.CostFunctionRes;
import cz.zcu.kiv.crce.vo.model.optimizer.CostFunctionDescriptorVO;

/**
 * Date: 17.6.16
 *
 * @author Jakub Danek
 */
@Path("/optimizers")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class CostFunctionResJersey implements CostFunctionRes {

    private static final Logger logger = LoggerFactory.getLogger(CostFunctionResJersey.class);

    @GET
    @Path("{id}")
    @Override
    public Response findOne(@PathParam("id") String id) {
        logger.debug("Searching for cost function with id: {}", id);
        CostFunctionFactory desc = Activator.instance().getCostFunctionRepository().findOne(id);
        CostFunctionDescriptorVO vo = Activator.instance().getMappingService().mapCostFunction(desc);

        return Response.ok(vo).build();
    }

    @GET
    @Override
    public Response list() {
        logger.debug("Listing cost functions.");
        List<CostFunctionFactory> descs = Activator.instance().getCostFunctionRepository().list();
        List<CostFunctionDescriptorVO> vos = Activator.instance().getMappingService().mapCostFunction(descs);
        logger.debug("Found {} cost functions. Sending {} cost functions.", descs.size(), vos.size());
        return Response.ok().entity(new GenericEntity<List<CostFunctionDescriptorVO>>(vos) {}).build();
    }
}
