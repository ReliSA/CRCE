package cz.zcu.kiv.crce.metadata;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nonnull;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 * @param <T>
 */
public interface PropertyProvider<T> extends Serializable {

    @Nonnull
    List<Property<T>> getProperties();

    @Nonnull
    List<Property<T>> getProperties(@Nonnull String namespace);

    boolean hasProperty(@Nonnull Property<T> property);

    void addProperty(@Nonnull Property<T> property);

    void removeProperty(@Nonnull Property<T> property);
}
