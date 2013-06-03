package cz.zcu.kiv.crce.metadata;

import java.io.Serializable;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents an Capability.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Capability extends AttributeProvider, DirectiveProvider, EqualityComparable<Capability>, Serializable {

    @Nonnull
    String getId();

    @Nonnull
    String getNamespace();

    @CheckForNull
    Resource getResource();

    void setResource(@Nullable Resource resource);

    @CheckForNull
    Capability getParent();

    boolean setParent(@Nullable Capability parent);

    boolean addChild(@Nonnull Capability capability);

    boolean removeChild(@Nonnull Capability capability);

    @Nonnull
    List<Capability> getChildren();
}
