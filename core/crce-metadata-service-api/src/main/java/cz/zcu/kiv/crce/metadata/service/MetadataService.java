package cz.zcu.kiv.crce.metadata.service;

import java.net.URI;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface MetadataService {

    @Nonnull
    Capability getIdentity(Resource resource);

    @Nonnull
    String getPresentationName(Resource resource);

    void setPresentationName(Resource resource, String name);

    @Nonnull
    String getPresentationName(Capability capability);

    @Nonnull
    String getPresentationName(Requirement requirement);

    @Nonnull
    String getPresentationName(Property property);

    @Nonnull
    String getPresentationName(Attribute<?> attribute);

    @Nonnull
    String getExternalId( @Nonnull Resource resource);

    void setExternalId(@Nonnull Resource resource, String externalId);

    @Nonnull
    URI getUri(Resource resource);

    void setUri(Resource resource, URI uri);

    /**
     *
     * @param resource
     * @param uri
     * @throws IllegalArgumentException If the given URI syntax is not valid.
     */
    void setUri(Resource resource, String uri) throws IllegalArgumentException;

    @Nonnull
    String getFileName(Resource resource);

    void setFileName(Resource resource, String fileName);

    long getSize(Resource resource);

    void setSize(Resource resource, long size);

    @Nonnull
    List<String> getCategories(Resource resource);

    void addCategory(Resource resource, String category);

    void removeCategory(Resource resource, String category);

    /**
     * Safely adds the given capability to the resource including capability children.
     * @param resource
     * @param capability
     */
    void addRootCapability(Resource resource, Capability capability);

    /**
     * Safely removes capability from the resource including capability children.
     * @param resource
     * @param capability
     */
    void removeCapability(Resource resource, Capability capability);

    /**
     * Safely adds the given child capability into children of the parent one including
     * fixing of references to the parent resource.
     * @param parent
     * @param child
     */
//    void addChild(Capability parent, Capability child);

    void addRequirement(Resource resource, Requirement requirement);

    void removeRequirement(Resource resource, Requirement requirement);

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
    Capability getSingletonCapability(Resource resource, String namespace);

    /**
     * Creates requirement for particular resource name (crce.identity name)
     * @param name desired value
     * @return requirement enforcing the constraint
     */
    Requirement createIdentityRequirement(String name);

    /**
     * Creates requirement for particular resource name (crce.identity name) and
     * version (crce.identity.version)
     * @param name desired name value
     * @param version desired version value
     * @return requirement enforcing the constraints
     */
    Requirement createIdentityRequirement(String name, String version);
}
