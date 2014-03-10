package cz.zcu.kiv.crce.metadata.impl;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.type.Version;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class GenericAttributeType implements AttributeType<Object> {

    private static final long serialVersionUID = -4513168310428826310L;

    private String name;
    private Class<?> type;

    public GenericAttributeType(String name, String type) {
        this.name = name;
        switch (type) {
            default:
            case "String":
            case "java.lang.String":
                this.type = String.class;
                break;

            case "Long":
            case "java.lang.Long":
                this.type = Long.class;
                break;

            case "Double":
            case "java.lang.Double":
                this.type = Double.class;
                break;

            case "Version":
            case "cz.zcu.kiv.crce.metadata.type.Version":
                this.type = Version.class;
                break;

            case "List":
            case "java.util.List":
                this.type = List.class;
                break;

            case "URI":
            case "java.net.URI":
                this.type = URI.class;
                break;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class getType() {
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
