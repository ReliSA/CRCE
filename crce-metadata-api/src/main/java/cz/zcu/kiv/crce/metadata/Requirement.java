package cz.zcu.kiv.crce.metadata;

import java.io.Serializable;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a requirement to a capability with the same name.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Requirement extends DirectiveProvider, Serializable {

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

    boolean addChild(@Nonnull Requirement requirement);

    boolean removeChild(@Nonnull Requirement requirement);

    @Nonnull
    List<Requirement> getChildren();

    @Nonnull
    <T> List<Attribute<T>> getAttributes(@Nonnull AttributeType<T> type);

    <T> boolean addAttribute(@Nonnull AttributeType<T> type, @CheckForNull T value);

    <T> boolean addAttribute(@Nonnull AttributeType<T> type, @CheckForNull T value, @Nonnull Operator operator);

    // TODO is this method AttributeType implementation-safe?
    <T> boolean addAttribute(@Nonnull String name, @Nonnull Class<T> type, @Nonnull T value, @Nonnull Operator operator);

    <T> boolean addAttribute(@Nonnull Attribute<T> attribute);
}
