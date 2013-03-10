package cz.zcu.kiv.crce.metadata.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class PropertyImpl extends AbstractEntityBase implements Property {

    private String namespace = null;
    private Resource resource = null;
    private Property parent = null;
    private List<Property> children = new ArrayList<>();
    
    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public Resource getResource() {
        return resource;
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
        if (!children.contains(property)) {
            property.setParent(this);
            return children.add(property);
        }
        return false;
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
        if (!Objects.equals(this.namespace, other.namespace)) {
            return false;
        }
        if (!Objects.equals(this.resource, other.resource)) {
            return false;
        }
        if (!Objects.equals(this.parent, other.parent)) {
            return false;
        }
        if (!Objects.equals(this.children, other.children)) {
            return false;
        }
        return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.namespace);
        hash = 97 * hash + Objects.hashCode(this.resource);
        hash = 97 * hash + Objects.hashCode(this.parent);
        hash = 97 * hash + Objects.hashCode(this.children);
        return 97 * hash + super.hashCode();
    }
}
