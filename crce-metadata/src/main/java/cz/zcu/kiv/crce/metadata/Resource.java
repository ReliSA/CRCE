package cz.zcu.kiv.crce.metadata;

import java.util.Map;
import org.osgi.framework.Version;

/**
 *
 * @author kalwi
 */
public interface Resource {
    
    String getId();

    String getSymbolicName();

    Version getVersion();

    String getPresentationName();

    String getURI();

    long getSize();

    String[] getCategories();

    Capability[] getCapabilities();

    Requirement[] getRequirements();
    
    Map getProperties();
    
    boolean hasCategory(String category);
    
    boolean hasCapability(Capability capability);
    
    boolean hasRequirement(Requirement requirement);
    

    void setSymbolicName(String name);
    
    void setVersion(Version version);
    
    void setVersion(String version);
    
    void setCategory(String category);

    void addCapability(Capability capability);

    void addRequirement(Requirement requirement);
    
    Capability createCapability(String name);
    
    Requirement createRequirement(String name);
    
}
