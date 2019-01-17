package cz.zcu.kiv.crce.webui.internal.custom;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.type.Version;
import cz.zcu.kiv.crce.webui.internal.legacy.NewProperty;
import cz.zcu.kiv.crce.webui.internal.legacy.Property;
import cz.zcu.kiv.crce.webui.internal.legacy.Type;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NewPropertyImpl implements NewProperty {

    private final cz.zcu.kiv.crce.metadata.Property property;

    public NewPropertyImpl(cz.zcu.kiv.crce.metadata.Property property) {
        this.property = property;
    }

    @Override
    public String getName() {
        return property.getNamespace();
    }

    @Override
    public Property[] getProperties() {
        List<? extends Attribute<?>> attributes = property.getAttributes();
        Property[] properties = new Property[attributes.size()];
        int i = 0;
        for (Attribute<?> attribute : attributes) {
            properties[i++] = new PropertyImpl(attribute);
        }
        return properties;
    }

    @Override
    public Property getProperty(String name) {
        Attribute<?> attribute = property.getAttributesMap().get(name);
        if (attribute != null) {
            return new PropertyImpl(attribute);
        }
        return null;
    }

    @Override
    public String getPropertyString(String name) {
        Attribute<?> attribute = property.getAttributesMap().get(name);
        if (attribute != null) {
            return attribute.getStringValue();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewProperty setProperty(Property property) {
        this.property.setAttribute(property.getName(), (Class<Object>) property.getType().getTypeClass(), property.getConvertedValue());
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewProperty setProperty(String name, String value, Type type) {
        this.property.setAttribute(name, (Class<Object>) type.getTypeClass(), Type.propertyValueFromString(type, value));
        return this;
    }

    @Override
    public NewProperty setProperty(String name, String string) {
        this.property.setAttribute(name, String.class, string);
        return this;
    }

    @Override
    public NewProperty setProperty(String name, Version version) {
        this.property.setAttribute(name, Version.class, version);
        return this;
    }

    @Override
    public NewProperty setProperty(String name, URL url) {
        this.property.setAttribute(name, String.class, url.toString());
        return this;
    }

    @Override
    public NewProperty setProperty(String name, URI uri) {
        this.property.setAttribute(name, String.class, uri.toString());
        return this;
    }

    @Override
    public NewProperty setProperty(String name, long llong) {
        this.property.setAttribute(name, Long.class, llong);
        return this;
    }

    @Override
    public NewProperty setProperty(String name, double ddouble) {
        this.property.setAttribute(name, Double.class, ddouble);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NewProperty setProperty(String name, Set values) {
        this.property.setAttribute(name, List.class, new ArrayList<>(values));
        return this;
    }

    @Override
    public NewProperty unsetProperty(String name) {
        this.property.removeAttribute(name);
        return this;
    }

}