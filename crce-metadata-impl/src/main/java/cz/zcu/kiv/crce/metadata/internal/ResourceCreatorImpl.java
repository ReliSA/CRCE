package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.wrapper.felix.ConvertedResolver;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resolver;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.WritableRepository;
import java.net.URI;
import org.apache.felix.bundlerepository.RepositoryAdmin;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class ResourceCreatorImpl implements ResourceCreator {

    private volatile RepositoryAdmin m_repoAdmin;
    
    @Override
    public Resource createResource() {
        return new ResourceImpl();
    }

    @Override
    public Capability createCapability(String name) {
        return new CapabilityImpl(name);
    }

    @Override
    public Requirement createRequirement(String name) {
        return new RequirementImpl(name);
    }

    @Override
    public WritableRepository createRepository(URI uri) {
        return new RepositoryImpl(uri);
    }

    /**
     * This implementation does not copy isWritable flag.
     */
    @Override
    public Resource createResource(Resource resource) {
        ResourceImpl out = new ResourceImpl();
        
        out.setId(resource.getId());
        out.setPresentationName(resource.getPresentationName());
        if (resource.getRepository() instanceof WritableRepository) {
            out.setRepository((WritableRepository) resource.getRepository());
        }
        try {
            out.setSize(resource.getSize());
        } catch (IllegalArgumentException e) {
            // do nothing (size of src resource is not set)
        }
        out.setSymbolicName(resource.getSymbolicName());
        out.setUri(resource.getUri());
        out.setVersion(resource.getVersion());
        
        for (Property property : resource.getProperties()) {
            out.setProperty(new PropertyImpl(property.getName(), property.getType(), property.getValue()));
        }
        for (String category : resource.getCategories()) {
            out.addCategory(category);
        }
        for (Capability capability : resource.getCapabilities()) {
            Capability cap = new CapabilityImpl(capability.getName());
            for (Property property : capability.getProperties()) {
                cap.setProperty(new PropertyImpl(property.getName(), property.getType(), property.getValue()));
            }
            out.addCapability(cap);
        }
        for (Requirement requirement : resource.getRequirements()) {
            Requirement req = new RequirementImpl(requirement.getName());
            req.setComment(requirement.getComment());
            req.setExtend(requirement.isExtend());
            req.setFilter(requirement.getFilter());
            req.setMultiple(requirement.isMultiple());
            req.setOptional(requirement.isOptional());
            out.addRequirement(req);
        }
        return out;
    }

    @Override
    public Resolver createResolver(Repository... repositories) {
        return new ConvertedResolver(m_repoAdmin, repositories);
    }

}
