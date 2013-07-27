package cz.zcu.kiv.crce.metadata.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.EqualityLevel;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

/**
 * Implementation of Requirement interface.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class RequirementImpl extends AbstractDirectiveProvider implements Requirement, Comparable<Requirement> {

    private static final long serialVersionUID = -2992854704112505654L;

    private final String id;
    private final List<Requirement> children = new ArrayList<>();
    protected final Map<String, List<Attribute<?>>> attributesMap = new HashMap<>();

    private String namespace = null;
    private Resource resource = null;
    private Requirement parent = null;

    public RequirementImpl(@Nonnull String namespace, @Nonnull String id) {
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
    public Requirement getParent() {
        return parent;
    }

    @Override
    public boolean setParent(Requirement parent) {
        this.parent = parent;
        return true;
    }

    @Override
    public boolean addChild(Requirement requirement) {
        return children.add(requirement);
    }

    @Override
    public boolean removeChild(Requirement requirement) {
        return children.remove(requirement);
    }

    @Override
    public List<Requirement> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public <T> boolean addAttribute(AttributeType<T> type, T value) {
        return addAttribute(type, value, Operator.EQUAL);
    }

    @Override
    public <T> boolean addAttribute(AttributeType<T> type, T value, Operator operator) {
        Attribute<T> attribute = new AttributeImpl<>(type, value, operator != null ? operator : Operator.EQUAL);
        return addAttribute(attribute);
    }

    @Override
    public <T> boolean addAttribute(String name, Class<T> type, T value, Operator operator) {
        AttributeType<T> attributeType = new SimpleAttributeType<>(name, type);
        return addAttribute(attributeType, value, operator);
    }

    @Override
    public <T> boolean addAttribute(Attribute<T> attribute) {
        AttributeType<T> type = attribute.getAttributeType();
        List<Attribute<?>> attributes = attributesMap.get(type.getName());
        if (attributes == null) {
            attributes = new ArrayList<>();
            attributes.add(attribute);
            attributesMap.put(type.getName(), attributes);
        } else if (!attributes.contains(attribute)) {
            attributes.add(attribute);
        }
        return true;
    }

    @Override
    public List<Attribute<?>> getAttributes() {
        List<Attribute<?>> result = new ArrayList<>();
        for (List<Attribute<?>> list : attributesMap.values()) {
            result.addAll(list);
        }
        return result;
    }

    @Override
    public Map<String, List<Attribute<?>>> getAttributesMap() {
        return Collections.unmodifiableMap(attributesMap);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<Attribute<T>> getAttributes(AttributeType<T> type) {
        List<Attribute<?>> attributes = attributesMap.get(type.getName());
        if (attributes == null) {
            return Collections.emptyList();
        }
        return (List<Attribute<T>>) (List) attributes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() == obj.getClass() || obj instanceof Requirement) {
            final Requirement other = (Requirement) obj;
            return Objects.equals(this.id, other.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equalsTo(Requirement other, EqualityLevel level) {
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
                if (!this.equalsTo(other, EqualityLevel.KEY)) {
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

    @Override
    public int compareTo(Requirement o) {
        return id.compareTo(o.getId());
    }
}
