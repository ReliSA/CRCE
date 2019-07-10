package cz.zcu.kiv.crce.webui.internal.custom;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.type.Version;
import cz.zcu.kiv.crce.webui.internal.legacy.NewProperty;
import cz.zcu.kiv.crce.webui.internal.legacy.Type;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CapabilityImpl implements cz.zcu.kiv.crce.webui.internal.legacy.Capability {

    private final Capability capability;

    public CapabilityImpl(Capability capability) {
        this.capability = capability;
    }

    @Override
    public String getName() {
        return capability.getNamespace();
    }

    @Override
    public NewProperty[] getNewProperties() {
        List<? extends Property> newProperties = capability.getProperties();
        NewProperty[] properties = new NewProperty[newProperties.size()];
        int i = 0;
        for (Property newProperty : newProperties) {
            properties[i++] = new NewPropertyImpl(newProperty);
        }
        return properties;
    }

    @Override
    public cz.zcu.kiv.crce.webui.internal.legacy.Property[] getProperties() {
        List<? extends Attribute<?>> attributes = capability.getAttributes();
        cz.zcu.kiv.crce.webui.internal.legacy.Property[] properties = new cz.zcu.kiv.crce.webui.internal.legacy.Property[attributes.size()];
        int i = 0;
        for (Attribute<?> attribute : attributes) {
            properties[i++] = new PropertyImpl(attribute);
        }
        return properties;
    }

    @Override
    public cz.zcu.kiv.crce.webui.internal.legacy.Property getProperty(String name) {
        Attribute<?> attribute = capability.getAttributesMap().get(name);
        if (attribute != null) {
            return new PropertyImpl(attribute);
        }
        return null;
    }

    @Override
    public String getPropertyString(String name) {
        Attribute<?> attribute = capability.getAttributesMap().get(name);
        if (attribute != null) {
            return attribute.getStringValue();
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public cz.zcu.kiv.crce.webui.internal.legacy.Capability setProperty(cz.zcu.kiv.crce.webui.internal.legacy.Property property) {
        capability.setAttribute(property.getName(), (Class<Object>) property.getType().getTypeClass(), property.getConvertedValue());
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public cz.zcu.kiv.crce.webui.internal.legacy.Capability setProperty(String name, String value, Type type) {
        capability.setAttribute(name, (Class<Object>) type.getTypeClass(), Type.propertyValueFromString(type, value));
        return this;
    }

    @Override
    public cz.zcu.kiv.crce.webui.internal.legacy.Capability setProperty(String name, String value) {
        capability.setAttribute(name, String.class, value);
        return this;
    }

    @Override
    public cz.zcu.kiv.crce.webui.internal.legacy.Capability setProperty(String name, Version version) {
        capability.setAttribute(name, Version.class, version);
        return this;
    }

    @Override
    public cz.zcu.kiv.crce.webui.internal.legacy.Capability setProperty(String name, URL url) {
        capability.setAttribute(name, String.class, url.toString());
        return this;
    }

    @Override
    public cz.zcu.kiv.crce.webui.internal.legacy.Capability setProperty(String name, URI uri) {
        capability.setAttribute(name, String.class, uri.toString());
        return this;
    }

    @Override
    public cz.zcu.kiv.crce.webui.internal.legacy.Capability setProperty(String name, long llong) {
        capability.setAttribute(name, Long.class, llong);
        return this;
    }

    @Override
    public cz.zcu.kiv.crce.webui.internal.legacy.Capability setProperty(String name, double ddouble) {
        capability.setAttribute(name, Double.class, ddouble);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public cz.zcu.kiv.crce.webui.internal.legacy.Capability setProperty(String name, Set values) {
        capability.setAttribute(name, List.class, new ArrayList<>(values));
        return this;
    }

    @Override
    public cz.zcu.kiv.crce.webui.internal.legacy.Capability unsetProperty(String name) {
        capability.removeAttribute(name);
        return this;
    }
}
