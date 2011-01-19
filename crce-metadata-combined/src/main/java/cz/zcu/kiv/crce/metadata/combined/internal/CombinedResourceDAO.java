package cz.zcu.kiv.crce.metadata.combined.internal;

import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.plugin.ResourceDAO;
import java.io.IOException;
import java.net.URI;
//import org.apache.felix.bundlerepository.Property;
//import org.apache.felix.bundlerepository.impl.CapabilityImpl;
//import org.apache.felix.bundlerepository.impl.RequirementImpl;
//import org.apache.felix.bundlerepository.impl.ResourceImpl;


/**
 *
 * @author kalwi
 */
public class CombinedResourceDAO implements ResourceDAO {

    private ResourceDAO m_staticResourceDAO;
    private ResourceDAO m_writableResourceDAO;

    public CombinedResourceDAO(ResourceDAO staticDAO, ResourceDAO writableDAO) {
        m_staticResourceDAO = staticDAO;
        m_writableResourceDAO = writableDAO;
    }
            
    @Override
    public void save(Resource resource) throws IOException {

        if (resource instanceof CombinedResourceImpl) {
            CombinedResourceImpl cres = (CombinedResourceImpl) resource;
            System.out.println("--- saving ---");
            System.out.println("");
            System.out.println("combined: " + resource);
            System.out.println("" + resource.getUri());

            System.out.println("");
            System.out.println("static: " + cres.getStaticResource());
            System.out.println("" + cres.getStaticResource().getUri());

            System.out.println("");
            System.out.println("writable: " + cres.getWritableResource());
            System.out.println("" + cres.getWritableResource().getUri());

            System.out.println("---");

            m_staticResourceDAO.save(cres.getStaticResource());
            m_writableResourceDAO.save(cres.getWritableResource());
        } else {
            throw new IllegalStateException("Not a CombinedResourceImpl"); // XXX
        }
        
    }

    @Override
    public void copy(Resource resource, URI uri) throws IOException {
        m_staticResourceDAO.copy(resource, uri);
        m_writableResourceDAO.copy(resource, uri);
    }

    @Override
    public Resource getResource(URI uri) throws IOException {
        Resource staticResource = m_staticResourceDAO.getResource(uri);
        Resource writableResource = m_writableResourceDAO.getResource(uri);
        
        if (staticResource == null && writableResource == null) {
            return null;
        }
        
        if (staticResource == null) {
            staticResource = Activator.getResourceCreator().createResource();
//            staticResource.setSymbolicName(createSymbolicname(uri));
        }
        if (writableResource == null) {
            writableResource = Activator.getResourceCreator().createResource();
        }
        if (staticResource.getSymbolicName() == null) {
            writableResource.setSymbolicName(createSymbolicname(uri));
        }
        
        // TODO set new resource's URIs ?
        return new CombinedResourceImpl(staticResource, writableResource);
    }
    
    private String createSymbolicname(URI uri) {
            String[] seg = uri.getPath().split("/");
            return seg[seg.length - 1];
    }

    @Override
    public String getPluginId() {
        return "combined resource dao";
    }

    @Override
    public String getName() {
        return "combined resource dao";
    }

    @Override
    public int getPluginPriority() {
        return 10;
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
//                    out.put(o, props.getResource(o));
//                }
//                
//            }
//        }
//        if ((props = major.getProperties()) != null) {
//            for (Object o : props.keySet()) {
//                // workaround of bug http://issues.apache.org/jira/browse/FELIX-2757
//                if (!Resource.CATEGORY.equals((String) o)) {    
//                    out.put(o, props.getResource(o));
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
//                    out.put(o, props.getResource(o));
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
//                    dest.put(o, props.getResource(o));
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
