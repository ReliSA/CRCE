package cz.zcu.kiv.crce.rest.v2.internal.ws;

import javax.ws.rs.core.Response;

/**
 * Date: 5.5.15
 *
 * @author Jakub Danek
 */
public interface BundlesResource {

    /**
     * Returns list of available bundles.
     *
     * Displays only basic crce identity information.
     *
     * @return
     */
    Response bundles();

    /**
     * Returns list of available bundles with given name.
     *
     * Displays only basic crce identity information.
     *
     * @param name name to filter by
     * @return
     */
    Response bundles(String name);

    /**
     * Returns particular bundle binary based on name and version provided.
     * @param name bundle name
     * @param version bundle version
     * @return
     */
    Response bundle(String name, String version);
}
