package cz.zcu.kiv.crce.rest.v2.internal;

/**
 * Custom CRCE HTTP headers
 *
 * Date: 30.8.16
 *
 * @author Jakub Danek
 */
public class Headers {

    /**
     * This header contains duration which the internal logic call (without VO mapping) took when processing
     * a webservice request.
     */
    public static final String REQUEST_DURATION = "logic-duration";
}
