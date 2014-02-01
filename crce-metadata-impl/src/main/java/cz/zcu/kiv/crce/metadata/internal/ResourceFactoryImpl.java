package cz.zcu.kiv.crce.metadata.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.UUID;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.EqualityComparable;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;

/**
 * Implementation of
 * <code>ResourceFactory</code> interface.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@Component(provides = ResourceFactory.class)
public class ResourceFactoryImpl implements ResourceFactory {

    @ServiceDependency
    private LogHelper LogSerializer;

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
        return new ResourceImpl(id, LogSerializer);
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
    public <T extends EqualityComparable<T>> Property<T> createProperty(String namespace) {
        return createProperty(namespace, generateId());
    }

    @Override
    public <T extends EqualityComparable<T>> Property<T> createProperty(String namespace, String id) {
        return new PropertyImpl<>(namespace, id);
    }

    @Override
    public Repository createRepository(URI uri) {
        return new RepositoryImpl(uri);
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
    public <T extends EqualityComparable<T>> Property<T> cloneProperty(Property<T> property) {
        return clone(property);
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
}
