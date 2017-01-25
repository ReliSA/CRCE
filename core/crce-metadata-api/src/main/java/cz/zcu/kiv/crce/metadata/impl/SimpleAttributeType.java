package cz.zcu.kiv.crce.metadata.impl;

import cz.zcu.kiv.crce.metadata.AttributeType;
import java.util.Objects;

/**
 *
 * @param <T> Data type of property.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class SimpleAttributeType<T> implements AttributeType<T> {
    
    private static final long serialVersionUID = 3314060433636463265L;

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
        if (!(obj instanceof AttributeType)) {
            return false;
        }
        final AttributeType<?> other = (AttributeType) obj;
        if (!Objects.equals(this.name, other.getName())) {
            return false;
        }
        return Objects.equals(this.type, other.getType());
    }
}
