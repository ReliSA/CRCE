package cz.zcu.kiv.crce.metadata;


import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Creates empty metadata entities.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface MetadataFactory {

    /**
     * Creates an empty resource with generated unique ID.
     * @return An empty resource.
     */
    @Nonnull
    Resource createResource();

    /**
     * Creates an empty resource with the given ID.
     * @param id Unique identifier.
     * @return An empty resource.
     */
    @Nonnull
    Resource createResource(String id);

    /**
     * Creates an empty requirement with given name.
     * @param namespace Name of created requirement.
     * @return An empty capability.
     */
    @Nonnull
    Requirement createRequirement(String namespace);

    /**
     * Creates an empty requirement with given name.
     * @param namespace Name of created requirement.
     * @param id
     * @return An empty capability.
     */
    @Nonnull
    Requirement createRequirement(String namespace, String id);

    @Nonnull
    Capability createCapability(String namespace);

    @Nonnull
    Capability createCapability(String namespace, String id);

    @Nonnull
    <T extends PropertyProvider<T>> Property<T> createProperty(String namespace);

    @Nonnull
    <T extends PropertyProvider<T>> Property<T> createProperty(String namespace, String id);

    <T> Attribute<T> createAttribute(AttributeType<T> type, T value);

    <T> Attribute<T> createAttribute(AttributeType<T> type, T value, Operator operator);

    <T> Attribute<T> createAttribute(String name, Class<T> type, T value);

    <T> Attribute<T> createAttribute(String name, Class<T> type, T value, Operator operator);

    @Nonnull
    Repository createRepository(URI uri);

    @Nonnull
    Repository createRepository(URI uri, String id);

    /**
     * Clone the given resource and return it's deep copy.
     *
     * <p><i>Don't forget about that the implementation of the given resource
     * could be various, so you don't know whether the given resource is a plain
     * resource or some kind of combined resource (more resources acting as one
     * resource). The clone made by this method (if concrete implementation does
     * not specify it differently) is a plain resource so the inner structure
     * could be another then the structure of the given resource. Use this
     * method only if you are sure that you want to get a plain resource.</i>
     *
     * @param resource Resource to be clonned.
     * @return deep copy of resource.
     */
    @Nonnull
    Resource cloneResource(Resource resource);

    /**
     * Creates an empty capability with given name.
     * @param capability Cloned capability.
     * @return An empty capability.
     */
    @Nonnull
    Capability cloneCapability(Capability capability);

    @Nonnull
    Requirement cloneRequirement(Requirement requirement);

    <T> Attribute<T> cloneAttribute(Attribute<T> attribute);

    @Nonnull
    <T extends PropertyProvider<T>> Property<T> cloneProperty(Property<T> property);
}
