package cz.zcu.kiv.crce.metadata;

import javax.annotation.Nonnull;

/**
 *
 * @param <T> 
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface AttributeType<T> {

    @Nonnull
    String getName();

    @Nonnull
    Class<T> getType();
}
