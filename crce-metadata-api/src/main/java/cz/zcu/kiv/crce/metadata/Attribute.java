package cz.zcu.kiv.crce.metadata;

/**
 * 
 * @param <T> 
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface Attribute<T> {

    public T getValue();
    
    public String getStringValue();
    
    public DataType<T> getDataType();
}
