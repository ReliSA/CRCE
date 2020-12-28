package cz.zcu.kiv.crce.rest.v2.internal.ws;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;


/**
 * Date: 5.5.15
 *
 * @author Jakub Danek
 */
public interface ResourceRes {

    /**
     * Returns list of available resources.
     *
     * Displays only basic crce identity information.
     *
     * @return
     */
    Response resources();

    /**
     * Returns list of available resources with given name.
     *
     * Displays only basic crce identity information.
     *
     * @param name name to filter by
     * @return
     */
    Response resources(String name);

    /**
     * Returns list of available resources with given name and version.
     *
     * @param name resource name
     * @param version resource version
     * @return
     */
    Response resources(String name, String version);

    /**
     * Returns concrete resource binary based on provided UUID
     * @param uuid resource id in crce
     * @return
     */
    Response resourceBinary(String uuid);

    /**
     * Allows sw upload of bundles into CRCE. Automatically saves the bundle into buffer and commits it to store.
     *
     * @param uploadedInputStream file stream
     * @param fileDetail          file headers
     * @param req                 request
     * @return OK if success, 403 otherwise
     */
    Response uploadResource(InputStream uploadedInputStream,
                                 FormDataContentDisposition fileDetail,
                                 HttpServletRequest req);
}
