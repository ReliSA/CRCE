package cz.zcu.kiv.crce.metadata;

import java.io.Serializable;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Resource represents an artifact and it's metadata.
 *
 * <p>An unique identificator of a resource is ID.
 *
 * <p>Resource have capabilities, requirements, properties and categories.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Resource extends Serializable {

    @Nonnull
    String getId();

    @CheckForNull
    Repository getRepository();

    @Nonnull
    List<Capability> getCapabilities();

    @Nonnull
    List<Capability> getCapabilities(@Nonnull String namespace);

    @Nonnull
    List<Capability> getRootCapabilities();

    @Nonnull
    List<Capability> getRootCapabilities(@Nonnull String namespace);

    @Nonnull
    List<Requirement> getRequirements();

    @Nonnull
    List<Requirement> getRequirements(@Nonnull String namespace);

    @Nonnull
    List<Property> getProperties();

    @Nonnull
    List<Property> getProperties(@Nonnull String namespace);

    boolean hasCapability(@Nonnull Capability capability);

    boolean hasRequirement(@Nonnull Requirement requirement);

    boolean hasProperty(@Nonnull Property property);

    /* --- setters --- */

    /**
     * Adds the given capability to the list of all capabilities.
     * <p>Note: This method doesn add the capability to the list of root capabilities.
     * @param capability Capability to be added.
     */
    void addCapability(@Nonnull Capability capability);

    /**
     * Adds the given capability to the list of root capabilities.
     * @param capability Capability to be added.
     */
    void addRootCapability(@Nonnull Capability capability);

    void addRequirement(@Nonnull Requirement requirement);

    void addProperty(@Nonnull Property property);

    void removeCapability(@Nonnull Capability capability);

    void removeRootCapability(@Nonnull Capability capability);

    void removeRequirement(@Nonnull Requirement requirement);

    void removeProperty(@Nonnull Property property);

    void setRepository(@Nonnull Repository repository);
}
