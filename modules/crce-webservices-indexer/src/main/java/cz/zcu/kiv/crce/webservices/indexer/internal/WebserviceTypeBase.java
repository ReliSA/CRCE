package cz.zcu.kiv.crce.webservices.indexer.internal;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.namespace.NsCrceIdentity;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class can be extended by any class implementing {@link cz.zcu.kiv.crce.webservices.indexer.internal.WebserviceType} interface. Since
 * {@link cz.zcu.kiv.crce.webservices.indexer.internal.WebserviceType} interface defines strictly only necessary functionality towards external usage this class
 * defines convenient common functionality that is often internally needed when implementing class for parsing of specific webservice IDL type.
 *
 * @author David Pejrimovsky (maxidejf@gmail.com)
 */
public abstract class WebserviceTypeBase {
    
    // in order to work with metadata these services are needed
    protected MetadataFactory metadataFactory;
    protected MetadataService metadataService;
    
    ////////////////
    // NAMESPACES //
    ////////////////
    
    // list of all namespaces used by this module for Capabilities
    public static final String NAMESPACE__CRCE_IDENTITY = NsCrceIdentity.NAMESPACE__CRCE_IDENTITY;
    public static final String NAMESPACE__WEBSERVICESCHEMA_IDENTITY = WebserviceDescriptionResourceIndexer.MAIN_CATEGORY + ".identity";
    public static final String NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE = WebserviceDescriptionResourceIndexer.MAIN_CATEGORY + ".ws";
    public static final String NAMESPACE__WEBSERVICE_ENDPOINT = NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE + ".endpoint";
    public static final String NAMESPACE__WEBSERVICE_TYPE = NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE + ".type";
    
    // list of all namespaces used by this module for Properties
    public static final String NAMESPACE__WEBSERVICE_ENDPOINT_PARAMETER = NAMESPACE__WEBSERVICE_ENDPOINT + ".parameter";
    public static final String NAMESPACE__WEBSERVICE_ENDPOINT_RESPONSE = NAMESPACE__WEBSERVICE_ENDPOINT + ".response";
    
    ////////////////
    // ATTRIBUTES //
    ////////////////
    
    // list of attributes used by this module for NAMESPACE__CRCE_IDENTITY Capability
    public static final AttributeType<String> ATTRIBUTE__CRCE_IDENTITY__MIME = new SimpleAttributeType<>("mime", String.class);
    public static final AttributeType<String> ATTRIBUTE__CRCE_IDENTITY__HASH = new SimpleAttributeType<>("hash", String.class);
    
    // list of attributes used by this module for NAMESPACE__WEBSERVICESCHEMA_IDENTITY Capability
    public static final AttributeType<String> ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__IDL_VERSION = new SimpleAttributeType<>("idl-version", String.class);
    public static final AttributeType<Long> ATTRIBUTE__WEBSERVICESCHEMA_IDENTITY__TIMESTAMP = new SimpleAttributeType<>("parsedAtTimestamp", Long.class);
    
    // list of attributes used by this module for NAMESPACE__WEBSERVICESCHEMA_WEBSERVICE Capability
    public static final AttributeType<String> ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__NAME = new SimpleAttributeType<>("name", String.class);
    public static final AttributeType<String> ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__TYPE = new SimpleAttributeType<>("type", String.class);
    public static final AttributeType<String> ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__URI = new SimpleAttributeType<>("uri", String.class);
    public static final AttributeType<String> ATTRIBUTE__WEBSERVICESCHEMA_WEBSERVICE__IDL_URI = new SimpleAttributeType<>("idl-uri", String.class);
    
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

    
    
    /**
     * This constructor sets up references to all necessary services for metadata manipulation.
     *
     * @param mf
     * @param ms
     */
    public WebserviceTypeBase(MetadataFactory mf, MetadataService ms) {
        metadataFactory = mf;
        metadataService = ms;
    }
    
    /**
     * Returns specific communication pattern used by implemented webservice type. E.g. "rpc", "messaging" or "rest".
     *
     * @return Specific communication pattern used by implemented webservice type.
     */
    public abstract String getSpecificWebserviceType();

    /**
     * This function simply takes input string and encode in into SHA-256 hash.
     *
     * @param idl Content of IDL document.
     * @return SHA-256 encrypted hash.
     */
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
    
    /**
     * This function sets <code>attribute</code> of {@link cz.zcu.kiv.crce.metadata.Capability} to a <code>value</code> provided that passed <code>value</code>
     * is not <code>null</code>.
     *
     * @param <T> Datatype of attribute-value pair.
     * @param capability {@link cz.zcu.kiv.crce.metadata.Capability} for which <code>attribute</code> will be set.
     * @param attribute <code>attribute</code> to set.
     * @param value <code>value</code> to set.
     * @return Returns <code>true</code> if <code>value</code> was set. Returns <code>false</code> otherwise.
     */
    protected static <T> boolean setIfSet(Capability capability, AttributeType<T> attribute, T value) {
        if (capability != null && value != null) {
            capability.setAttribute(attribute, value);
            return true;
        }
        return false;
    }
    
    /**
     * This function sets <code>attribute</code> of {@link cz.zcu.kiv.crce.metadata.Property} to a <code>value</code> provided that passed <code>value</code>
     * is not <code>null</code>.
     *
     * @param <T> Datatype of attribute-value pair.
     * @param property {@link cz.zcu.kiv.crce.metadata.Property} for which <code>attribute</code> will be set.
     * @param attribute <code>attribute</code> to set.
     * @param value <code>value</code> to set.
     * @return Returns <code>true</code> if <code>value</code> was set. Returns <code>false</code> otherwise.
     */
    protected static <T> boolean setIfSet(Property property, AttributeType<T> attribute, T value) {
        if (property != null && value != null) {
            property.setAttribute(attribute, value);
            return true;
        }
        return false;
    }

}
