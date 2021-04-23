package cz.zcu.kiv.crce.rest.client.indexer.internal;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.ListAttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.namespace.NsCrceIdentity;

/**
 * Created by ghessova on 18.04.2018.
 *
 * Constants for rest implementation representation in CRCE metada (namespaces, attributes).
 *
 */
public interface RestClientMetadataConstants {

    String MAIN_CATEGORY = "rest.client";

    // capabilities namespaces
    String NS__CRCE_IDENTITY = NsCrceIdentity.NAMESPACE__CRCE_IDENTITY;
    String NS__REST_CLIENT_IDENTITY = MAIN_CATEGORY + ".identity";
    String NS__REST_CLIENT_ENDPOINT = MAIN_CATEGORY + ".endpoint";

    String NS_REST_CLIENT_REQUEST = NS__REST_CLIENT_ENDPOINT + ".request";
    String NS_REST_CLIENT_RESPONSE = NS__REST_CLIENT_ENDPOINT + ".response";

    // properties namespaces
    String NS_REST_CLIENT_REQUEST_BODY = NS_REST_CLIENT_REQUEST + ".body";
    String NS_REST_CLIENT_RESPONSE_BODY = NS_REST_CLIENT_RESPONSE + ".body";
    String NS_REST_CLIENT_REQUEST_PARAMETER = NS_REST_CLIENT_REQUEST + ".parameter";
    String NS_REST_CLIENT_RESPONSE_PARAMETER = NS_REST_CLIENT_RESPONSE + ".parameter";

    // attributes for NS__RESTIMPL_IDENTITY capability
    /*
     * AttributeType<String> ATTR__RESTIMPL_FRAMEWORK = new SimpleAttributeType<>("framework",
     * String.class);
     */

    // attributes for NS__RESTIMPL_ENDPOINT capability
    // AttributeType<String> ATTR__RESTIMPL_NAME = new SimpleAttributeType<>("name", String.class);
    SimpleAttributeType<String> ATTR__REST_CLIENT_ENDPOINT_URL = new SimpleAttributeType<>("url", String.class);
    ListAttributeType ATTR__REST_CLIENT_ENDPOINT_METHOD = new ListAttributeType("method");
    ListAttributeType ATTR__REST_CLIENT_ENDPOINT_CONSUMES = new ListAttributeType("consumes");
    ListAttributeType ATTR__REST_CLIENT_ENDPOINT_PRODUCES = new ListAttributeType("produces");
    ListAttributeType ATTR__REST_CLIENT_ENDPOINT_EXPECTS = new ListAttributeType("expects");
    ListAttributeType ATTR__REST_CLIENT_ENDPOINT_SENDS = new ListAttributeType("sends");
    ListAttributeType ATTR__REST_CLIENT_ENDPOINT_PARAMETERS = new ListAttributeType("parameters");

    // attributes for NS_REST_CLIENT_PARAMETER capability
    // AttributeType<String> ATTR__REST_CLIENT_PARAMETER_NAME = new SimpleAttributeType<>("name",
    // String.class);
    AttributeType<String> ATTR__REST_CLIENT_DATATYPE =
            new SimpleAttributeType<>("datatype", String.class);
    AttributeType<String> ATTR__REST_CLIENT_PARAMETER_CATEGORY =
            new SimpleAttributeType<>("category", String.class);
    AttributeType<Long> ATTR__REST_CLIENT_IS_ARRAY = new SimpleAttributeType<>("isArray", Long.class);
    AttributeType<String> ATTR__REST_CLIENT_STRUCTURE = new SimpleAttributeType<>("structure", String.class);
    /*AttributeType<String> ATTR__RESTIMPL_RESPONSE_ID =
            new SimpleAttributeType<>("id", String.class);

     */



}
