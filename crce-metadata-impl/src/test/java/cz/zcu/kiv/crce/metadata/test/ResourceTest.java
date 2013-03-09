package cz.zcu.kiv.crce.metadata.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

import cz.zcu.kiv.crce.metadata.Capability;
import cz.zcu.kiv.crce.metadata.Resource;
import cz.zcu.kiv.crce.metadata.ResourceFactory;
import cz.zcu.kiv.crce.metadata.internal.ResourceFactoryImpl;

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

        root.addChild(child1);
        root.addChild(child2);
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
}
