package cz.zcu.kiv.crce.rest.client.indexer.processor.tools;

import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Header;

public class HeaderTools {
    private static final String ACCEPT = "Accept";

    /**
     * Detects if header describes accepting 
     * @source https://datatracker.ietf.org/doc/html/rfc7231#section-5
     * @param headerType
     * @return
     */
    public static boolean isConsumingType(String headerType) {
        return headerType.startsWith(ACCEPT);
    }

    public static boolean isConsumingType(Header header) {
        return header.getType().startsWith(ACCEPT);
    }

    /**
     * Detects if header describes sent content
     * @source https://datatracker.ietf.org/doc/html/rfc7231#section-3
     * 
     * @param headerType
     * @return
     */
    public static boolean isProducingType(String headerType) {
        return !headerType.startsWith(ACCEPT);
    }
}
