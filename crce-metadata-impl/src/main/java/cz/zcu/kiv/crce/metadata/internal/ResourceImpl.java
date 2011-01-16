package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Property;
import org.apache.felix.utils.version.VersionTable;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.osgi.framework.Version;

import static org.apache.felix.bundlerepository.Resource.*;

/**
 *
 * @author kalwi
 */
public class ResourceImpl extends AbstractPropertyProvider implements Resource {

//    private Map<String, Object> m_map;
    private boolean m_writable;
    private final Set<Capability> m_capabilities = new HashSet<Capability>();
    private final Set<Requirement> m_requirements = new HashSet<Requirement>();
    private final Set<String> m_categories = new HashSet<String>();

    public ResourceImpl() {
        m_writable = true;
//        m_map = new HashMap<String, Object>();
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
    public long getSize() {
        Property size = getProperty(SIZE);
        return size == null ? -1 : (Long) size.getConvertedValue();
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
    public Map<String, String> getPropertiesMap() {
        Map<String, String> map = new HashMap<String, String>();
        for (Property p : getProperties()) {
            map.put(p.getName(), p.getValue());
        }
        return map;
    }

    @Override
    public boolean hasCategory(String category) {
        return m_categories.contains(category);
    }

    @Override
    public void setSymbolicName(String name) {
        if (isWritable()) {
            setProperty(SYMBOLIC_NAME, name);
            setProperty(ID, name + "/" + getVersion());
        }
    }

    @Override
    public void setVersion(Version version) {
        if (version == null) {
            // TODO would be better to set version as 0.0.0 ?
            throw new NullPointerException("Version can not be null.");
        }
        if (isWritable()) {
            setProperty(VERSION, version);
            setProperty(ID, getSymbolicName() + "/" + version);
        }
    }

    @Override
    public void setVersion(String version) {
        // TODO check null ?
        if (isWritable()) {
            setProperty(VERSION, VersionTable.getVersion(version));
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
        if (isWritable()) {
            setProperty(ID, id);
        }
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
                throw new IllegalArgumentException("Size can't be less than zero");
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
            sb.append("  Opt: ").append(req.isOptional());
            sb.append(" Mult: ").append(req.isMultiple());
            sb.append(" Ext: ").append(req.isExtend());
            sb.append(req.getName()).append(" ").append(req.getFilter()).append(" ").append(req.getComment()).append("\n");
        }
        
        return sb.toString();
    }
}
