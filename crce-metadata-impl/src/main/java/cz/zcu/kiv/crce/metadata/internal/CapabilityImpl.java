package cz.zcu.kiv.crce.metadata.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.EqualityLevel;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 * Implementation of metadata <code>Capability</code> interface.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class CapabilityImpl extends AbstractEntityBase implements Capability {

    private static final long serialVersionUID = -813453152194473221L;

    private final String id;
    private final List<Capability> children = new ArrayList<>();

    private String namespace = null;
    private Resource resource = null;
    private Capability parent = null;

    public CapabilityImpl(@Nonnull String namespace, @Nonnull String id) {
        this.namespace = namespace.intern();
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
        return children.add(capability);
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
        if (getClass() == obj.getClass() || obj instanceof Capability) {
            final Capability other = (Capability) obj;
            return Objects.equals(this.id, other.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equalsTo(Capability other, EqualityLevel level) {
        if (other == null) {
            return false;
        }
        switch (level) {
            case KEY:
                return id.equals(other.getId());

            case SHALLOW_NO_KEY:
                if (!Objects.equals(this.namespace, other.getNamespace())) {
                    return false;
                }
                if (!Objects.equals(this.attributesMap, other.getAttributesMap())) {
                    return false;
                }
                if (!Objects.equals(this.directivesMap, other.getDirectives())) {
                    return false;
                }
                return true;

            case SHALLOW_WITH_KEY:
                if (!Util.equalsTo(this, other, EqualityLevel.KEY)) {
                    return false;
                }
                return this.equalsTo(other, EqualityLevel.SHALLOW_NO_KEY);

            case DEEP_NO_KEY:
                if (!Util.equalsTo(this, other, EqualityLevel.SHALLOW_NO_KEY)) {
                    return false;
                }
                if (!Util.equalsTo(resource, other.getResource(), EqualityLevel.SHALLOW_NO_KEY)) {
                    return false;
                }
                if (!Util.equalsTo(parent, other.getParent(), EqualityLevel.SHALLOW_NO_KEY)) {
                    return false;
                }
                if (!Util.equalsTo(children, other.getChildren(), EqualityLevel.DEEP_NO_KEY)) {
                    return false;
                }
                return true;

            case DEEP_WITH_KEY:
                if (!Util.equalsTo(this, other, EqualityLevel.SHALLOW_WITH_KEY)) {
                    return false;
                }
                if (!Util.equalsTo(resource, other.getResource(), EqualityLevel.SHALLOW_WITH_KEY)) {
                    return false;
                }
                if (!Util.equalsTo(parent, other.getParent(), EqualityLevel.SHALLOW_WITH_KEY)) {
                    return false;
                }
                if (!Util.equalsTo(children, other.getChildren(), EqualityLevel.DEEP_WITH_KEY)) {
                    return false;
                }
                return true;

            default:
                return equalsTo(other, EqualityLevel.KEY);
        }
    }
}
