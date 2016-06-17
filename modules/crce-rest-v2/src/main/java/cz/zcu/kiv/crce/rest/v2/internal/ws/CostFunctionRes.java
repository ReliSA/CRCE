package cz.zcu.kiv.crce.rest.v2.internal.ws;

import javax.ws.rs.core.Response;

/**
 * Date: 17.6.16
 *
 * @author Jakub Danek
 */
public interface CostFunctionRes {

    Response findOne(String id);

    Response list();

}
