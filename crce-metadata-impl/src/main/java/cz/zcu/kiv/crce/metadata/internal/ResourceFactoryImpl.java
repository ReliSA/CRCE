package cz.zcu.kiv.crce.metadata.internal;

import java.net.URI;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;

/**
 * Implementation of <code>ResourceCreator</code> interface.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class ResourceFactoryImpl implements ResourceFactory {

    @Override
    public Resource createResource() {
        return new ResourceImpl();
    }

    @Override
    public Resource cloneResource(Resource resource) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Capability cloneCapability(String namespace) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Requirement createRequirement(String namespace) {
        return new RequirementImpl(namespace);
    }

    @Override
    public Capability createCapability(String namespace) {
        return new CapabilityImpl(namespace);
    }

    @Override
    public Repository createRepository(URI uri) {
        return new RepositoryImpl(uri);
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

    /**
     * This implementation does not copy isWritable flag.
     */
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

}
