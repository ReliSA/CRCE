package cz.zcu.kiv.crce.rest.client.indexer.processor.tools;

import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Header;
import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.HeaderGroup;
import cz.zcu.kiv.crce.rest.client.indexer.config_v2.MethodArgType;

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

    public static boolean isControlType(MethodArgType headerType) {
        switch (headerType) {
            case CACHE_CONTROL:
            case EXPECT:
            case HOST:
            case MAX_FORWARDS:
            case RANGE:
            case TE:
                return true;
            default:;
        }
        return false;
    }

    public static boolean isConditionalType(MethodArgType headerType) {
        switch (headerType) {
            case IF_MATCH:
            case IF_NONE_MATCH:
            case IF_MODIFIED_SINCE:
            case IF_UNMODIFIED_SINCE:
            case IF_RANGE:
                return true;
            default:;
        }
        return false;
    }

    public static boolean isContentNegotiation(MethodArgType headerType) {
        switch (headerType) {
            case ACCEPT:
            case ACCEPT_CHARSET:
            case ACCEPT_ENCODING:
            case ACCEPT_LANGUAGE:
                return true;
            default:;
        }
        return false;
    }

    public static boolean isAuthenticationCredentials(MethodArgType headerType) {
        switch (headerType) {
            case AUTHORIZATION:
            case PROXY_AUTHORIZATION:
                return true;
            default:;
        }
        return false;
    }

    public static boolean isRequestContext(MethodArgType headerType) {
        switch (headerType) {
            case FROM:
            case REFERER:
            case USER_AGENT:
                return true;
            default:;
        }
        return false;
    }

    public static boolean isRepresentation(MethodArgType headerType) {
        switch (headerType) {
            case CONTENT_TYPE:
            case CONTENT_LENGTH:
                return true;
            default:;
        }
        return false;
    }

    public static MethodArgType getHeaderType(String headerType) {
        MethodArgType mArgType = MethodArgType.valueOf(headerType);

        return null;
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
