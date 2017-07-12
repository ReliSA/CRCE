package cz.zcu.kiv.crce.metadata.internal;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Operator;

/**
 *
 * @param <T>
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class AttributeImpl<T> implements Attribute<T> {

    private static final long serialVersionUID = 3231029096691170916L;

    private T value;
    private AttributeType<T> type;
    private Operator operator;

    public AttributeImpl(@Nonnull AttributeType<T> type, @Nonnull T value, Operator operator) {
        this.value = value;
        this.type = type;
        this.operator = operator != null ? operator : Operator.EQUAL;
    }

    public AttributeImpl(@Nonnull AttributeType<T> type, @Nonnull T value) {
        this(type, value, Operator.EQUAL);
    }

    @Override
    public String getName() {
        return type.getName();
    }

    @Override
    public Class<T> getType() {
        return type.getType();
    }

    @Override
    public T getValue() {
        if (value == null) {
            throw new IllegalStateException("Value is null for " + type.getName());
        }
        return value;
    }

    @Override
    public String getStringValue() {
        if (value == null) {
            throw new IllegalStateException("Value is null for " + type.getName());
        }
        if (List.class.equals(type.getType())) {
            StringBuilder sb = new StringBuilder();
            Iterator<?> iterator = ((List<?>) value).iterator();
            if (iterator.hasNext()) {
                sb.append(iterator.next());
            }
            while (iterator.hasNext()) {
                sb.append(",").append(iterator.next());
            }
            return sb.toString();
        }
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
        hash = 97 * hash + (this.operator != null ? this.operator.hashCode() : 0);
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
        final AttributeImpl<?> other = (AttributeImpl) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        return this.operator == other.operator;
    }

    @Override
    public Operator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return MetadataFactoryImpl.toString(this);
    }
}
