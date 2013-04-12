package cz.zcu.kiv.crce.metadata.impl;

import cz.zcu.kiv.crce.metadata.AttributeType;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class ListAttributeType implements AttributeType<List<String>> {
    
    private static final long serialVersionUID = -8744638498345181727L;

    private final String name;
    private final Class<List<String>> type;

    @SuppressWarnings("unchecked")
    public ListAttributeType(String name) {
        this.name = name;
        this.type = (Class<List<String>>) (Class<?>) List.class;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<List<String>> getType() {
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
