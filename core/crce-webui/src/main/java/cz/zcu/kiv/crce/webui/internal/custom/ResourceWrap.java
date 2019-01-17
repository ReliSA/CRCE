package cz.zcu.kiv.crce.webui.internal.custom;

import cz.zcu.kiv.crce.metadata.*;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.type.Version;
import cz.zcu.kiv.crce.webui.internal.legacy.NewProperty;

import java.net.URI;
import java.util.Collections;
import java.util.List;

abstract class ResourceWrap extends ResourceAdapter {

    // loose coupling with crce-metadata-osgi-bundle module
    private static final String OSG_IDENTITY_NAMESPACE = "osgi.identity";
    private static final AttributeType<String> ATTRIBUTE_SYMBOLIC_NAME = new SimpleAttributeType<>("symbolic-name", String.class);
    public static final AttributeType<Version> ATTRIBUTE_VERSION = new SimpleAttributeType<>(org.apache.felix.bundlerepository.Resource.VERSION, Version.class);

    protected Resource resource;
    private final MetadataService metadataService;

    protected ResourceWrap(Resource r, MetadataService metadataService) {
        this.resource = r;
        this.metadataService = metadataService;
    }

    @Override
    public cz.zcu.kiv.crce.webui.internal.legacy.Property[] getProperties() {
        cz.zcu.kiv.crce.webui.internal.legacy.Property[] properties;
        List<Capability> crceCapabilities = Collections.singletonList(metadataService.getIdentity(resource));

        int crceSize = crceCapabilities.isEmpty() ? 0 : crceCapabilities.get(0).getAttributes().size();

        properties = new cz.zcu.kiv.crce.webui.internal.legacy.Property[crceSize];
        int i = 0;
        if (crceSize > 0) {
            for (Attribute<?> atr : crceCapabilities.get(0).getAttributes()) {
                properties[i++] = new PropertyImpl(atr);
            }
        }
        return properties;
    }

    @Override
    public NewProperty[] getNewProperties() {
        List<? extends Property> newProperties = resource.getProperties();
        NewProperty[] properties = new NewProperty[newProperties.size()];
        int i = 0;
        for (Property newProperty : newProperties) {
            properties[i++] = new NewPropertyImpl(newProperty);
        }
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
        String name = "unknown-symbolic-name";
        List<Capability> capabilities = resource.getCapabilities(OSG_IDENTITY_NAMESPACE);
        if (!capabilities.isEmpty()) {
            name = capabilities.get(0).getAttributeValue(ATTRIBUTE_SYMBOLIC_NAME);
        }
        return name;
    }

    @Override
    public String getId() {
        return resource.getId();
    }

    @Override
    public Version getVersion() {
        Version version = null;
        List<Capability> capabilities = resource.getCapabilities(OSG_IDENTITY_NAMESPACE);
        if (!capabilities.isEmpty()) {
            version = capabilities.get(0).getAttributeValue(ATTRIBUTE_VERSION);
        }
        return version;
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
        return metadataService.getUri(resource);
    }

    @Override
    public long getSize() {
        return metadataService.getSize(resource);
    }
}
