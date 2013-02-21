package cz.zcu.kiv.crce.metadata;

import javax.annotation.Nonnull;

/**
 * 
 * @param <T> 
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Attribute<T> {

    @Nonnull
    public T getValue();
    
    @Nonnull
    public String getStringValue();
    
    @Nonnull
    public DataType<T> getDataType();
}
