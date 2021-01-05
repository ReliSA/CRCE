package cz.zcu.kiv.crce.restimpl.indexer.internal;

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
public interface RestimplMetadataConstants {

    String MAIN_CATEGORY = "restimpl";

    // capabilities namespaces
    String NS__CRCE_IDENTITY = NsCrceIdentity.NAMESPACE__CRCE_IDENTITY;
    String NS__RESTIMPL_IDENTITY = MAIN_CATEGORY + ".identity";
    String NS__RESTIMPL_ENDPOINT = MAIN_CATEGORY + ".endpoint";

    // properties namespaces
    String NS_RESTIMPL_REQUEST_BODY = NS__RESTIMPL_ENDPOINT + ".requestbody";
    String NS_RESTIMPL_REQUESTPARAMETER = NS__RESTIMPL_ENDPOINT + ".requestparameter";
    String NS_RESTIMPL_RESPONSEPARAMETER = NS__RESTIMPL_ENDPOINT + ".responseparameter";
    String NS_RESTIMPL_RESPONSE = NS__RESTIMPL_ENDPOINT + ".response";

    // attributes for NS__RESTIMPL_IDENTITY capability
    AttributeType<String> ATTR__RESTIMPL_FRAMEWORK = new SimpleAttributeType<>("framework", String.class);

    // attributes for NS__RESTIMPL_ENDPOINT capability
    AttributeType<String> ATTR__RESTIMPL_NAME = new SimpleAttributeType<>("name", String.class);
    ListAttributeType ATTR__RESTIMPL_ENDPOINT_PATH = new ListAttributeType("path");
    ListAttributeType ATTR__RESTIMPL_ENDPOINT_METHOD = new ListAttributeType("method");
    ListAttributeType ATTR__RESTIMPL_ENDPOINT_CONSUMES = new ListAttributeType("consumes");
    ListAttributeType ATTR__RESTIMPL_ENDPOINT_PRODUCES = new ListAttributeType("produces");

    // attributes for NS_RESTIMPL_PARAMETER capability
    //AttributeType<String> ATTR__RESTIMPL_PARAMETER_NAME = new SimpleAttributeType<>("name", String.class);
    AttributeType<String> ATTR__RESTIMPL_DATETYPE = new SimpleAttributeType<>("datetype", String.class);
    AttributeType<String> ATTR__RESTIMPL_DEFAULT_VALUE = new SimpleAttributeType<>("defaultValue", String.class);
    AttributeType<String> ATTR__RESTIMPL_PARAMETER_CATEGEORY = new SimpleAttributeType<>("category", String.class);
    AttributeType<Long> ATTR__RESTIMPL_ARRAY = new SimpleAttributeType<>("isArray", Long.class);
    AttributeType<Long> ATTR__RESTIMPL_OPTIONAL = new SimpleAttributeType<>("isOptional", Long.class);

    AttributeType<Long> ATTR__RESTIMPL_RESPONSE_STATUS = new SimpleAttributeType<>("status", Long.class);
    AttributeType<String> ATTR__RESTIMPL_RESPONSE_ID = new SimpleAttributeType<>("id", String.class);






}
