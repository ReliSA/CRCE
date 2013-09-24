package cz.zcu.kiv.crce.metadata.wrapper.felix;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.Type;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import cz.zcu.kiv.crce.metadata.internal.RequirementImpl;
import cz.zcu.kiv.crce.metadata.internal.ResourceImpl;
import java.net.URI;
import java.util.Map;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class ConvertedResource extends ResourceImpl implements Resource { // TODO ResourceImpl is probably not visible for other bundles

    public ConvertedResource(org.apache.felix.bundlerepository.Resource resource) {
        for (org.apache.felix.bundlerepository.Capability fcap : resource.getCapabilities()) {
            Capability cap = new CapabilityImpl(fcap.getName());
            for (org.apache.felix.bundlerepository.Property fprop : fcap.getProperties()) {
                cap.setProperty(fprop.getName(), fprop.getValue(), Type.getValue(fprop.getType()));
            }
            addCapability(cap);
        }
        for (org.apache.felix.bundlerepository.Requirement freq : resource.getRequirements()) {
            Requirement req = new RequirementImpl(freq.getName());
            
            req.setComment(freq.getComment());
            req.setFilter(freq.getFilter());
            req.setExtend(freq.isExtend());
            req.setMultiple(freq.isMultiple());
            req.setOptional(freq.isOptional());
            addRequirement(req);
        }
        for (String fcat : resource.getCategories()) {
            addCategory(fcat);
        }
        
        setSymbolicName(resource.getSymbolicName());
        setVersion(resource.getVersion());
        setSize(resource.getSize() != null ? resource.getSize() : 0);
        
        Map properties = resource.getProperties();
        for (Object o : properties.keySet()) {
            String key = (String) o;
            if (!org.apache.felix.bundlerepository.Resource.SYMBOLIC_NAME.equals(key) &&
                    !org.apache.felix.bundlerepository.Resource.VERSION.equals(key) &&
                    !org.apache.felix.bundlerepository.Resource.SIZE.equals(key) &&
                    !org.apache.felix.bundlerepository.Resource.CATEGORY.equals(key)) {
                setProperty(key, String.valueOf(properties.get(key)));
            }
        }
        
        
        try {
            setUri(new URI(resource.getURI()));
        } catch (Exception ex) {
//            System.out.println("Exception: " + ex.getLocalizedMessage() + ", uri: " + resource.getURI());
//            setUri(null); // TODO co s tim?
        }
        
        setVersion(resource.getVersion());
        
        setId(resource.getId());
        
        setWritable(false);
    }

}
