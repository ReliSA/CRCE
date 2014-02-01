package cz.zcu.kiv.crce.metadata;

import javax.annotation.Nonnull;

/**
 *
 * @param <T>
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Attribute<T> extends Entity {

    @Nonnull
    String getName();

    @Nonnull
    Class<T> getType();

    @Nonnull
    T getValue();

    @Nonnull
    String getStringValue();

    @Nonnull
    AttributeType<T> getAttributeType();

    @Nonnull
    Operator getOperator();
}
