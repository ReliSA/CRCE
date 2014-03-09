package cz.zcu.kiv.crce.it.json;

import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.felix.dm.Component;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.crce.it.IntegrationTestBase;
import cz.zcu.kiv.crce.it.Options;
import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.EqualityLevel;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.MetadataFactory;
import cz.zcu.kiv.crce.metadata.impl.ListAttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.json.MetadataJsonMapper;
import cz.zcu.kiv.crce.metadata.type.Version;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class MetadataJsonMapperIT extends IntegrationTestBase {

    private static final Logger logger = LoggerFactory.getLogger(MetadataJsonMapperIT.class);

    private volatile MetadataJsonMapper mapper;
    private volatile MetadataFactory metadataFactory;

    @org.ops4j.pax.exam.Configuration
    public final Option[] configuration() {

        logger.info("Option config");

        return options(
                CoreOptions.systemPackage("org.skyscreamer.jsonassert"),
                junitBundles(),
                Options.logging(),
                Options.Osgi.compendium(),
                Options.Felix.dependencyManager(),
                Options.Felix.configAdmin(),

                Options.Crce.metadata(),
                Options.Crce.metadataJson()
        );
    }

    @Override
    protected Component[] getDependencies() {
        return new Component[]{
            createComponent()
                .setImplementation(this)
                .add(createServiceDependency().setService(MetadataJsonMapper.class).setRequired(true))
                .add(createServiceDependency().setService(MetadataFactory.class).setRequired(true))
            };
    }

    @Test
    public void testContext() {
        assertNotNull(metadataFactory);
        assertNotNull(mapper);
    }

    @Test
    public void testResourceSerialization() throws Exception {
        Resource resource = createResource();

        // --- test ---

        String json = mapper.serialize(resource);
        assertNotNull(json);

        Resource actual = mapper.deserialize(json);
        assertNotNull(actual);

        assertTrue(actual.equalsTo(resource, EqualityLevel.DEEP_WITH_KEY));

        JSONAssert.assertEquals(json, mapper.serialize(actual), JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void testCapabilitySerialization() throws Exception {
        List<Capability> capabilities = Arrays.asList(new Capability[]{createCapability1(null), createCapability2(null)});
        for (Capability capability : capabilities) {

            String json = mapper.serialize(capability);
            assertNotNull(json);

            Capability actual = mapper.deserialize(json, Capability.class);
            assertNotNull(actual);

            assertTrue(actual.equalsTo(capability, EqualityLevel.DEEP_WITH_KEY));

            JSONAssert.assertEquals(json, mapper.serialize(actual), JSONCompareMode.NON_EXTENSIBLE);
        }
    }

    @Test
    public void testRequirementSerialization() throws Exception {
        List<Requirement> requirements = Arrays.asList(new Requirement[]{createRequirement1(null), createRequirement2(null)});
        for (Requirement requirement : requirements) {

            String json = mapper.serialize(requirement);
            assertNotNull(json);

            Requirement actual = mapper.deserialize(json, Requirement.class);
            assertNotNull(actual);

            assertTrue(actual.equalsTo(requirement, EqualityLevel.DEEP_WITH_KEY));

            JSONAssert.assertEquals(json, mapper.serialize(actual), JSONCompareMode.NON_EXTENSIBLE);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPropertySerialization() throws Exception {
        List<Property> properties = Arrays.asList(new Property[]{createProperty1(null), createProperty2(null)});
        for (Property property : properties) {

            String json = mapper.serialize(property);
            assertNotNull(json);

            Property actual = mapper.deserialize(json, Property.class);
            assertNotNull(actual);

            assertTrue(actual.equalsTo(property, EqualityLevel.DEEP_WITH_KEY));

            JSONAssert.assertEquals(json, mapper.serialize(actual), JSONCompareMode.NON_EXTENSIBLE);
        }
    }

    private Resource createResource() throws Exception {
        Repository repository = metadataFactory.createRepository(new URI("file://repository/path"));

        Resource resource = metadataFactory.createResource("res1");

        resource.setRepository(repository);

        createProperty1(resource);

        createCapability1(resource);

        createCapability2(resource);

        createRequirement1(resource);

        createRequirement2(resource);

        return resource;
    }

    private Property<?> createProperty1(Resource resource) {
        Property<Resource> resourceProperty = metadataFactory.createProperty("prop.ns1", "prop1");
        resourceProperty.setAttribute(new SimpleAttributeType<>("patr1", String.class), "pval1", Operator.NOT_EQUAL);
        resourceProperty.setAttribute(new SimpleAttributeType<>("patr2", Long.class), 123L, Operator.LESS);

        if (resource != null) {
            resource.addProperty(resourceProperty);
            resourceProperty.setParent(resource);
        }
        return resourceProperty;
    }

    private Requirement createRequirement2(Resource resource) {
        // --- requirement 2 ---
        Requirement requirement = metadataFactory.createRequirement("ns7", "req3");
        if (resource != null) {
            requirement.setResource(resource);
            resource.addRequirement(requirement);
        }

        requirement.addAttribute(new SimpleAttributeType<>("atr1", String.class), "a", Operator.GREATER);

        return requirement;
    }

    private Requirement createRequirement1(Resource resource) {
        Requirement requirement = metadataFactory.createRequirement("ns5", "req1");
        if (resource != null) {
            requirement.setResource(resource);
            resource.addRequirement(requirement);
        }

        requirement.addAttribute(new SimpleAttributeType<>("atr1", Version.class), new Version(1, 2, 3), Operator.GREATER);
        requirement.addAttribute(new SimpleAttributeType<>("atr2", Version.class), new Version(2, 0, 0), Operator.LESS);

        requirement.setDirective("dir1", "aa");
        requirement.setDirective("dir2", "bb");

        Requirement child = metadataFactory.createRequirement("ns6", "req2");
        if (resource != null) {
            child.setResource(resource);
        }
        requirement.addChild(child);
        child.setParent(requirement);

        child.addAttribute(new SimpleAttributeType<>("atr1", String.class), "a1", Operator.GREATER);
        child.addAttribute(new SimpleAttributeType<>("atr2", String.class), "a2", Operator.LESS);

        child.setDirective("dir1", "aa");
        child.setDirective("dir2", "bb");

        return requirement;
    }

    private Capability createCapability1(Resource resource) {
        Capability capability = metadataFactory.createCapability("ns1", "cap1");
        if (resource != null) {
            resource.addCapability(capability);
            resource.addRootCapability(capability);
            capability.setResource(resource);
        }

        // attributes
        capability.setAttribute(new SimpleAttributeType<>("atr1", String.class), "val1", Operator.EQUAL);
        capability.setAttribute(new SimpleAttributeType<>("atr2", Long.class), 123L, Operator.GREATER);
        capability.setAttribute(new SimpleAttributeType<>("atr3", Double.class), 3.14, Operator.APPROX);
        capability.setAttribute(new SimpleAttributeType<>("atr4", Version.class), new Version(1, 2, 3, "SNAPSHOT"), Operator.NOT_EQUAL);
        capability.setAttribute(new ListAttributeType("atr5"), Arrays.asList("a", "b", "c"));

        // directives
        capability.setDirective("d1", "a");
        capability.setDirective("d2", "b");

        createProperty2(capability);

        // --- capability 1 children ---
        // child 1
        Capability child = metadataFactory.createCapability("ns2", "cap2");
        if (resource != null) {
            resource.addCapability(child);
            child.setResource(resource);
        }

        capability.addChild(child);
        child.setParent(capability);

        child.setAttribute(new SimpleAttributeType<>("c1atr1", String.class), "c1val1", Operator.EQUAL);

        // child 2
        child = metadataFactory.createCapability("ns3", "cap3");
        if (resource != null) {
            resource.addCapability(child);
            child.setResource(resource);
        }

        capability.addChild(child);
        child.setParent(capability);

        child.setAttribute(new SimpleAttributeType<>("c2atr1", String.class), "c2val1", Operator.EQUAL);

        return capability;
    }

    private Property<?> createProperty2(Capability capability) {
        // properties
        Property<Capability> property = metadataFactory.createProperty("ns2", "prop1");
        property.setAttribute("atr6", String.class, "pval1");
        property.setAttribute("atr7", String.class, "pval2");

        if (capability != null) {
            capability.addProperty(property);
            property.setParent(capability);
        }

        return property;
    }

    private Capability createCapability2(Resource resource) {
        Capability capability = metadataFactory.createCapability("ns4", "cap4");
        if (resource != null) {
            resource.addCapability(capability);
            resource.addRootCapability(capability);
            capability.setResource(resource);
        }
        return capability;
    }
}
