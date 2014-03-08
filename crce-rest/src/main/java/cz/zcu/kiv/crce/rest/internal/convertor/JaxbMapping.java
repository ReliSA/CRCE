package cz.zcu.kiv.crce.rest.internal.convertor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.osgi.framework.Version;

import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.ServiceDependency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.metadata.Attribute;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiBundle;
import cz.zcu.kiv.crce.metadata.osgi.namespace.NsOsgiPackage;
import cz.zcu.kiv.crce.metadata.service.MetadataService;
import cz.zcu.kiv.crce.rest.internal.jaxb.ObjectFactory;

/**
 * Convert cz.zcu.kiv.crce.metadata.Resource to bean classes with JAXB annotations. These bean classes are ready to export metadata to xml.
 *
 * @author Jan Reznicek
 *
 */
@Component(provides = JaxbMapping.class)
public class JaxbMapping {

    private static final Logger logger = LoggerFactory.getLogger(JaxbMapping.class);

    public static final String OSGI_IDENTITY_CAP_NAME = "osgi.identity";
    public static final String OSGI_CONTENT_CAP_NAME = "osgi.content";
    public static final String CRCE_IDENTITY_CAP_NAME = "crce.identity";
    public static final String OSGI_WIRING_NAME = "osgi.wiring.package";
	public static final String OSGI_WIRING_BUNDLE = "osgi.wiring.bundle";
	public static final String CRCE_METRICS_NAME = "crce.metrics";

    @ServiceDependency private volatile MetadataService metadataService;
    @ServiceDependency private volatile MimeTypeSelector mimeTypeSelector;

    private final ObjectFactory objectFactory = new ObjectFactory();

    /**
     * Default host, should not be needed, information is gained from request context. But is there for sure TODO: add default rest host to
     * CRCE config file
     */
    public static final String DEFAULT_HOST = "http://localhost:8080/rest/";

    /**
     * Get original file name from resource or null, if name was not found.
     *
     * @param resource resource
     * @return original file name of resource or null.
     */
    private String getFileName(Resource resource) {
        return metadataService.getFileName(resource);
    }

    /**
     * Get URL of resource, that could be uset for REST action GET Bundle.
     *
     * @param resource resource
     * @param ui contextual info about URI
     * @return URL of the resource
     */
    private String getURL(Resource resource, UriInfo ui) {
        if (ui == null) {
            return DEFAULT_HOST + "bundle/" + resource.getId();
        }
        String url = ui.getBaseUri().toString();
        return url + "bundle/" + resource.getId();
    }

    /**
     * Get hexadecimal SHA-256 of file with resource or null, if error occurred during counting digest.
     *
     * @param resource
     * @return hexadecimal SHA-256 of file with resource or null
     */
    private String getSHA(Resource resource) {
        try (FileInputStream fis = new FileInputStream(new File(metadataService.getUri(resource)))) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] dataBytes = new byte[1024];

