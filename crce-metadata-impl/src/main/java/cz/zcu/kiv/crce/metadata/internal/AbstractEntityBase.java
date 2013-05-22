package cz.zcu.kiv.crce.metadata.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.AttributeProvider;

import cz.zcu.kiv.crce.metadata.AttributeType;
import cz.zcu.kiv.crce.metadata.DirectiveProvider;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;

/**
 * Abstract implementatio of
 * <code>PropertyProvider</code> interface. It serves to provide unique implementation of providing Properties.
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public abstract class AbstractEntityBase extends AbstractDirectiveProvider implements AttributeProvider, DirectiveProvider {

    protected final Map<String, Attribute<?>> attributesMap = new HashMap<>();


    @Override
    public <T> String getAttributeStringValue(AttributeType<T> type) {
        Attribute<?> attribute = attributesMap.get(type.getName());
        if (attribute != null) {
            return attribute.getStringValue();
        }
        return null;
    }

    @Override
    public <T> boolean setAttribute(AttributeType<T> type, T value) {
        attributesMap.put(type.getName(), new AttributeImpl<>(type, value));
        return true;
    }

    @Override
    public <T> boolean setAttribute(Attribute<T> attribute) {
        attributesMap.put(attribute.getAttributeType().getName(), attribute);
        return true;
    }

    @Override
    public <T> boolean removeAttribute(Attribute<T> attribute) {
        return attributesMap.remove(attribute.getAttributeType().getName()) != null;
    }

    @Override
    public <T> boolean setAttribute(AttributeType<T> type, T value, Operator operator) {
        attributesMap.put(type.getName(), new AttributeImpl<>(type, value, operator));
        return true;
    }

    @Override
    public <T> boolean setAttribute(String name, Class<T> type, T value) {
        AttributeType<T> attributeType = new SimpleAttributeType<>(name, type);
        Attribute<T> attribute = new AttributeImpl<>(attributeType, value);
        attributesMap.put(attributeType.getName(), attribute);
        return true;
    }

    @Override
    public <T> boolean removeAttribute(AttributeType<T> type) {
        return attributesMap.remove(type.getName()) != null;
    }

    @Override
    public <T> boolean removeAttribute(String name) {
        return attributesMap.remove(name) != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Attribute<T> getAttribute(AttributeType<T> type) {
        Attribute<?> attribute = attributesMap.get(type.getName());
        if (attribute != null) {
            return (Attribute<T>) attribute;
        }
        return null;
    }

    @Override
    public List<? extends Attribute<?>> getAttributes() {
        return Collections.unmodifiableList(new ArrayList<>(attributesMap.values()));
    }

    @Override
    public Map<String, ? extends Attribute<?>> getAttributesMap() {
        return Collections.unmodifiableMap(attributesMap);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAttributeValue(AttributeType<T> type) {
        Attribute<?> attribute = attributesMap.get(type.getName());
        if (attribute != null) {
            return (T) attribute.getValue();
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + Objects.hashCode(this.attributesMap);
        return hash;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractEntityBase other = (AbstractEntityBase) obj;
        if (!Objects.equals(this.attributesMap, other.attributesMap)) {
            return false;
        }
        return super.equals(obj);
    }
}
