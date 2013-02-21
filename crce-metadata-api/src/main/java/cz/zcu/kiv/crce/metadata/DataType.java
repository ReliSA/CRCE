package cz.zcu.kiv.crce.metadata;

import javax.annotation.Nonnull;

/**
 *
 * @param <T> 
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface DataType<T> {

    @Nonnull
    public String getName();

    @Nonnull
    public Class<T> getType();
}
