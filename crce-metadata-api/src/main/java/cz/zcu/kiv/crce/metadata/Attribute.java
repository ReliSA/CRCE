package cz.zcu.kiv.crce.metadata;

import javax.annotation.Nonnull;

/**
 * 
 * @param <T> 
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Attribute<T> {

    @Nonnull
    T getValue();
    
    @Nonnull
    String getStringValue();
    
    @Nonnull
    DataType<T> getDataType();
}
