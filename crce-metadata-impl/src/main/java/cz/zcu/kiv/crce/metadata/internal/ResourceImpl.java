package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.WritableRepository;
import java.util.List;
import cz.zcu.kiv.crce.metadata.Property;
import org.apache.felix.utils.version.VersionTable;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.osgi.framework.Version;

import static org.apache.felix.bundlerepository.Resource.*;

/**
 *
 * @author Jiri Kucera (kalwi@students.zcu.cz, kalwi@kalwi.eu)
 */
public class ResourceImpl extends AbstractPropertyProvider<Resource> implements Resource {

    private boolean m_writable;
    private boolean m_versionStatic;
    private boolean m_symbolicNameStatic;
    
    private final Set<Capability> m_capabilities = new HashSet<Capability>();
    private final Set<Requirement> m_requirements = new HashSet<Requirement>();
    private final Set<String> m_categories = new HashSet<String>();
    
    private WritableRepository m_repository = null;
    
    private transient int m_hash;

    public ResourceImpl() {
        m_writable = true;
    }

    @Override
    public String getId() {
        return getPropertyString(ID);
    }

    @Override
    public String getSymbolicName() {
        return getPropertyString(SYMBOLIC_NAME);
    }

    @Override
    public Version getVersion() {
        Property version = getProperty(VERSION);
        return version == null ? Version.emptyVersion : (Version) version.getConvertedValue();
    }

    @Override
    public String getPresentationName() {
        return getPropertyString(PRESENTATION_NAME);
    }

    @Override
    public URI getUri() {
        Property uri = getProperty(URI);
        return uri == null ? null : (URI) uri.getConvertedValue();
    }

    @Override
    public URI getRelativeUri() {
        URI absolute = getUri();
        if (absolute == null) {
            return null;
        }
        if (m_repository == null) {
            return absolute;
        }
        URI repo = m_repository.getURI();
        return repo == null ? absolute : repo.relativize(absolute);
    }

    @Override
    public long getSize() {
        Property size = getProperty(SIZE);
        return size == null ? -1 : (Long) size.getConvertedValue();
    }

    @Override
    public String[] getCategories() {
        synchronized (m_categories) {
            return m_categories.toArray(new String[m_categories.size()]);
        }
    }

    @Override
    public Capability[] getCapabilities() {
        synchronized (m_capabilities) {
            return m_capabilities.toArray(new Capability[m_capabilities.size()]);
        }
    }

    @Override
    public Capability[] getCapabilities(String name) {
        if (name == null) {
            return getCapabilities();
        }
        List<Capability> out = new ArrayList<Capability>();
        
        synchronized (m_capabilities) {
            for (Capability cap : m_capabilities) {
                if (name.equals(cap.getName())) {
                    out.add(cap);
                }
            }
        }
        
        return out.toArray(new Capability[out.size()]);
    }

    @Override
    public Requirement[] getRequirements() {
        synchronized (m_requirements) {
            return m_requirements.toArray(new Requirement[m_requirements.size()]);
        }
    }

    @Override
    public Requirement[] getRequirements(String name) {
        if (name == null) {
            return getRequirements();
        }
        List<Requirement> out = new ArrayList<Requirement>();
        
        synchronized (m_requirements) {
            for (Requirement req : m_requirements) {
                if (name.equals(req.getName())) {
                    out.add(req);
                }
            }
        }
        
        return out.toArray(new Requirement[out.size()]);
    }

    @Override
    public Map<String, String> getPropertiesMap() {
        Map<String, String> map = new HashMap<String, String>();
        for (Property p : getProperties()) {
            map.put(p.getName(), p.getValue());
        }
        return map;
    }

    @Override
    public boolean hasCategory(String category) {
        synchronized (m_categories) {
            return m_categories.contains(category);
        }
    }

    @Override
    public synchronized void setSymbolicName(String name) {
        setSymbolicName(name, false);
    }
    
    @Override
    public synchronized void setSymbolicName(String name, boolean isStatic) {
        if (name != null && isWritable() && !isSymbolicNameStatic()) {
            WritableRepository r;
            if ((r = m_repository) != null) {
                m_repository.removeResource(this);
            }
            setProperty(SYMBOLIC_NAME, name);
            setProperty(ID, name + "/" + getVersion());
            m_hash = 0;
            m_symbolicNameStatic = isStatic;
            if ((m_repository = r) != null) {
                m_repository.addResource(this);
            }
        }
    }

    @Override
    public synchronized void setVersion(Version version) {
        setVersion(version, false);
    }

    @Override
    public synchronized void setVersion(Version version, boolean isStatic) {
        if (version != null && isWritable() && !isVersionStatic()) {
            WritableRepository r;
            if ((r = m_repository) != null) {
                m_repository.removeResource(this);
            }
            setProperty(VERSION, version);
            setProperty(ID, getSymbolicName() + "/" + version);
            m_hash = 0;
            m_versionStatic = isStatic;
            if ((m_repository = r) != null) {
                m_repository.addResource(this);
            }
        }
    }


    @Override
    public synchronized void setVersion(String version) {
        setVersion(version, false);
    }

    @Override
    public synchronized void setVersion(String version, boolean isStatic) {
        setVersion(VersionTable.getVersion(version), isStatic);
    }

