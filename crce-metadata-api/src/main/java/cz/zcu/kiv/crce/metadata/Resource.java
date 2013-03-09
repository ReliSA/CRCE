package cz.zcu.kiv.crce.metadata;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Resource represents an artifact and it's OBR metadata.
 * 
 * <p>An unique identificator of a resource is it's symbolic name and version.
 * 
 * <p>Resource have capabilities, requirements, properties and categories.
 * 
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Resource {

    @CheckForNull
    Repository getRepository();

    @Nonnull
    List<Capability> getCapabilities();

    @Nonnull
    List<Capability> getRootCapabilities();

    @Nonnull
    List<Capability> getCapabilities(@Nonnull String namespace);

    @Nonnull
    List<Requirement> getRequirements();

    @Nonnull
    List<Requirement> getRequirements(@Nonnull String namespace);
    
    boolean hasCapability(@Nonnull Capability capability);

    boolean hasRequirement(@Nonnull Requirement requirement);
    
    /* --- setters --- */

    void addCapability(@Nonnull Capability capability);

    void addRequirement(@Nonnull Requirement requirement);

    void removeCapability(@Nonnull Capability capability);
    
    void removeRequirement(@Nonnull Requirement requirement);
    
    void setRepository(@Nonnull WritableRepository repository);
}