            int nread;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            byte[] mdbytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1)); // NOPMD better clarity
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException | IOException e) {
            logger.warn(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Add a new attribute to the list of objects. Attribute can contains name, type or value. These tree are obligatory, if you don't want
     * any of them, set parameter to null.
     *
     * @param list list of objects (attributtes, capabilities, directives)
     * @param name name or null
     * @param type type or null
     * @param value value or null
     * @param op operation or null
     */
    private void addAttribute(List<cz.zcu.kiv.crce.rest.internal.jaxb.Attribute> list, String name, String type, String value, String op) {

        cz.zcu.kiv.crce.rest.internal.jaxb.Attribute newAttributte = new cz.zcu.kiv.crce.rest.internal.jaxb.Attribute();

        if (name != null) {
            newAttributte.setName(name);
        }
        if (type != null) {
            newAttributte.setType(type);
        }
        if (value != null) {
            newAttributte.setValue(value);
        }
        // TODO 'op' is not in current XSD, but it was in previously generated classes
        if (op != null) {
//            newAttributte.setOp(op);
            logger.warn("'op' is not supported yet in XML format.");
        }

        list.add(newAttributte);

    }

    /**
     * Returns capability with osgi.identity.
     *
     * @param resource resource
     * @return capability with osgi.identity
     */
    private cz.zcu.kiv.crce.rest.internal.jaxb.Capability prepareOsgiIdentity(Resource resource) {

        cz.zcu.kiv.crce.rest.internal.jaxb.Capability osgiIdentity = new cz.zcu.kiv.crce.rest.internal.jaxb.Capability();
        osgiIdentity.setNamespace(OSGI_IDENTITY_CAP_NAME);
        List<cz.zcu.kiv.crce.rest.internal.jaxb.Attribute> attributes = osgiIdentity.getAttributes();

        addAttribute(attributes, "name", null, getBundleSymbolicName(resource), null);
        addAttribute(attributes, "version", "Version", String.valueOf(getBundleVersion(resource)), null);

        return osgiIdentity;
    }

    /**
     * Returns capability with osgi.content
     *
     * @param resource resource
     * @param ui contextual info about URI
     * @return capability with osgi.content
     */
    private cz.zcu.kiv.crce.rest.internal.jaxb.Capability prepareOsgiContent(Resource resource, UriInfo ui) {

        cz.zcu.kiv.crce.rest.internal.jaxb.Capability osgiContent = new cz.zcu.kiv.crce.rest.internal.jaxb.Capability();
        osgiContent.setNamespace(OSGI_CONTENT_CAP_NAME);
        List<cz.zcu.kiv.crce.rest.internal.jaxb.Attribute> attributes = osgiContent.getAttributes();

        addAttribute(attributes, "hash", null, getSHA(resource), null);
        addAttribute(attributes, "url", null, getURL(resource, ui), null);
        addAttribute(attributes, "size", "Long", Long.toString(metadataService.getSize(resource)), null);
        addAttribute(attributes, "mime", null, mimeTypeSelector.selectMimeType(resource), null);
        addAttribute(attributes, "crce.original-file-name", null, getFileName(resource), null);

        return osgiContent;

    }

    /**
     * Create from array of strings result string, where are string separated by one comma ','.
     *
     * @param categories array of strings
     * @return result string
     */
    private String categoriesToString(List<String> categories) {

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String category : categories) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(category);
        }

        return sb.toString();
    }

    /**
     * Returns capability with crce.identity
     *
     * @param resource resource
     * @return capability with crce.identity
     */
    private cz.zcu.kiv.crce.rest.internal.jaxb.Capability prepareCrceIdentity(Resource resource) {

        cz.zcu.kiv.crce.rest.internal.jaxb.Capability crceIdentity = new cz.zcu.kiv.crce.rest.internal.jaxb.Capability();
        crceIdentity.setNamespace(CRCE_IDENTITY_CAP_NAME);
        List<cz.zcu.kiv.crce.rest.internal.jaxb.Attribute> attributes = crceIdentity.getAttributes();

        addAttribute(attributes, "name", null, resource.getId(), null);
        addAttribute(attributes, "crce.categories", "List<String>", categoriesToString(metadataService.getCategories(resource)), null);
        addAttribute(attributes, "crce.status", null, "stored", null);

        return crceIdentity;

    }

    /**
     * Add all capabilities with OSGi bundle wiring to the resource
     *
     * @param capabilities list of capabilities of the resourceBean
     * @param resource resource
     */
    private void addBundleWirings(List<cz.zcu.kiv.crce.rest.internal.jaxb.Capability> capabilities, Resource resource) {
        List<Capability> caps = resource.getCapabilities(NsOsgiBundle.NAMESPACE__OSGI_BUNDLE);
        for (Capability cap : caps) {

            cz.zcu.kiv.crce.rest.internal.jaxb.Capability newCapBean = new cz.zcu.kiv.crce.rest.internal.jaxb.Capability();
            List<cz.zcu.kiv.crce.rest.internal.jaxb.Attribute> attributes = newCapBean.getAttributes();
            newCapBean.setNamespace(OSGI_WIRING_BUNDLE);
            // package attribute
            String manifestVersion = cap.getAttributeValue(NsOsgiBundle.ATTRIBUTE__MANIFEST_VERSION);
            if (manifestVersion != null) {
            	addAttribute(attributes, "manifest-version", null, manifestVersion, null);
            }
            String presentationName = cap.getAttributeValue(NsOsgiBundle.ATTRIBUTE__PRESENTATION_NAME);
            if (presentationName != null) {
            	addAttribute(attributes, "presentation-name", null, presentationName, null);
            }
            String symbolicName = cap.getAttributeValue(NsOsgiBundle.ATTRIBUTE__SYMBOLIC_NAME);
            if (symbolicName != null) {
            	addAttribute(attributes, "symbolic-name", null, symbolicName, null);
            }
            Version version = cap.getAttributeValue(NsOsgiBundle.ATTRIBUTE__VERSION);
            if (manifestVersion != null) {
            	addAttribute(attributes, "manifest-version", "Version", String.valueOf(version), null);
            }

            for (Property<Capability> property : cap.getProperties()) {
            	if (property.getNamespace().equals(CRCE_METRICS_NAME)) {
            		newCapBean.getProperties().add(createCrceMetrics(property));
            	}
            }

            // TODO directives

            capabilities.add(newCapBean);
        }
    }

    /**
     * Add all capabilities with package wiring to the resource
     *
     * @param capabilities list of capabilities of the resourceBean
     * @param resource resource
     * @param include what part of metadata should be included
     */
    private void addCapabilityWirings(List<cz.zcu.kiv.crce.rest.internal.jaxb.Capability> capabilities, Resource resource, MetadataFilter include) {
        List<Capability> caps = resource.getCapabilities(NsOsgiPackage.NAMESPACE__OSGI_PACKAGE);
        for (Capability cap : caps) {

            cz.zcu.kiv.crce.rest.internal.jaxb.Capability newCapBean = new cz.zcu.kiv.crce.rest.internal.jaxb.Capability();
            List<cz.zcu.kiv.crce.rest.internal.jaxb.Attribute> attributes = newCapBean.getAttributes();
            newCapBean.setNamespace(OSGI_WIRING_NAME);
            // package attribute
            String packageName = cap.getAttributeValue(NsOsgiPackage.ATTRIBUTE__NAME);
            if (packageName != null) {
                cz.zcu.kiv.crce.rest.internal.jaxb.Attribute packAtr = new cz.zcu.kiv.crce.rest.internal.jaxb.Attribute();
                packAtr.setName("name");
                packAtr.setValue(packageName);
                attributes.add(packAtr);
            }

            // version attribute
            Version packageVersion = cap.getAttributeValue(NsOsgiPackage.ATTRIBUTE__VERSION);
            if (packageVersion != null) {
                cz.zcu.kiv.crce.rest.internal.jaxb.Attribute versAtr = new cz.zcu.kiv.crce.rest.internal.jaxb.Attribute();
                versAtr.setName("version");
                versAtr.setType("Version");
                versAtr.setValue(packageVersion.toString());
                attributes.add(versAtr);
            }

            // crce.metrics
            if (includeProperty(CRCE_METRICS_NAME, include)) {
	            for (Property<Capability> property : cap.getProperties()) {
	            	if (property.getNamespace().equals(CRCE_METRICS_NAME)) {
	            		newCapBean.getProperties().add(createCrceMetrics(property));
	            	}
	            }
            }

            // TODO directives

            capabilities.add(newCapBean);

        }
    }

    /**
     * Add crce.metrics properties of Resource.
     *
     * @param prop List of properties of the resourceBean
     * @param resource Resource
     */
    private void addResourceCrceMetricsProperties(List<cz.zcu.kiv.crce.rest.internal.jaxb.Property> prop, Resource resource) {

        for (Property<Resource> property : resource.getProperties()) {
        	if (property.getNamespace().equals(CRCE_METRICS_NAME)) {
        		prop.add(createCrceMetrics(property));
        	}
        }
    }

    /**
     * Create <code>cz.zcu.kiv.crce.rest.internal.jaxb.Property</code> bean of crce.metrics property.
     *
     * @param crceMetricsProperty Property of crce.metrics to convert to bean.
     * @return Created crce.metrics <code>cz.zcu.kiv.crce.rest.internal.jaxb.Property</code>
     */
    private cz.zcu.kiv.crce.rest.internal.jaxb.Property createCrceMetrics(Property<?> crceMetricsProperty) {

    	cz.zcu.kiv.crce.rest.internal.jaxb.Property newCapBean = new cz.zcu.kiv.crce.rest.internal.jaxb.Property();
        List<cz.zcu.kiv.crce.rest.internal.jaxb.Attribute> attributes = newCapBean.getAttributes();
        newCapBean.setNamespace(CRCE_METRICS_NAME);

        for (Attribute<?> atr : crceMetricsProperty.getAttributes()) {
            cz.zcu.kiv.crce.rest.internal.jaxb.Attribute metricsAtr = new cz.zcu.kiv.crce.rest.internal.jaxb.Attribute();
            metricsAtr.setName(atr.getName());
            metricsAtr.setValue(atr.getValue().toString());
            attributes.add(metricsAtr);
        }

        return newCapBean;
    }

    /**
     * Add all requirements with package wiring to the resource
     *
     * @param requirements list of requirements of the resourceBean
     * @param resource resource
     */
    private void addRequirementWirings(List<cz.zcu.kiv.crce.rest.internal.jaxb.Requirement> requirements, Resource resource) {
        List<Requirement> reqs = resource.getRequirements(NsOsgiPackage.NAMESPACE__OSGI_PACKAGE);
        for (Requirement req : reqs) {

            cz.zcu.kiv.crce.rest.internal.jaxb.Requirement newReqBean = new cz.zcu.kiv.crce.rest.internal.jaxb.Requirement();
            List<cz.zcu.kiv.crce.rest.internal.jaxb.Attribute> atrDirReq = newReqBean.getAttributes();
            newReqBean.setNamespace(OSGI_WIRING_NAME);

            cz.zcu.kiv.crce.rest.internal.jaxb.Directive dir = new cz.zcu.kiv.crce.rest.internal.jaxb.Directive();
            dir.setName("filter");
            dir.setValue(req.getDirective("filter")); // TODO add support of filters
            newReqBean.getDirectives().add(dir);

            String name;
            try {
                FilterParser filterParser = new FilterParser();

                String[] parsedFilter = filterParser.parseFilter(req.getDirective("filter")); // TODO add support of filters
                name = parsedFilter[0];
                addAttribute(atrDirReq, "name", null, name, null);

                for (int i = 1; i < parsedFilter.length; i += 2) {
                    addAttribute(atrDirReq, "version", null, parsedFilter[i], parsedFilter[i + 1]);
                }

            } catch (RuntimeException e) {
                logger.warn("Exception during parsing wiring requirement filter.");
            }

            requirements.add(newReqBean);

        }
    }

    /**
     * Convert {@link Resource} to {@link ResourceBean}.
     *
     * @param resource resource
     * @param filter what part of metadata should be included
     * @param ui contextual info about URI
     * @return converted resource
     */
    public cz.zcu.kiv.crce.rest.internal.jaxb.Resource convertResource(Resource resource, MetadataFilter filter, UriInfo ui) {

        cz.zcu.kiv.crce.rest.internal.jaxb.Resource newResource = new cz.zcu.kiv.crce.rest.internal.jaxb.Resource();

        newResource.setId(resource.getId());

        List<cz.zcu.kiv.crce.rest.internal.jaxb.Capability> caps = newResource.getCapabilities();
        List<cz.zcu.kiv.crce.rest.internal.jaxb.Requirement> reqs = newResource.getRequirements();
        List<cz.zcu.kiv.crce.rest.internal.jaxb.Property> prop = newResource.getProperties();

        //core capabilities
        if (includeCapability(OSGI_IDENTITY_CAP_NAME, filter)) {
            caps.add(prepareOsgiIdentity(resource));
        }
        if (includeCapability(OSGI_CONTENT_CAP_NAME, filter)) {
            caps.add(prepareOsgiContent(resource, ui));
        }
        if (includeCapability(CRCE_IDENTITY_CAP_NAME, filter)) {
            caps.add(prepareCrceIdentity(resource));
        }

        // osgi.wiring.bundle
        if (includeCapability(OSGI_WIRING_BUNDLE, filter)) {
        	addBundleWirings(caps, resource);
        }

        //wirings
        if (includeCapability(OSGI_WIRING_NAME, filter)) {
            addCapabilityWirings(caps, resource, filter);
        }
        if (includeRequirement(OSGI_WIRING_NAME, filter)) {
            addRequirementWirings(reqs, resource);
        }

        // crce.metrics
        if (includeProperty(CRCE_METRICS_NAME, filter)) {
        	addResourceCrceMetricsProperties(prop, resource);
        }

        return newResource;
    }

    /**
     * Prepare RepositoryBean, that will contains metadata from array of resources.
     *
     * @param resources array of resources
     * @param include what part of metadata should be included
     * @param ui contextual info about URI
     * @return Object with metadata from array of resources, that is ready to XML export using JAXB.
     */
    public cz.zcu.kiv.crce.rest.internal.jaxb.Repository convertRepository(List<Resource> resources, MetadataFilter include, UriInfo ui) {

        cz.zcu.kiv.crce.rest.internal.jaxb.Repository repositoryBean = objectFactory.createRepository();
        List<cz.zcu.kiv.crce.rest.internal.jaxb.Resource> resourceBeans = repositoryBean.getResources();

        for (Resource res : resources) {
            resourceBeans.add(convertResource(res, include, ui));
        }

        return repositoryBean;
    }

    /**
     * Get cz.zcu.kiv.crce.rest.internal.jaxb.Resource with unknown crce.status. This cz.zcu.kiv.crce.rest.internal.jaxb.Resource contains only id and capability crce.identity with attribute crce.status, that
     * is "unknown"
     *
     * @param id the resource id
     * @return information about unknown resource
     */
    public cz.zcu.kiv.crce.rest.internal.jaxb.Resource getResourceWithUnknownStatus(String id) {
        cz.zcu.kiv.crce.rest.internal.jaxb.Resource resource = new cz.zcu.kiv.crce.rest.internal.jaxb.Resource();

        resource.setId(id);

        List<cz.zcu.kiv.crce.rest.internal.jaxb.Capability> caps = resource.getCapabilities();

        cz.zcu.kiv.crce.rest.internal.jaxb.Capability crceIdentity = new cz.zcu.kiv.crce.rest.internal.jaxb.Capability();
        crceIdentity.setNamespace("crce.identity");
        List<cz.zcu.kiv.crce.rest.internal.jaxb.Attribute> attributes = crceIdentity.getAttributes();
        addAttribute(attributes, "name", null, resource.getId(), null);
        addAttribute(attributes, "crce.status", null, "unknown", null);

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

    public cz.zcu.kiv.crce.rest.internal.jaxb.Repository mapRepository(List<Resource> resources, MetadataFilter filter, UriInfo uriInfo) {
        cz.zcu.kiv.crce.rest.internal.jaxb.Repository jaxbRepository = objectFactory.createRepository();

        jaxbRepository.setIncrement(0L);
        jaxbRepository.setName("store");

        for (Resource resource : resources) {
            jaxbRepository.getResources().add(mapResource(resource, filter, uriInfo));
        }
        return jaxbRepository;
    }

    public cz.zcu.kiv.crce.rest.internal.jaxb.Resource mapResource(Resource resource, MetadataFilter filter, UriInfo uriInfo) {
        cz.zcu.kiv.crce.rest.internal.jaxb.Resource jaxbResource = objectFactory.createResource();

        jaxbResource.setId(resource.getId());

        List<cz.zcu.kiv.crce.rest.internal.jaxb.Capability> jaxbCapabilities = jaxbResource.getCapabilities();
        for (Capability capability : resource.getRootCapabilities()) {
            if (includeCapability(capability.getNamespace(), filter)) {
                jaxbCapabilities.add(mapCapability(capability));
            }
        }

        List<cz.zcu.kiv.crce.rest.internal.jaxb.Requirement> jaxbRequirements = jaxbResource.getRequirements();
        for (Requirement requirement : resource.getRequirements()) {
            if (includeRequirement(requirement.getNamespace(), filter)) {
                jaxbRequirements.add(mapRequirement(requirement));
            }
        }

        List<cz.zcu.kiv.crce.rest.internal.jaxb.Property> jaxbProperties = jaxbResource.getProperties();
        for (Property<Resource> property : resource.getProperties()) {
            if (includeProperty(property.getNamespace(), filter)) {
                jaxbProperties.add(mapProperty(property));
            }
        }

        return jaxbResource;
    }

    private cz.zcu.kiv.crce.rest.internal.jaxb.Capability mapCapability(Capability capability) {
        cz.zcu.kiv.crce.rest.internal.jaxb.Capability jaxbCapability = objectFactory.createCapability();

        jaxbCapability.setNamespace(capability.getNamespace());
        jaxbCapability.setId(capability.getId());

        List<cz.zcu.kiv.crce.rest.internal.jaxb.Directive> directives = jaxbCapability.getDirectives();
        for (Map.Entry<String, String> entry : capability.getDirectives().entrySet()) {
            directives.add(mapDirective(entry));
        }

        List<cz.zcu.kiv.crce.rest.internal.jaxb.Attribute> attributes = jaxbCapability.getAttributes();
        for (Attribute<?> attribute : capability.getAttributes()) {
            attributes.add(mapAttribute(attribute));
        }

        List<cz.zcu.kiv.crce.rest.internal.jaxb.Capability> capabilities = jaxbCapability.getCapabilities();
        for (Capability child : capability.getChildren()) {
            capabilities.add(mapCapability(child));
        }

        List<cz.zcu.kiv.crce.rest.internal.jaxb.Property> properties = jaxbCapability.getProperties();
        for (Property<Capability> property : capability.getProperties()) {
            properties.add(mapProperty(property));
        }

        return jaxbCapability;
    }

    private cz.zcu.kiv.crce.rest.internal.jaxb.Requirement mapRequirement(Requirement requirement) {
        cz.zcu.kiv.crce.rest.internal.jaxb.Requirement jaxbRequirement = objectFactory.createRequirement();

        jaxbRequirement.setNamespace(requirement.getNamespace());
        jaxbRequirement.setId(requirement.getId());

        List<cz.zcu.kiv.crce.rest.internal.jaxb.Directive> directives = jaxbRequirement.getDirectives();
        for (Map.Entry<String, String> entry : requirement.getDirectives().entrySet()) {
            directives.add(mapDirective(entry));
        }

        List<cz.zcu.kiv.crce.rest.internal.jaxb.Attribute> attributes = jaxbRequirement.getAttributes();
        for (Attribute<?> attribute : requirement.getAttributes()) {
            attributes.add(mapAttribute(attribute));
        }

        List<cz.zcu.kiv.crce.rest.internal.jaxb.Requirement> requirements = jaxbRequirement.getRequirements();
        for (Requirement child : requirement.getChildren()) {
            requirements.add(mapRequirement(child));
        }

        return jaxbRequirement;
    }

    private cz.zcu.kiv.crce.rest.internal.jaxb.Property mapProperty(Property<?> property) {
        cz.zcu.kiv.crce.rest.internal.jaxb.Property jaxbProperty = objectFactory.createProperty();

        jaxbProperty.setNamespace(property.getNamespace());
        jaxbProperty.setId(property.getId());

        List<cz.zcu.kiv.crce.rest.internal.jaxb.Attribute> attributes = jaxbProperty.getAttributes();
        for (Attribute<?> attribute : property.getAttributes()) {
            attributes.add(mapAttribute(attribute));
        }

        // links not implemented yet

        return jaxbProperty;
    }

    private cz.zcu.kiv.crce.rest.internal.jaxb.Directive mapDirective(Map.Entry<String, String> entry) {
        cz.zcu.kiv.crce.rest.internal.jaxb.Directive jaxbDirective = objectFactory.createDirective();

        jaxbDirective.setName(entry.getKey());
        jaxbDirective.setValue(entry.getValue());

        return jaxbDirective;
    }

    private cz.zcu.kiv.crce.rest.internal.jaxb.Attribute mapAttribute(Attribute<?> attribute) {
        cz.zcu.kiv.crce.rest.internal.jaxb.Attribute jaxbAttribute = objectFactory.createAttribute();

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
