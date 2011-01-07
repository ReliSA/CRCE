package cz.zcu.kiv.crce.metadata.internal;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import java.util.Map;
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
    private String m_uri;
    private long m_size;
    
    
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
        return m_version;
    }

    @Override
    public String getPresentationName() {
        return m_presentationName;
    }

    @Override
    public String getURI() {
        return m_uri;
    }

    @Override
    public long getSize() {
        return m_size;
    }

    @Override
    public String[] getCategories() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Capability[] getCapabilities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Requirement[] getRequirements() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map getProperties() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasCategory(String category) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setSymbolicName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setVersion(Version version) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCategory(String category) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addCapability(Capability capability) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addRequirement(Requirement requirement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Capability createCapability(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Requirement createRequirement(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasCapability(Capability capability) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasRequirement(Requirement requirement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
