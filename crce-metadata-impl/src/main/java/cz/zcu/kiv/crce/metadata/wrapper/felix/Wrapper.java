package cz.zcu.kiv.crce.metadata.wrapper.felix;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Reason;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.Type;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class Wrapper {

    public static org.apache.felix.bundlerepository.Repository wrap(Repository repository) {
        return new RepositoryWrapper(repository);
    }
    
    public static org.apache.felix.bundlerepository.Resource wrap(Resource resource) {
        return new ResourceWrapper(resource);
    }
    
    public static org.apache.felix.bundlerepository.Requirement wrap(Requirement resolver) {
        return new RequirementWrapper(resolver);
    }

    public static org.apache.felix.bundlerepository.Capability wrap(Capability capability) {
        return new CapabilityWrapper(capability);
    }

    public static org.apache.felix.bundlerepository.Resource[] wrap(Resource[] resources)
    {
        org.apache.felix.bundlerepository.Resource[] res = new org.apache.felix.bundlerepository.Resource[resources.length];
        for (int i = 0; i < resources.length; i++)
        {
            res[i] = wrap(resources[i]);
        }
        return res;
    }

    public static org.apache.felix.bundlerepository.Requirement[] wrap(Requirement[] requirements)
    {
        org.apache.felix.bundlerepository.Requirement[] req = new org.apache.felix.bundlerepository.Requirement[requirements.length];
        for (int i = 0; i < requirements.length; i++)
        {
            req[i] = wrap(requirements[i]);
        }
        return req;
    }

    public static org.apache.felix.bundlerepository.Capability[] wrap(Capability[] capabilities)
    {
        org.apache.felix.bundlerepository.Capability[] cap = new org.apache.felix.bundlerepository.Capability[capabilities.length];
        for (int i = 0; i < capabilities.length; i++)
        {
            cap[i] = wrap(capabilities[i]);
        }
        return cap;
    }

    public static org.apache.felix.bundlerepository.Repository[] wrap(Repository[] repositories)
    {
        org.apache.felix.bundlerepository.Repository[] rep = new org.apache.felix.bundlerepository.Repository[repositories.length];
        for (int i = 0; i < repositories.length; i++)
        {
            rep[i] = wrap(repositories[i]);
        }
        return rep;
    }

    public static Resource unwrap(org.apache.felix.bundlerepository.Resource resource) {
        if (resource instanceof ResourceWrapper) {
            return ((ResourceWrapper) resource).resource;
        } else {
            return new ConvertedResource(resource);
        }
    }
    
    public static Resource[] unwrap(org.apache.felix.bundlerepository.Resource[] resources) {
        Resource[] out = new Resource[resources.length];
        if (resources[0] instanceof ResourceWrapper) {
            for (int i = 0; i < resources.length; i++) {
                out[i] = unwrap(resources[i]);
            }
        } else {
            for (int i = 0; i < resources.length; i++) {
                out[i] = new ConvertedResource(resources[i]);
            }
        }
        return out;
    }
    
    public static Capability unwrap(org.apache.felix.bundlerepository.Capability capability) {
        if (capability instanceof CapabilityWrapper) {
            return ((CapabilityWrapper) capability).m_capability;
        }
        Capability out = new CapabilityImpl(capability.getName());
        for (org.apache.felix.bundlerepository.Property property : capability.getProperties()) {
            String name = property.getName();
            String type = property.getType();
            String value = property.getValue();
            if (org.apache.felix.bundlerepository.Property.DOUBLE.equals(type)) {
                out.setProperty(name, value, Type.DOUBLE);
            } else if (org.apache.felix.bundlerepository.Property.LONG.equals(type)) {
                out.setProperty(name, value, Type.LONG);
            } else if (org.apache.felix.bundlerepository.Property.SET.equals(type)) {
                out.setProperty(name, value, Type.SET);
            } else if (org.apache.felix.bundlerepository.Property.URI.equals(type)) {
                out.setProperty(name, value, Type.URI);
            } else if (org.apache.felix.bundlerepository.Property.URL.equals(type)) {
                out.setProperty(name, value, Type.URL);
            } else if (org.apache.felix.bundlerepository.Property.VERSION.equals(type)) {
                out.setProperty(name, value, Type.VERSION);
            } else {
                out.setProperty(name, value, Type.STRING);
            }
        }
        return out;
    }
    
}
