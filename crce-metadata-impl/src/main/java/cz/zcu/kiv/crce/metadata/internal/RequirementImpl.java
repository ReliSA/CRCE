package cz.zcu.kiv.crce.metadata.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

/**
 * Implementation of Requirement interface.
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class RequirementImpl extends AbstractDirectiveProvider implements Requirement {

    private static final long serialVersionUID = -2992854704112505654L;

    private String namespace = null;
    private Resource resource = null;
    private Requirement parent = null;
    private final List<Requirement> nestedRequirements = new ArrayList<>();
    protected final Map<String, List<Attribute<?>>> attributesMap = new HashMap<>();

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
        // commented way would be a performance problem
//        if (!nestedRequirements.contains(requirement)) {
//            requirement.setParent(this);
            return nestedRequirements.add(requirement);
//        }
//        return false;
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
    public <T> boolean setAttribute(AttributeType<T> type, T value) {
        return setAttribute(type, value, Operator.EQUAL);
    }

    @Override
    public <T> boolean setAttribute(AttributeType<T> type, T value, Operator operator) {
        Attribute<T> attribute = new AttributeImpl<>(type, value, operator != null ? operator : Operator.EQUAL);
        return setAttribute(attribute);
    }

    @Override
    public <T> boolean setAttribute(String name, Class<T> type, T value, Operator operator) {
        AttributeType<T> attributeType = new SimpleAttributeType<>(name, type);
        return setAttribute(attributeType, value, operator);
    }

    @Override
    public <T> boolean setAttribute(Attribute<T> attribute) {
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
    public <T> Attribute<T> getAttribute(AttributeType<T> type) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        if (!Objects.equals(this.attributesMap, other.attributesMap)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + Objects.hashCode(this.namespace);
        hash = 97 * hash + Objects.hashCode(this.resource);
        hash = 97 * hash + Objects.hashCode(this.parent);
        hash = 97 * hash + Objects.hashCode(this.nestedRequirements);
        hash = 97 * hash + Objects.hashCode(this.attributesMap);
        return hash;
    }
}
