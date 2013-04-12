package cz.zcu.kiv.crce.metadata.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 * Implementation of metadata <code>Capability</code> interface.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class CapabilityImpl extends AbstractEntityBase implements Capability {
    
    private static final long serialVersionUID = -813453152194473221L;

    private String namespace = null;
    private Resource resource = null;
    private Capability parent = null;
    private List<Capability> children = new ArrayList<>();

    public CapabilityImpl(String namespace) {
        this.namespace = namespace.intern();
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
    public Capability getParent() {
        return parent;
    }

    @Override
    public boolean setParent(Capability parent) {
        this.parent = parent;
        return true;
    }

    @Override
    public boolean addChild(Capability capability) {
        if (!children.contains(capability)) {
            capability.setParent(this);
            return children.add(capability);
        }
        return false;
    }

    @Override
    public boolean removeChild(Capability capability) {
        return children.remove(capability);
    }

    @Override
    public List<Capability> getChildren() {
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
        final CapabilityImpl other = (CapabilityImpl) obj;
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
