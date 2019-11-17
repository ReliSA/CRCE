package cz.zcu.kiv.crce.apicomp.impl;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.ListAttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

public interface RestimplIndexerConstants {

    String MAIN_CATEGORY = "restimpl";

    String NS__RESTIMPL_ENDPOINT = MAIN_CATEGORY + ".endpoint";

    String NS_RESTIMPL_REQUESTPARAMETER = NS__RESTIMPL_ENDPOINT + ".requestparameter";

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
