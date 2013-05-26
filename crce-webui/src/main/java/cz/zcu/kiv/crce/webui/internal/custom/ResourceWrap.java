package cz.zcu.kiv.crce.webui.internal.custom;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import org.osgi.framework.Version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.legacy.LegacyMetadataHelper;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.webui.internal.legacy.Property;
import cz.zcu.kiv.crce.webui.internal.legacy.Type;

abstract class ResourceWrap extends ResourceAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ResourceWrap.class);

    protected Resource resource;
    private final MetadataService metadataService;

    protected ResourceWrap(Resource r, MetadataService metadataService) {
        this.resource = r;
        this.metadataService = metadataService;
    }

    @Override
    public Property[] getProperties() {
        Property[] properties;
        List<Capability> crceCapabilities = resource.getCapabilities(metadataService.getIdentityNamespace());
        List<Capability> osgiCapabilities = resource.getCapabilities(LegacyMetadataHelper.NS__OSGI_IDENTITY);

        int crceSize = crceCapabilities.size() > 0 ? crceCapabilities.get(0).getAttributes().size() : 0;
        int osgiSize = osgiCapabilities.size() > 0 ? osgiCapabilities.get(0).getAttributes().size() : 0;

        properties = new Property[crceSize + osgiSize];
        int i = 0;
        if (crceSize > 0) {
            for (Attribute<?> atr : crceCapabilities.get(0).getAttributes()) {
                properties[i++] = new PropertyImpl(atr);
            }
        }
        if (osgiSize > 0) {
            for (Attribute<?> atr : osgiCapabilities.get(0).getAttributes()) {
                properties[i++] = new PropertyImpl(atr);
            }
        }
//        for (Attribute atr : resource.getCapabilities(LegacyMetadataHelper.NS__CRCE_IDENTITY)

//        return resource.getProperties().toArray();

        return properties;
    }

    @Override
    public cz.zcu.kiv.crce.webui.internal.legacy.Capability[] getCapabilities() {
        List<Capability> capabilities = resource.getCapabilities();
        cz.zcu.kiv.crce.webui.internal.legacy.Capability[] result = new cz.zcu.kiv.crce.webui.internal.legacy.Capability[capabilities.size()];
        int i = 0;
        for (Capability capability : capabilities) {
            result[i++] = new CapabilityImpl(capability);
        }
        return result;
    }

    @Override
    public String[] getCategories() {
        return metadataService.getCategories(resource).toArray(new String[0]);
    }

    @Override
    public cz.zcu.kiv.crce.webui.internal.legacy.Requirement[] getRequirements() {
        List<Requirement> requirements = resource.getRequirements();
        cz.zcu.kiv.crce.webui.internal.legacy.Requirement[] result = new cz.zcu.kiv.crce.webui.internal.legacy.Requirement[requirements.size()];
        int i = 0;
        for (Requirement requirement : requirements) {
            result[i++] = new RequirementImpl(requirement);
        }
        return result;
    }

    @Override
    public String getSymbolicName() {
        return LegacyMetadataHelper.getSymbolicName(resource);
    }

    @Override
    public String getId() {
        return resource.getId();
    }

    @Override
    public Version getVersion() {
        return LegacyMetadataHelper.getVersion(resource);
    }

    @Override
    public String getPresentationName() {
        return metadataService.getPresentationName(resource);
    }

    @Override
    public URI getUri() {
        return metadataService.getUri(resource);
    }

    @Override
    public URI getRelativeUri() {
        return metadataService.getRelativeUri(resource);
    }

    @Override
    public long getSize() {
        return metadataService.getSize(resource);
    }

    private static class PropertyImpl implements Property {

        private final Attribute<?> attribute;

        public PropertyImpl(Attribute<?> attribute) {
            this.attribute = attribute;
        }

        @Override
        public String getName() {
            return attribute.getAttributeType().getName();
        }

        @Override
        public Type getType() {
            return Type.getValue(attribute.getAttributeType().getType().getSimpleName());
        }

        @Override
        public String getValue() {
            return attribute.getStringValue();
        }

        @Override
        public Object getConvertedValue() {
            return attribute.getValue();
        }

        @Override
        public boolean isWritable() {
            return true;
        }
    }

    private static class CapabilityImpl implements cz.zcu.kiv.crce.webui.internal.legacy.Capability {

        private final Capability capability;

        public CapabilityImpl(Capability capability) {
            this.capability = capability;
        }

        @Override
        public String getName() {
            return capability.getNamespace();
        }

        @Override
        public Property[] getProperties() {
            List<? extends Attribute<?>> attributes = capability.getAttributes();
            Property[] properties = new Property[attributes.size()];
            int i = 0;
            for (Attribute<?> attribute : attributes) {
                properties[i++] = new PropertyImpl(attribute);
            }
            return properties;
        }

        @Override
        public Property getProperty(String name) {
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
        public cz.zcu.kiv.crce.webui.internal.legacy.Capability setProperty(Property property) {
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

    private static class RequirementImpl implements cz.zcu.kiv.crce.webui.internal.legacy.Requirement {

        private final Requirement requirement;

        public RequirementImpl(@Nonnull Requirement requirement) {
            this.requirement = requirement;
        }

        @Override
        public String getName() {
            return requirement.getNamespace();
        }

        @Override
        public String getFilter() {
            return requirement.getDirective("filter");
        }

        @Override
        public boolean isMultiple() {
            return Boolean.valueOf(requirement.getDirective("multiple"));
        }

        @Override
        public boolean isOptional() {
            return Boolean.valueOf(requirement.getDirective("optional"));
        }

        @Override
        public boolean isExtend() {
            return Boolean.valueOf(requirement.getDirective("extend"));
        }

        @Override
        public String getComment() {
            return requirement.getDirective("comment");
        }

        @Override
        public boolean isWritable() {
            return true;
        }

        @Override
        public boolean isSatisfied(cz.zcu.kiv.crce.webui.internal.legacy.Capability capability) {
            logger.warn("Method isSatisfied is not supported by new Metadata API, returning false for Capability: {}, Requirement: {}", capability, requirement);
            return false;
        }

        @Override
        public cz.zcu.kiv.crce.webui.internal.legacy.Requirement setFilter(String filter) {
            requirement.setDirective("filter", filter);
            return this;
        }

        @Override
        public cz.zcu.kiv.crce.webui.internal.legacy.Requirement setMultiple(boolean multiple) {
            requirement.setDirective("multiple", String.valueOf(multiple));
            return this;
        }

        @Override
        public cz.zcu.kiv.crce.webui.internal.legacy.Requirement setOptional(boolean optional) {
            requirement.setDirective("optional", String.valueOf(optional));
            return this;
        }

        @Override
        public cz.zcu.kiv.crce.webui.internal.legacy.Requirement setExtend(boolean extend) {
            requirement.setDirective("extend", String.valueOf(extend));
            return this;
        }

        @Override
        public cz.zcu.kiv.crce.webui.internal.legacy.Requirement setComment(String comment) {
            requirement.setDirective("comment", String.valueOf(comment));
            return this;
        }
    }
}
