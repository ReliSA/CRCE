package cz.zcu.kiv.crce.rest.client.indexer.config_v2;

import java.util.HashMap;
import java.util.Map;

public enum MethodArgType {

        ENDPOINT_DATA("ENDPOINT_DATA"), RESPONSE("RESPONSE"), REQUEST_BODY(
                        "REQUEST_BODY"), REQUEST_CALLBACK("REQUEST_CALLBACK"), URI_CALLBACK(
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
        private static Map<String, MethodArgType> mapValueToEnumName = Map.of("A-IM", A_IM,
                        "Accept", ACCEPT, "Accept-Charset", ACCEPT_CHARSET, "Accept-Encoding",
                        ACCEPT_ENCODING, "Accept-Language", ACCEPT_LANGUAGE, "Accept-Datetime",
                        ACCEPT_DATETIME, "Access-Control-Request-Method",
                        ACCESS_CONTROL_REQUEST_METHOD, "Access-Control-Request-Headers",
                        ACCESS_CONTROL_REQUEST_HEADERS, "Authorization", AUTHORIZATION, "Cookie",
                        COOKIE, "Expect", EXPECT, "Forwarded", FORWARDED, "From", FROM, "Host",
                        HOST, "If-Match", IF_MATCH, "If-Modified-Since", IF_MODIFIED_SINCE,
                        "If-None-Match", IF_NONE_MATCH, "If-Range", IF_RANGE, "If-Unmodified-Since",
                        IF_UNMODIFIED_SINCE, "Max-Forwards", MAX_FORWARDS, "Origin", ORIGIN,
                        "Proxy-Authorization", PROXY_AUTHORIZATION, "Range", RANGE, "Referer",
                        REFERER, "TE", TE, "User-Agent", USER_AGENT, "Connection", CONNECTION,
                        "Content-Length", CONTENT_LENGTH, "Content-Type", CONTENT_TYPE, "Date",
                        DATE, "Pragma", PRAGMA, "Upgrade", UPGRADE, "Via", VIA, "Cache-Control",
                        CACHE_CONTROL, "Accept-Patch", ACCEPT_PATCH, "Accept-Ranges", ACCEPT_RANGES,
                        "Age", AGE, "Allow", ALLOW, "Alt-Svc", ALT_SVC, "Content-Disposition",
                        CONTENT_DISPOSITION, "Content-Encoding", CONTENT_ENCODING,
                        "Content-Language", CONTENT_LANGUAGE, "Content-Location", CONTENT_LOCATION,
                        "Content-Range", CONTENT_RANGE, "Delta-Base", DELTA_BASE, "ETag", ETAG,
                        "Expires", EXPIRES, "IM", IM, "Last-Modified", LAST_MODIFIED, "Link", LINK,
                        "Location", LOCATION, "Proxy-Authenticate", PROXY_AUTHENTICATE,
                        "Public-Key-Pins", PUBLIC_KEY_PINS, "Retry-After", RETRY_AFTER, "Server",
                        SERVER, "Set-Cookie", SET_COOKIE, "Strict-Transport-Security",
                        STRICT_TRANSPORT_SECURITY, "Trailer", TRAILER, "Transfer-Encoding",
                        TRANSFER_ENCODING, "Tk", TK, "Vary", VARY, "Warning", WARNING,
                        "WWW-Authenticate", WWW_AUTHENTICATE);

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