    @Override
    public void addCategory(String category) {
        if (isWritable() && category != null && !"".equals(category.trim())) {
            synchronized (m_categories) {
                m_categories.add(category);
            }
        }
    }

    @Override
    public void addCapability(Capability capability) {
        if (isWritable() && capability != null) {
            synchronized (m_capabilities) {
                m_capabilities.add(capability);
            }
        }
    }

    @Override
    public void addRequirement(Requirement requirement) {
        if (isWritable() && requirement != null) {
            synchronized (m_requirements) {
                m_requirements.add(requirement);
            }
        }
    }

    @Override
    public Capability createCapability(String name) {
        Capability c = new CapabilityImpl(name);
        if (isWritable()) {
            synchronized (m_capabilities) {
                m_capabilities.add(c);
            }
        }
        return c;
    }

    @Override
    public Requirement createRequirement(String name) {
        Requirement r = new RequirementImpl(name);
        if (isWritable()) {
            synchronized (m_requirements) {
                m_requirements.add(r);
            }
        }
        return r;
    }

    @Override
    public void unsetCategory(String category) {
        if (isWritable()) {
            synchronized (m_categories) {
                m_categories.remove(category);
            }
        }
    }

    @Override
    public void unsetCapability(Capability capability) {
        if (isWritable()) {
            synchronized (m_capabilities) {
                m_capabilities.remove(capability);
            }
        }
    }

    @Override
    public void unsetRequirement(Requirement requirement) {
        if (isWritable()) {
            synchronized (m_requirements) {
                m_requirements.remove(requirement);
            }
        }
    }

    @Override
    public boolean hasCapability(Capability capability) {
        synchronized (m_capabilities) {
            return m_capabilities.contains(capability);
        }
    }

    @Override
    public boolean hasRequirement(Requirement requirement) {
        synchronized (m_requirements) {
            return m_requirements.contains(requirement);
        }
    }

    @Override
    public boolean isWritable() {
        return m_writable;
    }

    @Override
    public void setPresentationName(String name) {
        if (isWritable()) {
            setProperty(PRESENTATION_NAME, name);
        }
    }

    @Override
    public void setSize(long size) {
        if (isWritable()) {
            if (size < 0) {
                throw new IllegalArgumentException("Size can't be less than zero: " + size);
            }
            setProperty(SIZE, size);
        }
    }

    @Override
    public void setUri(URI uri) {
        if (isWritable()) {
            setProperty(URI, uri);
        }
    }
    
    @Override
    public String toString() {
        return getSymbolicName() + "/" + getVersion().toString();
    }

    @Override
    public String asString() {
        StringBuilder sb = new StringBuilder();

        sb.append("ID                : ").append(getId()).append("\n");
        sb.append("Symbolic name     : ").append(getSymbolicName()).append("\n");
        sb.append("Version           : ").append(getVersion()).append("\n");
        sb.append("Presentation name : ").append(getPresentationName()).append("\n");
        sb.append("Size              : ").append(getSize()).append("\n");
        sb.append("URI               : ").append(getUri()).append("\n");
        sb.append("Categories:\n");
        for (String cat : getCategories()) {
            sb.append("  ").append(cat).append("\n");
        }
        sb.append("Capabilities:\n");
        for (Capability cap : getCapabilities()) {
            sb.append("  ").append(cap.getName()).append("\n");
            for (Property prop : cap.getProperties()) {
                sb.append("    ").append(prop.getName()).append("[").append(prop.getType()).append("]: ").append(prop.getValue()).append("\n");
            }
        }
        sb.append("Requirements:\n");
        for (Requirement req : getRequirements()) {
            sb.append("  O: ").append(req.isOptional() ? "T" : "F");
            sb.append("\tM: ").append(req.isMultiple() ? "T" : "F");
            sb.append("\tE: ").append(req.isExtend() ? "T" : "F");
            sb.append("\tN: ").append(req.getName());
            sb.append("\tF: ").append(req.getFilter());
            sb.append("\tC: ").append(req.getComment());
            sb.append("\n");
        }
        
        return sb.toString();
    }

    @Override
    public void unsetWritable() {
        m_writable = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Resource) {
            if (getSymbolicName() == null || getVersion() == null) {
                return this == obj;
            }
            return getSymbolicName().equals(((Resource) obj).getSymbolicName())
                    && getVersion().equals(((Resource) obj).getVersion());
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (m_hash == 0) {
            if (getSymbolicName() == null || getVersion() == null) {
                m_hash = super.hashCode();
            } else {
                m_hash = getSymbolicName().hashCode() ^ getVersion().hashCode();
            }
        }
        return m_hash;
    }
    
    protected void setWritable(boolean writable) {
        m_writable = writable;
    }

    protected void setId(String id) {
        if (isWritable()) {
            setProperty(ID, id);
        }
    }

    protected synchronized void setRepository(WritableRepository repository) {
        m_repository = repository;
    }
    
    protected WritableRepository getRepository() {
        return m_repository;
    }

    @Override
    public boolean isVersionStatic() {
        return m_versionStatic;
    }

    @Override
    public boolean isSymbolicNameStatic() {
        return m_symbolicNameStatic;
    }

    @Override
    protected Resource getThis() {
        return this;
    }
}
