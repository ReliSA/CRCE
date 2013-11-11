package cz.zcu.kiv.crce.metadata;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 * @param <T>
 */
public interface Property<T> extends AttributeProvider, Serializable {

    @Nonnull
    String getId();

    @Nonnull
    String getNamespace();

    @CheckForNull
    T getParent();

    void setParent(@Nullable T parent);
}
