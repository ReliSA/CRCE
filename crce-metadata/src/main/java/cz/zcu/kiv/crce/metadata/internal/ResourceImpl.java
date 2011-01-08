package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.osgi.framework.Version;

/**
 *
 * @author kalwi
 */
public class ResourceImpl implements Resource {

    private String m_id;
    private String m_symbolicName;
    private Version m_version;
    private String m_presentationName;
    private URI m_uri;
    private long m_size;
    
    private boolean m_writable;
    
//    private final List<Capability> m_capabilities = new ArrayList<Capability>();
//    private final List<Requirement> m_requirements = new ArrayList<Requirement>();
    private final Set<Capability> m_capabilities = new HashSet<Capability>();
    private final Set<Requirement> m_requirements = new HashSet<Requirement>();
    
    private final Set<String> m_categories = new HashSet<String>();

    public ResourceImpl() {
        m_writable = true;
    }
    
    @Override
    public String getId() {
        return m_id;
    }

    @Override
    public String getSymbolicName() {
        return m_symbolicName;
    }

    @Override
    public Version getVersion() {
        return (m_version == null) ? Version.emptyVersion : m_version;
    }

    @Override
    public String getPresentationName() {
        return m_presentationName;
    }

    @Override
    public URI getUri() {
        return m_uri;
    }

    @Override
    public long getSize() {
        return m_size;
    }

    @Override
    public String[] getCategories() {
        return m_categories.toArray(new String[m_categories.size()]);
    }

    @Override
    public Capability[] getCapabilities() {
        return m_capabilities.toArray(new Capability[m_capabilities.size()]);
    }

    @Override
    public Requirement[] getRequirements() {
        return m_requirements.toArray(new Requirement[m_requirements.size()]);
    }

    @Override
    public Map getProperties() {
        throw new UnsupportedOperationException("Not supported yet.");        
//        Map map = new HashMap();
//        return map;
    }

    @Override
    public boolean hasCategory(String category) {
        return m_categories.contains(category);
    }

    @Override
    public void setSymbolicName(String name) {
        if (isWritable()) {
            m_symbolicName = name;
        }
    }

    @Override
    public void setVersion(Version version) {
        if (version == null) {
            throw new NullPointerException("Version can not be null.");
        }
        if (isWritable()) {
            m_version = version;
        }
    }

    @Override
    public void setVersion(String version) {
        if (isWritable()) {
            m_version = new Version(version);
        }
    }
    
    @Override
    public void setCategory(String category) {
        if (isWritable()) {
            m_categories.add(category);
        }
    }

    @Override
    public void addCapability(Capability capability) {
        if (isWritable()) {
            m_capabilities.add(capability);
        }
    }

    @Override
    public void addRequirement(Requirement requirement) {
        if (isWritable()) {
            m_requirements.add(requirement);
        }
    }

    @Override
    public Capability createCapability(String name) {
        Capability c = new CapabilityImpl(name);
        if (isWritable()) {
            m_capabilities.add(c);
        }
        return c;
    }

    @Override
    public Requirement createRequirement(String name) {
        Requirement r = new RequirementImpl(name);
        if (isWritable()) {
            m_requirements.add(r);
        }
        return r;
    }

    @Override
    public boolean hasCapability(Capability capability) {
        return m_capabilities.contains(capability);
    }

    @Override
    public boolean hasRequirement(Requirement requirement) {
        return m_requirements.contains(requirement);
    }

    @Override
    public boolean isWritable() {
        return m_writable;
    }

    protected void setWritable(boolean writable) {
        m_writable = writable;
    }
    
    protected void setId(String id) {
        // TODO writable check?
        m_id = id;
    }

    @Override
    public void setPresentationName(String name) {
        if (isWritable()) {
            m_presentationName = name;
        }
    }

    @Override
    public void setSize(long size) {
        if (isWritable()) {
            m_size = size;
        }
    }

    @Override
    public void setUri(URI uri) {
        if (isWritable()) {
            m_uri = uri;
        }
    }

}
 