package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.DataModelHelperExt;
import cz.zcu.kiv.crce.metadata.Metadata;
import cz.zcu.kiv.crce.metadata.ReadOnlyException;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import org.apache.felix.bundlerepository.Property;
import org.apache.felix.bundlerepository.Resource;
import org.apache.felix.bundlerepository.impl.CapabilityImpl;
import org.apache.felix.bundlerepository.impl.RequirementImpl;
import org.apache.felix.bundlerepository.impl.ResourceImpl;
import org.apache.felix.bundlerepository.impl.wrapper.Wrapper;
import org.osgi.framework.Version;
import org.osgi.service.obr.Capability;
import org.osgi.service.obr.Requirement;


/**
 *
 * @author kalwi
 */
public class MetadataImpl implements Metadata {

    private File m_metafile;

    private Resource m_resourceStatic;
    private ResourceImpl m_resourceExt;
    private ResourceImpl m_resourceAll;
            
    private DataModelHelperExt m_dataModel;

    private MetadataImpl(File metafile) {
        m_metafile = metafile;
        m_dataModel = Activator.getHelper();
        m_resourceAll = new ResourceImpl();
        
//        if (m_metafile.exists()) {
//            readMetadata();
//        } else {
//            m_metafile.createNewFile();
//            m_resource = new ResourceImpl();
//        }
    }
    
    public MetadataImpl(File metafile, Resource staticResource, ResourceImpl metaResource) {
//        this(metafile);
        m_metafile = metafile;
        m_dataModel = Activator.getHelper();
        
        m_resourceStatic = staticResource;
        m_resourceExt = metaResource;
        m_resourceAll = cloneResource(staticResource);
    }
    
//    @Override
//    public Capability[] getCapabilities() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public Requirement[] getRequirements() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    @Override
    public void addCapability(Capability capability) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addRequirement(Requirement requirement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCapability(String capability, String property, String value) {
        // TODO
    }

    @Override
    public void setCapability(String capability, String property, Version value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCapability(String capability, String property, URI value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCapability(String capability, String property, URL value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCapability(String capability, String property, long value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCapability(String capability, String property, double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCapability(String capability, String property, Set value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public org.osgi.service.obr.Resource getResource() {
        return m_resourceAll == null ? null : Wrapper.wrap(m_resourceAll);
    }
    
    protected org.apache.felix.bundlerepository.Resource getFelixResource() {
        return m_resourceAll;
    }

//    private void readMetadata() throws FileNotFoundException, IOException {
//        Reader reader = new InputStreamReader(new FileInputStream(m_metafile));
//        
//        try {
//            m_resource = (ResourceImpl) m_dataModel.readResource(reader);
//        } catch (Exception e) {
//            e.printStackTrace(); // XXX
//        } finally {
//            reader.close();
//        }
//        
//        
//    }

    @Override
    public void setSymbolicName(String name) throws ReadOnlyException {
        if (m_resourceStatic.getSymbolicName() == null) {
            m_resourceExt.put(Resource.SYMBOLIC_NAME, name);
        } else {
            throw new ReadOnlyException("Symbolic name of this resource is read only.");
        }
        
    }

    @Override
    public void setVersion(String version) throws ReadOnlyException {
        if ("0.0.0".equals(m_resourceStatic.getVersion().toString())) {
            m_resourceExt.put(Resource.VERSION, version);
        } else {
            throw new ReadOnlyException("Version of this resource is read only.");
        }
        
    }
    
    public static ResourceImpl cloneResource(Resource src) {
        
        ResourceImpl dest = new ResourceImpl();

        // categories
        for (String cat : src.getCategories()) {
            dest.addCategory(cat);
        }
        
        // properties
        Map props;
        if ((props = src.getProperties()) != null) {
            for (Object o : props.keySet()) {
                // workaround of bug http://issues.apache.org/jira/browse/FELIX-2757
                if (!Resource.CATEGORY.equals((String) o)) {    
                    dest.put(o, props.get(o));
                }
                
            }
        }
        
        // capabilities
        for (org.apache.felix.bundlerepository.Capability cap : src.getCapabilities()) {
            CapabilityImpl neww = new CapabilityImpl(cap.getName());
            for (Property p : cap.getProperties()) {
                neww.addProperty(p);
            }
            dest.addCapability(neww);
        }
        
        // requirements
        for (org.apache.felix.bundlerepository.Requirement req : src.getRequirements()) {
            RequirementImpl neww = new RequirementImpl(req.getName());
            neww.setFilter(req.getFilter());
            neww.setExtend(req.isExtend());
            neww.setMultiple(req.isMultiple());
            neww.setOptional(req.isOptional());
            neww.addText(req.getComment());
            dest.addRequire(neww);
        }
        
        return dest;
    }
}
