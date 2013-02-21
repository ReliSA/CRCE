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
public interface Resource extends AttributeProvider {

    @CheckForNull
    public Repository getRepository();

    @Nonnull
    public List<Capability> getCapabilities();

    @Nonnull
    public List<Capability> getRootCapabilities();

    @Nonnull
    public List<Capability> getCapabilities(@Nonnull String namespace);

    @Nonnull
    public List<Requirement> getRequirements();

    @Nonnull
    public List<Requirement> getRequirements(@Nonnull String namespace);
    
    public boolean hasCapability(@Nonnull Capability capability);

    public boolean hasRequirement(@Nonnull Requirement requirement);
    
    /* --- setters --- */

    public void addCapability(@Nonnull Capability capability);

    public void addRequirement(@Nonnull Requirement requirement);

    public void removeCapability(@Nonnull Capability capability);
    
    public void removeRequirement(@Nonnull Requirement requirement);
    
    @Nonnull
    public Capability createCapability(@Nonnull String namespace);

    @Nonnull
    public Requirement createRequirement(@Nonnull String namespace);

    public void setRepository(@Nonnull WritableRepository repository);
}
