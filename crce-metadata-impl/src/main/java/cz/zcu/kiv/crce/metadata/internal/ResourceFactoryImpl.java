package cz.zcu.kiv.crce.metadata.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.UUID;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;

/**
 * Implementation of
 * <code>ResourceCreator</code> interface.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class ResourceFactoryImpl implements ResourceFactory {

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

    @Override
    public Requirement createRequirement(String namespace, String id) {
        return new RequirementImpl(namespace, id);
    }

    @Override
    public Capability createCapability(String namespace, String id) {
        return new CapabilityImpl(namespace, id);
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
    public Property cloneRequirement(Property property) {
        return clone(property);
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

//    @Override
//    public Capability createCapability(String name) {
//        return new CapabilityImpl(name);
//    }
//    @Override
//    public Requirement createRequirement(String name) {
//        return new RequirementImpl(name);
//    }
//    @Override
//    public WritableRepository createRepository(URI uri) {
//        return new RepositoryImpl(uri);
//    }
//    /**
//     * This implementation does not copy isWritable flag.
//     */
//    @Override
//    public Resource createResource(Resource resource) {
//        ResourceImpl out = new ResourceImpl();
//
//        out.setId(resource.getId());
//        out.setPresentationName(resource.getPresentationName());
//        if (resource.getRepository() instanceof WritableRepository) {
//            out.setRepository((WritableRepository) resource.getRepository());
//        }
//        try {
//            out.setSize(resource.getSize());
//        } catch (IllegalArgumentException e) {
//            // do nothing (size of src resource is not set)
//        }
//        out.setSymbolicName(resource.getSymbolicName());
//        out.setUri(resource.getUri());
//        out.setVersion(resource.getVersion());
//
//        for (Property_ property : resource.getAttributes()) {
//            out.setProperty(new PropertyImpl2(property.getName(), property.getType(), property.getValue()));
//        }
//        for (String category : resource.getCategories()) {
//            out.addCategory(category);
//        }
//        for (Capability capability : resource.getCapabilities()) {
//            Capability cap = new CapabilityImpl(capability.getNamespace());
//            for (Property_ property : capability.getAttributes()) {
//                cap.setProperty(new PropertyImpl2(property.getName(), property.getType(), property.getValue()));
//            }
//            out.addCapability(cap);
//        }
//        for (Requirement requirement : resource.getRequirements()) {
//            Requirement req = new RequirementImpl(requirement.getName());
//            req.setComment(requirement.getComment());
//            req.setExtend(requirement.isExtend());
//            req.setFilter(requirement.getFilter());
//            req.setMultiple(requirement.isMultiple());
//            req.setOptional(requirement.isOptional());
//            out.addRequirement(req);
//        }
//        return out;
//    }

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
