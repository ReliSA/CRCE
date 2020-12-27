package cz.zcu.kiv.crce.metadata;

import java.io.Serializable;

import javax.annotation.Nonnull;

/**
 *
 * @param <T>
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface AttributeType<T> extends Serializable {

    @Nonnull
    String getName();

    @Nonnull
    Class<T> getType();
}
