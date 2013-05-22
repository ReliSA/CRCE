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
    String getNamespace();

    @CheckForNull
    Resource getResource();

    void setResource(@Nullable Resource resource);

    @CheckForNull
    Requirement getParent();

    boolean setParent(@Nullable Requirement parent);

    boolean addNestedRequirement(@Nonnull Requirement requirement);

    boolean removeNestedRequirement(@Nonnull Requirement requirement);

    @Nonnull
    List<Requirement> getNestedRequirements();

    @CheckForNull
    <T> Attribute<T> getAttribute(@Nonnull AttributeType<T> type);

    <T> boolean setAttribute(@Nonnull AttributeType<T> type, @CheckForNull T value);

    <T> boolean setAttribute(@Nonnull AttributeType<T> type, @CheckForNull T value, @Nonnull Operator operator);

    // TODO is this method AttributeType implementation-safe?
    <T> boolean setAttribute(@Nonnull String name, @Nonnull Class<T> type, @Nonnull T value, @Nonnull Operator operator);

    <T> boolean setAttribute(@Nonnull Attribute<T> attribute);
}
