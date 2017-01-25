package cz.zcu.kiv.crce.rest.internal.mapping;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.apache.felix.dm.annotation.api.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.compatibility.namespace.NsCrceCompatibility;
import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiBundle;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiIdentity;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.metadata.type.Version;
import cz.zcu.kiv.crce.rest.internal.Activator;
import cz.zcu.kiv.crce.rest.internal.jaxb.metadata.ObjectFactory;

/**
 * Convert cz.zcu.kiv.crce.metadata.Resource to bean classes with JAXB annotations. These bean classes are ready to export metadata to xml.
 *
 * @author Jan Reznicek
 *
 */
@Component(provides = JaxbMapping.class)
public class JaxbMapping {

    private static final Logger logger = LoggerFactory.getLogger(JaxbMapping.class); // NOPMD

    public static final String OSGI_IDENTITY_CAP_NAME = "osgi.identity";
    public static final String OSGI_CONTENT_CAP_NAME = "osgi.content";
    public static final String CRCE_IDENTITY_CAP_NAME = "crce.identity";
    public static final String OSGI_WIRING_NAME = "osgi.wiring.package";
	public static final String OSGI_WIRING_BUNDLE = "osgi.wiring.bundle";
	public static final String CRCE_METRICS_NAME = "crce.metrics";

    private final ObjectFactory metadataObjectFactory = new ObjectFactory();

    private final CompatibilityMapper compatibilityMapper = new CompatibilityMapper();

    /**
     * Default host, should not be needed, information is gained from request context. But is there for sure TODO: add default rest host to
     * CRCE config file
     */
    public static final String DEFAULT_HOST = "http://localhost:8080/rest/"; // TODO this won't always work

    /**
     * Get URL of resource, that could be uset for REST action GET Bundle.
     *
     * @param resource resource
     * @param ui contextual info about URI
     * @return URL of the resource
     */
    private String getURL(Resource resource, UriInfo ui) {
        MetadataService metadataService = Activator.instance().getMetadataService();
        String name = metadataService.getSingletonCapability(resource, NsOsgiIdentity.NAMESPACE__OSGI_IDENTITY)
                .getAttributeValue(NsOsgiIdentity.ATTRIBUTE__NAME);

        if (name == null) {
            return null;
        }

        if (ui == null) {
            return DEFAULT_HOST + "bundle/" + name;
        }
        String url = ui.getBaseUri().toString();
        return url + "bundle/" + name;
    }


    /**
     * Get cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Resource with unknown crce.status.
     * This cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Resource contains only id and capability crce.identity with attribute crce.status, that
     * is "unknown"
     *
     * @param id the resource id
     * @return information about unknown resource
     */
    public cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Resource getResourceWithUnknownStatus(String id) { // TODO check purpose of this method
        cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Resource resource = new cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Resource();

        resource.setId(id);

        List<cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Capability> caps = resource.getCapabilities();

        cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Capability crceIdentity = new cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Capability();
        crceIdentity.setNamespace("crce.identity");
        List<cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Attribute> attributes = crceIdentity.getAttributes();

        cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Attribute jaxbAttribute = metadataObjectFactory.createAttribute();
        jaxbAttribute.setName("name");
        jaxbAttribute.setValue(resource.getId());
        attributes.add(jaxbAttribute);

        jaxbAttribute = metadataObjectFactory.createAttribute();
        jaxbAttribute.setName("status");
        jaxbAttribute.setValue("unknown");
        attributes.add(jaxbAttribute);

        caps.add(crceIdentity);

        return resource;
    }

    // TODO the following methods are candidates for a common OSGi service

    protected Version getBundleVersion(Resource resource) {
        if (resource != null) {
            List<Capability> resCapabilities = resource.getCapabilities(NsOsgiBundle.NAMESPACE__OSGI_BUNDLE);
            if (!resCapabilities.isEmpty()) {
                return resCapabilities.get(0).getAttributeValue(NsOsgiBundle.ATTRIBUTE__VERSION);
            }
        }
        return null;
    }

    protected String getBundleSymbolicName(Resource resource) {
        if (resource != null) {
            List<Capability> resCapabilities = resource.getCapabilities(NsOsgiBundle.NAMESPACE__OSGI_BUNDLE);
            if (!resCapabilities.isEmpty()) {
                return resCapabilities.get(0).getAttributeValue(NsOsgiBundle.ATTRIBUTE__SYMBOLIC_NAME);
            }
        }
        return null;
    }

    // new mappings

    public cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Repository mapRepository(List<Resource> resources, MetadataFilter filter, UriInfo uriInfo) {
        cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Repository jaxbRepository = metadataObjectFactory.createRepository();

        jaxbRepository.setIncrement(0L);
        jaxbRepository.setName("store");

        for (Resource resource : resources) {
            jaxbRepository.getResources().add(mapResource(resource, filter, uriInfo));
        }
        return jaxbRepository;
    }

