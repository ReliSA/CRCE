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
public class ConvertedResource implements Resource {

    public ConvertedResource(org.apache.felix.bundlerepository.Resource resource) {
        
    }
    
    @Override
    public String getId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getSymbolicName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Version getVersion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getPresentationName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getURI() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getSize() {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public boolean hasCapability(Capability capability) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasRequirement(Requirement requirement) {
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
    public void setVersion(String version) {
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

}
