package cz.zcu.kiv.crce.metadata.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.namespace.NsCrceIdentity;
import cz.zcu.kiv.crce.metadata.namespace.NsCrceView;

/**
 * Implementation of
 * <code>MetadataFactory</code> interface.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@Component(provides = MetadataFactory.class)
public class MetadataFactoryImpl implements MetadataFactory {

    @ServiceDependency(required = false)
    private static LogHelper logHelper;

    @Override
    public Resource createResource() {
        return createResource(generateId());
    }

    @Override
    public Requirement createRequirement(String namespace) {
        return createRequirement(namespace, generateId());
    }

    @Override
    public Capability createCapability(String namespace) {
        return createCapability(namespace, generateId());
    }

    @Override
    public Resource createResource(String id) {
        return new ResourceImpl(id);
    }

    //TODO move to (Store?)
    @Override
    public Resource createResourceView(Resource... resources) {
        Resource view = createResource();
        Capability identityCap = createCapability(NsCrceIdentity.NAMESPACE__CRCE_IDENTITY);
        identityCap.setAttribute(NsCrceIdentity.ATTRIBUTE__NAME, view.getId());
        identityCap.setAttribute(NsCrceIdentity.ATTRIBUTE__EXTERNAL_ID, view.getId());
        try {
            identityCap.setAttribute(NsCrceIdentity.ATTRIBUTE__URI, new URI("http://nofile"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            //not gonna happen
        }


        Capability viewCap = createCapability(NsCrceView.NAMESPACE__CRCE_VIEW_IDENTITY);

        List<String> ids = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> categories  = new ArrayList<>();

        Capability identity;
        Attribute<String> nameAt;
        Attribute<Long> sizeAt;
        Attribute<List<String>> categoryAt;

        String name;
        Long size = 0L;

        boolean first = true;
        for (Resource resource : resources) {
            ids.add(resource.getId());

            identity = resource.getRootCapabilities(NsCrceIdentity.NAMESPACE__CRCE_IDENTITY).get(0);
            if(first) {
                String repository = identity.getAttributeStringValue(NsCrceIdentity.ATTRIBUTE__REPOSITORY_ID);
                identityCap.setAttribute(NsCrceIdentity.ATTRIBUTE__REPOSITORY_ID, repository != null ? repository : "");
            }

            nameAt = identity.getAttribute(NsCrceIdentity.ATTRIBUTE__NAME);
            name = nameAt != null ? nameAt.getValue() : "-";
            names.add(name);

            sizeAt = identity.getAttribute(NsCrceIdentity.ATTRIBUTE__SIZE);
            if(sizeAt != null) {
                size += sizeAt.getValue();
            }

            categoryAt = identity.getAttribute(NsCrceIdentity.ATTRIBUTE__CATEGORIES);
            if(categoryAt != null) {
                categories.addAll(categoryAt.getValue());
            }
            if(first) first = false;
        }

        viewCap.setAttribute(NsCrceView.ATTRIBUTE__RESOURCE_IDS, ids);
        viewCap.setAttribute(NsCrceView.ATTRIBUTE__RESOURCE_NAMES, names);
        identityCap.setAttribute(NsCrceIdentity.ATTRIBUTE__CATEGORIES, categories);
        if(size != 0L) {
            identityCap.setAttribute(NsCrceIdentity.ATTRIBUTE__SIZE, size);
        }

        view.addCapability(identityCap);
        view.addRootCapability(identityCap);
        view.addCapability(viewCap);
        view.addRootCapability(viewCap);

        return view;
    }

    @Override
    public Requirement createRequirement(String namespace, String id) {
        return new RequirementImpl(namespace, id);
    }

    @Override
    public Capability createCapability(String namespace, String id) {
        return new CapabilityImpl(namespace, id);
    }

    @Override
    public Property createProperty(String namespace) {
        return createProperty(namespace, generateId());
    }

    @Override
    public Property createProperty(String namespace, String id) {
        return new PropertyImpl(namespace, id);
    }

    @Override
    public <T> Attribute<T> createAttribute(AttributeType<T> type, T value) {
        return createAttribute(type, value, Operator.EQUAL);
    }

    @Override
    public <T> Attribute<T> createAttribute(AttributeType<T> type, T value, Operator operator) {
        return new AttributeImpl<>(type, value, operator);
    }

    @Override
    public <T> Attribute<T> createAttribute(String name, Class<T> type, T value) {
        return createAttribute(name, type, value, Operator.EQUAL);
    }

    @Override
    public <T> Attribute<T> createAttribute(String name, Class<T> type, T value, Operator operator) {
        AttributeType<T> attributeType = new SimpleAttributeType<>(name, type);
        return createAttribute(attributeType, value, operator);
    }

    @Override
    public Repository createRepository(URI uri) {
        return createRepository(uri, generateId());
    }

    @Override
    public Repository createRepository(URI uri, String id) {
        return new RepositoryImpl(uri, id);
    }

    @Override
    public Resource cloneResource(Resource resource) {
        return clone(resource);
    }

    @Override
    public Capability cloneCapability(Capability capability) {
        return clone(capability);
    }

    @Override
    public Requirement cloneRequirement(Requirement requirement) {
        return clone(requirement);
    }

    @Override
    public Property cloneProperty(Property property) {
        return clone(property);
    }

    @Override
    public <T> Attribute<T> cloneAttribute(Attribute<T> attribute) {
        return clone(attribute);
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Experimental cloning method.
     *
     * @param <T>
     * @param object
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T extends Serializable> T clone(T object) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try (ObjectOutputStream objOut = new ObjectOutputStream(byteArrayOutputStream)) {
                objOut.writeObject(object);
            }

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            return (T) new ObjectInputStream(byteArrayInputStream).readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    static String toString(Resource resource) {
        if (logHelper != null && logHelper.available()) {
            return logHelper.toString(resource);
        }
        return "ResourceImpl{" + "id=" + resource.getId() + "}";
    }

    static String toString(Capability capability) {
        if (logHelper != null && logHelper.available()) {
            return logHelper.toString(capability);
        }
        return "CapabilityImpl{" + "id=" + capability.getId() + "}";
    }

    static String toString(Requirement requirement) {
        if (logHelper != null && logHelper.available()) {
            return logHelper.toString(requirement);
        }
        return "RequirementImpl{" + "id=" + requirement.getId() + "}";
    }

    static String toString(Property property) {
        if (logHelper != null && logHelper.available()) {
            return logHelper.toString(property);
        }
        return "PropertyImpl{" + "id=" + property.getId() + "}";
    }

    static String toString(Attribute<?> attribute) {
        if (logHelper != null && logHelper.available()) {
            return logHelper.toString(attribute);
        }
        return "AttributeImpl{" + "name=" + attribute.getName() + ", type=" + attribute.getType().getName() + ", value=" + attribute.getStringValue() + "}";
    }
}
