package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.CombinedResource;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import java.util.Arrays;
import java.util.Map;
import org.osgi.framework.Version;

/**
 *
 * @author kalwi
 */
public class CombinedResourceImpl implements CombinedResource {

    private Resource m_staticResource;
    private Resource m_writableResource;
    
    public CombinedResourceImpl(Resource stat, Resource wr) {
        m_staticResource = stat;
        m_writableResource = wr;
    }
    
    @Override
    public String getId() {
        return m_staticResource.getId() != null ? m_staticResource.getId() : m_writableResource.getId();
    }

    @Override
    public String getSymbolicName() {
        return m_staticResource.getSymbolicName() != null ? m_staticResource.getSymbolicName() : m_writableResource.getSymbolicName();
    }

    @Override
    public Version getVersion() {
        return !"0.0.0".equals(m_staticResource.getVersion().toString()) ? m_staticResource.getVersion() : m_writableResource.getVersion();
    }

    @Override
    public String getPresentationName() {
        return m_staticResource.getPresentationName() != null ? m_staticResource.getPresentationName() : m_writableResource.getPresentationName();
    }

    @Override
    public String getURI() {
        return "file:///not/implemented/combined/uri";
    }

    @Override
    public long getSize() {
        return m_staticResource.getSize();
    }

    @Override
    public String[] getCategories() {
        return concat(m_staticResource.getCategories(), m_writableResource.getCategories());
    }

    @Override
    public Capability[] getCapabilities() {
        return concat(m_staticResource.getCapabilities(), m_writableResource.getCapabilities());
    }

    @Override
    public Requirement[] getRequirements() {
        return concat(m_staticResource.getRequirements(), m_writableResource.getRequirements());
    }

    @Override
    public Map getProperties() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasCategory(String category) {
        return m_staticResource.hasCategory(category) || m_writableResource.hasCategory(category);
    }

    @Override
    public void setSymbolicName(String name) {
        if (m_staticResource.getSymbolicName() == null) {
            m_writableResource.setSymbolicName(name);
        }
    }

    @Override
    public void setVersion(Version version) {
        if ("0.0.0".equals(m_staticResource.getVersion().toString())) {
            m_writableResource.setVersion(version);
        }
    }

    @Override
    public void setCategory(String category) {
        if (!m_staticResource.hasCategory(category)) {
            m_writableResource.setCategory(category);
        }
    }

    @Override
    public void addCapability(Capability capability) {
        if (!m_staticResource.hasCapability(capability)) {
            m_writableResource.addCapability(capability);
        }
    }

    @Override
    public void addRequirement(Requirement requirement) {
        if (!m_staticResource.hasRequirement(requirement)) {
            m_writableResource.addRequirement(requirement);
        }
    }

    @Override
    public Capability createCapability(String name) {
        return m_writableResource.createCapability(name);
    }

    @Override
    public Requirement createRequirement(String name) {
        return m_writableResource.createRequirement(name);
    }

    @Override
    public boolean hasCapability(Capability capability) {
        return m_staticResource.hasCapability(capability) || m_writableResource.hasCapability(capability);
    }

    @Override
    public boolean hasRequirement(Requirement requirement) {
        return m_staticResource.hasRequirement(requirement) || m_writableResource.hasRequirement(requirement);
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
    
    public Resource getWritableResource() {
        return m_writableResource;
    }

    @Override
    public Resource getStaticResource() {
        return m_staticResource;
    }
}