    public cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Resource mapResource(Resource resource, MetadataFilter filter, UriInfo uriInfo) {
        cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Resource jaxbResource = metadataObjectFactory.createResource();

        jaxbResource.setId(resource.getId());

        List<cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Capability> jaxbCapabilities = jaxbResource.getCapabilities();

        // #81: 'crce.identity' must precede all remaining capabilities
        if (includeCapability(CRCE_IDENTITY_CAP_NAME, filter)) {
            List<Capability> capabilities = resource.getCapabilities(CRCE_IDENTITY_CAP_NAME);
            if (!capabilities.isEmpty()) {
                cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Capability jaxbCapability = mapCapability(capabilities.get(0));
                String url = getURL(resource, uriInfo);
                if (url != null) {
                    cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Attribute attribute = metadataObjectFactory.createAttribute();
                    attribute.setName("url"); // TODO hardcoded
                    attribute.setValue(url);
                    jaxbCapability.getAttributes().add(attribute);
                }
                jaxbCapabilities.add(jaxbCapability);
            }
        }

        for (Capability capability : resource.getRootCapabilities()) {
            if (CRCE_IDENTITY_CAP_NAME.equals(capability.getNamespace())) {
                continue;
            }
            if (includeCapability(capability.getNamespace(), filter)) {
                cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Capability jaxbCapability = mapCapability(capability);
                jaxbCapabilities.add(jaxbCapability);
            }
        }

        List<cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Requirement> jaxbRequirements = jaxbResource.getRequirements();
        for (Requirement requirement : resource.getRequirements()) {
            if (includeRequirement(requirement.getNamespace(), filter)) {
                jaxbRequirements.add(mapRequirement(requirement));
            }
        }

        List<cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Property> jaxbProperties = jaxbResource.getProperties();
        for (Property property : resource.getProperties()) {
            if (includeProperty(property.getNamespace(), filter)) {
                jaxbProperties.add(mapProperty(property));
            }
        }
        if (includeProperty(NsCrceCompatibility.NAMESPACE__CRCE_COMPATIBILITY, filter)) {
            cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Property p = compatibilityMapper.mapCompatibility(resource);
            if (p != null) {
                jaxbProperties.add(p);
            }
        }


        return jaxbResource;
    }

    private cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Capability mapCapability(Capability capability) {
        cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Capability jaxbCapability = metadataObjectFactory.createCapability();

        jaxbCapability.setNamespace(capability.getNamespace());
        jaxbCapability.setId(capability.getId());

        List<cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Directive> directives = jaxbCapability.getDirectives();
        for (Map.Entry<String, String> entry : capability.getDirectives().entrySet()) {
            directives.add(mapDirective(entry));
        }

        List<cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Attribute> attributes = jaxbCapability.getAttributes();
        for (Attribute<?> attribute : capability.getAttributes()) {
            attributes.add(mapAttribute(attribute));
        }

        List<cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Capability> capabilities = jaxbCapability.getCapabilities();
        for (Capability child : capability.getChildren()) {
            capabilities.add(mapCapability(child));
        }

        List<cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Property> properties = jaxbCapability.getProperties();
        for (Property property : capability.getProperties()) {
            properties.add(mapProperty(property));
        }

        return jaxbCapability;
    }

    private cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Requirement mapRequirement(Requirement requirement) {
        cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Requirement jaxbRequirement = metadataObjectFactory.createRequirement();

        jaxbRequirement.setNamespace(requirement.getNamespace());
        jaxbRequirement.setId(requirement.getId());

        List<cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Directive> directives = jaxbRequirement.getDirectives();
        for (Map.Entry<String, String> entry : requirement.getDirectives().entrySet()) {
            directives.add(mapDirective(entry));
        }

        List<cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Attribute> attributes = jaxbRequirement.getAttributes();
        for (Attribute<?> attribute : requirement.getAttributes()) {
            attributes.add(mapAttribute(attribute));
        }

        List<cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Requirement> requirements = jaxbRequirement.getRequirements();
        for (Requirement child : requirement.getChildren()) {
            requirements.add(mapRequirement(child));
        }

        return jaxbRequirement;
    }

    private cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Property mapProperty(Property property) {
        cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Property jaxbProperty = metadataObjectFactory.createProperty();

        jaxbProperty.setNamespace(property.getNamespace());
        jaxbProperty.setId(property.getId());

        List<cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Attribute> attributes = jaxbProperty.getAttributes();
        for (Attribute<?> attribute : property.getAttributes()) {
            attributes.add(mapAttribute(attribute));
        }

        // links not implemented yet

        return jaxbProperty;
    }

    private cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Directive mapDirective(Map.Entry<String, String> entry) {
        cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Directive jaxbDirective = metadataObjectFactory.createDirective();

        jaxbDirective.setName(entry.getKey());
        jaxbDirective.setValue(entry.getValue());

        return jaxbDirective;
    }

    private cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Attribute mapAttribute(Attribute<?> attribute) {
        cz.zcu.kiv.crce.rest.internal.jaxb.metadata.Attribute jaxbAttribute = metadataObjectFactory.createAttribute();

        jaxbAttribute.setName(attribute.getName());
        jaxbAttribute.setValue(attribute.getStringValue());
        if (!String.class.equals(attribute.getType())) {
            jaxbAttribute.setType(attribute.getType().getSimpleName());
        }
        if (!Operator.EQUAL.equals(attribute.getOperator())) {
            jaxbAttribute.setOperator(attribute.getOperator().getValue());
        }
        return jaxbAttribute;
    }

    private boolean includeCapability(String namespace, MetadataFilter filter) {
        if (filter.isCoreCapabilities() && (namespace.endsWith(".content") || namespace.endsWith(".identity"))) {
            //core capability
            return true;
        }

        return filter.includeCapabilities()
                && (filter.getCapabilityNamespace() == null || namespace.equals(filter.getCapabilityNamespace()));
    }

    private boolean includeRequirement(String namespace, MetadataFilter filter) {
        return filter.includeRequirements()
                && (filter.getRequirementNamespace() == null || namespace.equals(filter.getRequirementNamespace()));
    }

    private boolean includeProperty(String namespace, MetadataFilter filter) {
        return filter.includeProperties() && (filter.getPropertyNamespace() == null || namespace.equals(filter.getPropertyNamespace()));
    }
}
