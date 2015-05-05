package cz.zcu.kiv.crce.rest.v2.internal.ws;

import javax.ws.rs.core.Response;

/**
 * Date: 5.5.15
 *
 * @author Jakub Danek
 */
public interface MetadataResource {

    /**
     * Enumeration of possible operations.
     *
     * Used when searching for compatible bundles to specify whether user
     * aims for update or downgrade.
     *
     */
    enum Operation {

    }

    /**
     * Returns list of all resources available.
     *
     * Only basic crce identity metadata displayed.
     *
     * @return
     */
    Response metadata();

    /**
     * Returns list of available bundles with given name.
     *
     * Displays only basic crce identity information.
     *
     * @param name name to filter by
     * @return
     */
    Response metadata(String name);

    /**
     * Displays detailed metadata about the given resource.
     * @param name name of the resource
     * @param version version of the resource
     * @return
     */
    Response metadata(String name, String version);

    /**
     * Displays differences between the resource identified by name and version
     * and the resource identified by otherName and otherVersion.
     *
     * Both otherName and otherVersion are optional filtering parameters. If none are provided
     * all available diffs for the resource are returned.
     *
     * @param name
     * @param version
     * @param otherName optional parameter
     * @param otherVersion optional parameter
     * @return
     */
    Response diffs(String name, String version, String otherName, String otherVersion);

    /**
     * Returns metadata for a compatible resource with "our" resource (specified by name and version)
     * based on the operation provided.
     *
     * @param name name of our resource
     * @param version version of our resource
     * @param operation desired operation
     * @return
     */
    Response compatible(String name, String version, Operation operation);
}
