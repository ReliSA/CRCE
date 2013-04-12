package cz.zcu.kiv.crce.metadata.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.MatchingAttribute;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

/**
 * Implementation of Requirement interface.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class RequirementImpl extends AbstractEntityBase implements Requirement {
    
    private static final long serialVersionUID = -2992854704112505654L;

    private String namespace = null;
    private Resource resource = null;
    private Requirement parent = null;
    private final List<Requirement> nestedRequirements = new ArrayList<>();

    public RequirementImpl(String namespace) {
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
    public Requirement getParent() {
        return parent;
    }

    @Override
    public boolean setParent(Requirement parent) {
        this.parent = parent;
        return true;
    }

    @Override
    public boolean addNestedRequirement(Requirement requirement) {
        if (!nestedRequirements.contains(requirement)) {
            requirement.setParent(this);
            return nestedRequirements.add(requirement);
        }
        return false;
    }

    @Override
    public boolean removeNestedRequirement(Requirement requirement) {
        return nestedRequirements.remove(requirement);
    }

    @Override
    public List<Requirement> getNestedRequirements() {
        return Collections.unmodifiableList(nestedRequirements);
    }

    @Override
    public <T> MatchingAttribute<T> getAttribute(AttributeType<T> type) {
        return (MatchingAttribute<T>) super.getAttribute(type);
    }

    @Override
    public <T> boolean setAttribute(AttributeType<T> type, T value, Operator operator) {
        return super.setAttribute(new MatchingAttributeImpl<>(type, value, operator));
    }

    @Override
    public <T> boolean setAttribute(Attribute<T> attribute, Operator operator) {
        return super.setAttribute(new MatchingAttributeImpl<>(attribute.getAttributeType(), attribute.getValue(), operator));
    }

    @Override
    public <T> boolean setAttribute(String name, Class<T> type, T value, Operator operator) {
        AttributeType<T> attributeType = new SimpleAttributeType<>(name, type);
        Attribute<T> attribute = new MatchingAttributeImpl<>(attributeType, value, operator);
        return super.setAttribute(attribute);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<MatchingAttribute<?>> getAttributes() {
        return Collections.unmodifiableList((List<MatchingAttribute<?>>) super.getAttributes());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, MatchingAttribute<?>> getAttributesMap() {
        return (Map<String, MatchingAttribute<?>>) super.getAttributesMap();
    }

    @Override
    public Operator getAttributeOperator(AttributeType<?> type) {
        Attribute<?> attribute = super.getAttribute(type);
        if (attribute != null) {
            return ((MatchingAttribute<?>) attribute).getOperator();
        }
        return Operator.EQUAL;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RequirementImpl other = (RequirementImpl) obj;
        if (!Objects.equals(this.namespace, other.namespace)) {
            return false;
        }
        if (!Objects.equals(this.resource, other.resource)) {
            return false;
        }
        if (!Objects.equals(this.parent, other.parent)) {
            return false;
        }
        if (!Objects.equals(this.nestedRequirements, other.nestedRequirements)) {
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
        hash = 97 * hash + Objects.hashCode(this.nestedRequirements);
        return 97 * hash + super.hashCode();
    }
}
