package cz.zcu.kiv.crce.metadata.wrapper.osgi;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class Wrapper {

//    public static Resource wrap(org.osgi.service.obr.Resource resource) {
//        Resource out = new ResourceImpl();
//        
//        resource.
//    }
    
    public static org.osgi.service.obr.Resource wrap(Resource resource) {
        return new ResourceWrapper(resource);
    }

    public static org.osgi.service.obr.Requirement wrap(Requirement resolver) {
        return new RequirementWrapper(resolver);
    }

    public static org.osgi.service.obr.Capability wrap(Capability capability) {
        return new CapabilityWrapper(capability);
    }

    public static org.osgi.service.obr.Resource[] wrap(Resource[] resources)
    {
        org.osgi.service.obr.Resource[] res = new org.osgi.service.obr.Resource[resources.length];
        for (int i = 0; i < resources.length; i++)
        {
            res[i] = wrap(resources[i]);
        }
        return res;
    }

    public static org.osgi.service.obr.Requirement[] wrap(Requirement[] requirements)
    {
        org.osgi.service.obr.Requirement[] req = new org.osgi.service.obr.Requirement[requirements.length];
        for (int i = 0; i < requirements.length; i++)
        {
            req[i] = wrap(requirements[i]);
        }
        return req;
    }

    public static org.osgi.service.obr.Capability[] wrap(Capability[] capabilities)
    {
        org.osgi.service.obr.Capability[] cap = new org.osgi.service.obr.Capability[capabilities.length];
        for (int i = 0; i < capabilities.length; i++)
        {
            cap[i] = wrap(capabilities[i]);
        }
        return cap;
    }

}
