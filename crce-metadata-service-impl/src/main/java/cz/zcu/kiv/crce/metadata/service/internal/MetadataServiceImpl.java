package cz.zcu.kiv.crce.metadata.service.internal;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;
import org.apache.felix.dm.annotation.api.Start;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.impl.ListAttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.service.MetadataService;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@Component(provides = MetadataService.class)
public class MetadataServiceImpl implements MetadataService {

    private static final Logger logger = LoggerFactory.getLogger(MetadataServiceImpl.class);

    public static final String NAMESPACE__CRCE_IDENTITY = "crce.identity";
    public static final AttributeType<String> ATTRIBUTE__NAME = new SimpleAttributeType<>("name", String.class);
    public static final AttributeType<URI> ATTRIBUTE__URI = new SimpleAttributeType<>("uri", URI.class);
    public static final AttributeType<String> ATTRIBUTE__FILE_NAME = new SimpleAttributeType<>("file-name", String.class);
    public static final AttributeType<Long> ATTRIBUTE__SIZE = new SimpleAttributeType<>("size", Long.class);
    public static final AttributeType<List<String>> ATTRIBUTE__CATEGORIES = new ListAttributeType("categories");


    @ServiceDependency
    private ResourceFactory resourceFactory;

    @Start
    public void activate() {
        logger.info("CRCE MetadataService started.");
    }

    @Override
    public String getIdentityNamespace() {
        return NAMESPACE__CRCE_IDENTITY;
    }

    @Override
    public String getPresentationName(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource is null.");
        }

        String name = getSingleCapability(resource, NAMESPACE__CRCE_IDENTITY).getAttributeValue(ATTRIBUTE__NAME);

        if (name == null || name.isEmpty()) {
            logger.warn("Resource with id {} has no name specified.", resource.getId());
            name =  "unknown-name:" + resource.getId();
        }
        return name;
    }

    @Override
    public void setPresentationName(Resource resource, String name) {
        getSingleCapability(resource, NAMESPACE__CRCE_IDENTITY).setAttribute(ATTRIBUTE__NAME, name);
    }

    @Override
    public String getPresentationName(Capability capability) {
        if (capability == null) {
            throw new IllegalArgumentException("Capability is null.");
        }

        String name = capability.getAttributeValue(ATTRIBUTE__NAME);

        if (name == null || name.isEmpty()) {
            logger.warn("Capability with id {} has no name specified.", capability.getId());
            name = "unknown-name:" + capability.getId();
        }
        return name;
    }

    @Override
    public String getPresentationName(Requirement requirement) {
        if (requirement == null) {
            throw new IllegalArgumentException("Requirement is null.");
        }
        List<Attribute<String>> attributes = requirement.getAttributes(ATTRIBUTE__NAME);

        String name;
        if (attributes.isEmpty()) {
            logger.warn("Requirement with id {} has no name specified.", requirement.getId());
            name = "unknown-name:" + requirement.getId();
        } else if (attributes.size() == 1) {
            name = attributes.get(0).getAttributeType().getName();
        } else {
            Set<String> set = new HashSet<>();
            for (Attribute<String> attribute : attributes) {
                set.add(attribute.getAttributeType().getName());
            }
            if (set.size() == 1) {
                name = set.iterator().next();
            } else {
                Iterator<String> it = set.iterator();
                StringBuilder sb = new StringBuilder("(");
                sb.append(it.next());
                while (it.hasNext()) {
                    sb.append(",").append(it.next());
                }
                sb.append(")");
                name = sb.toString();
            }
        }

        return name;
    }

    @Override
    public String getPresentationName(Property property) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getPresentationName(Attribute<?> attribute) {
        if (attribute == null) {
            throw new IllegalArgumentException("Attribute is null.");
        }
        return attribute.getAttributeType().getName();
    }

    @Override
    public URI getUri(Resource resource) {
        URI uri = getSingleCapability(resource, NAMESPACE__CRCE_IDENTITY).getAttributeValue(ATTRIBUTE__URI);
        if (uri == null) {
            throw new IllegalStateException("URI is null, resource: " + resource.getId());
        }
        return uri;
    }

    @Override
    public URI getRelativeUri(Resource resource) {
        URI absolute = getUri(resource);
        Repository repository = resource.getRepository();
        if (repository != null) {
            URI repositoryUri = repository.getURI();
            return repositoryUri.relativize(absolute);
        }
        return absolute;
    }

    @Override
    public void setUri(Resource resource, URI uri) {
        getSingleCapability(resource, NAMESPACE__CRCE_IDENTITY).setAttribute(ATTRIBUTE__URI, uri);
    }

    @Override
    public String getFileName(Resource resource) {
        String fileName = getSingleCapability(resource, NAMESPACE__CRCE_IDENTITY).getAttributeValue(ATTRIBUTE__FILE_NAME);
        if (fileName == null) {
            throw new IllegalStateException("File name attribute is null");
        }
        return fileName;
    }

    @Override
    public void setFileName(Resource resource, String fileName) {
        getSingleCapability(resource, NAMESPACE__CRCE_IDENTITY).setAttribute(ATTRIBUTE__FILE_NAME, fileName);
    }

    @Override
    public long getSize(Resource resource) {
        Long size = getSingleCapability(resource, NAMESPACE__CRCE_IDENTITY).getAttributeValue(ATTRIBUTE__SIZE);
        if (size == null) {
            throw new IllegalStateException("Size attribute is null"); // -1 would be optionally returned
        }
        return size;
    }

    @Override
    public void setSize(Resource resource, long size) {
        getSingleCapability(resource, NAMESPACE__CRCE_IDENTITY).setAttribute(ATTRIBUTE__SIZE, size);
    }

    @Override
    public List<String> getCategories(Resource resource) {
        List<String> categories = getSingleCapability(resource, NAMESPACE__CRCE_IDENTITY).getAttributeValue(ATTRIBUTE__CATEGORIES);
        if (categories == null) {
            return Collections.emptyList();
        }
        return categories;
    }

    @Override
    public void addCategory(Resource resource, String category) {
        Capability identity = getSingleCapability(resource, NAMESPACE__CRCE_IDENTITY);
        List<String> categories = identity.getAttributeValue(ATTRIBUTE__CATEGORIES);
        if (categories == null) {
            categories = new ArrayList<>();
            identity.setAttribute(ATTRIBUTE__CATEGORIES, categories);
        }
        if (!categories.contains(category)) {
            categories.add(category);
        }
    }

    @Override
    public void removeCategory(Resource resource, String category) {
        List<String> categories = getSingleCapability(resource, NAMESPACE__CRCE_IDENTITY).getAttributeValue(ATTRIBUTE__CATEGORIES);
        if (categories != null && !categories.isEmpty()) {
            categories.remove(category);
        }
    }

    // ---

    @Nonnull
    private Capability getSingleCapability(@Nonnull Resource resource, @Nonnull String namespace) {
        List<Capability> capabilities = resource.getCapabilities(namespace);

        assert capabilities.size() < 2;

        Capability capability;
        if (capabilities.isEmpty()) {
            capability = resourceFactory.createCapability(namespace);
            resource.addCapability(capability);
        } else {
            capability = capabilities.get(0);
        }

        assert capability != null;

        return capability;
    }
}
