package cz.zcu.kiv.crce.rest.client.indexer.config_v2;

import java.util.HashMap;
import java.util.Map;

public enum MethodArgType {

        ENDPOINT_DATA("ENDPOINT_DATA"), RESPONSE("RESPONSE"), REQUEST_BODY("REQUEST_BODY"), OBJECT(
                        "OBJECT"), REQUEST_CALLBACK("REQUEST_CALLBACK"), URI_CALLBACK(
                                        "URI_CALLBACK"), HEADER("HEADER"), KEY("KEY"), VALUE(
                                                        "VALUE"), HTTP_METHOD(
                                                                        "HTTP_METHOD"), URI_VARIABLE(
                                                                                        "URI_VARIABLE"), URL(
                                                                                                        "URL"), BASE_URL(
                                                                                                                        "BASE_URL"), PATH(
                                                                                                                                        "PATH"), UNKNOWN(
                                                                                                                                                        "UNKNOWN"), GENERIC(
                                                                                                                                                                        "GENERIC"), MATRIX(
                                                                                                                                                                                        "MATRIX"), //HEADER_TYPES
        EMPTY(""),
        //REQUEST HEADERS
        A_IM("A-IM"), ACCEPT("Accept"), ACCEPT_CHARSET("Accept-Charset"), //
        ACCEPT_ENCODING("Accept-Encoding"), ACCEPT_LANGUAGE("Accept-Language"), ACCEPT_DATETIME(
                        "Accept-Datetime"), //
        ACCESS_CONTROL_REQUEST_METHOD(
                        "Access-Control-Request-Method"), ACCESS_CONTROL_REQUEST_HEADERS(
                                        "Access-Control-Request-Headers"), AUTHORIZATION(
                                                        "Authorization"), //
        COOKIE("Cookie"), EXPECT("Expect"), //
        FORWARDED("Forwarded"), FROM("From"), HOST("Host"), //
        IF_MATCH("If-Match"), IF_MODIFIED_SINCE("If-Modified-Since"), IF_NONE_MATCH(
                        "If-None-Match"), //
        IF_RANGE("If-Range"), IF_UNMODIFIED_SINCE("If-Unmodified-Since"), MAX_FORWARDS(
                        "Max-Forwards"), //
        ORIGIN("Origin"), PROXY_AUTHORIZATION("Proxy-Authorization"), //
        RANGE("Range"), REFERER("Referer"), TE("TE"), USER_AGENT("User-Agent"), //
        CONNECTION("Connection"), CONTENT_LENGTH("Content-Length"), CONTENT_TYPE(
                        "Content-Type"), DATE("Date"), PRAGMA("Pragma"), UPGRADE(
                                        "Upgrade"), VIA("Via"), CACHE_CONTROL("Cache-Control"),

        //RESPONSE HEADERS
        ACCEPT_PATCH("Accept-Patch"), ACCEPT_RANGES("Accept-Ranges"), AGE("Age"), ALLOW(
                        "Allow"), ALT_SVC("Alt-Svc"), CONTENT_DISPOSITION(
                                        "Content-Disposition"), CONTENT_ENCODING(
                                                        "Content-Encoding"), CONTENT_LANGUAGE(
                                                                        "Content-Language"), CONTENT_LOCATION(
                                                                                        "Content-Location"), CONTENT_RANGE(
                                                                                                        "Content-Range"), DELTA_BASE(
                                                                                                                        "Delta-Base"), ETAG(
                                                                                                                                        "ETag"), EXPIRES(
                                                                                                                                                        "Expires"), IM("IM"), LAST_MODIFIED(
                                                                                                                                                                        "Last-Modified"), LINK(
                                                                                                                                                                                        "Link"), LOCATION(
                                                                                                                                                                                                        "Location"), PROXY_AUTHENTICATE(
                                                                                                                                                                                                                        "Proxy-Authenticate"), PUBLIC_KEY_PINS(
                                                                                                                                                                                                                                        "Public-Key-Pins"), RETRY_AFTER(
                                                                                                                                                                                                                                                        "Retry-After"), SERVER(
                                                                                                                                                                                                                                                                        "Server"), SET_COOKIE(
                                                                                                                                                                                                                                                                                        "Set-Cookie"), STRICT_TRANSPORT_SECURITY(
                                                                                                                                                                                                                                                                                                        "Strict-Transport-Security"), TRAILER(
                                                                                                                                                                                                                                                                                                                        "Trailer"), TRANSFER_ENCODING(
                                                                                                                                                                                                                                                                                                                                        "Transfer-Encoding"), TK(
                                                                                                                                                                                                                                                                                                                                                        "Tk"), VARY("Vary"), WARNING(
                                                                                                                                                                                                                                                                                                                                                                        "Warning"), WWW_AUTHENTICATE(
                                                                                                                                                                                                                                                                                                                                                                                        "WWW-Authenticate");

        private String methodArgType;
        private static final Map<String, MethodArgType> mapValueToEnumName = new HashMap<>();
        static {
                for (MethodArgType type : MethodArgType.values()) {
                        mapValueToEnumName.put(type.getMethodArgType(), type);
                }
        }

        public static MethodArgType ofValue(String value) {
                return mapValueToEnumName.get(value);
        }

        /**
         * @param methodArgType
         */
        private MethodArgType(String methodArgType) {
                this.methodArgType = methodArgType;
        }

        /**
         * @return the methodArgType
         */
        public String getMethodArgType() {
                return methodArgType;
        }
}
