package cz.zcu.kiv.crce.metadata.service;

import java.net.URI;
import java.util.List;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.PropertyProvider;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface MetadataService {

    @Nonnull
    String getIdentityNamespace();

    @Nonnull
    String getPresentationName(@Nonnull Resource resource);

    void setPresentationName(@Nonnull Resource resource, @Nonnull String name);

    @Nonnull
    String getPresentationName(@Nonnull Capability capability);

    @Nonnull
    String getPresentationName(@Nonnull Requirement requirement);

    @Nonnull
    <T extends PropertyProvider<T>> String getPresentationName(@Nonnull Property<T> property);

    @Nonnull
    String getPresentationName(@Nonnull Attribute<?> attribute);

    @Nonnull
    URI getUri(@Nonnull Resource resource);

    @Nonnull
    URI getRelativeUri(@Nonnull Resource resource);

    void setUri(@Nonnull Resource resource, @Nonnull URI uri);

    /**
     *
     * @param resource
     * @param uri
     * @throws IllegalArgumentException If the given URI syntax is not valid.
     */
    void setUri(@Nonnull Resource resource, @Nonnull String uri) throws IllegalArgumentException;

    @Nonnull
    String getFileName(@Nonnull Resource resource);

    void setFileName(@Nonnull Resource resource, @Nonnull String fileName);

    long getSize(@Nonnull Resource resource);

    void setSize(@Nonnull Resource resource, long size);

    @Nonnull
    List<String> getCategories(@Nonnull Resource resource);

    void addCategory(@Nonnull Resource resource, @Nonnull String category);

    void removeCategory(@Nonnull Resource resource, @Nonnull String category);

    /**
     * Safely adds the given capability to the resource including capability children.
     * @param resource
     * @param capability
     */
    void addRootCapability(@Nonnull Resource resource, @Nonnull Capability capability);

    /**
     * Safely removes capability from the resource including capability children.
     * @param resource
     * @param capability
     */
    void removeCapability(@Nonnull Resource resource, @Nonnull Capability capability);

    /**
     * Safely adds the given child capability into children of the parent one including
     * fixing of references to the parent resource.
     * @param parent
     * @param child
     */
    void addChild(@Nonnull Capability parent, @Nonnull Capability child);

    void addRequirement(@Nonnull Resource resource, @Nonnull Requirement requirement);

    void removeRequirement(@Nonnull Resource resource, @Nonnull Requirement requirement);

    /**
     * Returns a capability of the given namespace which exists only as a singleton
     * in scope of the given resource.<p>
     * If the requested capability doesn't exist, then a new one is created and set as
     * a root capability.
     *
     * @param resource
     * @param namespace
     * @return
     */
    @Nonnull
    Capability getSingletonCapability(@Nonnull Resource resource, @Nonnull String namespace);
}
