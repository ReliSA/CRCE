package cz.zcu.kiv.crce.metadata;

/**
 * 
 * @param <T> Data type of property.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class SimpleDataType<T> implements AttributeType<T> {

    private String name;
    private Class<T> type;

    public SimpleDataType(String name, Class<T> type) {
        if (name == null) {
            throw new IllegalArgumentException("Name must be specified.");
        }
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<T> getType() {
        return type;
    }
}
