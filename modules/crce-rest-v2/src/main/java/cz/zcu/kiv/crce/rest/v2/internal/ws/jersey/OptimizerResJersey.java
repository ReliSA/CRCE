package cz.zcu.kiv.crce.rest.v2.internal.ws.jersey;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.resolver.optimizer.ResultOptimizer;
import cz.zcu.kiv.crce.rest.v2.internal.Activator;
import cz.zcu.kiv.crce.rest.v2.internal.ws.OptimizerRes;
import cz.zcu.kiv.crce.vo.model.optimizer.ResultOptimizerVO;

/**
 * Date: 17.6.16
 *
 * @author Jakub Danek
 */
@Path("/optimizers/methods")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class OptimizerResJersey implements OptimizerRes {

    private static final Logger logger = LoggerFactory.getLogger(OptimizerResJersey.class);

    @GET
    @Path("{id}")
    @Override
    public ResultOptimizerVO findOne(@PathParam("id") String id) {
        logger.debug("Searching for cost function with id: {}", id);
        ResultOptimizer desc = Activator.instance().getResultOptimizerRepository().findOne(id);
        return Activator.instance().getMappingService().mapResultOptimizer(desc);
    }

    @GET
    @Override
    public List<ResultOptimizerVO> list() {
        logger.debug("Listing cost functions.");
        List<ResultOptimizer> descs = Activator.instance().getResultOptimizerRepository().list();
        List<ResultOptimizerVO> vos = Activator.instance().getMappingService().mapResultOptimizer(descs);
        logger.debug("Found {} optimizer methods. Sending {} optimizer methods.", descs.size(), vos.size());
        return vos;
    }
}
