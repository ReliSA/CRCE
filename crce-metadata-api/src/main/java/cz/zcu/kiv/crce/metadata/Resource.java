package cz.zcu.kiv.crce.metadata;

import java.net.URI;
import java.util.Map;
import org.osgi.framework.Version;

/**
 *
 * @author kalwi
 */
public interface Resource extends PropertyProvider {

    String getId();

    String getSymbolicName();

    Version getVersion();

    String getPresentationName();

    URI getUri();

    /**
     * Returns the resource size in bytes or -1 if size is unknown.
     * @return the resource size.
     */
    long getSize();

    String[] getCategories();

    Capability[] getCapabilities();

    Requirement[] getRequirements();

    Map<String, String> getPropertiesMap();

    boolean hasCategory(String category);

    boolean hasCapability(Capability capability);

    boolean hasRequirement(Requirement requirement);


    void setSymbolicName(String name);

    void setPresentationName(String name);

    void setVersion(Version version);

    void setVersion(String version);

    void addCategory(String category);

    void addCapability(Capability capability);

    void addRequirement(Requirement requirement);

    Capability createCapability(String name);

    Requirement createRequirement(String name);

    /**
     * Sets resource size.
     * @param size size in bytes to set.
     */
    void setSize(long size);

    void setUri(URI uri);

    boolean isWritable();
    
    void unsetWritable();
    
    String asString();
}
