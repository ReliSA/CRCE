package cz.zcu.kiv.crce.rest.v2.internal.ws;

import javax.ws.rs.core.Response;

/**
 * Interface for accessing API compatibility comparison functionality.
 *
 */
public interface ApiCompRes {

    /**
     * Compares two APIs and returns {@link cz.zcu.kiv.crce.apicomp.result.CompatibilityCheckResult}.
     *
     * @param id1 Id of the resource which represents the first API.
     * @param id2 Id of the resource which represents the second API.
     * @param force If set, API won't perform DB lookup and will calculate the compatibility. This will overwrite
     *              any existing compatibility records in DB for this resource pair.
     * @return Response with comparison result.
     */
    Response compareApis(String id1, String id2, boolean force);
}
