package cz.zcu.kiv.crce.metadata;

import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Represents an Capability.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@ParametersAreNonnullByDefault
public interface Capability
        extends AttributeProvider, DirectiveProvider, PropertyProvider<Capability>, EqualityComparable<Capability>, Entity {

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

    boolean addChild(Capability capability);

    boolean removeChild(Capability capability);

    @Nonnull
    List<Capability> getChildren();
}
