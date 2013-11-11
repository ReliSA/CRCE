package cz.zcu.kiv.crce.metadata.internal;

import java.util.Objects;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Property;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 * @param <T>
 */
public class PropertyImpl<T> extends AttributeProviderImpl implements Property<T> {

    private static final long serialVersionUID = -7003533524061344584L;

    private final String id;
    private String namespace = null;
    private T parent;

    public PropertyImpl(@Nonnull String namespace, @Nonnull String id) {
        this.namespace = namespace;
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public T getParent() {
        return parent;
    }

    @Override
    public void setParent(T parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PropertyImpl<?> other = (PropertyImpl<?>) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
        return hash;
    }
}
