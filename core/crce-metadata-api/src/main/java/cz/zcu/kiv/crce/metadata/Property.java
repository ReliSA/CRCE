package cz.zcu.kiv.crce.metadata;


import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 * @param <T>
 */
public interface Property<T extends PropertyProvider<T>> extends AttributeProvider, EqualityComparable<Property<T>>, Entity {

    @Nonnull
    String getId();

    @Nonnull
    String getNamespace();

    @CheckForNull
    T getParent();

    void setParent(@Nullable T parent);
}
