package cz.zcu.kiv.crce.metadata;

import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Represents a requirement to a capability with the same name.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface Requirement extends DirectiveProvider, EqualityComparable<Requirement>, Entity {

    @Nonnull
    String getId();

    @Nonnull
    String getNamespace();

    @CheckForNull
    Resource getResource();

    void setResource(@Nullable Resource resource);

    @CheckForNull
    Requirement getParent();

    boolean setParent(@Nullable Requirement parent);

    boolean addChild(Requirement requirement);

    boolean removeChild(Requirement requirement);

    @Nonnull
    List<Requirement> getChildren();

    @Nonnull
    List<Attribute<?>> getAttributes();

    @Nonnull
    Map<String, List<Attribute<?>>> getAttributesMap();

    @Nonnull
    <T> List<Attribute<T>> getAttributes(AttributeType<T> type);

    <T> boolean addAttribute(AttributeType<T> type, T value);

    <T> boolean addAttribute(AttributeType<T> type, T value, Operator operator);

    // TODO is this method AttributeType implementation-safe?
    <T> boolean addAttribute(String name, Class<T> type, T value);

    <T> boolean addAttribute(String name, Class<T> type, T value, Operator operator);

    <T> boolean addAttribute(Attribute<T> attribute);
}
