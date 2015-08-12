package cz.zcu.kiv.crce.rest.v2.internal.ws.jersey;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Debug exception mapper which forces write of exceptions into
 * logs.
 *
 * Date: 18.5.15
 *
 * @author Jakub Danek
 */
@Provider
public class HelpMeExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger logger = LoggerFactory.getLogger(HelpMeExceptionMapper.class);

    @Override
    public Response toResponse(Exception e) {
        logger.error(e.getMessage(), e);
        return Response
                .status(Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(e.getCause())
                .build();
    }

}
