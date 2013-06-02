package cz.zcu.kiv.crce.metadata.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class PropertyImpl extends AbstractEntityBase implements Property {

    private static final long serialVersionUID = -7003533524061344584L;

    private final String id;
    private String namespace = null;
    private Resource resource = null;
    private Property parent = null;
    private final List<Property> children = new ArrayList<>();

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
    public Resource getResource() {
        return resource;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public Property getParent() {
        return parent;
    }

    @Override
    public boolean setParent(Property parent) {
        this.parent = parent;
        return true;
    }

    @Override
    public boolean addChild(Property property) {
        return children.add(property);
    }

    @Override
    public boolean removeChild(Property property) {
        return children.remove(property);
    }

    @Override
    public List<Property> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PropertyImpl other = (PropertyImpl) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
        return hash;
    }
}
