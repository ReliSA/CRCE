package cz.zcu.kiv.crce.webservices.indexer.internal;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.service.internal.MetadataServiceImpl;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public abstract class WebserviceTypeBase {
    
    protected MetadataFactory metadataFactory;
    protected MetadataService metadataService;
    
    ////////////////
    // NAMESPACES //
    ////////////////
    
    // list of all namespaces used by this module for Capabilities
    public static final String NAMESPACE__CRCE_IDENTITY = MetadataServiceImpl.NAMESPACE__CRCE_IDENTITY;
    public static final String NAMESPACE__WEBSERVICE_IDENTITY = "webservice.identity";
    public static final String NAMESPACE__WEBSERVICE_ENDPOINT = "webservice.endpoint";
    public static final String NAMESPACE__WEBSERVICE_TYPE = "webservice.type";
    
    // list of all namespaces used by this module for Properties
    public static final String NAMESPACE__WEBSERVICE_ENDPOINT_PARAMETER = "webservice.endpoint.parameter";
    public static final String NAMESPACE__WEBSERVICE_ENDPOINT_RESPONSE = "webservice.endpoint.response";
    
    ////////////////
    // ATTRIBUTES //
    ////////////////
    
    // list of attributes used by this module for NAMESPACE__CRCE_IDENTITY Capability
    public static final AttributeType<String> ATTRIBUTE__CRCE_IDENTITY__MIME = new SimpleAttributeType<>("mime", String.class);
    public static final AttributeType<String> ATTRIBUTE__CRCE_IDENTITY__HASH = new SimpleAttributeType<>("hash", String.class);
    
    // list of attributes used by this module for NAMESPACE__WEBSERVICE_IDENTITY Capability
    public static final AttributeType<String> ATTRIBUTE__WEBSERVICE_IDENTITY__IDL_VERSION = new SimpleAttributeType<>("idl-version", String.class);
    public static final AttributeType<String> ATTRIBUTE__WEBSERVICE_IDENTITY__TYPE = new SimpleAttributeType<>("type", String.class);
    public static final AttributeType<String> ATTRIBUTE__WEBSERVICE_IDENTITY__URI = new SimpleAttributeType<>("uri", String.class);
    public static final AttributeType<String> ATTRIBUTE__WEBSERVICE_IDENTITY__IDL_URI = new SimpleAttributeType<>("idl-uri", String.class);
    public static final AttributeType<Long> ATTRIBUTE__WEBSERVICE_IDENTITY__TIMESTAMP = new SimpleAttributeType<>("parsedAtTimestamp", Long.class);
    
    // list of attributes used by this module for NAMESPACE__WEBSERVICE_ENDPOINT Capability
    public static final AttributeType<String> ATTRIBUTE__WEBSERVICE_ENDPOINT__NAME = new SimpleAttributeType<>("name", String.class);
    public static final AttributeType<String> ATTRIBUTE__WEBSERVICE_ENDPOINT__URL = new SimpleAttributeType<>("url", String.class);
    
    // list of attributes used by this module for NAMESPACE__WEBSERVICE_ENDPOINT_PARAMETER Property
    public static final AttributeType<String> ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__NAME = new SimpleAttributeType<>("name", String.class);
    public static final AttributeType<String> ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__TYPE = new SimpleAttributeType<>("type", String.class);
    public static final AttributeType<Long> ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__ORDER = new SimpleAttributeType<>("order", Long.class);
    public static final AttributeType<Long> ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__OPTIONAL = new SimpleAttributeType<>("isOptional", Long.class);
    public static final AttributeType<Long> ATTRIBUTE__WEBSERVICE_ENDPOINT_PARAMETER__ARRAY = new SimpleAttributeType<>("isArray", Long.class);
    
    // list of attributes used by this module for NAMESPACE__WEBSERVICE_ENDPOINT_RESPONSE Property
    public static final AttributeType<String> ATTRIBUTE__WEBSERVICE_ENDPOINT_RESPONSE__TYPE = new SimpleAttributeType<>("type", String.class);
    public static final AttributeType<Long> ATTRIBUTE__WEBSERVICE_ENDPOINT_RESPONSE__ARRAY = new SimpleAttributeType<>("isArray", Long.class);

    
    
    public WebserviceTypeBase(MetadataFactory mf, MetadataService ms) {
        metadataFactory = mf;
        metadataService = ms;
    }
    
    public abstract String getSpecificWebserviceType();

    public static String getIdlHash(String idl) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(idl.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    protected static <T> boolean setIfSet(Capability capability, AttributeType<T> attribute, T value) {
        if (capability != null && value != null) {
            capability.setAttribute(attribute, value);
            return true;
        }
        return false;
    }
    
    protected static <T> boolean setIfSet(Property property, AttributeType<T> attribute, T value) {
        if (property != null && value != null) {
            property.setAttribute(attribute, value);
            return true;
        }
        return false;
    }

}
