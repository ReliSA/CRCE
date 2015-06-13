package cz.zcu.kiv.crce.webservices.indexer.internal;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
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
    
    // list of all namespaces used by this module for Capabilities
    public static final String NAMESPACE__CRCE_IDENTITY = MetadataServiceImpl.NAMESPACE__CRCE_IDENTITY;
    public static final String NAMESPACE__WEBSERVICE_IDENTITY = "webservice.identity";
    public static final String NAMESPACE__WEBSERVICE_ENDPOINT = "webservice.endpoint";
    public static final String NAMESPACE__WEBSERVICE_TYPE = "webservice.type";
    
    // list of attributes used by this module for NAMESPACE__CRCE_IDENTITY Capability
    public static final AttributeType<String> ATTRIBUTE__MIME = new SimpleAttributeType<>("mime", String.class);
    public static final AttributeType<String> ATTRIBUTE__HASH = new SimpleAttributeType<>("hash", String.class);
    
    public WebserviceTypeBase(MetadataFactory mf, MetadataService ms) {
        metadataFactory = mf;
        metadataService = ms;
    }
    
    public abstract String getSpecificWebserviceCategory();

    public String getIdlHash(String idl) {
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

}
