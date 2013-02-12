package cz.zcu.kiv.crce.metadata;

/**
 *
 * @param <T> 
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public interface DataType<T> {

    public String getName();

    public Class<T> getType();
}
