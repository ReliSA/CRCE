package cz.zcu.kiv.crce.repository.internal;

import java.net.URI;
import java.util.List;

import org.osgi.framework.Version;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.SimpleAttributeType;

/**
 * This is quick temporary helper to set/extract values to/from new metadata using old way.
 * <p>Methods could be refactored and either moved to common service, or moved to component model specific plugin.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class LegacyMetadataHelper {

    public static final AttributeType<URI> CRCE_URI = new SimpleAttributeType<>("uri", URI.class);
    public static final AttributeType<String> CRCE_FILE_NAME = new SimpleAttributeType<>("file-name", String.class);
    public static final AttributeType<String> OSGI_SYMBOLIC_NAME = new SimpleAttributeType<>("name", String.class);
    public static final AttributeType<String> OSGI_PRESENTATION_NAME = new SimpleAttributeType<>("presentation-name", String.class);
    public static final AttributeType<Version> OSGI_VERSION = new SimpleAttributeType<>("presentation-name", Version.class);

    public static final String NS__IDENTITY = "crce.identity";
    public static final String NS__OSGI_IDENTITY = "osgi.identity";

    public static URI getUri(Resource resource) {
        List<Capability> capabilities = resource.getCapabilities(NS__IDENTITY);

        assert !capabilities.isEmpty();

        return capabilities.get(0).getAttributeValue(CRCE_URI);
    }

    public static void setFileName(ResourceFactory factory, Resource resource, String fileName) {
        getSingleCapability(factory, resource, NS__IDENTITY).setAttribute(CRCE_FILE_NAME, fileName);
    }

    public static void setSymbolicName(ResourceFactory factory, Resource resource, String symbolicName) {
        getSingleCapability(factory, resource, NS__OSGI_IDENTITY).setAttribute(OSGI_SYMBOLIC_NAME, symbolicName);
    }

    public static String getSymbolicName(Resource resource) {
        List<Capability> capabilities = resource.getCapabilities(NS__OSGI_IDENTITY);

        if (capabilities.isEmpty()) {
            return null;
        }

        return capabilities.get(0).getAttributeValue(OSGI_SYMBOLIC_NAME);
    }

    public static void setPresentationName(ResourceFactory factory, Resource resource, String symbolicName) {
        getSingleCapability(factory, resource, NS__OSGI_IDENTITY).setAttribute(OSGI_PRESENTATION_NAME, symbolicName);
    }

    public static String getPresentationName(Resource resource) {
        List<Capability> capabilities = resource.getCapabilities(NS__OSGI_IDENTITY);

        if (capabilities.isEmpty()) {
            return null;
        }

        return capabilities.get(0).getAttributeValue(OSGI_PRESENTATION_NAME);
    }

    public static Version getVersion(Resource resource) {
        List<Capability> capabilities = resource.getCapabilities(NS__OSGI_IDENTITY);

        Version version = null;
        if (!capabilities.isEmpty()) {
            version = capabilities.get(0).getAttributeValue(OSGI_VERSION);
        }

        if (version == null) {
            version = Version.emptyVersion;
        }

        return version;
    }

    private static Capability getSingleCapability(ResourceFactory factory, Resource resource, String namespace) {
        List<Capability> capabilities = resource.getCapabilities(namespace);

        assert capabilities.size() < 2;

        Capability capability;
        if (capabilities.isEmpty()) {
            capability = factory.createCapability(namespace);
            resource.addCapability(capability);
        } else {
            capability = capabilities.get(1);
        }
        return capability;
    }
}
