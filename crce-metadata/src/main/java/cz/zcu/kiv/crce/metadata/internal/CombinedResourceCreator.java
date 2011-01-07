package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.ResourceCreator;
import cz.zcu.kiv.crce.metadata.Resource;
import java.io.InputStream;
import java.net.URI;
//import org.apache.felix.bundlerepository.Property;
//import org.apache.felix.bundlerepository.impl.CapabilityImpl;
//import org.apache.felix.bundlerepository.impl.RequirementImpl;
//import org.apache.felix.bundlerepository.impl.ResourceImpl;


/**
 *
 * @author kalwi
 */
public class CombinedResourceCreator implements ResourceCreator {

    private ResourceCreator m_staticResourceCreator;
    private ResourceCreator m_writableResourceCreator;

    CombinedResourceCreator(ResourceCreator staticCreator, ResourceCreator writableCreator) {
        m_staticResourceCreator = staticCreator;
        m_writableResourceCreator = writableCreator;
    }
            
    @Override
    public void save(Resource resource) {
        m_staticResourceCreator.save(resource);
        m_writableResourceCreator.save(resource);
        InputStream i;
    }

    @Override
    public void move(Resource resource, URI uri) {
        m_staticResourceCreator.move(resource, uri);
        m_writableResourceCreator.move(resource, uri);
    }

    @Override
    public Resource getResource(URI uri) {
        Resource staticResource = m_staticResourceCreator.getResource(uri);
        Resource writableResource = m_writableResourceCreator.getResource(uri);
        
        return new CombinedResourceImpl(staticResource, writableResource);
    }
    
    
//    @Override
//    public void setSymbolicName(String name) throws ReadOnlyException {
//        if (m_resourceStatic.getSymbolicName() == null) {
//            m_resourceExt.put(Resource.SYMBOLIC_NAME, name);
//            m_resourceAll.put(Resource.SYMBOLIC_NAME, name);
//        } else {
//            throw new ReadOnlyException("Symbolic name of this resource is read only.");
//        }
//    }
//
//    @Override
//    public void setVersion(String version) throws ReadOnlyException {
//        if ("0.0.0".equals(m_resourceStatic.getVersion().toString())) {
//            m_resourceExt.put(Resource.VERSION, version);
//            m_resourceAll.put(Resource.VERSION, version);
//        } else {
//            throw new ReadOnlyException("Version of this resource is read only.");
//        }
//    }

//    public static ResourceImpl mergeResourcesCombine(Resource deep, Resource shallow) {
//        ResourceImpl out = cloneResourceDeep(deep);
//        
//        return out;
//    }
        
