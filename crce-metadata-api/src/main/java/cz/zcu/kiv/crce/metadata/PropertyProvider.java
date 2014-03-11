package cz.zcu.kiv.crce.metadata;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 * @param <T>
 */
@ParametersAreNonnullByDefault
public interface PropertyProvider<T extends PropertyProvider<T>> extends EqualityComparable<T>, Entity, Serializable {

    @Nonnull
    List<Property<T>> getProperties();

    @Nonnull
    List<Property<T>> getProperties(String namespace);

    boolean hasProperty(Property<T> property);

    void addProperty(Property<T> property);

    void removeProperty(Property<T> property);
}
