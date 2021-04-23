package cz.zcu.kiv.crce.rest.client.indexer.processor.tools;

public class HeaderTools {
    private static final String ACCEPT = "Accept";

    private static String CONTENTBASE = "Content";
    public static final String CONTENTTYPE = CONTENTBASE + "-Type";

    public static boolean isConsumingType(String headerType) {
        return headerType.startsWith(ACCEPT);
    }

    public static boolean isProducingType(String headerType) {
        return headerType.startsWith(CONTENTTYPE);
    }
}
