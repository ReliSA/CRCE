package cz.zcu.kiv.crce.metadata;


import java.net.URI;
import javax.annotation.Nonnull;

/**
 * Creates empty metadata entities.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
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
    Resource createResource(@Nonnull String id);

    /**
     * Creates an empty requirement with given name.
     * @param namespace Name of created requirement.
     * @return An empty capability.
     */
    @Nonnull
    Requirement createRequirement(@Nonnull String namespace);

    /**
     * Creates an empty requirement with given name.
     * @param namespace Name of created requirement.
     * @param id
     * @return An empty capability.
     */
    @Nonnull
    Requirement createRequirement(@Nonnull String namespace, @Nonnull String id);

    @Nonnull
    Capability createCapability(@Nonnull String namespace);

    @Nonnull
    Capability createCapability(@Nonnull String namespace, @Nonnull String id);

    @Nonnull
    <T extends EqualityComparable<T>> Property<T>createProperty(@Nonnull String namespace);

    @Nonnull
    <T extends EqualityComparable<T>> Property<T>createProperty(@Nonnull String namespace, @Nonnull String id);

    <T> Attribute<T> createAttribute(@Nonnull AttributeType<T> type, @Nonnull T value);

    <T> Attribute<T> createAttribute(@Nonnull AttributeType<T> type, @Nonnull T value, @Nonnull Operator operator);

    <T> Attribute<T> createAttribute(@Nonnull String name, @Nonnull Class<T> type, @Nonnull T value);

    <T> Attribute<T> createAttribute(@Nonnull String name, @Nonnull Class<T> type, @Nonnull T value, @Nonnull Operator operator);

    @Nonnull
    Repository createRepository(@Nonnull URI uri);

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
    Resource cloneResource(@Nonnull Resource resource);

    /**
     * Creates an empty capability with given name.
     * @param capability Cloned capability.
     * @return An empty capability.
     */
    @Nonnull
    Capability cloneCapability(@Nonnull Capability capability);

    @Nonnull
    Requirement cloneRequirement(@Nonnull Requirement requirement);

    <T> Attribute<T> cloneAttribute(@Nonnull Attribute<T> attribute);

    @Nonnull
    <T extends EqualityComparable<T>> Property<T> cloneProperty(@Nonnull Property<T> property);
}
