package cz.zcu.kiv.crce.metadata.wrapper.felix;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Type;
import cz.zcu.kiv.crce.metadata.internal.CapabilityImpl;
import cz.zcu.kiv.crce.metadata.internal.RequirementImpl;
import cz.zcu.kiv.crce.metadata.internal.ResourceImpl;
import java.net.URI;

/**
 *
 * @author kalwi
 */
public class ConvertedResource extends ResourceImpl {

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
            setCategory(fcat);
        }
        
        setSymbolicName(resource.getSymbolicName());
        setPresentationName(resource.getPresentationName());
        
//        resource.getProperties(); // TODO
        
        setSize(resource.getSize() != null ? resource.getSize() : 0);
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
