package cz.zcu.kiv.crce.metadata;

import java.util.Objects;

/**
 * 
 * @param <T> Data type of property.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class SimpleAttributeType<T> implements AttributeType<T> {

    private String name;
    private Class<T> type;

    public SimpleAttributeType(String name, Class<T> type) {
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.type);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SimpleAttributeType other = (SimpleAttributeType) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        return true;
    }
}
