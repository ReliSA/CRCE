package cz.zcu.kiv.crce.rest.client.indexer.internal;

import cz.zcu.kiv.crce.metadata.impl.ListAttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.namespace.NsCrceIdentity;

/**
 * Inspired by ghessova on 18.04.2018.
 *
 * Constants for rest client implementation representation in CRCE metadata (namespaces, attributes).
 *
 */
public interface RestClientMetadataConstants {

        String MAIN_CATEGORY = "rest.client";

        // capabilities namespaces
        String NS__CRCE_IDENTITY = NsCrceIdentity.NAMESPACE__CRCE_IDENTITY;
        String NS__REST_CLIENT_IDENTITY = MAIN_CATEGORY + ".identity";
        String NS__REST_CLIENT_ENDPOINT = MAIN_CATEGORY + ".ws";

        String NS_REST_CLIENT_REQUEST = NS__REST_CLIENT_ENDPOINT + ".request";
        String NS_REST_CLIENT_RESPONSE = NS__REST_CLIENT_ENDPOINT + ".response";
        String NS_REST_CLIENT_HEADERS = NS__REST_CLIENT_ENDPOINT + ".headers";

        String NS_HEADER = "header.";
        String NS_PARAMETER = "parameter.";
        String NS_RESPONSE = "response.";
        // properties namespaces

        SimpleAttributeType<String> ATTR__REST_CLIENT_ENDPOINT_BASEURL =
                        new SimpleAttributeType<>("baseURL", String.class);
        SimpleAttributeType<String> ATTR__REST_CLIENT_ENDPOINT_PATH =
                        new SimpleAttributeType<>("path", String.class);

        ListAttributeType ATTR__REST_CLIENT_ENDPOINT_METHOD = new ListAttributeType("httpMethod");
        ListAttributeType ATTR__REST_CLIENT_ENDPOINT_HEADER_CONTROLS =
                        new ListAttributeType(NS_HEADER + "control");
        ListAttributeType ATTR__REST_CLIENT_ENDPOINT_HEADER_CONDITIONALS =
                        new ListAttributeType(NS_HEADER + "conditionals");
        ListAttributeType ATTR__REST_CLIENT_ENDPOINT_HEADER_CONTENT_NEGOTIATION =
                        new ListAttributeType(NS_HEADER + "contentNegotiation");
        ListAttributeType ATTR__REST_CLIENT_ENDPOINT_HEADER_AUTHENTICATION_CREDENTIALS =
                        new ListAttributeType(NS_HEADER + "authenticationCredentials");
        ListAttributeType ATTR__REST_CLIENT_ENDPOINT_HEADER_REQUEST_CONTEXT =
                        new ListAttributeType(NS_HEADER + "requestContext");
        ListAttributeType ATTR__REST_CLIENT_ENDPOINT_HEADER_REPRESENTATION =
                        new ListAttributeType(NS_HEADER + "representation");
        ListAttributeType ATTR__REST_CLIENT_ENDPOINT_HEADER_RESPONSE =
                        new ListAttributeType(NS_HEADER + "response");
        ListAttributeType ATTR__REST_CLIENT_ENDPOINT_HEADER_EXPECTED_RESPONSE =
                        new ListAttributeType(NS_RESPONSE + "expected");
        ListAttributeType ATTR__REST_CLIENT_ENDPOINT_REQUEST_BODY_TYPES =
                        new ListAttributeType("body");
        ListAttributeType ATTR__REST_CLIENT_ENDPOINT_HEADER_COOKIE =
                        new ListAttributeType("cookie");
        ListAttributeType ATTR__REST_CLIENT_ENDPOINT_PARAMETERS_OTHERS =
                        new ListAttributeType("uriParameter");
        ListAttributeType ATTR__REST_CLIENT_ENDPOINT_HEADER_REQUEST_BODY_STRUCTURES =
                        new ListAttributeType("structure");
        ListAttributeType ATTR__REST_CLIENT_ENDPOINT_CALLED_FROM =
                        new ListAttributeType("calledFrom");

        ListAttributeType ATTR__REST_CLIENT_ENDPOINT_CONSUMES = new ListAttributeType("consumes");
        ListAttributeType ATTR__REST_CLIENT_ENDPOINT_PRODUCES = new ListAttributeType("produces");
        ListAttributeType ATTR__REST_CLIENT_ENDPOINT_EXPECTS = new ListAttributeType("expects");
        ListAttributeType ATTR__REST_CLIENT_ENDPOINT_SENDS = new ListAttributeType("sends");
        ListAttributeType ATTR__REST_CLIENT_ENDPOINT_PARAMETERS =
                        new ListAttributeType("parameters");

}
