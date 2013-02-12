package cz.zcu.kiv.crce.metadata;

import java.util.List;

/**
 * Resource represents an artifact and it's OBR metadata.
 * 
 * <p>An unique identificator of a resource is it's symbolic name and version.
 * 
 * <p>Resource have capabilities, requirements, properties and categories.
 * 
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Resource extends AttributeProvider {

    Repository getRepository();

    List<Capability> getCapabilities();

    List<Capability> getCapabilities(String namespace);

    List<Requirement> getRequirements();

    List<Requirement> getRequirements(String namespace);
    
    boolean hasCapability(Capability capability);

    boolean hasRequirement(Requirement requirement);
    
    /* --- setters --- */

    void addCapability(Capability capability);

    void addRequirement(Requirement requirement);

    void removeCapability(Capability capability);
    
    void removeRequirement(Requirement requirement);
    
    Capability createCapability(String namespace);

    Requirement createRequirement(String namespace);

    void setRepository(WritableRepository repository);
}
