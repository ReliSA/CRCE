package cz.zcu.kiv.crce.rest.client.indexer.processor.tools;

import cz.zcu.kiv.crce.rest.client.indexer.classmodel.structures.Header;
import cz.zcu.kiv.crce.rest.client.indexer.config.MethodArgType;

public class HeaderTools {
    /**
     * 
     * @param headerType
     * @return
     */
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

}
