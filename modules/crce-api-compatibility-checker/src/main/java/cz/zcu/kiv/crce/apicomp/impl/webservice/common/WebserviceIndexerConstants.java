package cz.zcu.kiv.crce.apicomp.impl.webservice.common;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.namespace.NsCrceIdentity;

/**
 * Constants related to web service metadata.
 */
public interface WebserviceIndexerConstants {

    String MAIN_CATEGORY = "ws-schema";
    
    ////////////////
    // NAMESPACES //
    ////////////////
    
    // list of all namespaces used by this module for Capabilities
    String NAMESPACE__CRCE_IDENTITY = NsCrceIdentity.NAMESPACE__CRCE_IDENTITY;
    String NAMESPACE__WEBSERVICESCHEMA_IDENTITY = MAIN_CATEGORY + ".identity";
    String NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE = MAIN_CATEGORY + ".ws";
    String NAMESPACE__WEBSERVICE_ENDPOINT = NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE + ".endpoint";
    String NAMESPACE__WEBSERVICE_TYPE = NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE + ".type";
    
    // list of all namespaces used by this module for Properties
    String NAMESPACE__WEBSERVICE_ENDPOINT_PARAMETER = NAMESPACE__WEBSERVICE_ENDPOINT + ".parameter";
    String NAMESPACE__WEBSERVICE_ENDPOINT_RESPONSE = NAMESPACE__WEBSERVICE_ENDPOINT + ".response";
    
    ////////////////
    // ATTRIBUTES //
    ////////////////
    
    // list of attributes used by this module for NAMESPACE__CRCE_IDENTITY Capability
    AttributeType<String> ATTRIBUTE__CRCE_IDENTITY__MIME = new SimpleAttributeType<>("mime", String.class);
    AttributeType<String> ATTRIBUTE__CRCE_IDENTITY__HASH = new SimpleAttributeType<>("hash", String.class);
    
    // list of attributes used by this module for NAMESPACE__WEBSERVICESCHEMA_IDENTITY Capability
    AttributeType<String> ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__IDL_VERSION = new SimpleAttributeType<>("idl-version", String.class);
    AttributeType<Long> ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__TIMESTAMP = new SimpleAttributeType<>("parsedAtTimestamp", Long.class);
    
    // list of attributes used by this module for NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE Capability
    AttributeType<String> ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__NAME = new SimpleAttributeType<>("name", String.class);
    AttributeType<String> ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE = new SimpleAttributeType<>("type", String.class);
    AttributeType<String> ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__URI = new SimpleAttributeType<>("uri", String.class);
    AttributeType<String> ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__IDL_URI = new SimpleAttributeType<>("idl-uri", String.class);
    
    // list of attributes used by this module for NAMESPACE__WEBSERVICE_ENDPOINT Capability
    AttributeType<String> ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME = new SimpleAttributeType<>("name", String.class);
    AttributeType<String> ATTRIBUTE__WEBSERVICE_ENDPOINT__URL = new SimpleAttributeType<>("url", String.class);
    
    // list of attributes used by this module for NAMESPACE__WEBSERVICE_ENDPOINT_PARAMETER Property
    AttributeType<String> ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__NAME = new SimpleAttributeType<>("name", String.class);
    AttributeType<String> ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__TYPE = new SimpleAttributeType<>("type", String.class);
    AttributeType<Long> ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__ORDER = new SimpleAttributeType<>("order", Long.class);
    AttributeType<Long> ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__OPTIONAL = new SimpleAttributeType<>("isOptional", Long.class);
    AttributeType<Long> ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__ARRAY = new SimpleAttributeType<>("isArray", Long.class);
    
    // list of attributes used by this module for NAMESPACE__WEBSERVICE_ENDPOINT_RESPONSE Property
    AttributeType<String> ATTRIBUTE__WEBSERVICE_ENDPOINT_RESPONSE__TYPE = new SimpleAttributeType<>("type", String.class);
    AttributeType<Long> ATTRIBUTE__WEBSERVICE_ENDPOINT_RESPONSE__ARRAY = new SimpleAttributeType<>("isArray", Long.class);


}
