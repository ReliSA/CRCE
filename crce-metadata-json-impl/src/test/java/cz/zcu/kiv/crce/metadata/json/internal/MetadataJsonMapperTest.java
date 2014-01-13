package cz.zcu.kiv.crce.metadata.json.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.osgi.framework.Version;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Property;
import cz.zcu.kiv.crce.metadata.Repository;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.impl.ListAttributeType;
import cz.zcu.kiv.crce.metadata.impl.SimpleAttributeType;
import cz.zcu.kiv.crce.metadata.internal.ResourceFactoryImpl;
import cz.zcu.kiv.crce.metadata.json.MetadataJsonMapper;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class MetadataJsonMapperTest extends Assert {

    private static MetadataJsonMapper mapper;
    private static ResourceFactory resourceFactory;

    @BeforeClass
    public static void beforeClass() {
        resourceFactory = new ResourceFactoryImpl();
        mapper = new MetadataJsonMapperImpl(resourceFactory);
    }

    @Test
    @SuppressWarnings("CallToThreadDumpStack")
    public void testSerialization() throws URISyntaxException {
        Repository repository = resourceFactory.createRepository(new URI("file://repository/path"));

        Resource resource = resourceFactory.createResource("res1");

        resource.setRepository(repository);

        Property<Resource> resourceProperty = resourceFactory.createProperty("prop.ns1", "prop1");
        resourceProperty.setAttribute(new SimpleAttributeType<>("patr1", String.class), "pval1", Operator.NOT_EQUAL);
        resourceProperty.setAttribute(new SimpleAttributeType<>("patr2", Long.class), 123L, Operator.LESS);

        resource.addProperty(resourceProperty);
        resourceProperty.setParent(resource);


        {   // --- capability 1 ---
            Capability capability = resourceFactory.createCapability("ns1", "cap1");
            resource.addCapability(capability);
            resource.addRootCapability(capability);
            capability.setResource(resource);

            // attributes
            capability.setAttribute(new SimpleAttributeType<>("atr1", String.class), "val1", Operator.EQUAL);
            capability.setAttribute(new SimpleAttributeType<>("atr2", Long.class), 123L, Operator.GREATER);
            capability.setAttribute(new SimpleAttributeType<>("atr3", Double.class), 3.14, Operator.APPROX);
            capability.setAttribute(new SimpleAttributeType<>("atr4", Version.class), new Version(1, 2, 3, "SNAPSHOT"), Operator.NOT_EQUAL);
            capability.setAttribute(new ListAttributeType("atr5"), Arrays.asList("a", "b", "c"));

            // directives
            capability.setDirective("d1", "a");
            capability.setDirective("d2", "b");

            // properties
            resourceFactory.createProperty("ns2", "prop1");

            // --- capability 1 children ---

            // child 1
            Capability child = resourceFactory.createCapability("ns2", "cap2");
            resource.addCapability(child);
            child.setResource(resource);

            capability.addChild(child);
            child.setParent(capability);

            child.setAttribute(new SimpleAttributeType<>("c1atr1", String.class), "c1val1", Operator.EQUAL);

            // child 2
            child = resourceFactory.createCapability("ns3", "cap3");
            resource.addCapability(child);
            child.setResource(resource);

            capability.addChild(child);
            child.setParent(capability);

            child.setAttribute(new SimpleAttributeType<>("c2atr1", String.class), "c2val1", Operator.EQUAL);
        }

        // --- capability 2 ---
        Capability capability = resourceFactory.createCapability("ns4", "cap4");
        resource.addCapability(capability);
        resource.addRootCapability(capability);
        capability.setResource(resource);

        // --- requirement 1 ---
        {
            Requirement requirement = resourceFactory.createRequirement("ns5", "req1");
            requirement.setResource(resource);
            resource.addRequirement(requirement);

            requirement.addAttribute(new SimpleAttributeType<>("atr1", Version.class), new Version(1, 2, 3), Operator.GREATER);
            requirement.addAttribute(new SimpleAttributeType<>("atr2", Version.class), new Version(2, 0, 0), Operator.LESS);

            requirement.setDirective("dir1", "aa");
            requirement.setDirective("dir2", "bb");

            Requirement child = resourceFactory.createRequirement("ns6", "req2");
            child.setResource(resource);
            requirement.addChild(child);
            child.setParent(requirement);

            child.addAttribute(new SimpleAttributeType<>("atr1", String.class), "a1", Operator.GREATER);
            child.addAttribute(new SimpleAttributeType<>("atr2", String.class), "a2", Operator.LESS);

            child.setDirective("dir1", "aa");
            child.setDirective("dir2", "bb");
        }

        // --- requirement 2 ---
        Requirement requirement = resourceFactory.createRequirement("ns7", "req3");
        requirement.setResource(resource);
        resource.addRequirement(requirement);

        requirement.addAttribute(new SimpleAttributeType<>("atr1", String.class), "a", Operator.GREATER);


        String json = mapper.serialize(resource);

        assertNotNull(json);

//        System.out.println("------------------------------------- Serialized --------------------------------------");
//        System.out.println(json);
//        System.out.println("---------------------------------------------------------------------------------------");
//
//        Resource result = mapper.deserialize(json);
//        json = mapper.serialize(result);
//
//        System.out.println("------------------------------------ Deserialized -------------------------------------");
//        System.out.println(json);
//        System.out.println("---------------------------------------------------------------------------------------");
//
//
//        assertTrue(result.equalsTo(resource, EqualityLevel.DEEP_WITH_KEY));


    }
}
