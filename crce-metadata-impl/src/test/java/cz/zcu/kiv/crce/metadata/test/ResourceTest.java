package cz.zcu.kiv.crce.metadata.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Operator;
import cz.zcu.kiv.crce.metadata.Requirement;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.internal.ResourceFactoryImpl;

// TODO test of properties
// TODO test of removing entities

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class ResourceTest {

    private ResourceFactory factory;

    @Before
    public void before() throws Exception {
        factory = new ResourceFactoryImpl();
    }

    @Test
    public void testSimpleCapability() {
        Resource resource = factory.createResource();

        Capability capability = factory.createCapability("a");
        resource.addCapability(capability);
        assertTrue(resource.hasCapability(capability));

        List<Capability> capabilities = resource.getCapabilities();
        assertTrue(capabilities.contains(capability));

        capabilities = resource.getCapabilities("a");
        assertTrue(capabilities.contains(capability));

        capabilities = resource.getCapabilities("b");
        assertFalse(capabilities.contains(capability));

        capabilities = resource.getRootCapabilities();
        assertTrue(capabilities.contains(capability));
    }

    @Test
    public void testChangeSimpleCapability() {
        Resource resource = factory.createResource();

        Capability capability = factory.createCapability("a");
        resource.addCapability(capability);

        capability.setAttribute("atr", String.class, "val");
        assertTrue(resource.hasCapability(capability));

        List<Capability> capabilities = resource.getCapabilities();
        assertTrue(capabilities.contains(capability));

        capabilities = resource.getCapabilities("a");
        assertTrue(capabilities.contains(capability));

        capabilities = resource.getCapabilities("b");
        assertFalse(capabilities.contains(capability));

        capabilities = resource.getRootCapabilities();
        assertTrue(capabilities.contains(capability));
    }

    @Test
    public void testCapabilitiesHierarchyByRoot() {
        Resource resource = factory.createResource();

        Capability root = factory.createCapability("a");

        Capability child1 = factory.createCapability("a.b");
        child1.setAttribute("key", String.class, "child1");

        Capability child2 = factory.createCapability("a.b");
        child2.setAttribute("key", String.class, "child2");

        assertNotEquals(child1, child2);

        root.addChild(child1);
        root.addChild(child2);
        resource.addCapability(root);

        assertTrue(resource.hasCapability(root));
        assertTrue(resource.hasCapability(child1));
        assertTrue(resource.hasCapability(child2));

        List<Capability> rootCapabilities = resource.getRootCapabilities();
        assertTrue(rootCapabilities.contains(root));
        assertFalse(rootCapabilities.contains(child1));
        assertFalse(rootCapabilities.contains(child2));

        List<Capability> capabilities = resource.getCapabilities();
        assertTrue(capabilities.contains(root));
        assertTrue(capabilities.contains(child1));
        assertTrue(capabilities.contains(child2));
    }

    @Test
    public void testCapabilitiesHierarchyByChild() {
        Resource resource = factory.createResource();

        Capability root = factory.createCapability("a");

        Capability child1 = factory.createCapability("a.b");
        child1.setAttribute("key", String.class, "child1");

        Capability child2 = factory.createCapability("a.b");
        child2.setAttribute("key", String.class, "child2");

        // checking of presence of added child is commented because of possible performance decrease
        root.addChild(child1);
        // with the check enabled, this would not be necessary
        child1.setParent(root);

        root.addChild(child2);
        // with the check enabled, this would not be necessary
        child2.setParent(root);

        resource.addCapability(child1);

        assertTrue(resource.hasCapability(root));
        assertTrue(resource.hasCapability(child1));
        assertTrue(resource.hasCapability(child2));

        List<Capability> rootCapabilities = resource.getRootCapabilities();
        assertTrue(rootCapabilities.contains(root));
        assertFalse(rootCapabilities.contains(child1));
        assertFalse(rootCapabilities.contains(child2));

        List<Capability> capabilities = resource.getCapabilities();
        assertTrue(capabilities.contains(root));
        assertTrue(capabilities.contains(child1));
        assertTrue(capabilities.contains(child2));
    }

    @Test
    public void testRequirement() {
        Resource resource = factory.createResource();

        Requirement req = factory.createRequirement("a");

        resource.addRequirement(req);
        assertTrue(resource.hasRequirement(req));

        req.setAttribute("atr", String.class, "val", Operator.GREATER);
        assertTrue(resource.hasRequirement(req));

        List<Requirement> requirements = resource.getRequirements("a");
        assertTrue(requirements.contains(req));

        Requirement req2 = factory.createRequirement("a");
        assertFalse(requirements.contains(req2));

        requirements = resource.getRequirements("b");
        assertFalse(requirements.contains(req));
    }

    @Test
    public void testNestedRequirementByNest() {
        Resource resource = factory.createResource();

        Requirement nest = factory.createRequirement("a");
        Requirement nested = factory.createRequirement("a");
        nest.addChild(nested);

        resource.addRequirement(nest);

        assertTrue(resource.hasRequirement(nest));
        assertFalse(resource.hasRequirement(nested));
    }

    @Test
    public void testNestedRequirementByNested() {
        Resource resource = factory.createResource();

        Requirement nest = factory.createRequirement("a");
        Requirement nested = factory.createRequirement("a");
        nest.addChild(nested);

        // checking of presence of added child is commented because of possible performance decrease

        // with the check enabled, this would not be necessary
        nested.setParent(nest);

        resource.addRequirement(nested);

        assertTrue(resource.hasRequirement(nest));
        assertFalse(resource.hasRequirement(nested));
    }
}
