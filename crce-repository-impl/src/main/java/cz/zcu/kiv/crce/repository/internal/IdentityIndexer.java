package cz.zcu.kiv.crce.repository.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.service.MetadataService;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@Component(provides = IdentityIndexer.class)
public class IdentityIndexer {

    private static final Logger logger = LoggerFactory.getLogger(IdentityIndexer.class);

    private static final String MIME__APPLICATION_OCTET_STREAM = "application/octet-stream";


    @ServiceDependency private volatile MetadataService metadataService;

    public void preIndex(File file, String name, Resource resource) {
        Capability identity = metadataService.getIdentity(resource);

        metadataService.setUri(resource, file.toURI().normalize());
        metadataService.setFileName(resource, name);

        identity.setAttribute("original-file-name", String.class, name); // TODO hardcoded
    }

    public void postIndex(File file, Resource resource) {
        Capability identity = metadataService.getIdentity(resource);

        metadataService.setSize(resource, file.length());
        String hash = getSHA(file);
        if (hash != null) {
            identity.setAttribute("hash", String.class, hash); // TODO hardcoded
        }

        SimpleAttributeType<String> mime = new SimpleAttributeType<>("mime", String.class); // TODO hardcoded
        Attribute<String> attribute = identity.getAttribute(mime);
        if (attribute == null) {
            identity.setAttribute(mime, MIME__APPLICATION_OCTET_STREAM);
        }
    }

    /**
     * Get hexadecimal SHA-256 of file with resource or null, if error occurred during counting digest.
     *
     * @param file
     * @return hexadecimal SHA-256 of file with resource or null
     */
    private String getSHA(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] buffer = new byte[10 * 1024];

            int nread;
            while ((nread = fis.read(buffer)) != -1) {
                md.update(buffer, 0, nread);
            }
            byte[] mdbytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1)); // NOPMD better clarity
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException | IOException e) {
            logger.warn("Hash generation failed.", e);
            return null;
        }
    }
}
