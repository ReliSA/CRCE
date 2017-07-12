package cz.zcu.kiv.crce.metadata.service.internal;

import java.net.URI;
import java.net.URISyntaxException;
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
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.namespace.NsCrceIdentity;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.type.Version;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@Component(provides = MetadataService.class)
public class MetadataServiceImpl implements MetadataService {

    private static final Logger logger = LoggerFactory.getLogger(MetadataServiceImpl.class);

    @ServiceDependency
    private MetadataFactory metadataFactory;

    @Start
    public void activate() {
        logger.info("CRCE MetadataService started.");
    }

    @Override
    public Capability getIdentity(Resource resource) {
        return getSingletonCapability(resource, NsCrceIdentity.NAMESPACE__CRCE_IDENTITY);
    }

    @Override
    public String getPresentationName(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource is null.");
        }

        String name = getSingletonCapability(resource, NsCrceIdentity.NAMESPACE__CRCE_IDENTITY).getAttributeValue(NsCrceIdentity.ATTRIBUTE__NAME);

        if (name == null || name.isEmpty()) {
            logger.warn("Resource with id {} has no name specified.", resource.getId());
            name =  "unknown-name:" + resource.getId();
        }
        return name;
    }

    @Override
    public void setPresentationName(Resource resource, String name) {
        getSingletonCapability(resource, NsCrceIdentity.NAMESPACE__CRCE_IDENTITY).setAttribute(NsCrceIdentity.ATTRIBUTE__NAME, name);
    }

    @Override
    public String getExternalId(@Nonnull Resource resource) {
        String id = getIdentity(resource).getAttributeValue(NsCrceIdentity.ATTRIBUTE__EXTERNAL_ID);

        if(id == null) {
            id = "unknown-external-id: " + resource.getId();
        }

        return id;
    }

    @Override
    public void setExternalId(@Nonnull Resource resource, String externalId) {
        getIdentity(resource).setAttribute(NsCrceIdentity.ATTRIBUTE__EXTERNAL_ID, externalId);
    }

    @Override
    public String getPresentationName(Capability capability) {
        if (capability == null) {
            throw new IllegalArgumentException("Capability is null.");
        }

        String name = capability.getAttributeValue(NsCrceIdentity.ATTRIBUTE__NAME);

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
        List<Attribute<String>> attributes = requirement.getAttributes(NsCrceIdentity.ATTRIBUTE__NAME);

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
        if (property == null) {
            throw new IllegalArgumentException("Attribute is null.");
        }
        String name = property.getAttributeValue(NsCrceIdentity.ATTRIBUTE__NAME);

        if (name == null || name.isEmpty()) {
            logger.warn("Capability with id {} has no name specified.", property.getId());
            name = "unknown-name:" + property.getId();
        }
        return name;
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
        URI uri = getSingletonCapability(resource, NsCrceIdentity.NAMESPACE__CRCE_IDENTITY).getAttributeValue(NsCrceIdentity.ATTRIBUTE__URI);
        if (uri == null) {
            throw new IllegalStateException("URI is null, resource: " + resource.getId());
        }
        return uri;
    }

