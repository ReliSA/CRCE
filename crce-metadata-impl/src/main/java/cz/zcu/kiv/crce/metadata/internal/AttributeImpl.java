package cz.zcu.kiv.crce.metadata.internal;

import java.util.Objects;
import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;

/**
 *
 * @param <T>
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class AttributeImpl<T> implements Attribute<T> {

    private T value;
    private AttributeType<T> type;

    public AttributeImpl(@Nonnull AttributeType<T> type, @Nonnull T value) {
        this.value = value;
        this.type = type;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public String getStringValue() {
        return String.valueOf(value);
    }

    @Override
    public AttributeType<T> getAttributeType() {
        return type;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.value);
        hash = 97 * hash + Objects.hashCode(this.type);
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
        final AttributeImpl other = (AttributeImpl) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        return true;
    }
}
