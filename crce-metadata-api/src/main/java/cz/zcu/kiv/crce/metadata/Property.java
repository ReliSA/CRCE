package cz.zcu.kiv.crce.metadata;

import java.io.Serializable;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Property extends Serializable {

    @Nonnull
    String getNamespace();

    @CheckForNull
    Resource getResource();

    void setResource(@Nullable Resource resource);

    @CheckForNull
    Property getParent();

    boolean setParent(@Nullable Property parent);

    boolean addChild(@Nonnull Property capability);

    boolean removeChild(@Nonnull Property capability);

    @Nonnull
    List<Property> getChildren();
}