//    @Override
//    public URI getRelativeUri(Resource resource) {
//        URI absolute = getUri(resource);
//        Repository repository = resource.getRepository();
//        if (repository != null) {
//            URI repositoryUri = repository.getUri();
//            return repositoryUri.relativize(absolute);
//        }
//        return absolute;
//    }

    @Override
    public void setUri(Resource resource, URI uri) {
        getSingletonCapability(resource, NsCrceIdentity.NAMESPACE__CRCE_IDENTITY).setAttribute(NsCrceIdentity.ATTRIBUTE__URI, uri);
    }

    @Override
    public void setUri(Resource resource, String uri) throws IllegalArgumentException {
        try {
            setUri(resource, new URI(uri));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getFileName(Resource resource) {
        String fileName = getSingletonCapability(resource, NsCrceIdentity.NAMESPACE__CRCE_IDENTITY).getAttributeValue(NsCrceIdentity.ATTRIBUTE__FILE_NAME);
        if (fileName == null) {
            throw new IllegalStateException("File name attribute is null");
        }
        return fileName;
    }

    @Override
    public void setFileName(Resource resource, String fileName) {
        getSingletonCapability(resource, NsCrceIdentity.NAMESPACE__CRCE_IDENTITY).setAttribute(NsCrceIdentity.ATTRIBUTE__FILE_NAME, fileName);
    }

    @Override
    public long getSize(Resource resource) {
        Long size = getSingletonCapability(resource, NsCrceIdentity.NAMESPACE__CRCE_IDENTITY).getAttributeValue(NsCrceIdentity.ATTRIBUTE__SIZE);
        if (size == null) {
            throw new IllegalStateException("Size attribute is null"); // -1 would be optionally returned
        }
        return size;
    }

    @Override
    public void setSize(Resource resource, long size) {
        getSingletonCapability(resource, NsCrceIdentity.NAMESPACE__CRCE_IDENTITY).setAttribute(NsCrceIdentity.ATTRIBUTE__SIZE, size);
    }

    @Override
    public List<String> getCategories(Resource resource) {
        List<String> categories = getSingletonCapability(resource, NsCrceIdentity.NAMESPACE__CRCE_IDENTITY).getAttributeValue(NsCrceIdentity.ATTRIBUTE__CATEGORIES);
        if (categories == null) {
            return Collections.emptyList();
        }
        return categories;
    }

    @Override
    public void addCategory(Resource resource, String category) {
        Capability identity = getSingletonCapability(resource, NsCrceIdentity.NAMESPACE__CRCE_IDENTITY);
        List<String> categories = identity.getAttributeValue(NsCrceIdentity.ATTRIBUTE__CATEGORIES);
        if (categories == null) {
            categories = new ArrayList<>();
            identity.setAttribute(NsCrceIdentity.ATTRIBUTE__CATEGORIES, categories);
        }
        if (!categories.contains(category)) {
            categories.add(category);
        }
    }

    @Override
    public void removeCategory(Resource resource, String category) {
        List<String> categories = getSingletonCapability(resource, NsCrceIdentity.NAMESPACE__CRCE_IDENTITY).getAttributeValue(NsCrceIdentity.ATTRIBUTE__CATEGORIES);
        if (categories != null && !categories.isEmpty()) {
            categories.remove(category);
        }
    }

    @Override
    public void addRootCapability(Resource resource, Capability capability) {
        List<Capability> rootCapabilities = resource.getRootCapabilities(capability.getNamespace());
        if (!rootCapabilities.contains(capability)) {
            resource.addRootCapability(capability);
        }

        addCapabilityTree(resource, capability);
    }

    private void addCapabilityTree(@Nonnull Resource resource, @Nonnull Capability capability) {
        List<Capability> capabilities = resource.getCapabilities(capability.getNamespace());
        if (!capabilities.contains(capability)) {
            resource.addCapability(capability);
        }

        for (Capability child : capability.getChildren()) {
            addCapabilityTree(resource, child);
        }
    }

    @Override
    public void removeCapability(Resource resource, Capability capability) {
        resource.removeCapability(capability);
        resource.removeRootCapability(capability);

        for (Capability child : capability.getChildren()) {
            removeCapability(resource, child);
        }
    }

//    @Override
//    public void addChild(Capability parent, Capability child) {
//        if (!parent.getChildren().contains(child)) {
//            parent.addChild(child);
//            child.setParent(parent);
//            Resource resource = parent.getResource();
//            if (resource != null) {
//                addChildTree(resource, child);
//            }
//        }
//    }

//    private void addChildTree(@Nonnull Resource resource, @Nonnull Capability capability) {
//        if (!resource.getCapabilities(capability.getNamespace()).contains(capability)) {
//            resource.addCapability(capability);
//        }
//        capability.setResource(resource);
//        for (Capability child : capability.getChildren()) {
//            addChildTree(resource, child);
//        }
//    }

    @Override
    public void addRequirement(Resource resource, Requirement requirement) {
        List<Requirement> requirements = resource.getRequirements(requirement.getNamespace());
        if (!requirements.contains(requirement)) {
            resource.addRequirement(requirement);
        }

        addRequirementTree(resource, requirement);
    }

    private void addRequirementTree(@Nonnull Resource resource, @Nonnull Requirement requirement) {
        for (Requirement child : requirement.getChildren()) {
            addRequirementTree(resource, child);
        }
    }

    @Override
    public void removeRequirement(Resource resource, Requirement requirement) {
        resource.removeRequirement(requirement);
        removeRequirementTree(requirement);
    }

    private void removeRequirementTree(@Nonnull Requirement requirement) {
        for (Requirement child : requirement.getChildren()) {
            removeRequirementTree(child);
        }
    }

    // ---

    @Nonnull
    @Override
    public Capability getSingletonCapability(@Nonnull Resource resource, @Nonnull String namespace) {
        List<Capability> capabilities = resource.getCapabilities(namespace);

        assert capabilities.size() < 2;

        Capability capability;
        if (capabilities.isEmpty()) {
            capability = metadataFactory.createCapability(namespace);
            resource.addCapability(capability);
            resource.addRootCapability(capability);
        } else {
            capability = capabilities.get(0);
        }

        assert capability != null;

        return capability;
    }

    @Override
    public Requirement createIdentityRequirement(String name) {
        Requirement req = metadataFactory.createRequirement(NsCrceIdentity.NAMESPACE__CRCE_IDENTITY);
        req.addAttribute(NsCrceIdentity.ATTRIBUTE__EXTERNAL_ID, name);
        return req;
    }

    @Override
    public Requirement createIdentityRequirement(String name, String version) {
        Requirement req = createIdentityRequirement(name);
        req.addAttribute(NsCrceIdentity.ATTRIBUTE__VERSION, new Version(version));
        return req;
    }
}