    /**
     * Merges capabilities, requirements and properties of both given reources
     * into new one.
     * @param major 
     * @param minor
     * @return 
     */
//    public static ResourceImpl mergeResourcesDeep(Resource major, Resource minor) {
//        ResourceImpl out = new ResourceImpl();
//        
//        // categories
//        for (String cat : minor.getCategories()) {
//            out.addCategory(cat);
//        }
//        for (String cat : major.getCategories()) {
//            out.addCategory(cat);
//        }
//        
//        // properties
//        Map props;
//        if ((props = minor.getProperties()) != null) {
//            for (Object o : props.keySet()) {
//                // workaround of bug http://issues.apache.org/jira/browse/FELIX-2757
//                if (!Resource.CATEGORY.equals((String) o)) {    
//                    out.put(o, props.get(o));
//                }
//                
//            }
//        }
//        if ((props = major.getProperties()) != null) {
//            for (Object o : props.keySet()) {
//                // workaround of bug http://issues.apache.org/jira/browse/FELIX-2757
//                if (!Resource.CATEGORY.equals((String) o)) {    
//                    out.put(o, props.get(o));
//                }
//                
//            }
//        }
//        
//        // capabilities
//        for (org.apache.felix.bundlerepository.Capability cap : minor.getCapabilities()) {
//            CapabilityImpl neww = new CapabilityImpl(cap.getName());
//            for (Property p : cap.getProperties()) {
//                neww.addProperty(p);
//            }
//            out.addCapability(neww);
//        }
//        for (org.apache.felix.bundlerepository.Capability cap : major.getCapabilities()) {
//            CapabilityImpl neww = new CapabilityImpl(cap.getName());
//            for (Property p : cap.getProperties()) {
//                neww.addProperty(p);
//            }
//            out.addCapability(neww);
//        }
//        
//        // requirements
//        for (org.apache.felix.bundlerepository.Requirement req : minor.getRequirements()) {
//            RequirementImpl neww = new RequirementImpl(req.getName());
//            neww.setFilter(req.getFilter());
//            neww.setExtend(req.isExtend());
//            neww.setMultiple(req.isMultiple());
//            neww.setOptional(req.isOptional());
//            neww.addText(req.getComment());
//            out.addRequire(neww);
//        }
//        for (org.apache.felix.bundlerepository.Requirement req : major.getRequirements()) {
//            RequirementImpl neww = new RequirementImpl(req.getName());
//            neww.setFilter(req.getFilter());
//            neww.setExtend(req.isExtend());
//            neww.setMultiple(req.isMultiple());
//            neww.setOptional(req.isOptional());
//            neww.addText(req.getComment());
//            out.addRequire(neww);
//        }
//
//        return out;
//    }
//
//    public static ResourceImpl cloneResourcesShallow(Resource src) {
//        ResourceImpl out = new ResourceImpl();
//        
//        // categories
//        for (String cat : src.getCategories()) {
//            out.addCategory(cat);
//        }
//        
//        // properties
//        Map props;
//        if ((props = src.getProperties()) != null) {
//            for (Object o : props.keySet()) {
//                // workaround of bug http://issues.apache.org/jira/browse/FELIX-2757
//                if (!Resource.CATEGORY.equals((String) o)) {    
//                    out.put(o, props.get(o));
//                }
//                
//            }
//        }
//        
//        // capabilities
//        for (org.apache.felix.bundlerepository.Capability cap : src.getCapabilities()) {
//            out.addCapability(cap);
//        }
//        
//        // requirements
//        for (org.apache.felix.bundlerepository.Requirement req : src.getRequirements()) {
//            out.addRequire(req);
//        }
//        
//        return out;
//    }
//    
//
//    /**
//     * Makes deep clone of resource.
//     * @param src
//     * @return 
//     */
//    public static ResourceImpl cloneResourceDeep(Resource src) {
//        
//        ResourceImpl dest = new ResourceImpl();
//
//        // categories
//        for (String cat : src.getCategories()) {
//            dest.addCategory(cat);
//        }
//        
//        // properties
//        Map props;
//        if ((props = src.getProperties()) != null) {
//            for (Object o : props.keySet()) {
//                // workaround of bug http://issues.apache.org/jira/browse/FELIX-2757
//                if (!Resource.CATEGORY.equals((String) o)) {    
//                    dest.put(o, props.get(o));
//                }
//                
//            }
//        }
//        
//        // capabilities
//        for (org.apache.felix.bundlerepository.Capability cap : src.getCapabilities()) {
//            CapabilityImpl neww = new CapabilityImpl(cap.getName());
//            for (Property p : cap.getProperties()) {
//                neww.addProperty(p);
//            }
//            dest.addCapability(neww);
//        }
//        
//        // requirements
//        for (org.apache.felix.bundlerepository.Requirement req : src.getRequirements()) {
//            RequirementImpl neww = new RequirementImpl(req.getName());
//            neww.setFilter(req.getFilter());
//            neww.setExtend(req.isExtend());
//            neww.setMultiple(req.isMultiple());
//            neww.setOptional(req.isOptional());
//            neww.addText(req.getComment());
//            dest.addRequire(neww);
//        }
//        
//        return dest;
//    }

}
