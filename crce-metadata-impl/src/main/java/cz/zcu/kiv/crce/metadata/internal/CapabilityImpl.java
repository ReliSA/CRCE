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
 * @author Jiri Kucera (kalwi@students.zcu.cz, jiri.kucera@kalwi.eu)
 */
public class CapabilityImpl extends AbstractEntityBase implements Capability {

    private String namespace = null;
    private Resource resource = null;
    private Capability parent = null;
    private List<Capability> children = new ArrayList<>();
    
    public CapabilityImpl(String namespace) {
        this.namespace = namespace.intern();
    }

    @Override
    public synchronized String getNamespace() {
        return namespace;
    }

    @Override
    public synchronized Resource getResource() {
        return resource;
    }

    @Override
    public synchronized Capability getParent() {
        return parent;
    }

    @Override
    public synchronized boolean setParent(Capability parent) {
        this.parent = parent;
        return true;
    }

    @Override
    public synchronized boolean addChild(Capability capability) {
        return children.add(capability);
    }

    @Override
    public synchronized boolean removeChild(Capability capability) {
        return children.remove(capability);
    }

    @Override
    public synchronized List<Capability> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public synchronized boolean equals(Object obj) {
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
    public synchronized int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.namespace);
        hash = 97 * hash + Objects.hashCode(this.resource);
        hash = 97 * hash + Objects.hashCode(this.parent);
        hash = 97 * hash + Objects.hashCode(this.children);
        return 97 * hash + super.hashCode();
    }
}
