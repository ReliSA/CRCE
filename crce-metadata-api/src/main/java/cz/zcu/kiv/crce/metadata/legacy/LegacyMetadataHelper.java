package cz.zcu.kiv.crce.metadata.legacy;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import org.osgi.framework.Version;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.impl.ListAttributeType;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

/**
 * This is quick temporary helper to set/extract values to/from new metadata using old way.
 * <p>Methods could be refactored and either moved to common service, or moved to component model specific plugin.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class LegacyMetadataHelper {

    public static final String NS__CRCE_IDENTITY = "crce.identity";
    public static final String NS__OSGI_IDENTITY = "osgi.identity";

    public static final AttributeType<URI> CRCE_URI = new SimpleAttributeType<>("uri", URI.class);
    public static final AttributeType<String> CRCE_FILE_NAME = new SimpleAttributeType<>("file-name", String.class);
    public static final AttributeType<List<String>> CRCE_CATEGORIES = new ListAttributeType("categories");
    public static final AttributeType<Long> CRCE_SIZE = new SimpleAttributeType<>("size", Long.class);

    public static final AttributeType<String> OSGI_SYMBOLIC_NAME = new SimpleAttributeType<>("name", String.class);
    public static final AttributeType<String> OSGI_PRESENTATION_NAME = new SimpleAttributeType<>("presentation-name", String.class);
    public static final AttributeType<Version> OSGI_VERSION = new SimpleAttributeType<>("presentation-name", Version.class);

    public static URI getUri(@Nonnull Resource resource) {
        List<Capability> capabilities = resource.getCapabilities(NS__CRCE_IDENTITY);

        assert !capabilities.isEmpty();

        return capabilities.get(0).getAttributeValue(CRCE_URI);
    }

    public static void setFileName(ResourceFactory factory, Resource resource, String fileName) {
        getSingleCapability(factory, resource, NS__CRCE_IDENTITY).setAttribute(CRCE_FILE_NAME, fileName);
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

    public static void setVersion(ResourceFactory factory, Resource resource, Version version) {
        getSingleCapability(factory, resource, NS__OSGI_IDENTITY).setAttribute(OSGI_VERSION, version);
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

    public static URI getRelativeUri(@Nonnull Resource resource) {
        URI absolute = getUri(resource);
        if (absolute == null) {
            return null;
        }
        Repository repository = resource.getRepository();
        if (repository != null) {
            URI repo = repository.getURI();
            return repo.relativize(absolute);
        }
        return absolute;
    }

    public static List<String> getCategories(@Nonnull Resource resource) {
        List<Capability> capabilities = resource.getCapabilities(NS__CRCE_IDENTITY);

        if (capabilities.isEmpty()) {
            return Collections.emptyList();
        }
        Capability capability = capabilities.get(0);
        List<String> categories = capability.getAttributeValue(CRCE_CATEGORIES);
        if (categories != null) {
            return categories;
        }
        return Collections.emptyList();
    }

    public static void addCategory(ResourceFactory factory, Resource resource, String category) {
        Capability capability = getSingleCapability(factory, resource, NS__CRCE_IDENTITY);
        List<String> categories = capability.getAttributeValue(CRCE_CATEGORIES);
        if (categories == null) {
            categories = new ArrayList<>();
            capability.setAttribute(CRCE_CATEGORIES, categories);
        }
        categories.add(category);
    }

    public static void removeCategory(Resource resource, String category) {
        List<Capability> capabilities = resource.getCapabilities(NS__CRCE_IDENTITY);
        if (!capabilities.isEmpty()) {
            Capability capability = capabilities.get(0);
            List<String> capabilitiesList = capability.getAttributeValue(CRCE_CATEGORIES);
            if (capabilitiesList != null && !capabilitiesList.isEmpty()) {
                capabilitiesList.remove(category);
            }
        }
    }

    public static long getSize(Resource resource) {
        List<Capability> capabilities = resource.getCapabilities(NS__CRCE_IDENTITY);
        if (!capabilities.isEmpty()) {
            Long size = capabilities.get(0).getAttributeValue(CRCE_SIZE);
            if (size != null && size >= 0) {
                return size;
            }
        }
        return -1;
    }

    private static Capability getSingleCapability(ResourceFactory factory, Resource resource, String namespace) {
        List<Capability> capabilities = resource.getCapabilities(namespace);

        assert capabilities.size() < 2;

        Capability capability;
        if (capabilities.isEmpty()) {
            capability = factory.createCapability(namespace);
            resource.addCapability(capability);
        } else {
            capability = capabilities.get(0);
        }
        return capability;
    }
}
