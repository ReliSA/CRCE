package cz.zcu.kiv.crce.metadata.internal.wrapper;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 *
 * @author kalwi
 */
public class Wrapper {

//    public static Resource wrap(org.apache.felix.bundlerepository.Resource resource) {
//        Resource out = new ResourceImpl();
//        
//        resource.
//    }
    
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

}
