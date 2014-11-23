package cz.zcu.kiv.crce.metadata.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.DirectiveProvider;
import cz.zcu.kiv.crce.metadata.EqualityLevel;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.PropertyProvider;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;

/**
 * Implementation of metadata <code>Capability</code> interface.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class CapabilityImpl extends AttributeProviderImpl implements Capability, Comparable<Capability> {

    private static final long serialVersionUID = -813453152194473221L;

    private final String id;
    private final List<Capability> children = new ArrayList<>();

    private final Map<String, List<Requirement>> allRequirements = new HashMap<>();
    private final PropertyProvider<Capability> propertyProvider = new PropertyProviderImpl<>();
    private final DirectiveProvider directiveProvider = new DirectiveProviderImpl();

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

    // delegated methods

    @Override
    public List<Property<Capability>> getProperties() {
        return propertyProvider.getProperties();
    }

    @Override
    public List<Property<Capability>> getProperties(String namespace) {
        return propertyProvider.getProperties(namespace);
    }

    @Override
    public boolean hasProperty(Property<Capability> property) {
        return propertyProvider.hasProperty(property);
    }

    @Override
    public void addProperty(Property<Capability> property) {
        propertyProvider.addProperty(property);
    }

    @Override
    public void removeProperty(Property<Capability> property) {
        propertyProvider.removeProperty(property);
    }

    @Override
    public String getDirective(String name) {
        return directiveProvider.getDirective(name);
    }

    @Override
    public Map<String, String> getDirectives() {
        return directiveProvider.getDirectives();
    }

    @Override
    public boolean setDirective(String name, String directive) {
        return directiveProvider.setDirective(name, directive);
    }

    @Override
    public boolean unsetDirective(String name) {
        return directiveProvider.unsetDirective(name);
    }

    @Override
    public List<Requirement> getRequirements() {
        List<Requirement> result = new ArrayList<>();
        for (List<Requirement> requirements : allRequirements.values()) {
            result.addAll(requirements);
        }
        return result;
    }

    @Override
    public List<Requirement> getRequirements(String namespace) {
        List<Requirement> result = allRequirements.get(namespace);
        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    @Override
    public boolean hasRequirement(Requirement requirement) {
        List<Requirement> requirements = allRequirements.get(requirement.getNamespace());
        if (requirements != null) {
            return requirements.contains(requirement);
        }
        return false;
    }

    @Override
    public void addRequirement(Requirement requirement) {
        List<Requirement> requirements = allRequirements.get(requirement.getNamespace());
        if (requirements == null) {
            requirements = new ArrayList<>();
            allRequirements.put(requirement.getNamespace(), requirements);
        }
        requirements.add(requirement);
    }

    @Override
    public void removeRequirement(Requirement requirement) {
        List<Requirement> requirements = allRequirements.get(requirement.getNamespace());
        if (requirements != null) {
            requirements.remove(requirement);
        }
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
                if (!Objects.equals(this.getDirectives(), other.getDirectives())) {
                    return false;
                }
                if (!Objects.equals(propertyProvider.getProperties(), other.getProperties())) {
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
                if (!Util.equalsTo(getRequirements(), other.getRequirements(), EqualityLevel.DEEP_NO_KEY)) {
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
                if (!Util.equalsTo(getRequirements(), other.getRequirements(), EqualityLevel.DEEP_WITH_KEY)) {
                    return false;
                }
                return true;

            default:
                return equalsTo(other, EqualityLevel.KEY);
        }
    }

    @Override
    public int compareTo(Capability o) {
        return id.compareTo(o.getId());
    }

    @Override
    public String toString() {
        return MetadataFactoryImpl.toString(this);
    }
}
