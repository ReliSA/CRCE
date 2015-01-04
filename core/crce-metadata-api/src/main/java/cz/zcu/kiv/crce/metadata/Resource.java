package cz.zcu.kiv.crce.metadata;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Resource represents an artifact and it's metadata.
 *
 * <p>An unique identificator of a resource is ID.
 *
 * <p>Resource have capabilities, requirements, properties and categories.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface Resource extends PropertyProvider, RequirementProvider, EqualityComparable<Resource>, Entity {

    @Nonnull
    String getId();

    @Nonnull
    List<Capability> getCapabilities();

    @Nonnull
    List<Capability> getCapabilities(String namespace);

    @Nonnull
    List<Capability> getRootCapabilities();

    @Nonnull
    List<Capability> getRootCapabilities(String namespace);

    boolean hasCapability(Capability capability);

    /* --- setters --- */

    /**
     * Adds the given capability to the list of all capabilities.
     * <p>Note: This method doesn't add the capability to the list of root capabilities.
     * @param capability Capability to be added.
     */
    void addCapability(Capability capability);

    /**
     * Adds the given capability to the list of root capabilities.
     * @param capability Capability to be added.
     */
    void addRootCapability(Capability capability);

    void removeCapability(Capability capability);

    void removeRootCapability(Capability capability);
}
